package com.miruni.feature.mypage

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MainColor
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.designsystem.MiruniTypography
import com.miruni.core.navigation.MyPageRoute

private const val TAG = "MyPageScreen"

@Composable
fun MyPageRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d(TAG, "MyPageRoute LaunchedEffect - collecting effects")
        viewModel.effect.collect { effect ->
            Log.d(TAG, "Effect received: $effect")
            when (effect) {
                MyPageContract.Effect.Navigation.NavigateToSettingAccount -> {
                    Log.d(TAG, "Navigating to SettingAccount")
                    navController.navigate(MyPageRoute.MyPageSettingAccount.route)
                }

                MyPageContract.Effect.Navigation.NavigateToSettingNotification -> {
                    Log.d(TAG, "Navigating to SettingNotification")
                    navController.navigate(MyPageRoute.MyPageSettingNotification.route)
                }

                MyPageContract.Effect.Navigation.NavigateToInfo -> {
                    Log.d(TAG, "Navigating to Info")
                    navController.navigate(MyPageRoute.MyPageInfo.route)
                }

                is MyPageContract.Effect.Message.Toast -> {
                    Log.d(TAG, "Showing toast: ${effect.message}")
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is MyPageContract.Effect.Message.Error -> {
                    Log.e(TAG, "Showing error: ${effect.message}")
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }

                MyPageContract.Effect.ProfileUpdateSuccess -> {
                    Log.d(TAG, "Profile update success")
                }
            }
        }
    }

    MyPageScreen(
        state = state,
        onEvent = viewModel::setEvent
    )
}

@Composable
fun MyPageScreen(
    state: MyPageContract.State,
    onEvent: (MyPageContract.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Log.d(TAG, "MyPageScreen recomposing - isEditMode: ${state.isEditMode}, isLoading: ${state.isLoading}")

    Scaffold(
        containerColor = MainColor.miruni_green,
        topBar = {
            TopSection(
                modifier = modifier.wrapContentHeight(),
                state = state,
                onEvent = onEvent
            )
        },
        bottomBar = {
            BottomSection(
                state = state,
                onEvent = onEvent,
            )
        }
    ) { innerPadding ->
        MyPageContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
        )
    }
}

@Composable
fun MyPageContent(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        // Content area - can be extended later
    }
}

@Composable
private fun TopSection(
    modifier: Modifier,
    state: MyPageContract.State,
    onEvent: (MyPageContract.Event) -> Unit
) {
    Log.d(TAG, "TopSection - isEditMode: ${state.isEditMode}, selectedIndex: ${state.selectedProfileImageIndex}")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("topSection"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar with edit and notification icons
        Row(
            modifier = modifier
                .fillMaxWidth()
                .testTag("topBarRow"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            // 수정 모드가 아닐 때만 편집/알림 아이콘 표시
            if (!state.isEditMode) {
                IconButton(
                    onClick = {
                        Log.d(TAG, "Edit button clicked")
                        onEvent(MyPageContract.Event.OnTopBarEditClick)
                    },
                    modifier = Modifier.testTag("editButton")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "edit",
                        modifier = Modifier.padding(4.dp)
                    )
                }

                IconButton(
                    onClick = {
                        Log.d(TAG, "Notification button clicked")
                        onEvent(MyPageContract.Event.OnTopBarNotificationClick)
                    },
                    modifier = Modifier.testTag("notificationButton")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "notifications",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            } else {
                // 수정 모드일 때는 빈 공간 유지 (레이아웃 높이 유지)
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Profile image section
        ProfileImageSection(
            state = state,
            onEvent = onEvent,
            modifier = modifier
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Nickname section
        NicknameSection(
            state = state,
            onEvent = onEvent
        )

        // Email (view mode only)
        if (!state.isEditMode && state.email.isNotEmpty()) {
            Text(
                text = state.email,
                style = AppTypography.body_regular_14,
                color = Gray.gray_700,
                modifier = Modifier.testTag("emailText")
            )
        }

        // Complete button (edit mode only)
        if (state.isEditMode) {
            CompleteButton(
                state = state,
                onEvent = onEvent
            )
        }

        // Error message
        state.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = Color.Red,
                style = AppTypography.body_regular_14,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("errorMessage")
            )
        }
    }

    Spacer(modifier = Modifier.height(28.dp))
}

@Composable
private fun ProfileImageSection(
    state: MyPageContract.State,
    onEvent: (MyPageContract.Event) -> Unit,
    modifier: Modifier
) {
    val images = state.profileImages
    val current = state.selectedProfileImageIndex
    val size = images.size

    val prevIndex = (current - 1 + size) % size
    val nextIndex = (current + 1) % size

    Log.d(TAG, "ProfileImageSection - isEditMode: ${state.isEditMode}, current: $current, prev: $prevIndex, next: $nextIndex")

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.testTag("profileImageSection")
    ) {
        // 수정 모드일 때만 이전 이미지와 화살표 표시
        if (state.isEditMode) {
            Image(
                painter = painterResource(images[prevIndex].resId),
                contentDescription = "previous profile",
                modifier = modifier
                    .size(60.dp)
                    .testTag("prevProfileImage"),
            )

            IconButton(
                onClick = {
                    Log.d(TAG, "Previous arrow clicked")
                    onEvent(MyPageContract.Event.OnProfileImagePrevClick)
                },
                modifier = Modifier.testTag("prevArrowButton")
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, "profile prev click")
            }
        }

        // 현재 선택된 프로필 이미지 (항상 표시)
        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(images[current].resId),
                contentDescription = "current profile",
                modifier = modifier
                    .size(87.dp)
                    .testTag("currentProfileImage"),
            )

            // 로딩 중일 때 오버레이
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("loadingIndicator"),
                    color = Color.White
                )
            }
        }

        // 수정 모드일 때만 다음 화살표와 이미지 표시
        if (state.isEditMode) {
            IconButton(
                onClick = {
                    Log.d(TAG, "Next arrow clicked")
                    onEvent(MyPageContract.Event.OnProfileImageNextClick)
                },
                modifier = Modifier.testTag("nextArrowButton")
            ) {
                Icon(Icons.Default.KeyboardArrowRight, "profile next click")
            }

            Image(
                painter = painterResource(images[nextIndex].resId),
                contentDescription = "next profile",
                modifier = modifier
                    .size(60.dp)
                    .testTag("nextProfileImage"),
            )
        }
    }
}

