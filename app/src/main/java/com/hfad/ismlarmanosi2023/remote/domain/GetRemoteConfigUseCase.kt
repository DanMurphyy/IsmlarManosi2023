package com.hfad.ismlarmanosi2023.remote.domain

import com.hfad.ismlarmanosi2023.remote.Resource
import com.hfad.ismlarmanosi2023.remote.data.RemoteConfigRepository
import com.hfad.ismlarmanosi2023.remote.model.RemoteConfigModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRemoteConfigUseCase @Inject constructor(private val remoteConfigRepository: RemoteConfigRepository) {
    suspend operator fun invoke(id: String): Flow<Resource<RemoteConfigModel>> {
        return remoteConfigRepository.getRemoteConfig(id)
    }
}