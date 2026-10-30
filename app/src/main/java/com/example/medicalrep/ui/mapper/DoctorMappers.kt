package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.Doctor
import com.example.medicalrep.ui.models.DoctorUi

fun Doctor.toUi() = DoctorUi(id = id, name = name, classCategory = classCategory?.toUi())
