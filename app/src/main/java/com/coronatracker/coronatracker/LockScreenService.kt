package com.coronatracker.coronatracker

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat


/**
 * Created on 2/20/2016.
 */
class LockScreenService : Service() {
    private var mReceiver: BroadcastReceiver? = null
    private var isShowing = false
    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

    private var windowManager: WindowManager? = null
    private var textview: TextView? = null
    private var mView: View? = null
    var params: WindowManager.LayoutParams? = null
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        mView = View.inflate(baseContext, com.coronatracker.coronatracker.R.layout.lockscreen_view, null)
        mView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_VISIBLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        mView!!.visibility = View.VISIBLE
        //set parameters for the textview

        val flag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            flag,
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        params!!.gravity = Gravity.BOTTOM

        //Register receiver for determining screen off and if user is present
        mReceiver = LockScreenStateReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(mReceiver, filter)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    inner class LockScreenStateReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            if (intent.action == Intent.ACTION_SCREEN_ON) {
                //if screen is turn off show the textview
                if (!isShowing) {
                    windowManager!!.addView(mView, params)
                    isShowing = true
                }
            } else if (intent.action == Intent.ACTION_USER_PRESENT) {
                //Handle resuming events if user is present/screen is unlocked remove the textview immediately
                if (isShowing) {
                    windowManager!!.removeViewImmediate(textview)
                    isShowing = false
                }
            }
        }
    }

    override fun onDestroy() {
        //unregister receiver when the service is destroy
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }

        //remove view if it is showing and the service is destroy
        if (isShowing) {
            windowManager!!.removeViewImmediate(textview)
            isShowing = false
        }
        super.onDestroy()
    }
}