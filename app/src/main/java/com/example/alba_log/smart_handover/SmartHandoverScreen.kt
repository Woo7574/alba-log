package com.example.alba_log.smart_handover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box // 추가
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn // 추가 (다이얼로그 리스트 높이 제한용)
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog // 추가
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton // 추가
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration // 추가 (화면 높이 얻기)
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alba_log.ui.theme.Alba_logTheme


@Composable
fun SmartHandoverScreen(
    modifier: Modifier = Modifier,
    viewModel: SmartHandoverViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var newItemText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var showConfirmationDialog by remember { mutableStateOf(false) } // 다이얼로그 상태

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "스마트 인수인계",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                label = { Text("새 인수인계 항목") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newItemText.isNotBlank()) {
                            viewModel.addItem(newItemText)
                            newItemText = ""
                            keyboardController?.hide()
                        }
                    }
                )
            )
            Button(
                onClick = {
                    if (newItemText.isNotBlank()) {
                        viewModel.addItem(newItemText)
                        newItemText = ""
                        keyboardController?.hide()
                    }
                },
                enabled = newItemText.isNotBlank()
            ) {
                Icon(Icons.Filled.Add, contentDescription = "항목 추가")
            }
        }

        if (uiState.handoverItems.isEmpty()) {
            Text(
                text = "인수인계 항목이 없습니다. 새로운 항목을 추가해주세요.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.handoverItems, key = { it.id }) { item ->
                    HandoverListItem(
                        item = item,
                        onItemCheckedChange = { viewModel.toggleItemCompletion(item) },
                        onDeleteItemClick = { viewModel.deleteItem(item) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp)) // 버튼 간 간격

        Button(
            onClick = { viewModel.completeHandover() },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.handoverItems.isNotEmpty()
        ) {
            Text("인수인계 완료")
        }

        Spacer(modifier = Modifier.height(8.dp)) // 버튼 간 간격

        Button(
            onClick = { showConfirmationDialog = true }, // 다이얼로그 표시
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.handoverItems.isNotEmpty()
        ) {
            Text("인수인계 확인")
        }
    }

    if (showConfirmationDialog) {
        HandoverConfirmationDialog(
            handoverItems = uiState.handoverItems,
            onDismiss = { showConfirmationDialog = false }
        )
    }
}

@Composable
fun HandoverListItem(
    item: HandoverItem,
    onItemCheckedChange: () -> Unit,
    onDeleteItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = { onItemCheckedChange() }
        )
        Text(
            text = item.task,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        IconButton(onClick = onDeleteItemClick) {
            Icon(Icons.Filled.Delete, contentDescription = "항목 삭제")
        }
    }
}

@Composable
fun HandoverConfirmationDialog(
    handoverItems: List<HandoverItem>,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("인수인계 사항 확인") },
        text = {
            if (handoverItems.isEmpty()) {
                Text("확인할 인수인계 항목이 없습니다.")
            } else {
                // 다이얼로그 내용의 최대 높이를 화면 높이의 일부로 제한
                Box(modifier = Modifier.heightIn(max = screenHeight * 0.6f)) {
                    LazyColumn {
                        items(handoverItems, key = { "confirm_${it.id}" }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (item.isCompleted) "✅" else "⬜️",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(item.task)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun SmartHandoverScreenPreview() {
    Alba_logTheme {
        val navController = rememberNavController()
        var previewNewItemText by remember { mutableStateOf("") }
        val previewItems = remember {
            mutableStateOf(
                listOf(
                    HandoverItem(1, "매장 바닥 청소 (미리보기)", false),
                    HandoverItem(2, "유통기한 확인 (미리보기)", true),
                    HandoverItem(3, "음료 채우기 (미리보기)", false),
                    HandoverItem(4, "과자 정리 (미리보기)", true),
                    HandoverItem(5, "카운터 청소 (미리보기)", false)
                )
            )
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        var showPreviewDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "스마트 인수인계",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = previewNewItemText,
                    onValueChange = { previewNewItemText = it },
                    label = { Text("새 인수인계 항목") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (previewNewItemText.isNotBlank()) {
                                val newId = (previewItems.value.maxOfOrNull { it.id } ?: 0) + 1
                                previewItems.value = listOf(HandoverItem(newId, previewNewItemText, false)) + previewItems.value
                                previewNewItemText = ""
                                keyboardController?.hide()
                            }
                        }
                    )
                )
                Button(
                    onClick = {
                        if (previewNewItemText.isNotBlank()) {
                            val newId = (previewItems.value.maxOfOrNull { it.id } ?: 0) + 1
                            previewItems.value = listOf(HandoverItem(newId, previewNewItemText, false)) + previewItems.value
                            previewNewItemText = ""
                            keyboardController?.hide()
                        }
                    },
                    enabled = previewNewItemText.isNotBlank()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "항목 추가")
                }
            }

            if (previewItems.value.isEmpty()) {
                Text(
                    text = "인수인계 항목이 없습니다. 새로운 항목을 추가해주세요.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(previewItems.value, key = { it.id }) { item ->
                        HandoverListItem(
                            item = item,
                            onItemCheckedChange = {
                                previewItems.value = previewItems.value.map {
                                    if (it.id == item.id) item.copy(isCompleted = !item.isCompleted) else it
                                }
                            },
                            onDeleteItemClick = {
                                previewItems.value = previewItems.value.filter { it.id != item.id }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    println("Preview: Complete Handover clicked. Items: ${previewItems.value}")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = previewItems.value.isNotEmpty()
            ) {
                Text("인수인계 완료")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { showPreviewDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = previewItems.value.isNotEmpty()
            ) {
                Text("인수인계 확인")
            }
        }

        if (showPreviewDialog) {
            HandoverConfirmationDialog(
                handoverItems = previewItems.value,
                onDismiss = { showPreviewDialog = false }
            )
        }
    }
}
