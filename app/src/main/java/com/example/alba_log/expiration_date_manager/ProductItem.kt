package com.example.alba_log.expiration_date_manager

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * 유통기한 관리 대상 상품 정보를 담는 데이터 클래스 (Firestore 모델)
 *
 * @property id Firestore 문서의 고유 ID
 * @property name 상품명
 * @property barcode 상품 바코드 번호
 * @property expirationDate 상품의 유통기한 (Timestamp)
 */
data class ProductItem(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val barcode: String = "",
    val expirationDate: Timestamp? = null
)
