package com.viabana.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Red = Color(0xFFC71920)
private val Yellow = Color(0xFFFFD43B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ViabanaHome() }
    }
}

@Composable
fun ViabanaHome() {
    var selected by remember { mutableStateOf("Potes") }
    val categories = listOf("Potes", "Palitos", "Kiosco", "Comida para llevar")
    MaterialTheme {
        Column(Modifier.fillMaxSize().background(Color.White)) {
            Column(Modifier.fillMaxWidth().background(Red).padding(24.dp)) {
                Text("VIABANA", color = Yellow, fontSize = 34.sp)
                Text("Hacé tu pedido", color = Color.White, fontSize = 20.sp)
            }
            LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories.size) { index ->
                    val category = categories[index]
                    Button(
                        onClick = { selected = category },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected == category) Red else Yellow,
                            contentColor = if (selected == category) Color.White else Color.Black
                        )
                    ) { Text(category) }
                }
                item {
                    Text("Categoría: $selected", fontSize = 22.sp)
                    Text("Próximamente: catálogo, selección de sabores y carrito.")
                }
            }
        }
    }
}
