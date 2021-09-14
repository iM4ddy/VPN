package com.supervpn.fast.vpn.free.proxy.unblock.domain.model

import com.supervpn.fast.vpn.free.proxy.unblock.BuildConfig

data class AdsConfig(val appId: String?, val banner: String?, val show: String?) {
    companion object {
        fun fromFirebase(google: MutableMap<*, *>): AdsConfig {
            val appId = google["appId"] as? String
            var banner = google["banner"] as? String
            var show = google["show"] as? String

            if(BuildConfig.DEBUG){
                banner = "ca-app-pub-3940256099942544/6300978111"
                show = "ca-app-pub-3940256099942544/1033173712"
            }
            return AdsConfig(appId, banner, show)
        }
    }
}