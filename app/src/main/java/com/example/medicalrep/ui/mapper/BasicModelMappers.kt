package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.ui.models.BasicModelUi

fun BasicModel.toUi() = BasicModelUi(id = id, name = name)

fun BasicModelUi.toDomain() = BasicModel(id = id, name = name)
