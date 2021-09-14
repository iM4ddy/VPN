package com.supervpn.fast.vpn.free.proxy.unblock.data.repos

import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.FirebaseConstant
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.rxGet
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.Server
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.ServerRepository
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Observable
import javax.inject.Inject

class ServerRepositoryImpl @Inject constructor() : ServerRepository {
    override fun getServers(): Observable<List<Server>> {
        return Firebase.firestore
            .collection(FirebaseConstant.SERVERS)
            .whereEqualTo("status", true)
            .orderBy("premium", Query.Direction.ASCENDING)
            .rxGet()
            .map { documents ->
                val servers = mutableListOf<Server>()
                documents.mapTo(servers) {
                    return@mapTo Server.fromFirebase(it)
                }
                return@map servers
            }

    }
}