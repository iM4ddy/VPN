package com.supervpn.fast.vpn.free.proxy.unblock.data.repos

import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.FirebaseConstant
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.rxGet
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.AdsConfig
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.ConfigRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Observable
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor() : ConfigRepository {
    override fun getConfig(): Observable<AdsConfig> {
        return Firebase.firestore
            .collection(FirebaseConstant.CONFIGS)
            .rxGet()
            .map {
                val ads = it.documents.firstOrNull()?.data?.get("ads") as? MutableList<*>
                val map = ads?.firstOrNull() as MutableMap<*, *>
                val google = map["google"] as MutableMap<*, *>
                return@map AdsConfig.fromFirebase(google)
            }
    }
}