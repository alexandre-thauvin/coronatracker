package com.coronatracker.mycoronatrackerapp

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.coronatracker.mycoronatrackerapp.network.ApiServiceImp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/* Created by *-----* Alexandre Thauvin *-----* */

class DataWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    private val disposable = CompositeDisposable()
    override fun doWork(): Result {
         disposable.add(
            ApiServiceImp.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val intent = Intent(MainActivity.ALL)
                    intent.putExtra(MainActivity.ALL, it)
                    applicationContext.sendBroadcast(intent)
                }, Throwable::printStackTrace)
        )
        disposable.add(
            ApiServiceImp.getByCountry()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val intent = Intent(MainActivity.BY_COUNTRY)
                    intent.putParcelableArrayListExtra(MainActivity.BY_COUNTRY, ArrayList(it))
                    applicationContext.sendBroadcast(intent)
                }, Throwable::printStackTrace)
        )
        return Result.success()
    }
}