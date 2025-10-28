package com.example.alba_log.expiration_date_manager

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots // 오류 해결을 위해 추가
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Product 데이터에 대한 접근을 관리하는 저장소.
 * 데이터 소스(Firestore)와 ViewModel 사이의 중재자 역할을 한다.
 */
class ProductRepository {

    // Firestore 인스턴스 및 'product_items' 컬렉션 참조
    private val productCollection = Firebase.firestore.collection("product_items")

    /**
     * Firestore의 모든 상품 목록을 Flow 형태로 가져온다.
     * 데이터가 변경되면 자동으로 스트림에 새로운 값이 발행된다.
     */
    val allProducts: Flow<List<ProductItem>> = productCollection
        .orderBy("expirationDate", Query.Direction.ASCENDING) // 유통기한 오름차순으로 정렬
        .snapshots() // 실시간 업데이트 수신
        .map { snapshot ->
            snapshot.toObjects<ProductItem>()
        }

    /**
     * 새로운 상품을 Firestore에 추가한다.
     */
    suspend fun insert(product: ProductItem) {
        // Firestore의 add 함수는 자동으로 ID를 생성하며,
        // ProductItem의 @DocumentId 필드는 데이터를 읽어올 때 자동으로 채워진다.
        productCollection.add(product).await()
    }

    /**
     * 특정 상품을 Firestore에서 삭제한다.
     * @param productId 삭제할 상품의 문서 ID
     */
    suspend fun delete(productId: String) {
        productCollection.document(productId).delete().await()
    }
}
