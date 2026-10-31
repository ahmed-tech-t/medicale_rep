package com.example.medicalrep.ui.screens.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.Notification
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraImage
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.notifications.main.NotificationEvents
import com.example.medicalrep.ui.screens.notifications.main.NotificationState
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate800
import java.text.DateFormat
import java.util.Date

/** Figma 622:1180 (empty) / 622:1181 (list). */
@Composable
fun NotificationsUi(
    state: NotificationState,
    onEvent: (NotificationEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppHeader(
            title = stringResource(R.string.cd_notifications),
            onBack = { onEvent(NotificationEvents.OnBack) },
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate25),
        ) {
            if (state.notifications.isEmpty()) {
                NotificationsEmpty(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        vertical = dim.screenPaddingVertical,
                        horizontal = dim.screenPaddingHorizontal
                    ),
                    verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
                ) {
                    items(state.notifications, key = { it.id }) { NotificationCard(it) }
                }
            }
        }
    }
}

@Composable
private fun NotificationsEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
    ) {

        NexoraImage(
            painter = painterResource(R.drawable.ic_notification_empty_icon),
            contentDescription = null,
            modifier = Modifier.size(dim.horizontalCardImageHeight),
        )

        NexoraText(
            text = stringResource(R.string.notifications_empty),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate800,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun NotificationCard(notification: Notification, modifier: Modifier = Modifier) {
    val time = remember(notification.createdAtMillis) {
        DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(notification.createdAtMillis))
    }

    NexoraCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = spacing.spaceMedium,
            vertical = spacing.spaceSmallPlus
        ),
    ) {
        Column {
            NexoraText(
                text = notification.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Slate800,
            )
            if (notification.message.isNotBlank()) {
                NexoraText(
                    text = notification.message,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                    color = Slate800,
                )
            }
            NexoraText(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
            )
        }

    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NotificationsEmptyPreview() {
    MedicalRepTheme { NotificationsUi(state = NotificationState(), onEvent = {}) }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NotificationsFilledPreview() {
    val now = System.currentTimeMillis()
    MedicalRepTheme {
        NotificationsUi(
            state = NotificationState(
                notifications = listOf(
                    Notification(
                        id = "1",
                        repId = "r1",
                        title = "تمت الموافقة على طلب الإجازة",
                        message = "تم اعتماد إجازتك الاعتيادية من 02/11 إلى 04/11/2026.",
                        createdAtMillis = now
                    ),
                    Notification(
                        id = "2",
                        repId = "r1",
                        title = "زيارات جديدة في خطتك",
                        message = "أضاف المشرف 3 زيارات لخطة يوم الأحد 01/11.",
                        createdAtMillis = now - 3_600_000
                    ),
                    Notification(
                        id = "3",
                        repId = "r1",
                        title = "لم يتم رفع زيارة",
                        message = "زيارة Dr. Hany لم تُرفع بسبب مشكلة في الاتصال.",
                        createdAtMillis = now - 7_200_000
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}
