package com.example.medicalrep.data.repo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.medicalrep.data.local.dao.DaoTestBase
import com.example.medicalrep.data.local.media.MediaFileStorage
import org.junit.After
import java.io.File

/** An in-memory database plus a media folder that is removed after each test. */
abstract class RepoTestBase : DaoTestBase() {
    private val mediaDir = File(ApplicationProvider.getApplicationContext<Context>().cacheDir, "repo-test-media")
    protected val media = MediaFileStorage(mediaDir)

    @After
    fun deleteMedia() {
        mediaDir.deleteRecursively()
    }
}
