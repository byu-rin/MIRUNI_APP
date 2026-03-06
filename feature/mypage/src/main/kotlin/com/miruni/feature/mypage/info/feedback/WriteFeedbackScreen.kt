package com.miruni.feature.mypage.info.feedback

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MainColor
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.navigation.MyPageRoute
import com.miruni.feature.mypage.R
import com.miruni.feature.mypage.component.MyPageBottomBar
import com.miruni.feature.mypage.component.MyPageTopBar
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "WriteFeedbackScreen"

private const val MAX_PHOTO_COUNT = 10

@Composable
fun WriteFeedbackScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FeedbackContract.Effect.Navigation.NavigateToSubmitFeedback -> {
                    navController.navigate(MyPageRoute.MyPageSubmitFeedback.route)
                }
                is FeedbackContract.Effect.Navigation.NavigateBack -> {
                    navController.popBackStack()
                }
                is FeedbackContract.Effect.Navigation.NavigateToInformation -> {
                    navController.navigate(MyPageRoute.MyPageInfo.route) {
                        popUpTo(MyPageRoute.MyPageInfo.route) { inclusive = true }
                    }
                }
                is FeedbackContract.Effect.Message.Toast -> {
                    // Handle toast
                }
                is FeedbackContract.Effect.Message.Error -> {
                    // Handle error
                }
            }
        }
    }

    WriteFeedbackContent(
        state = state,
        onTitleChange = { viewModel.setEvent(FeedbackContract.Event.OnTitleChange(it)) },
        onContentChange = { viewModel.setEvent(FeedbackContract.Event.OnContentChange(it)) },
        onPrivacyConsentChange = { viewModel.setEvent(FeedbackContract.Event.OnPrivacyConsentChange(it)) },
        onPhotosSelected = { viewModel.setEvent(FeedbackContract.Event.OnPhotosSelected(it)) },
        onSubmitClick = { viewModel.setEvent(FeedbackContract.Event.OnSubmitClick) },
        onBackClick = { viewModel.setEvent(FeedbackContract.Event.OnBackClick) },
        modifier = modifier
    )
}

@Composable
private fun WriteFeedbackContent(
    state: FeedbackContract.State,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onPrivacyConsentChange: (Boolean) -> Unit,
    onPhotosSelected: (List<Uri>) -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(MAX_PHOTO_COUNT)
    ) { uris ->
        if (uris.isNotEmpty()) {
            val photos = uris.take(MAX_PHOTO_COUNT)
            Log.d(TAG, "Selected ${photos.size} photos")
            onPhotosSelected(photos)
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Permission granted, launching photo picker")
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            Log.d(TAG, "Permission denied")
            Toast.makeText(
                context,
                "앨범에 접근하려면 권한 허용이 필요합니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Function to handle camera/album button click
    val onAlbumClick: () -> Unit = {
        Log.d(TAG, "Album button clicked")

        // Check if photo picker is available (Android 11+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses photo picker without permission
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11-12 uses photo picker without permission
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            // Android 10 and below requires READ_EXTERNAL_STORAGE permission
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            when {
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Permission already granted")
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                else -> {
                    Log.d(TAG, "Requesting permission")
                    permissionLauncher.launch(permission)
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF6F5F6),
        topBar = {
            MyPageTopBar(
                text = "문의 및 피드백",
                onBackClick = {
                    Log.d(TAG, "Back button clicked")
                    onBackClick()
                }
            )
        },
        bottomBar = {
            MyPageBottomBar(
                canConfirm = state.isSubmitEnabled,
                containerColor = if (state.isSubmitEnabled) MainColor.miruni_green else Gray.gray_500,
                onConfirmClick = onSubmitClick
            )
        }

    ) { innerPadding ->
        Column {
            Card(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    Color.White
                )
            ) {
                Column(
                    modifier = modifier
                        .padding(20.dp)
                ) {
                    Text(
                        text = "제목",
                        style = AppTypography.sub_medium_14,
                        color = Gray.gray_700
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.title,
                        onValueChange = onTitleChange,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Gray.gray_300,
                            unfocusedBorderColor = Gray.gray_400
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("titleTextField")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "내용",
                        style = AppTypography.sub_medium_14,
                        color = Gray.gray_700
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.content,
                        onValueChange = onContentChange,
                        modifier = modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("contentTextField"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Gray.gray_300,
                            unfocusedBorderColor = Gray.gray_400
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        onClick = onAlbumClick,
                        modifier = modifier
                            .size(56.dp)
                            .testTag("cameraButton"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Gray.gray_300
                        ),
                        border = BorderStroke(1.dp, Gray.gray_400)
                    ) {
                        Column(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_camera_24),
                                contentDescription = "camera",
                                modifier = modifier.size(20.dp),
                            )
                            Text(
                                text = "${state.selectedPhotos.size}/$MAX_PHOTO_COUNT",
                                style = AppTypography.description_regular_9
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Make the entire row clickable and handle the state change
                            .clickable(
                                role = Role.Checkbox,
                                onClick = { onPrivacyConsentChange(!state.isPrivacyConsentChecked) }
                            )
                            .testTag("privacyCheckbox"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.isPrivacyConsentChecked,
                            onCheckedChange = null // Set to null as the parent Row handles clicks
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "개인정보 수집 이용 동의 (필수)",
                            style = AppTypography.body_regular_12
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WriteFeedbackScreenPreview() {
    MiruniTheme {
        WriteFeedbackContent(
            state = FeedbackContract.State(),
            onTitleChange = {},
            onContentChange = {},
            onPrivacyConsentChange = {},
            onPhotosSelected = {},
            onSubmitClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WriteFeedbackScreenFilledPreview() {
    MiruniTheme {
        WriteFeedbackContent(
            state = FeedbackContract.State(
                title = "제목 입력됨",
                content = "내용 입력됨",
                isPrivacyConsentChecked = true
            ),
            onTitleChange = {},
            onContentChange = {},
            onPrivacyConsentChange = {},
            onPhotosSelected = {},
            onSubmitClick = {},
            onBackClick = {}
        )
    }
}
