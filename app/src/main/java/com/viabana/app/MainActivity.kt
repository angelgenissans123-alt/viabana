package com.viabana.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val rojo = Color(0xFFC71920)
private val amarillo = Color(0xFFFFD43B)

data class Pote(val nombre: String, val precio: Int, val maxSabores: Int)
data class ItemCarrito(val pote: Pote, val sabores: List<String>, val cantidad: Int)
private val potes = listOf(
    Pote("Pote 1 kilo", 10900, 4),
    Pote("Pote 1/2 kilo", 6900, 3),
    Pote("Pote 1/4 kilo", 4300, 2)
)
// Lista provisional: la disponibilidad real se conectará al inventario.
private val saboresDisponibles = listOf(
    "Chocolate", "Dulce de leche", "Frutilla", "Vainilla",
    "Granizado", "Sambayón", "Limón", "Americana"
)
private fun pesos(valor: Int): String = "$" + "%,d".format(java.util.Locale.GERMANY, valor)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PantallaViabana() }
    }
}

@Composable
fun PantallaViabana() {
    var categoria by remember { mutableStateOf("Potes") }
    var poteElegido by remember { mutableStateOf<Pote?>(null) }
    var seleccion by remember { mutableStateOf(setOf<String>()) }
    var carritoVisible by remember { mutableStateOf(false) }
    val carrito = remember { mutableStateListOf<ItemCarrito>() }
    val categorias = listOf("Potes", "Palitos", "Kiosco", "Comida para llevar")
    MaterialTheme {
        Column(Modifier.fillMaxSize().background(Color.White)) {
            Column(Modifier.fillMaxWidth().background(rojo).padding(20.dp)) {
                Text("VIABANA", color = amarillo, fontSize = 32.sp)
                Text("Hacé tu pedido", color = Color.White, fontSize = 20.sp)
            }
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total: " + pesos(carrito.sumOf { it.pote.precio * it.cantidad }))
                TextButton(onClick = { carritoVisible = !carritoVisible }) {
                    Text(if (carritoVisible) "Seguir comprando" else "Carrito (${carrito.sumOf { it.cantidad }})")
                }
            }
            if (carritoVisible) {
                LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(carrito.toList()) { item ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text("${item.pote.nombre} x${item.cantidad} - ${pesos(item.pote.precio * item.cantidad)}")
                                Text(item.sabores.joinToString(", "))
                                Row {
                                    TextButton(onClick = {
                                        val i = carrito.indexOf(item)
                                        if (i >= 0) carrito[i] = item.copy(cantidad = item.cantidad + 1)
                                    }) { Text("+") }
                                    TextButton(onClick = {
                                        val i = carrito.indexOf(item)
                                        if (i >= 0) {
                                            if (item.cantidad <= 1) carrito.removeAt(i)
                                            else carrito[i] = item.copy(cantidad = item.cantidad - 1)
                                        }
                                    }) { Text("−") }
                                    TextButton(onClick = { carrito.remove(item) }) { Text("Quitar") }
                                }
                            }
                        }
                    }
                    item { Text("El pago y el envío del pedido se habilitarán en una próxima versión.") }
                }
            } else {
                LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(categorias) { nombre ->
                        Button(
                            onClick = { categoria = nombre; poteElegido = null; seleccion = emptySet() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (categoria == nombre) rojo else amarillo,
                                contentColor = if (categoria == nombre) Color.White else Color.Black
                            )
                        ) { Text(nombre) }
                    }
                    if (categoria == "Potes") {
                        items(potes) { pote ->
                            OutlinedButton(onClick = {
                                poteElegido = pote
                                seleccion = emptySet()
                            }, modifier = Modifier.fillMaxWidth()) {
                                Text("${pote.nombre} — ${pesos(pote.precio)}")
                            }
                        }
                        val elegido = poteElegido
                        if (elegido != null) {
                            item {
                                Text("Elegí de 1 a ${elegido.maxSabores} sabores", fontSize = 18.sp)
                            }
                            items(saboresDisponibles) { sabor ->
                                Row {
                                    Checkbox(
                                        checked = sabor in seleccion,
                                        onCheckedChange = { marcado ->
                                            seleccion = if (marcado && seleccion.size < elegido.maxSabores)
                                                seleccion + sabor
                                            else if (!marcado) seleccion - sabor
                                            else seleccion
                                        }
                                    )
                                    Text(sabor, modifier = Modifier.padding(top = 12.dp))
                                }
                            }
                            item {
                                Button(
                                    enabled = seleccion.isNotEmpty(),
                                    onClick = {
                                        val sabores = seleccion.sorted()
                                        val i = carrito.indexOfFirst {
                                            it.pote == elegido && it.sabores == sabores
                                        }
                                        if (i >= 0) carrito[i] = carrito[i].copy(cantidad = carrito[i].cantidad + 1)
                                        else carrito.add(ItemCarrito(elegido, sabores, 1))
                                        seleccion = emptySet()
                                        poteElegido = null
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Agregar al carrito") }
                            }
                        }
                    } else {
                        item { Text("Próximamente: productos de $categoria") }
                    }
                }
            }
        }
    }
}
