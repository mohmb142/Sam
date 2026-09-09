package com.mohmb142.sam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mohmb142.sam.voice.VoiceEngine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SamApp() }
    }
}

@Composable
private fun SamApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val voice = remember { VoiceEngine(context) }
    var input by remember { mutableStateOf("") }
    var listening by remember { mutableStateOf(false) }
    var showModels by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf("مرحباً، أنا سام. كيف أساعدك؟") }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            listening = true
            voice.listen({ text -> input = text; listening = false }, { error -> messages += error; listening = false })
        } else messages += "أحتاج إذن الميكروفون لاستخدام الصوت."
    }
    DisposableEffect(Unit) { onDispose { voice.release() } }

    MaterialTheme {
        CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            Scaffold(topBar = {
                TopAppBar(title = { Text("سام") }, actions = {
                    TextButton(onClick = { showModels = true }) { Text("النماذج") }
                })
            }) { padding ->
                Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                    LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(messages) { Text(it, style = MaterialTheme.typography.bodyLarge) }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = input, onValueChange = { input = it }, Modifier.weight(1f),
                            placeholder = { Text("قل لسام ما تريد…") }
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            if (input.isNotBlank()) {
                                messages += input
                                messages += "وصل طلبك إلى محرك سام. يمكنك الآن إضافة نموذج AI من زر «النماذج»."
                                input = ""
                            }
                        }) { Text("إرسال") }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        if (!listening && ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            listening = true
                            voice.listen({ text -> input = text; listening = false }, { error -> messages += error; listening = false })
                        } else if (!listening) permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        else listening = false
                    }, Modifier.fillMaxWidth()) {
                        Text(if (listening) "جاري الاستماع…" else "تحدث مع سام")
                    }
                }
            }
        }
    }

    if (showModels) AlertDialog(
        onDismissRequest = { showModels = false },
        title = { Text("نماذج الذكاء الاصطناعي") },
        text = { Text("تم تجهيز التخزين الآمن للمفاتيح ودعم واجهات OpenAI-compatible. واجهة إدارة النماذج الكاملة ستكون الخطوة التالية.") },
        confirmButton = { TextButton(onClick = { showModels = false }) { Text("حسناً") } }
    )
}
