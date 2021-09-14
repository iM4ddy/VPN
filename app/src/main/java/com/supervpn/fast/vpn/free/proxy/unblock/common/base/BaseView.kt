package com.supervpn.fast.vpn.free.proxy.unblock.common.base

interface BaseView {
    fun showProgressDialog(isShow: Boolean)
    fun showError(throwable: Throwable)
}