@Composable
private fun NicknameSection(
    state: MyPageContract.State,
    onEvent: (MyPageContract.Event) -> Unit
) {
    Log.d(TAG, "NicknameSection - isEditMode: ${state.isEditMode}, nickname: ${state.nickName}")

    if (state.isEditMode) {
        // 수정 모드: TextField
        TextField(
            value = state.nickName,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
            ),
            onValueChange = { newValue ->
                Log.d(TAG, "Nickname changed to: $newValue")
                onEvent(MyPageContract.Event.OnNicknameChange(nickname = newValue))
            },
            singleLine = true,
            enabled = !state.isLoading,
            modifier = Modifier
                .width(150.dp)
                .testTag("nicknameTextField"),
            textStyle = TextStyle(textAlign = TextAlign.Center)
        )
    } else {
        // 보기 모드: Text
        Text(
            text = state.nickName,
            style = MiruniTypography.titleMedium,
            color = Color.Black,
            modifier = Modifier.testTag("nicknameText")
        )
    }
}

@Composable
private fun CompleteButton(
    state: MyPageContract.State,
    onEvent: (MyPageContract.Event) -> Unit
) {
    Log.d(TAG, "CompleteButton - isCompleteEnabled: ${state.isCompleteEnabled}, isLoading: ${state.isLoading}")

    TextButton(
        enabled = state.isCompleteEnabled && !state.isLoading,
        onClick = {
            Log.d(TAG, "Complete button clicked")
            onEvent(MyPageContract.Event.OnCompleteClick)
        },
        modifier = Modifier.testTag("completeButton")
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "완료",
                color = if (state.isCompleteEnabled) Color.White else Gray.gray_500,
                style = TextStyle(
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}

@Composable
private fun BottomSection(
    state: MyPageContract.State,
    onEvent: (MyPageContract.Event) -> Unit,
) {
    // setting content
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(415.dp)
            .testTag("bottomSection"),
        color = Color.White,
        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                text = "설정",
                style = AppTypography.header_bold_16
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 계정 설정
            SettingItem(
                icon = Icons.Outlined.Person,
                text = "계정 설정",
                onClick = {
                    Log.d(TAG, "Account setting clicked")
                    onEvent(MyPageContract.Event.OnSettingAccountClick)
                },
                testTag = "accountSettingItem"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 알림 설정
            SettingItem(
                icon = Icons.Outlined.Notifications,
                text = "알림 설정",
                onClick = {
                    Log.d(TAG, "Notification setting clicked")
                    onEvent(MyPageContract.Event.OnSettingNotificationClick)
                },
                testTag = "notificationSettingItem"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 문의 및 정보
            SettingItem(
                icon = Icons.Outlined.Info,
                text = "문의 및 정보",
                onClick = {
                    Log.d(TAG, "Info clicked")
                    onEvent(MyPageContract.Event.OnInfoClick)
                },
                testTag = "infoSettingItem"
            )
        }
    }
}

@Composable
private fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Gray.gray_300),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(24.dp)
            .clickable(onClick = onClick)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            textAlign = TextAlign.Justify,
            style = AppTypography.body_regular_14
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    MiruniTheme {
        MyPageScreen(
            state = MyPageContract.State(isEditMode = false),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenEditModePreview() {
    MiruniTheme {
        MyPageScreen(
            state = MyPageContract.State(
                isEditMode = true,
                nickName = "테스트닉네임"
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenLoadingPreview() {
    MiruniTheme {
        MyPageScreen(
            state = MyPageContract.State(
                isEditMode = true,
                isLoading = true,
                nickName = "테스트닉네임"
            ),
            onEvent = {},
        )
    }
}
