package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.DraftVisitDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.mapper.voiceNotePath
import com.example.medicalrep.data.local.media.MediaFileStorage
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.repo.DraftVisitRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val VOICE_FILE = "draft_voice.m4a"

class DraftVisitRepoImpl @Inject constructor(
    private val dao: DraftVisitDao,
    private val media: MediaFileStorage,
) : DraftVisitRepo {

    override fun draft(): Flow<DraftVisit?> = dao.get().map { it?.toDomain(media::read) }

    override suspend fun save(draft: DraftVisit) {
        val voicePath = draft.visit.voiceNote?.let { media.save(VOICE_FILE, it) }
        if (voicePath == null) dao.getOnce()?.voiceNotePath()?.let(media::delete)
        dao.upsert(draft.toEntity(voicePath))
    }

    override suspend fun discard() {
        dao.getOnce()?.voiceNotePath()?.let(media::delete)
        dao.clear()
    }
}
