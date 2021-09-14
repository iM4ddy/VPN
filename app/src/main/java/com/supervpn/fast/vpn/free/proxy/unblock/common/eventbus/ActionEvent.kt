package com.supervpn.fast.vpn.free.proxy.unblock.common.eventbus

import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.Action.ACTION_REMOVE_SERVER_LIST_FRAGMENT
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.Action.ACTION_SHOULD_SHOW_BANNER

class ActionEvent(val action: String) {
    companion object {
        val removeSearchListFragment = ActionEvent(ACTION_REMOVE_SERVER_LIST_FRAGMENT)
        val shouldShowBanner = ActionEvent(ACTION_SHOULD_SHOW_BANNER)
    }
}