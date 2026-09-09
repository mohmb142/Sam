package com.mohmb142.sam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SamApp() }
    }
}

@Composable
private fun SamApp() {
    var input by remember { mutableStateOf("") }
    var listening by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf("مرحباً، أنا سام. كيف أساعدك؟") }

    MaterialTheme {
        CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            Scaffold(topBar = { TopAppBar(title = { Text("سام") }) }) { padding ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(messages) { Text(it, style = MaterialTheme.typography.bodyLarge) }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("قل لسام ما تريد…") }
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            if (input.isNotBlank()) {
                                messages += input
                                messages += "تم استلام طلبك. محرك الوكيل سيحلله عند ربط نموذج الذكاء الاصطناعي."
                                input = ""
                            }
                        }) { Text("إرسال") }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { listening = !listening }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (listening) "إيقاف الاستماع" else "تحدث مع سام")
                    }
                }
            }
        }
    }
}
