package com.supervpn.fast.vpn.free.proxy.unblock.presentation.servers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.supervpn.fast.vpn.free.proxy.unblock.R
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.common.di.component.AppComponent
import com.supervpn.fast.vpn.free.proxy.unblock.common.dialog.ConfirmDialogFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.eventbus.ActionEvent
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.postEvent
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.setLetterSpacing
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.updateColorStatusBar
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.Server
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.servers.tab.TabFragment
import kotlinx.android.synthetic.main.fragment_server_list.*
import javax.inject.Inject

class ServerListFragment : BaseFragment<ServerListView, ServerListPresenter>(), ServerListView {

    @Inject
    lateinit var presenter: ServerListPresenter

    private val tabs = mutableListOf<TabFragment>()

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<ServerListView>? {
        return presenter
    }

    override fun viewIF(): ServerListView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_server_list
    }

    override fun initView() {
        btnLeft.setOnClickListener { postEvent(ActionEvent.removeSearchListFragment) }
        tabs.add(TabFragment.newInstance(TabFragment.ALL_LOCATION))
        tabs.add(TabFragment.newInstance(TabFragment.RECOMMENDED))

        viewPager.apply {
            adapter = ViewPagerAdapter(childFragmentManager)
        }
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setLetterSpacing(0.12f)
    }

    override fun initData() {
        presenter.getServers()
    }

    override fun onResume() {
        super.onResume()
        activity?.updateColorStatusBar(R.color.colorNavBottomBackground)
    }

    override fun onPause() {
        super.onPause()
        activity?.updateColorStatusBar(R.color.colorPrimary)
    }

    override fun showError(throwable: Throwable) {
        context?.let {
            ConfirmDialogFragment.showMessage(
                context = it,
                fragmentManager = childFragmentManager,
                onPositiveClickListener = {
                    postEvent(ActionEvent.removeSearchListFragment)
                },
                message = throwable.localizedMessage ?: "Server error..."
            )
        }
    }

    inner class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return tabs[position]
        }

        override fun getCount(): Int {
            return tabs.count()
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) context?.getString(R.string.tab_all_location) else context?.getString(R.string.tab_recommended)
        }
    }

    override fun showProgressDialog(isShow: Boolean) {
        tabs.forEach { it.showProgressDialog(isShow) }
    }

    override fun onGetServersSuccess(servers: List<Server>?) {
        tabs.forEach { it.onGetServersSuccess(servers) }
    }
}
