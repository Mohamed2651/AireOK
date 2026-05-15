package com.mohammed.aireok.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammed.aireok.domain.entity.auth.UserEntity
import com.mohammed.aireok.domain.useCase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = authUseCase.currentUser

    fun logout() {
        viewModelScope.launch { authUseCase.logout() }
    }
}
