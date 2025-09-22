package com.example.alba_log.smart_handover

data class HandoverItem(
    val id: Int,
    val task: String,
    var isCompleted: Boolean = false
)
