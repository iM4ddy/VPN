package com.supervpn.fast.vpn.free.proxy.unblock.presentation.home

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseView

interface HomeView : BaseView {
    fun onConnected()
    fun onDisconnected()
    fun onUpdateConnectionStatus(upload: String? = null, download: String? = null)
}