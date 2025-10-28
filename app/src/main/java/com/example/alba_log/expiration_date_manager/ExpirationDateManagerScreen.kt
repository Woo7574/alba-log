package com.example.alba_log.expiration_date_manager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alba_log.ui.theme.Alba_logTheme
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// Timestamp를 LocalDate로 변환하는 확장 함수
fun Timestamp.toLocalDate(): LocalDate {
    return this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpirationDateManagerScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ExpirationDateManagerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("유통기한 관리자") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "상품 추가")
            }
        }
    ) { paddingValues ->
        if (showAddDialog) {
            AddProductDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, barcode, date ->
                    viewModel.addProduct(name, barcode, date)
                    showAddDialog = false
                }
            )
        }

        if (uiState.productItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "등록된 상품이 없습니다.\nFAB를 눌러 새 상품을 등록하세요.",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.productItems, key = { it.id }) { item ->
                    ProductListItem(
                        item = item,
                        onDeleteClick = { viewModel.deleteProduct(item) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, barcode: String, expirationDate: LocalDate) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = expirationDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            expirationDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        showDatePicker = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새 상품 등록") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("상품명") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("바코드") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = expirationDate.format(dateFormatter),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("유통기한") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "날짜 선택"
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, barcode, expirationDate) },
                enabled = name.isNotBlank() && barcode.isNotBlank()
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun ProductListItem(
    item: ProductItem,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val expirationLocalDate = item.expirationDate?.toLocalDate()

    val dDay = expirationLocalDate?.let { ChronoUnit.DAYS.between(today, it) }

    val dDayText = when {
        dDay == null -> "-"
        dDay < 0 -> "기한 만료"
        dDay == 0L -> "D-DAY"
        else -> "D-$dDay"
    }
    val dDayColor = when {
        dDay == null -> Color.Gray
        dDay <= 3 -> MaterialTheme.colorScheme.error
        dDay <= 7 -> Color(0xFFFFA500) // 주황색
        else -> MaterialTheme.colorScheme.primary
    }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    val formattedDate = expirationLocalDate?.format(dateFormatter) ?: "날짜 없음"

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "바코드: ${item.barcode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "유통기한: $formattedDate",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = dDayText,
                color = dDayColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "삭제"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpirationDateManagerScreenPreview() {
    Alba_logTheme {
        val navController = rememberNavController()
        // Preview에서는 ViewModel의 실제 데이터를 사용하기 어려우므로,
        // 디자인을 확인하는 용도로만 사용합니다.
        ExpirationDateManagerScreen(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListItemPreview() {
    Alba_logTheme {
        val previewDate = LocalDate.now().plusDays(3)
        val previewInstant = previewDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val previewTimestamp = Timestamp(previewInstant.epochSecond, previewInstant.nano)

        ProductListItem(
            item = ProductItem(
                id = "previewId123",
                name = "서울우유 1L",
                barcode = "8801115115212",
                expirationDate = previewTimestamp
            ),
            onDeleteClick = {}
        )
    }
}
