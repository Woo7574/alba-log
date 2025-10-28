package com.example.alba_log

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

/**
 * 앱의 생명주기와 연결된 Application 클래스.
 * Firebase 초기화 및 전역 인스턴스 관리에 사용될 수 있다.
 */
class AlbaLogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 앱 시작 시 Firebase SDK를 수동으로 명시적 초기화합니다. (오타 수정)
        Firebase.initialize(this)
    }
}
