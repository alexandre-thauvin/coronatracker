package com.coronatracker.coronatracker

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


class OverlayService : Service() {
    var mWindowManager: WindowManager? = null
    var mView: View? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        registerOverlayReceiver()
        val mWindowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val mView: View = View.inflate(baseContext, R.layout.lockscreen_view, null)
        val mLayoutParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )
        mView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_VISIBLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        mView.visibility = View.VISIBLE
        mWindowManager.addView(mView, mLayoutParams)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showView() {
        val mWindowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val mView: View = View.inflate(baseContext, R.layout.lockscreen_view, null)
        val mLayoutParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )
        mView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_VISIBLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        mView.visibility = View.VISIBLE
        mWindowManager.addView(mView, mLayoutParams)
        /* val mWindowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val mView: View = View.inflate(baseContext, R.layout.lockscreen_view, null)
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val param = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        val mLayoutParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )
        mView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_VISIBLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        mView.visibility = View.VISIBLE
        mWindowManager.addView(mView, param)*/
    }

   /* private fun hideDialog() {
        if (mView != null && mWindowManager != null) {
            mWindowManager!!.removeView(mView)
            mView = null
        }
    }*/

    override fun onDestroy() {
        unregisterOverlayReceiver()
        super.onDestroy()
    }

    private fun registerOverlayReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(overlayReceiver, filter)
    }

    private fun unregisterOverlayReceiver() {
        //hideDialog()
        unregisterReceiver(overlayReceiver)
    }

    private val overlayReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val action = intent.action
            Log.d(TAG, "[onReceive]$action")
            if (action == Intent.ACTION_SCREEN_ON) {
                showView()
            } else if (action == Intent.ACTION_USER_PRESENT) {
                //hideDialog()
            } else if (action == Intent.ACTION_SCREEN_OFF) {
                //hideDialog()
            }
        }
    }

    companion object {
        private val TAG = OverlayService::class.java.simpleName
    }
}