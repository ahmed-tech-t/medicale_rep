package com.example.medicalrep.ui.common.connectivity

import com.example.medicalrep.domain.models.ConnectionState
import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    /** The current state first, then every change: Online only while the network can really reach the internet. */
    val connection: Flow<ConnectionState>
}
