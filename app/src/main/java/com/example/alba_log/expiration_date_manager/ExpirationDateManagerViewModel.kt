package com.example.alba_log.expiration_date_manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

// UI 상태를 나타내는 데이터 클래스
data class ExpirationDateUiState(
    val productItems: List<ProductItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExpirationDateManagerViewModel : ViewModel() {

    private val repository = ProductRepository()

    // Firestore의 실시간 데이터를 UI 상태(StateFlow)로 변환
    val uiState: StateFlow<ExpirationDateUiState> = repository.allProducts
        .map { products -> ExpirationDateUiState(productItems = products, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            // 구독자가 있을 때만 Flow를 활성화하고, 5초 후에 중지
            started = SharingStarted.WhileSubscribed(5000),
            // 초기 상태는 로딩 중으로 표시
            initialValue = ExpirationDateUiState(isLoading = true)
        )

    /**
     * 새로운 상품을 Firestore에 추가한다.
     */
    fun addProduct(name: String, barcode: String, expirationDate: LocalDate) {
        viewModelScope.launch {
            // LocalDate를 Firestore의 Timestamp로 변환
            val instant = expirationDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val timestamp = Timestamp(instant.epochSecond, instant.nano)

            val newItem = ProductItem(
                name = name,
                barcode = barcode,
                expirationDate = timestamp
            )
            repository.insert(newItem)
        }
    }

    /**
     * 특정 상품을 Firestore에서 삭제한다.
     */
    fun deleteProduct(product: ProductItem) {
        viewModelScope.launch {
            // id가 비어있으면 삭제를 시도하지 않음
            if (product.id.isNotBlank()) {
                repository.delete(product.id)
            }
        }
    }
}
