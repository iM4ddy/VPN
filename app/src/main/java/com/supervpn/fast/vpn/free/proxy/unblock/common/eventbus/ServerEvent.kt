package com.supervpn.fast.vpn.free.proxy.unblock.common.eventbus

import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.Server

data class ServerEvent private constructor(val value: Server?, val change: Boolean) {
    companion object {
        fun init(value: Server?, change: Boolean) = ServerEvent(value, change)
    }
}