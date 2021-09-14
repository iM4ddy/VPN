package com.supervpn.fast.vpn.free.proxy.unblock.presentation.servers.tab

import android.os.Bundle
import android.view.View
import com.supervpn.fast.vpn.free.proxy.unblock.R
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.common.di.component.AppComponent
import com.supervpn.fast.vpn.free.proxy.unblock.common.dialog.ConfirmDialogFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.eventbus.ChangeTabEvent
import com.supervpn.fast.vpn.free.proxy.unblock.common.eventbus.ServerEvent
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.Feature
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.SharePrefs
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.getBooleanPref
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.postEvent
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.Server
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.servers.ServerListView
import de.blinkt.openvpn.core.OpenVPNService
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.android.synthetic.main.layout_loading.*
import javax.inject.Inject

class TabFragment : BaseFragment<TabView, TabPresenter>(), TabView, ServerListView {
    companion object {
        private const val EXTRA_SCREEN = "EXTRA_SCREEN"
        const val ALL_LOCATION = "ALL_LOCATION"
        const val RECOMMENDED = "RECOMMENDED"

        fun newInstance(tab: String): TabFragment {
            val bundle = Bundle()
                .apply {
                    putString(EXTRA_SCREEN, tab)
                }

            return TabFragment()
                .apply {
                    arguments = bundle
                }
        }
    }

    @Inject
    lateinit var presenter: TabPresenter

    private var tabAdapter: TabAdapter? = null

    private val screen: String by lazy {
        return@lazy arguments?.getString(EXTRA_SCREEN) ?: ALL_LOCATION
    }

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<TabView>? {
        return presenter
    }

    override fun viewIF(): TabView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_tab
    }

    override fun initView() {
        val serverDraft = Server.getDraft()
        tabAdapter = TabAdapter(serverDraft) { server ->
            when (true) {
                Feature.IS_ADMIN ->{
                    postEvent(ServerEvent.init(server, false))
                }
                !getBooleanPref(SharePrefs.KEY_PREMIUM) && server?.premium == true -> {
                    postEvent(ChangeTabEvent.premium)
                }
                server != serverDraft && OpenVPNService.getStatus() == "CONNECTED" -> {
                    context?.let {
                        ConfirmDialogFragment.changeVpn(it, childFragmentManager) {
                            postEvent(ServerEvent.init(server, true))
                        }
                    }
                }
                else -> {
                    postEvent(ServerEvent.init(server, false))
                }
            }
        }

        recyclerView
            .apply {
                adapter = tabAdapter
            }
    }

    override fun showProgressDialog(isShow: Boolean) {
        viewLoading?.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun onLoadDataSuccess(index: Int) {
        if (index != -1) {
            recyclerView.layoutManager?.scrollToPosition(index + 1)
        }
    }

    override fun onGetServersSuccess(servers: List<Server>?) {
        tabAdapter?.servers = servers?.filter {
            if (screen == RECOMMENDED) {
                return@filter it.recommend == true
            }
            return@filter true
        } ?: listOf()
    }
}
