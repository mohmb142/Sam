package com.mohmb142.sam

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mohmb142.sam.ai.OpenRouterDefaults
import com.mohmb142.sam.ai.SamChatViewModel
import com.mohmb142.sam.voice.VoiceEngine

class MainActivity : ComponentActivity() {
    private val vm: SamChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SamApp(vm, onEnableCallScreening = ::requestCallScreeningRole) }
    }

    private fun requestCallScreeningRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
            startActivityForResult(
                roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING),
                REQUEST_CALL_SCREENING
            )
        }
    }

    companion object { private const val REQUEST_CALL_SCREENING = 401 }
}

@Composable
private fun SamApp(vm: SamChatViewModel, onEnableCallScreening: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val voice = remember { VoiceEngine(context) }
    val messages by vm.messages.collectAsState()
    val models by vm.models.collectAsState()
    val busy by vm.busy.collectAsState()
    var input by remember { mutableStateOf("") }
    var listening by remember { mutableStateOf(false) }
    var showModels by remember { mutableStateOf(false) }
    var apiKey by remember { mutableStateOf("") }
    var model by remember { mutableStateOf(OpenRouterDefaults.DEFAULT_MODEL) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            listening = true
            voice.listen({ text -> input = text; listening = false }, { error -> input = error; listening = false })
        }
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
                        if (busy) item { Text("سام يفكر…") }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("قل لسام ما تريد…") },
                            enabled = !busy
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(enabled = !busy, onClick = {
                            val text = input.trim()
                            if (text.isNotEmpty()) { vm.send(text); input = "" }
                        }) { Text("إرسال") }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        if (!listening && ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            listening = true
                            voice.listen({ text -> input = text; listening = false }, { error -> input = error; listening = false })
                        } else if (!listening) permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        else listening = false
                    }, Modifier.fillMaxWidth()) {
                        Text(if (listening) "جاري الاستماع…" else "تحدث مع سام")
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onEnableCallScreening, Modifier.fillMaxWidth()) {
                        Text("تفعيل التعرف على المكالمات")
                    }
                }
            }
        }
    }

    if (showModels) {
        AlertDialog(
            onDismissRequest = { showModels = false },
            title = { Text("إعداد OpenRouter") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("عدد النماذج المحفوظة: ${models.size}")
                    OutlinedTextField(apiKey, { apiKey = it }, modifier = Modifier.fillMaxWidth(), label = { Text("OpenRouter API Key") }, singleLine = true)
                    OutlinedTextField(model, { model = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Model ID") }, singleLine = true)
                    Text("Base URL: ${OpenRouterDefaults.BASE_URL}")
                    Text("مثال مجاني متاح حالياً: openai/gpt-oss-120b:free")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.saveOpenRouter(apiKey, model)
                    apiKey = ""
                    showModels = false
                }) { Text("حفظ") }
            },
            dismissButton = { TextButton(onClick = { showModels = false }) { Text("إلغاء") } }
        )
    }
}
