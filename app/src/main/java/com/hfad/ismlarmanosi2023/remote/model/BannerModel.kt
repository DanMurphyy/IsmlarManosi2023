package com.hfad.ismlarmanosi2023.remote.model

data class BannerModel(
    var id: String = "1",
    var packageName: String = "0",
    var playMarketLink: String = "0",
    var imageLink: String = "0",
) {
    constructor() : this("1", "0", "0", "0")
}