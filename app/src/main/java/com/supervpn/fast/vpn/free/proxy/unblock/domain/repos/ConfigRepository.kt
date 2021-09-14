package com.supervpn.fast.vpn.free.proxy.unblock.domain.repos

import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.AdsConfig
import io.reactivex.Observable

interface ConfigRepository {
    fun getConfig(): Observable<AdsConfig>
}