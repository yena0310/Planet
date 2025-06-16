package com.example.planet.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// 앱 전체에서 사용할 사용자 상태 관리 싱글톤
object UserStateManager {
    private const val PREFS_NAME = "user_state_prefs"
    private const val KEY_USER_ID = "current_user_id"

    private var sharedPrefs: SharedPreferences? = null

    var currentUserId: String? by mutableStateOf(null)
        private set

    var isLoggedIn: Boolean by mutableStateOf(false)
        private set

    // SharedPreferences 초기화
    fun initialize(context: Context) {
        if (sharedPrefs == null) {
            sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            // 저장된 사용자 ID 복원
            val savedUserId = sharedPrefs?.getString(KEY_USER_ID, null)
            if (!savedUserId.isNullOrEmpty()) {
                currentUserId = savedUserId
                isLoggedIn = true
                Log.d("UserStateManager", "저장된 사용자 복원: $savedUserId")
            }
        }
    }

    // 사용자 로그인 설정
    fun setUser(userId: String) {
        currentUserId = userId
        isLoggedIn = true
        // SharedPreferences에 저장
        sharedPrefs?.edit()?.putString(KEY_USER_ID, userId)?.apply()
        Log.d("UserStateManager", "사용자 설정됨: $userId")
    }

    // 사용자 로그아웃
    fun clearUser() {
        currentUserId = null
        isLoggedIn = false
        // SharedPreferences에서 제거
        sharedPrefs?.edit()?.remove(KEY_USER_ID)?.apply()
        Log.d("UserStateManager", "사용자 정보 클리어됨")
    }

    // 현재 사용자 ID 가져오기 (UserState > Firebase Auth 순)
    fun getUserId(): String? {
        return currentUserId ?: com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
    }
}