package com.supervpn.fast.vpn.free.proxy.unblock.domain.repos

import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.Server
import io.reactivex.Observable

interface ServerRepository {
    fun getServers(): Observable<List<Server>>
}