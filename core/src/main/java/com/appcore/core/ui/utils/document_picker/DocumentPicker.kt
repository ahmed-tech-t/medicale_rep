package com.appcore.core.ui.utils.document_picker

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.appcore.core.domain.utils.UiText
import com.appcore.core.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PickedDocument(
    val uri: Uri,
    val fileName: String?,
    val sizeBytes: Long,
)

val DOCUMENT_PICKER_MIME_TYPES = arrayOf("image/jpeg", "image/png", "application/pdf")

const val MAX_DOCUMENT_BYTES = 5L * 1024L * 1024L

fun documentSizeErrorOrNull(sizeBytes: Long): UiText? =
    if (sizeBytes > MAX_DOCUMENT_BYTES) {
        UiText.StringResource(R.string.document_too_large)
    } else {
        null
    }

@Composable
fun rememberDocumentPickerLauncher(
    onPicked: (PickedDocument) -> Unit,
): ManagedActivityResultLauncher<Array<String>, Uri?> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        var fileName: String? = null
        var sizeBytes = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIndex >= 0) fileName = cursor.getString(nameIndex)
                if (sizeIndex >= 0) sizeBytes = cursor.getLong(sizeIndex)
            }
        }
        onPicked(PickedDocument(uri = uri, fileName = fileName, sizeBytes = sizeBytes))
    }
}


@Composable
fun rememberCameraLauncher(
    imageUri: Uri,
    onPicked: (PickedDocument) -> Unit,
): ManagedActivityResultLauncher<Uri, Boolean> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (!success) return@rememberLauncherForActivityResult

        var fileName: String? = null
        var sizeBytes = 0L

        // Query the provided Uri for its resulting name and size
        context.contentResolver.query(imageUri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIndex >= 0) fileName = cursor.getString(nameIndex)
                if (sizeIndex >= 0) sizeBytes = cursor.getLong(sizeIndex)
            }
        }

        onPicked(PickedDocument(uri = imageUri, fileName = fileName, sizeBytes = sizeBytes))
    }
}


fun createCameraImageUri(context: Context): Uri {
    // 1. Create a unique file name using a timestamp
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    // 2. Create the temporary file in the app's internal cache directory
    val imageFile = File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        context.cacheDir /* directory */
    )

    // 3. Wrap the file in a secure content:// Uri using FileProvider
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}