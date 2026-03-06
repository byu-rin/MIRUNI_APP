package com.miruni.feature.calendar.presentation.components

import android.graphics.Rect
import android.util.Log
import android.view.ViewTreeObserver
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Black
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MainColor
import com.miruni.core.designsystem.White
import com.miruni.feature.calendar.common.MiruniButton
import com.miruni.feature.calendar.common.MiruniTextField
import com.miruni.feature.calendar.domain.model.PlanPriority

@Composable
fun isKeyboardVisible(): Boolean {
    val view = LocalView.current
    var isKeyboardVisible by remember { mutableStateOf(false) }

    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            isKeyboardVisible = keypadHeight > screenHeight * 0.15
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    return isKeyboardVisible
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBottomSheet(
    title: String,
    description: String,
    date: String,
    priority: PlanPriority,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClickPlanDetail: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteDialog by remember { mutableStateOf(false) }
    // 3dot 메뉴 상태
    var showMenu by remember { mutableStateOf(false) }

    var editedDescription by remember(description) { mutableStateOf(description) }
    val descriptionFocusRequester = remember { FocusRequester() }
    val isKeyboardVisible = isKeyboardVisible()

    Log.d("Refresh/Get Plan", "11. ScheduleBottomSheet Title: $title, Description: $description")

    if (showDeleteDialog) {
        DeleteScheduleDialog(
            onConfirm = {
                onDelete()
                showDeleteDialog = false
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 30.dp)
                    .imePadding()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = AppTypography.sub_bold_14,
                            color = Black
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        PriorityBadge(priority = priority)
                    }
                    //3dot
                    Box(
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "더보기",
                                tint = Gray.gray_700
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            containerColor = White
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "전체보기",
                                        modifier = Modifier.fillMaxWidth(),
                                        style = AppTypography.body_regular_14,
                                        textAlign = TextAlign.Center
                                    )
                                },
                                onClick = {
                                    onClickPlanDetail()
                                    showMenu = false
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(1.dp)
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "수정하기",
                                        modifier = Modifier.fillMaxWidth(),
                                        style = AppTypography.body_regular_14,
                                        textAlign = TextAlign.Center
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(1.dp)
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "삭제하기",
                                        modifier = Modifier.fillMaxWidth(),
                                        style = AppTypography.body_regular_14,
                                        textAlign = TextAlign.Center
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 설명
                MiruniTextField.InputText(
                    value = editedDescription,
                    onValueChange = {editedDescription = it},
                    enabled = false,
                    focusRequester = descriptionFocusRequester,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 날짜
                MiruniTextField.InputText(
                    value = date,
                    onValueChange = {},
                    enabled = false,
                    textColor = Gray.gray_500,
                    focusRequester = descriptionFocusRequester,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                                tint = Gray.gray_500,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DeleteScheduleDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        title = null,
        shape = RoundedCornerShape(20.dp),
        text = {
            Text(
                text = "해당 일정을 삭제하시겠어요?",
                style = AppTypography.body_regular_14,
                color = Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 취소 버튼
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Gray.gray_400)
                ) {
                    Text(
                        text = "취소",
                        style = AppTypography.sub_medium_14,
                        color = Gray.gray_700
                    )
                }

                // 확인 버튼
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor.miruni_green
                    )
                ) {
                    Text(
                        text = "확인",
                        style = AppTypography.sub_medium_14,
                        color = White
                    )
                }
            }
        }
    )
}

@Composable
@Preview
fun ScheduleBottomSheetPreview() {
    ScheduleBottomSheet(
        title = "타이틀",
        description = "설명",
        date = "~~~~",
        priority = PlanPriority.MEDIUM,
        onDismiss = {},
        onEdit = {},
        onDelete = {},
        onClickPlanDetail = {}
    )
}