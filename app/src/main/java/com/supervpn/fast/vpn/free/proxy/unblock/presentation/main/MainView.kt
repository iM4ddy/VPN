package com.supervpn.fast.vpn.free.proxy.unblock.presentation.main

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseView
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.AdsConfig

interface MainView : BaseView {
    fun onGetConfigsSuccess(adsConfig: AdsConfig?)
}