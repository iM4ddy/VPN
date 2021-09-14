package com.supervpn.fast.vpn.free.proxy.unblock.common.eventbus

data class ChangeTabEvent(val tabIndex: Int){
    companion object{
        val premium = ChangeTabEvent(1)
    }
}