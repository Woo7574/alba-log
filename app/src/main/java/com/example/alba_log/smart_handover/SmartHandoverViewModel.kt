package com.example.alba_log.smart_handover

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SmartHandoverViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SmartHandoverUiState())
    val uiState: StateFlow<SmartHandoverUiState> = _uiState.asStateFlow()

    init {
        // 초기 샘플 데이터 로드 (최신 항목이 위로 오도록)
        loadInitialHandoverItems()
    }

    private fun loadInitialHandoverItems() {
        val initialItems = listOf(
            HandoverItem(5, "다음 근무자에게 전달사항 메모 남기기", false),
            HandoverItem(4, "포스 마감 준비", false),
            HandoverItem(3, "재고 정리 (음료 코너)", true),
            HandoverItem(2, "유통기한 확인 (냉장 식품)", false),
            HandoverItem(1, "매장 바닥 청소", false)
        )
        // ID 순서대로 정렬 후 역순으로 (최신 ID가 위로 오도록, addItem과 일관성 유지)
        _uiState.value = _uiState.value.copy(handoverItems = initialItems.sortedBy { it.id }.reversed())
    }

    fun toggleItemCompletion(item: HandoverItem) {
        _uiState.update { currentState ->
            val updatedItems = currentState.handoverItems.map {
                if (it.id == item.id) {
                    it.copy(isCompleted = !it.isCompleted)
                } else {
                    it
                }
            }
            currentState.copy(handoverItems = updatedItems)
        }
    }

    fun addItem(task: String) {
        _uiState.update { currentState ->
            val newId = (currentState.handoverItems.maxOfOrNull { it.id } ?: 0) + 1
            val newItem = HandoverItem(id = newId, task = task, isCompleted = false)
            // 새 항목을 리스트의 맨 앞에 추가
            currentState.copy(handoverItems = listOf(newItem) + currentState.handoverItems)
        }
    }

    fun deleteItem(itemToDelete: HandoverItem) {
        _uiState.update { currentState ->
            val updatedItems = currentState.handoverItems.filter { it.id != itemToDelete.id }
            currentState.copy(handoverItems = updatedItems)
        }
    }

    fun completeHandover() {
        val itemsToSave = _uiState.value.handoverItems
        if (itemsToSave.isEmpty()) {
            println("SmartHandoverViewModel: No items to complete.")
            return
        }
        println("SmartHandoverViewModel: Completing handover with ${itemsToSave.size} items:")
        itemsToSave.forEach { item ->
            println("  Item ID: ${item.id}, Task: \"${item.task}\", Completed: ${item.isCompleted}")
        }
        // TODO: 여기에 실제 데이터 저장 로직 (예: Room DB, Firebase 등) 추가
        // 예를 들어, 완료된 항목들을 DB에 저장하거나, 서버로 전송할 수 있습니다.
        // 현재는 로그 출력으로 대체합니다.

        // 완료 후, 선택적으로 항목 목록을 비우거나 다른 상태로 변경할 수 있습니다.
        // 예를 들어, 모든 항목을 완료된 것으로 간주하고 목록을 비우려면:
        // _uiState.update { it.copy(handoverItems = emptyList(), /* 다른 상태 업데이트 */ ) }
        // 지금은 상태를 변경하지 않고 그대로 둡니다.
    }
}

data class SmartHandoverUiState(
    val handoverItems: List<HandoverItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
