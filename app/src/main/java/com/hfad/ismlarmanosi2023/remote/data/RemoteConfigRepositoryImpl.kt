package com.hfad.ismlarmanosi2023.remote.data

import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.ismlarmanosi2023.remote.Constants
import com.hfad.ismlarmanosi2023.remote.Resource
import com.hfad.ismlarmanosi2023.remote.model.RemoteConfigModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteConfigRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : RemoteConfigRepository {
    override suspend fun getRemoteConfig(id: String): Flow<Resource<RemoteConfigModel>> = flow {

        val document = firestore.collection(Constants.REMOTE).document(id).get().await()

        if (document.exists()) {
            val remoteConfig = document.toObject(RemoteConfigModel::class.java)!!
            emit(Resource.Success(remoteConfig))

        } else {
            emit(Resource.Error("No Remote config"))
        }
    }.flowOn(Dispatchers.IO).catch { e ->
        emit(Resource.Error(e.message ?: "Unknown error occurred"))
    }
}