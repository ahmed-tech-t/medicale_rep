package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.Doctor
import com.example.medicalrep.domain.models.SyncStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** A doctor round-trips through its Room row; blank class or specialty text means "not set". */
class DoctorMapperTest {

    @Test
    fun `a doctor survives entity and back`() {
        val doctor = Doctor("d1", "b1", "Dr. Yasmin", BasicModel("B", "B"), BasicModel("Cardio", "Cardio"), SyncStatus.Synced)
        assertEquals(doctor, doctor.toEntity().toDomain())
    }

    @Test
    fun `no class or specialty is stored blank and read back as null`() {
        val entity = Doctor("d2", name = "Dr. Tarek").toEntity()
        assertEquals("", entity.classCategory)
        assertEquals("", entity.specialty)
        val back = entity.toDomain()
        assertNull(back.classCategory)
        assertNull(back.specialty)
    }
}
