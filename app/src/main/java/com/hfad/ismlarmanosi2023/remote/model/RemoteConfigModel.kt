package com.hfad.ismlarmanosi2023.remote.model

data class RemoteConfigModel(
    var id: String = "1",
    var versionName: String = "0",
) {
    constructor() : this("1", "0")
}