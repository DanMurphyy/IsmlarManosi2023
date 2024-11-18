package com.hfad.ismlarmanosi2023.remote.data

import com.hfad.ismlarmanosi2023.remote.Resource
import com.hfad.ismlarmanosi2023.remote.model.RemoteConfigModel
import kotlinx.coroutines.flow.Flow

interface RemoteConfigRepository {
    suspend fun getRemoteConfig(id: String): Flow<Resource<RemoteConfigModel>>
}