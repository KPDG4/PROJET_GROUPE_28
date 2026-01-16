package com.example.projet1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun Calculator() {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE91E63),
            Color(0xFFFF9800),
            Color(0xFF2196F3),
            Color(0xFF4CAF50)
        )
    )

    var displayValue by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }
    var firstNumber by remember { mutableDoubleStateOf(0.0) }
    var operation by remember { mutableStateOf("") }
    var shouldResetDisplay by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Zone Titre
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CALCULATRICE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.DarkGray
                )
            }

            // Zone d'Écran (Affiche l'expression ET la valeur)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Display(expression = expression, value = displayValue)
            }

            // Clavier structuré
            Keyboard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onNumberClick = { number ->
                    if (shouldResetDisplay) {
                        displayValue = number
                        shouldResetDisplay = false
                    } else {
                        displayValue = if (displayValue == "0") number else displayValue + number
                    }
                },
                onDecimalClick = {
                    if (shouldResetDisplay) {
                        displayValue = "0."
                        shouldResetDisplay = false
                    } else if (!displayValue.contains(".")) {
                        displayValue += "."
                    }
                },
                onOperationClick = { op ->
                    firstNumber = displayValue.toDoubleOrNull() ?: 0.0
                    operation = op
                    expression = "$displayValue $op"
                    shouldResetDisplay = true
                },
                onEqualsClick = {
                    val secondNumber = displayValue.toDoubleOrNull() ?: 0.0
                    val result = when (operation) {
                        "+" -> firstNumber + secondNumber
                        "-" -> firstNumber - secondNumber
                        "*" -> firstNumber * secondNumber
                        "/" -> if (secondNumber != 0.0) firstNumber / secondNumber else Double.NaN
                        else -> secondNumber
                    }
                    expression = if (operation.isNotEmpty()) "$expression $displayValue =" else "$displayValue ="
                    displayValue = if (result.isNaN()) "Erreur"
                    else if (result == result.toLong().toDouble()) result.toLong().toString()
                    else "%.2f".format(result)
                    operation = ""
                    shouldResetDisplay = true
                },
                onClearClick = {
                    displayValue = "0"
                    expression = ""
                    firstNumber = 0.0
                    operation = ""
                    shouldResetDisplay = false
                }
            )
        }
    }
}

@Composable
private fun Display(expression: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = expression, fontSize = 18.sp, color = Color.Gray)
            Text(text = value, fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
private fun Keyboard(
    modifier: Modifier = Modifier,
    onNumberClick: (String) -> Unit,
    onDecimalClick: () -> Unit,
    onOperationClick: (String) -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Ligne 1 : C, Division
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onClearClick, modifier = Modifier.weight(3f), shape = RoundedCornerShape(72.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("C / EFFACER", fontWeight = FontWeight.Bold)
            }
            OperatorButton("/", Modifier.weight(1f), onOperationClick)
        }
        
        // Lignes de chiffres
        calculatorRow(onNumberClick, listOf("7", "8", "9"), "*", onOperationClick)
        calculatorRow(onNumberClick, listOf("4", "5", "6"), "-", onOperationClick)
        calculatorRow(onNumberClick, listOf("1", "2", "3"), "+", onOperationClick)

        // Ligne du bas : 0, point, égal
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { onNumberClick("0") }, modifier = Modifier.weight(2f), shape = RoundedCornerShape(72.dp)) {
                Text("0", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onDecimalClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(72.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                Text(".", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Button(onClick = onEqualsClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(72.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                Text("=", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun calculatorRow(onNumberClick: (String) -> Unit, numbers: List<String>, op: String, onOpClick: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        numbers.forEach { num ->
            Button(onClick = { onNumberClick(num) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(72.dp)) {
                Text(num, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
        OperatorButton(op, Modifier.weight(1f), onOpClick)
    }
}

@Composable
private fun OperatorButton(op: String, modifier: Modifier, onOpClick: (String) -> Unit) {
    Button(onClick = { onOpClick(op) }, modifier = modifier, shape = RoundedCornerShape(72.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))) {
        Text(op, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}
