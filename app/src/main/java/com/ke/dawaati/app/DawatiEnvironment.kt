package com.ke.dawaati.app

sealed class DawatiEnvironment(val url: String) {
    object Production : DawatiEnvironment("https://api.dawati.co.ke/")
    object Development : DawatiEnvironment("https://api.dawati.co.ke/")
}
