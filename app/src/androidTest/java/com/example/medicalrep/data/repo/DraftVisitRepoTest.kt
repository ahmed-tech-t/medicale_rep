package com.example.medicalrep.data.repo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.models.Visit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

/** Save then read back; saving again replaces it; the voice note goes to a file and is removed on discard. */
@RunWith(AndroidJUnit4::class)
class DraftVisitRepoTest : RepoTestBase() {
    private val repo get() = DraftVisitRepoImpl(db.draftVisitDao(), media)

    @Test fun noDraftAtFirst() = runTest { assertNull(repo.draft().first()) }

    @Test fun saveThenReadAndReplace() = runTest {
        repo.save(DraftVisit(1, Visit(city = "Cairo")))
        assertEquals("Cairo", repo.draft().first()?.visit?.city)
        repo.save(DraftVisit(2, Visit(city = "Giza")))
        assertEquals(2, repo.draft().first()?.step)
        assertEquals("Giza", repo.draft().first()?.visit?.city)
    }

    @Test fun voiceNoteIsKeptInAFileAndDiscardRemovesEverything() = runTest {
        repo.save(DraftVisit(0, Visit(voiceNote = byteArrayOf(7, 8))))
        val draft = repo.draft().first()!!
        assertArrayEquals(byteArrayOf(7, 8), draft.visit.voiceNote)
        val path = draft.visit.voiceNotePath!!
        repo.discard()
        assertNull(repo.draft().first())
        assertNull(media.read(path))
    }
}
