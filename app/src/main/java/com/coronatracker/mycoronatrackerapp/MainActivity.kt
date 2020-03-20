package com.coronatracker.mycoronatrackerapp

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.coronatracker.mycoronatrackerapp.adapter.ConfirmedAdapter
import com.coronatracker.mycoronatrackerapp.adapter.DeathsAdapter
import com.coronatracker.mycoronatrackerapp.adapter.RecoveredAdapter
import com.coronatracker.mycoronatrackerapp.network.ApiServiceImp
import com.coronatracker.mycoronatrackerapp.widget.CasesWidget
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var disposable = CompositeDisposable()
    private val REQUEST_CODE = 10001
    private lateinit var confirmedAdapter: ConfirmedAdapter
    private lateinit var deathsAdapter: DeathsAdapter
    private lateinit var recoveredAdapter: RecoveredAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        initView()
        initListeners()
    }

    private fun initView() {
        recycler_confirmed.layoutManager = LinearLayoutManager(this)
        recycler_confirmed.itemAnimator = DefaultItemAnimator()
        confirmedAdapter = ConfirmedAdapter()
        recycler_confirmed.adapter = confirmedAdapter

        recycler_deaths.layoutManager = LinearLayoutManager(this)
        recycler_deaths.itemAnimator = DefaultItemAnimator()
        deathsAdapter = DeathsAdapter()
        recycler_deaths.adapter = deathsAdapter

        recycler_recovered.layoutManager = LinearLayoutManager(this)
        recycler_recovered.itemAnimator = DefaultItemAnimator()
        recoveredAdapter = RecoveredAdapter()
        recycler_recovered.adapter = recoveredAdapter
    }

    private fun initListeners(){
        swipeConfirmed.setOnRefreshListener {
            pullAllAndCountries()
        }
        swipeDeaths.setOnRefreshListener {
            pullAllAndCountries()
        }
        swipeRecovered.setOnRefreshListener {
            pullAllAndCountries()
        }
    }

    override fun onStart() {
        super.onStart()
        pullAllAndCountries()
    }

    private fun renderRefreshing(){
        swipeConfirmed.isRefreshing = false
        swipeDeaths.isRefreshing = false
        swipeRecovered.isRefreshing = false
    }

    private fun pullAllAndCountries() {
        disposable.add(
            ApiServiceImp.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    renderRefreshing()
                    tvNumberTotalConfirmed.text = it.cases.toString()
                    tvNumberTotalDeaths.text = it.deaths.toString()
                    tvNumberTotalRecovered.text = it.recovered.toString()
                }, Throwable::printStackTrace)
        )
        disposable.add(ApiServiceImp.getByCountry()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                renderRefreshing()
                confirmedAdapter.update(it)
                deathsAdapter.update(it)
                recoveredAdapter.update(it)
            }, Throwable::printStackTrace)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
