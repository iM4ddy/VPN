package com.supervpn.fast.vpn.free.proxy.unblock.presentation.servers

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.observableTransformer
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.ServerRepository
import com.steve.utilities.core.extensions.addToCompositeDisposable
import javax.inject.Inject

class ServerListPresenter @Inject constructor() : BasePresenter<ServerListView>() {
    @Inject
    lateinit var serverRepository: ServerRepository

    fun getServers() {
        serverRepository
            .getServers()
            .compose(observableTransformer(view))
            .subscribe({
                view?.onGetServersSuccess(it)
            }, {
                view?.showError(it)
            })
            .addToCompositeDisposable(disposable)
    }
}