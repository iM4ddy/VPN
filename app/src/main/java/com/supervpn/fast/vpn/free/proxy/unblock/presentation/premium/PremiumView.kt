package com.supervpn.fast.vpn.free.proxy.unblock.presentation.premium

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseView

interface PremiumView : BaseView {
    fun onAcknowledgedPurchase(index: Int, newRequest: Boolean)
    fun shouldShowAdMod(premium: Boolean)
    fun updatePrice(price1: String?, price2: String?, save: Int)
}