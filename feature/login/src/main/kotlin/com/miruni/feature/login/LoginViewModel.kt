package com.miruni.feature.login

import androidx.lifecycle.viewModelScope
import com.miruni.core.common.BaseViewModel
import com.miruni.core.domain.fcm.DeviceIdProvider
import com.miruni.core.domain.fcm.RegisterFcmTokenUseCase
import com.miruni.core.result.DataResult
import com.miruni.feature.login.domain.repository.PermissionProvider
import com.miruni.feature.login.domain.usecase.GetGoogleLoginUseCase
import com.miruni.feature.login.domain.usecase.GetKakaoLoginUseCase
import com.miruni.feature.login.domain.usecase.GetLoginUseCase
import com.miruni.feature.login.domain.usecase.GetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getLoginUseCase: GetLoginUseCase,
    private val getGoogleLoginUseCase: GetGoogleLoginUseCase,
    private val getKakaoLoginUseCase: GetKakaoLoginUseCase,
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val deviceIdProvider: DeviceIdProvider,
    private val permissionProvider: PermissionProvider
) :
    BaseViewModel<LoginContract.Event, LoginContract.State, LoginContract.Effect>() {

    override fun setInitialState(): LoginContract.State = LoginContract.State()

    override fun handleEvents(event: LoginContract.Event) {
        when (event) {

            is LoginContract.Event.OnIdChanged -> {
                setState {
                    copy(
                       id = id.copy(value = event.id).clearError()
                    )
                }
            }

            is LoginContract.Event.OnPwChanged -> {
                setState {
                    copy(
                        password = password.copy(value = event.pw).clearError()
                    )
                }
            }

            LoginContract.Event.OnTogglePasswordVisible -> {
                setState { copy(passwordVisible = !passwordVisible) }
            }

            is LoginContract.Event.OnAutoLoginChanged -> {
                setState { copy(autoLogin = event.enabled) }
            }

            LoginContract.Event.OnClearError -> {
                setState {
                    copy(
                        id = id.clearError(),
                        password = password.clearError()
                    )
                }
            }

            LoginContract.Event.OnSignUpClicked -> {
                setEffect { LoginContract.Effect.Navigation.ToSignUp }
            }

            LoginContract.Event.OnResetPasswordClicked -> {
                setEffect { LoginContract.Effect.Navigation.ToResetPassword }
            }

            LoginContract.Event.OnLoginClicked -> {
                viewModelScope.launch {

                    val result = withContext(Dispatchers.IO) {
                        getLoginUseCase(
                            id = viewState.value.id.value,
                            password = viewState.value.password.value,
                            autoLogin = viewState.value.autoLogin
                        )
                    }
                    when (result) {
                        is DataResult.Success -> {
                            handleLoginSuccess()
                        }

                        is DataResult.Error -> {
                            setState {
                                copy(
                                    id = id.copy(
                                        isError = true,
                                        errorMessage = result.error.errorMessage
                                    ),
                                    password = password.copy(
                                        isError = true,
                                        errorMessage = result.error.errorMessage
                                    ),
                                )
                            }
                        }
                    }
                }
//                viewModelScope.launch {
//                    val result = withContext(Dispatchers.IO) {
//                        getLoginUseCase(
//                            id = viewState.value.id.value,
//                            password = viewState.value.password.value,
//                            autoLogin = viewState.value.autoLogin
//                        )
//                    }
//                    when (result) {
//                        is DataResult.Success -> {
//                            val token = result.data
//                            val fcmToken = withContext(Dispatchers.IO) {
//                                registerFcmTokenUseCase(token.accessToken)
//                            }
//                            when (fcmToken) {
//                                is DataResult.Success -> {
//                                    setEffect { LoginContract.Effect.Navigation.ToHome }
//                                }
//                                is DataResult.Error -> {
//                                    setEffect { LoginContract.Effect.Message.Snackbar(fcmToken.error.errorMessage) }
//                                }
//                            }
//
//                        }
//
//                        is DataResult.Error -> {
//                            setState {
//                                copy(
//                                    password = password.copy(
//                                        isError = true,
//                                        errorMessage = result.error.errorMessage
//                                    ),
//                                )
//                            }
//                        }
//                    }
//                }
            }



            LoginContract.Event.OnGoogleLoginClicked -> {
                setEffect { LoginContract.Effect.GoogleLogin }
            }

            is LoginContract.Event.OnGoogleLoginSuccess -> {
                viewModelScope.launch {
                    val result = withContext(Dispatchers.IO) {
                        getGoogleLoginUseCase(event.accessToken, viewState.value.autoLogin)
                    }
                    when (result) {
                        is DataResult.Success -> {
                            handleLoginSuccess()
                        }

                        is DataResult.Error -> {
                            setEffect { LoginContract.Effect.Message.Snackbar(result.error.errorMessage) }
                        }
                    }

                }
            }

            is LoginContract.Event.OnGoogleLoginFail -> {
                setEffect { LoginContract.Effect.Message.Snackbar(event.message) }
            }

            LoginContract.Event.OnKakaoLoginClicked -> {
                setEffect { LoginContract.Effect.KakaoLogin }
            }

            is LoginContract.Event.OnKakaoLoginSuccess -> {
                viewModelScope.launch {
                    val result = withContext(Dispatchers.IO) {
                        getKakaoLoginUseCase(event.accessToken, viewState.value.autoLogin)
                    }
                    when (result) {
                        is DataResult.Success -> {
                            handleLoginSuccess()
                        }

                        is DataResult.Error -> {
                            setEffect { LoginContract.Effect.Message.Snackbar(result.error.errorMessage) }
                        }
                    }
                }

            }

            is LoginContract.Event.OnKakaoLoginFail -> {
                setEffect { LoginContract.Effect.Message.Snackbar(event.message) }
            }

            LoginContract.Event.OnNotificationClicked -> {
                registerFcmAndNavigate(LoginContract.Effect.Navigation.ToStart)
            }

            LoginContract.Event.OnOpenDialog -> {
                setState { copy(isDialogOpen = true) }
            }

            LoginContract.Event.OnCloseDialog -> {
                setState { copy(isDialogOpen = false) }
            }

            LoginContract.Event.OnStartedClicked -> {
                setEffect { LoginContract.Effect.Navigation.ToHome }
            }

        }
    }

    /**
     * 로그인 성공 시 권한 여부에 따른 처리
     */
    private fun handleLoginSuccess() {
        if (permissionProvider.hasNotificationPermission()) {
            // 권한이 이미 있으면 FCM 등록 후 바로 홈으로 이동
            registerFcmAndNavigate(LoginContract.Effect.Navigation.ToHome)
        } else {
            // 권한이 없으면 알림 설정 화면으로 이동
            setEffect { LoginContract.Effect.Navigation.ToNotification }
        }
    }

    /**
     * FCM 토큰을 등록하고 지정된 목적지로 이동
     */
    private fun registerFcmAndNavigate(destination: LoginContract.Effect.Navigation) {
        viewModelScope.launch {
            val deviceId = deviceIdProvider.getDeviceId()
            val tokenResult = withContext(Dispatchers.IO) { getTokenUseCase() }

            when (tokenResult) {
                is DataResult.Success -> {
                    val fcmResult = withContext(Dispatchers.IO) {
                        registerFcmTokenUseCase(
                            token = tokenResult.data,
                            deviceId = deviceId
                        )
                    }
                    if (fcmResult is DataResult.Error) {
                        setEffect { LoginContract.Effect.Message.Snackbar(fcmResult.error.errorMessage) }
                    }
                    setEffect { destination }
                }
                is DataResult.Error -> {
                    setEffect { LoginContract.Effect.Message.Snackbar(tokenResult.error.errorMessage) }
                    // 토큰 획득에 실패해도 일단 목적지로 이동 (또는 기획에 따라 처리)
                    setEffect { destination }
                }
            }
        }
    }
}
