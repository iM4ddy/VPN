package com.supervpn.fast.vpn.free.proxy.unblock.presentation.home

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.supervpn.fast.vpn.free.proxy.unblock.R
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.common.di.component.AppComponent
import com.supervpn.fast.vpn.free.proxy.unblock.common.dialog.ConfirmDialogFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.eventbus.ActionEvent
import com.supervpn.fast.vpn.free.proxy.unblock.common.eventbus.ServerEvent
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.*
import com.supervpn.fast.vpn.free.proxy.unblock.common.widget.bottomnav.BottomNavBar
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.Server
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.main.MainActivity
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.main.MainFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.servers.ServerListFragment
import com.airbnb.lottie.LottieDrawable
import de.blinkt.openvpn.DisconnectVPNActivity
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import de.blinkt.openvpn.eventbus.VpnErrorEvent
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_traffic.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : BaseFragment<HomeView, HomePresenter>(), HomeView, MainFragment.OnTabChanged {
    companion object {
        private const val STATE_IDLE = 0
        private const val STATE_CONNECTING = 1
        private const val STATE_CONNECTED = 2
        private const val RQ_START_VPN_PROFILE = 99
    }

    @Inject
    lateinit var presenter: HomePresenter
    private var animator: ValueAnimator? = null
    private var state: Int = STATE_IDLE
    private val handle = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        val useContext = context

        if (getBooleanPref(SharePrefs.kEY_RATING_APP) || useContext == null) {
            return@Runnable
        }

        ConfirmDialogFragment.showRatingApp(useContext, childFragmentManager,
            onPositiveClickListener = {
                putBooleanPref(SharePrefs.kEY_RATING_APP, true)
                useContext.rateApp()
            },
            onOtherClickListener = {
                putBooleanPref(SharePrefs.kEY_RATING_APP, true)
            })
    }

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        initBtnConnect(presenter.isConnected)
        this.registerEventBus()
    }

    override fun onPause() {
        super.onPause()
        this.unregisterEventBus()
    }

    override fun presenter(): BasePresenter<HomeView>? {
        return presenter
    }

    override fun viewIF(): HomeView? {
        return this
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_home
    }

    override fun initView() {
        btnServers.setOnClickListener { openServerList() }
        btnConnect.setOnClickListener {
            when (true) {
                state == STATE_CONNECTING -> {
                    //do nothing
                }
                presenter.isConnected,
                state == STATE_CONNECTED -> {
                    DisconnectVPNActivity.start(context)
                }
                !hasInternet -> showNetworkError()
                else -> prepareStartVpn()
            }
        }
    }

    override fun initData() {
        val serverDraf = Server.getDraft()
        updateServerButton(serverDraf)
        onUpdateConnectionStatus()
        initChart()
        initBtnConnect(presenter.isConnected)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            RQ_START_VPN_PROFILE -> startVpn(500L)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handle.removeCallbacks(runnable)
    }

    fun removeServerListFragmentIfNeeded() {
        childFragmentManager.findFragmentByTag(ServerListFragment::class.java.name)
            ?.let {
                childFragmentManager
                    .beginTransaction()
                    .remove(it)
                    .commit()
            }
    }

    /**
     * MainFragment.OnTabChanged
     */
    override fun onChange(tabIndex: Int) {
        if (tabIndex != BottomNavBar.TAB_HOME) {
            return
        }

        childFragmentManager.findFragmentByTag(ServerListFragment::class.java.name)
            ?.let {
                activity?.updateColorStatusBar(R.color.colorNavBottomBackground)
            }
            ?: run {
                activity?.updateColorStatusBar(R.color.colorPrimary)
            }
    }

    //region #Private method

    private fun initChart() {
        chartUpload.init(intArrayOf(5, 30, 100, 65, 80))
        chartDownload.init(intArrayOf(5, 30, 65, 50, 100))
    }

    private fun initBtnConnect(connected: Boolean) {
        if (connected) {
            tvState.text = getString(R.string.disconnect)
            viewProgress.visibility = View.VISIBLE
            animator?.cancel()
            viewProgress.layoutParams = (viewProgress.layoutParams as FrameLayout.LayoutParams).apply {
                width = FrameLayout.LayoutParams.MATCH_PARENT
            }
            updateLottie(STATE_CONNECTED)
            tvUpload.text = OpenVPNService.getUpload()
            tvDownload.text = OpenVPNService.getDownload()

        } else {
            handle.removeCallbacks(runnable)
            if (state == STATE_CONNECTING) {
                return
            }
            tvState.text = getString(R.string.connect)
            viewProgress.visibility = View.INVISIBLE
            updateLottie(STATE_IDLE)
            presenter.syncDataIfNeed()
        }
    }

    private fun prepareStartVpn() {
        VpnService.prepare(context)
            ?.let {
                startActivityForResult(it, RQ_START_VPN_PROFILE)
            }
            ?: run {
                startVpn()
            }
    }

    private fun startVpn(startDelay: Long = 0L) {
        val serverDraft = Server.getDraft() ?: run {
            openServerList()
            return
        }

        val result = presenter.startVpn(serverDraft)

        if (result) {
            lottieLogo.playAnimation()
            fakeProgress(startDelay)
        } else {
            context?.showToast(getString(R.string.something_wrong))
        }
    }

    private fun stopVpn() {
        OpenVPNThread.stop()
        initBtnConnect(false)
    }

    private fun fakeProgress(startDelay: Long = 0L) {
        updateLottie(STATE_CONNECTING)
        viewProgress.visibility = View.VISIBLE
        val layoutParam: FrameLayout.LayoutParams =
            viewProgress.layoutParams as FrameLayout.LayoutParams
        val originWidth = viewProgress.width
        animator = ValueAnimator.ofFloat(0f, 100f)
            .apply {
                duration = 5000L
                interpolator = DecelerateInterpolator()
                setStartDelay(startDelay)
                addUpdateListener {
                    val value = it.animatedValue as Float
                    val process = originWidth.times(value) / 100
                    layoutParam.width = process.toInt()
                    tvState.text = getString(R.string.connecting, "${value.toInt()}%")
                    viewProgress.layoutParams = layoutParam
                    if (viewProgress.visibility == View.INVISIBLE) {
                        viewProgress.visibility = View.VISIBLE
                    }
                }

                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        if (!presenter.isConnected) {
                            tvState.text = getString(R.string.waiting)
                        }
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }

                })
            }
        animator?.start()
    }

    private fun updateLottie(state: Int) {
        this.state = state
        when (state) {
            STATE_IDLE,/*-> lottieLogo.setAnimation(R.raw.ic_logo_connect)*/
            STATE_CONNECTING -> lottieLogo.apply {
                setAnimation(R.raw.ic_logo_connect)
                repeatMode = LottieDrawable.RESTART
                playAnimation()
            }
            STATE_CONNECTED -> lottieLogo.apply {
                setAnimation(R.raw.ic_logo_connected)
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }
        }
    }

    private fun openServerList() {
        val fragment = ServerListFragment()
        childFragmentManager
            .beginTransaction()
            .add(R.id.container, fragment, ServerListFragment::class.java.name)
            .commit()
    }

    private fun updateServerButton(value: Server? = null) {
        btnServers.apply {
            setFlag(Util.getResId(value?.countryCode) ?: R.drawable.ic_globe)
            setTitle(value?.country ?: getString(R.string.select_the_fastest_server))
            setDescription(value?.state)
            value?.saveDraf()
        }
    }

    //endregion

    /**
     * EventBus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVpnInfoEvent(event: ServerEvent) {
        if (event.change) {
            stopVpn()
        }
        removeServerListFragmentIfNeeded()
        updateServerButton(event.value)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onActionEvent(event: ActionEvent) {
        when (event.action) {
            Action.ACTION_REMOVE_SERVER_LIST_FRAGMENT -> removeServerListFragmentIfNeeded()
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onVpnErrorEvent(event: VpnErrorEvent) {
        if (Feature.FEATURE_SYNC_ERROR) {
            Timber.i(event.message)
            presenter.syncError(event.message)
        }
    }

    //---------------------------HomeView-----------------------
    override fun onConnected() {
        handle.postDelayed(runnable, 1000)
        (activity as? MainActivity)?.showAdIfVpnConnected()
        context.showToast(getString(R.string.connected))
        initBtnConnect(true)
    }

    override fun onDisconnected() {
        animator?.cancel()
        updateLottie(STATE_IDLE)
        initBtnConnect(false)
    }

    override fun onUpdateConnectionStatus(upload: String?, download: String?) {
        tvUpload.text = upload ?: "0 B"
        tvDownload.text = download ?: "0 B"
    }
}
