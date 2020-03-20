package com.coronatracker.mycoronatrackerapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.coronatracker.mycoronatrackerapp.MainActivity
import com.coronatracker.mycoronatrackerapp.R
import com.coronatracker.mycoronatrackerapp.network.ApiServiceImp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Implementation of App Widget functionality.
 */
class CasesWidget : AppWidgetProvider() {
    private val disposable = CompositeDisposable()
    private lateinit var widgetListener: WidgetListener

    interface WidgetListener {
        fun updateData()
    }
    fun addListener(widgetListener: WidgetListener){
        this.widgetListener = widgetListener
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        //widgetListener.updateData()
        pullAll(context)
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.cases_widget).apply {
                setOnClickPendingIntent(R.id.clRoot, pendingIntent)
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val views = RemoteViews(context!!.packageName, R.layout.cases_widget)
        views.setTextViewText(R.id.tvNumberTotalConfirmedWidget, intent!!.getIntExtra(CONFIRMED, -1).toString())
        views.setTextViewText(R.id.tvNumberTotalConfirmedWidget, intent.getIntExtra(DEATHS, -1).toString())
        views.setTextViewText(R.id.tvNumberTotalConfirmedWidget, intent.getIntExtra(RECOVERED, -1).toString())
    }

    private fun pullAll(context: Context) {
        val views = RemoteViews(context.packageName, R.layout.cases_widget)
        disposable.add(
            ApiServiceImp.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    views.setTextViewText(R.id.tvNumberTotalConfirmedWidget, it.cases.toString())
                    views.setTextViewText(R.id.tvNumberTotalDeathsWidget, it.deaths.toString())
                    views.setTextViewText(R.id.tvNumberTotalRecoveredWidget, it.recovered.toString())
                }, Throwable::printStackTrace)
        )
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        const val CONFIRMED = "confirmed"
        const val DEATHS = "deaths"
        const val RECOVERED = "recovered"
    }
}