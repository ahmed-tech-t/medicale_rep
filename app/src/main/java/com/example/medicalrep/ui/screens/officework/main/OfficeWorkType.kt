package com.example.medicalrep.ui.screens.officework.main

import androidx.annotation.StringRes
import com.example.medicalrep.R

sealed class OfficeWorkType(@StringRes val labelRes: Int) {
    data object MeetingSupervisor : OfficeWorkType(R.string.office_work_type_meeting_supervisor)
    data object Training : OfficeWorkType(R.string.office_work_type_training)
    data object Reports : OfficeWorkType(R.string.office_work_type_reports)
    data object SamplesGifts : OfficeWorkType(R.string.office_work_type_samples_gifts)
    data object Vacation : OfficeWorkType(R.string.office_work_type_vacation)

    companion object {
        val all = listOf(MeetingSupervisor, Training, Reports, SamplesGifts, Vacation)
    }
}
