package com.supervpn.fast.vpn.free.proxy.unblock.presentation.servers

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseView
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.Server

interface ServerListView : BaseView {
    fun onGetServersSuccess(servers: List<Server>?)
}