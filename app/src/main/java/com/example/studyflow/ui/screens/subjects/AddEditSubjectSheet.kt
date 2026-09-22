package com.example.studyflow.ui.screens.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyflow.data.local.SubjectEntity
import com.example.studyflow.ui.components.parseHexColor

private val PALETTE = listOf(
    "#2E9B68", // green
    "#173B67", // blue
    "#D64545", // red
    "#C78A2E", // amber
    "#8E44AD", // purple
    "#16A085", // teal
    "#E67E22", // orange
    "#3498DB"  // sky
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubjectSheet(
    existing: SubjectEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, code: String?, colourHex: String, lecturer: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var code by remember { mutableStateOf(existing?.code ?: "") }
    var lecturer by remember { mutableStateOf(existing?.lecturer ?: "") }
    var colour by remember { mutableStateOf(existing?.colourHex ?: PALETTE.first()) }
    var nameError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F2744),
        contentColor = Color(0xFFE8EEF6)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                if (existing == null) "New subject" else "Edit subject",
                color = Color(0xFFE8EEF6),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = { Text("Subject name") },
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = sheetFieldColors()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Subject code (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = sheetFieldColors()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = lecturer,
                onValueChange = { lecturer = it },
                label = { Text("Lecturer (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = sheetFieldColors()
            )

            Spacer(Modifier.height(16.dp))
            Text("Colour", color = Color(0xFFE8EEF6).copy(alpha = 0.8f), fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PALETTE.forEach { hex ->
                    val isSelected = hex == colour
                    // Parse once per swatch — not a composable call, pure function
                    val swatchColor = remember(hex) { parseHexColor(hex) }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(swatchColor, CircleShape)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .clickable { colour = hex }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Cancel", color = Color(0xFFE8EEF6))
                }
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            nameError = "Name is required"
                            return@Button
                        }
                        onSave(name.trim(), code, colour, lecturer)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E9B68),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (existing == null) "Add" else "Save")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun sheetFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF2E9B68),
    unfocusedBorderColor = Color(0xFFE8EEF6).copy(alpha = 0.4f),
    focusedLabelColor = Color(0xFF2E9B68),
    unfocusedLabelColor = Color(0xFFE8EEF6).copy(alpha = 0.7f),
    focusedTextColor = Color(0xFFE8EEF6),
    unfocusedTextColor = Color(0xFFE8EEF6),
    cursorColor = Color(0xFF2E9B68)
)