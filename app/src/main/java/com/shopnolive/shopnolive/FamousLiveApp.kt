package com.shopnolive.shopnolive

import android.app.Application
import android.content.SharedPreferences
import com.shopnolive.shopnolive.utils.Constants.AgoraConst.DEFAULT_PROFILE_IDX
import com.shopnolive.shopnolive.utils.Constants.AgoraConst.PREF_ENABLE_STATS
import com.shopnolive.shopnolive.utils.Constants.AgoraConst.PREF_MIRROR_ENCODE
import com.shopnolive.shopnolive.utils.Constants.AgoraConst.PREF_MIRROR_LOCAL
import com.shopnolive.shopnolive.utils.Constants.AgoraConst.PREF_MIRROR_REMOTE
import com.shopnolive.shopnolive.utils.Constants.AgoraConst.PREF_RESOLUTION_IDX
import com.shopnolive.shopnolive.utils.agora.FileUtil
import com.shopnolive.shopnolive.utils.agora.PrefManager
import com.shopnolive.shopnolive.utils.agora.rtc.AgoraEventHandler
import com.shopnolive.shopnolive.utils.agora.rtc.EngineConfig
import com.shopnolive.shopnolive.utils.agora.rtc.EventHandler
import com.shopnolive.shopnolive.utils.agora.stats.StatsManager
import io.agora.rtc.RtcEngine
import okhttp3.OkHttpClient

class FamousLiveApp : Application() {

    private var mRtcEngine: RtcEngine? = null
    private val mGlobalConfig: EngineConfig = EngineConfig()
    private val mHandler: AgoraEventHandler = AgoraEventHandler()
    private val mStatsManager: StatsManager = StatsManager()

    override fun onCreate() {
        super.onCreate()

        appInstance = this

        try {
            mRtcEngine =
                RtcEngine.create(applicationContext, getSharedPreferences("login", MODE_PRIVATE).getString("agoratoken",getString(R.string.private_app_id)), mHandler)
            mRtcEngine!!.setLogFile(FileUtil.initializeLogFile(this))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initConfig()
    }

    companion object {

        private lateinit var appInstance: FamousLiveApp

        var okHttpClient: OkHttpClient? = null

        fun getAppInstance(): FamousLiveApp {
            return appInstance
        }
    }

    private fun initConfig() {
        val pref: SharedPreferences = PrefManager.getPreferences(applicationContext)
        mGlobalConfig.videoDimenIndex = pref.getInt(
            PREF_RESOLUTION_IDX, DEFAULT_PROFILE_IDX
        )
        val showStats = pref.getBoolean(PREF_ENABLE_STATS, false)
        mGlobalConfig.setIfShowVideoStats(showStats)
        mStatsManager.enableStats(showStats)
        mGlobalConfig.mirrorLocalIndex = pref.getInt(PREF_MIRROR_LOCAL, 0)
        mGlobalConfig.mirrorRemoteIndex = pref.getInt(PREF_MIRROR_REMOTE, 0)
        mGlobalConfig.mirrorEncodeIndex = pref.getInt(PREF_MIRROR_ENCODE, 1)
    }

    fun engineConfig(): EngineConfig {
        return mGlobalConfig
    }

    fun rtcEngine(): RtcEngine? {
        return mRtcEngine
    }

    fun statsManager(): StatsManager {
        return mStatsManager
    }

    fun registerEventHandler(handler: EventHandler?) {
        mHandler.addHandler(handler)
    }

    fun removeEventHandler(handler: EventHandler?) {
        mHandler.removeHandler(handler)
    }

    override fun onTerminate() {
        super.onTerminate()
        RtcEngine.destroy()
    }
}