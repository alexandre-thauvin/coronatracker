package com.coronatracker.mycoronatrackerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.coronatracker.mycoronatrackerapp.adapter.ConfirmedAdapter
import com.coronatracker.mycoronatrackerapp.adapter.DeathsAdapter
import com.coronatracker.mycoronatrackerapp.adapter.RecoveredAdapter
import com.coronatracker.mycoronatrackerapp.model.Data
import com.coronatracker.mycoronatrackerapp.network.ApiServiceImp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private var disposable = CompositeDisposable()
    private lateinit var confirmedAdapter: ConfirmedAdapter
    private lateinit var deathsAdapter: DeathsAdapter
    private lateinit var recoveredAdapter: RecoveredAdapter
    private var isDataReady = false
    private lateinit var countryDetailDialog: CountryDetailDialog
    private var dataUpdateReceiver: DataUpdateReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
        else {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        countryDetailDialog = CountryDetailDialog()
        scheduleJob()
        initListeners()
    }

    private fun scheduleJob(){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<DataWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork("fetch_data", ExistingPeriodicWorkPolicy.REPLACE, work)
    }

    private fun initView() {
        recycler_confirmed.layoutManager = LinearLayoutManager(this)
        recycler_confirmed.itemAnimator = DefaultItemAnimator()
        confirmedAdapter = ConfirmedAdapter(this::showCountryDetailPopup)
        recycler_confirmed.adapter = confirmedAdapter

        recycler_deaths.layoutManager = LinearLayoutManager(this)
        recycler_deaths.itemAnimator = DefaultItemAnimator()
        deathsAdapter = DeathsAdapter(this::showCountryDetailPopup)
        recycler_deaths.adapter = deathsAdapter

        recycler_recovered.layoutManager = LinearLayoutManager(this)
        recycler_recovered.itemAnimator = DefaultItemAnimator()
        recoveredAdapter = RecoveredAdapter(this::showCountryDetailPopup)
        recycler_recovered.adapter = recoveredAdapter

        val mSwipeRefreshLayoutConfirmed =
            findViewById<View>(R.id.swipeConfirmed) as SwipeRefreshLayout
        val mSwipeRefreshLayoutDeaths =
            findViewById<View>(R.id.swipeDeaths) as SwipeRefreshLayout
        val mSwipeRefreshLayoutRecovered =
            findViewById<View>(R.id.swipeRecovered) as SwipeRefreshLayout
        try {
            val fConfirmed: Field = mSwipeRefreshLayoutConfirmed::class.java.getDeclaredField("mCircleView")
            fConfirmed.setAccessible(true)
            val imgConfirmed: ImageView = fConfirmed.get(mSwipeRefreshLayoutConfirmed) as ImageView
            imgConfirmed.setImageResource(R.drawable.ic_virus)

            val fDeaths: Field = mSwipeRefreshLayoutConfirmed::class.java.getDeclaredField("mCircleView")
            fDeaths.setAccessible(true)
            val imgDeaths: ImageView = fDeaths.get(mSwipeRefreshLayoutDeaths) as ImageView
            imgDeaths.setImageResource(R.drawable.ic_virus)

            val fRecovered: Field = mSwipeRefreshLayoutConfirmed::class.java.getDeclaredField("mCircleView")
            fRecovered.setAccessible(true)
            val imgRecovered: ImageView = fRecovered.get(mSwipeRefreshLayoutRecovered) as ImageView
            imgRecovered.setImageResource(R.drawable.ic_virus)

        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        val rotate = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f)
        rotate.duration = 1300
        rotate.repeatCount = Animation.INFINITE
        ivLogo.startAnimation(rotate)
        ivLogo.visibility = View.VISIBLE
        clAll.visibility = View.GONE

        pullAllAndCountries()
    }

    private fun showCountryDetailPopup(data: Data){
        val bundle = Bundle()
        bundle.putParcelable("data", data)
        countryDetailDialog.arguments = bundle
        countryDetailDialog.show(supportFragmentManager, "CountryDetailPopup")
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



    private fun renderRefreshing(){
        swipeConfirmed.isRefreshing = false
        swipeDeaths.isRefreshing = false
        swipeRecovered.isRefreshing = false
    }

    private fun pullAllAndCountries() {
        isDataReady = false
        disposable.add(
            ApiServiceImp.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    ivLogo.clearAnimation()
                    ivLogo.visibility = View.GONE
                    clAll.visibility = View.VISIBLE
                    Toast.makeText(this, "API is down, please come back soon", Toast.LENGTH_LONG).show()
                }
                .subscribe({
                    renderRefreshing()
                    updateTotalNumbers(it)
                    if (isDataReady){
                        ivLogo.clearAnimation()
                        ivLogo.visibility = View.GONE
                        clAll.visibility = View.VISIBLE
                    }
                }, Throwable::printStackTrace)
        )
        disposable.add(ApiServiceImp.getByCountry()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                ivLogo.clearAnimation()
                ivLogo.visibility = View.GONE
                clAll.visibility = View.VISIBLE
                Toast.makeText(this, "API is down, please come back soon", Toast.LENGTH_LONG).show()
            }
            .subscribe({
                renderRefreshing()
                updateAdapter(it)
                isDataReady = true
                if (!tvNumberTotalRecovered.text.toString().isNullOrEmpty()){
                    ivLogo.clearAnimation()
                    ivLogo.visibility = View.GONE
                    clAll.visibility = View.VISIBLE
                }
            }, Throwable::printStackTrace)
        )
    }

    inner class DataUpdateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == ALL) {
                val it = intent.getParcelableExtra<Data>(ALL)!!
                updateTotalNumbers(it)
            }
            else {
                val it = intent.getParcelableArrayListExtra<Data>(BY_COUNTRY)!!.toList()
                updateAdapter(it)
            }
        }
    }

    private fun updateAdapter(it: List<Data>){
        confirmedAdapter.update(it.filter { d -> !d.country.contains("World") })
        deathsAdapter.update(it.filter { d -> !d.country.contains("World") })
        recoveredAdapter.update(it.filter { d -> !d.country.contains("World") })
    }

    private fun updateTotalNumbers(it: Data){
        tvNumberTotalConfirmed.text = it.cases.toString()
        tvNumberTotalDeaths.text = it.deaths.toString()
        tvNumberTotalRecovered.text = it.recovered.toString()
    }

    override fun onStart() {
        super.onStart()
        initView()
        /*val startServiceIntent = Intent(this, FetchDataJobService::class.java)
        startService(startServiceIntent)*/
    }

    override fun onResume() {
        super.onResume()
        if (dataUpdateReceiver == null) {
            dataUpdateReceiver = DataUpdateReceiver()
        }
        val intentFilter = IntentFilter(ALL)
        registerReceiver(dataUpdateReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        if (dataUpdateReceiver != null) {
            unregisterReceiver(dataUpdateReceiver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
    companion object {
        const val ALL = "all"
        const val BY_COUNTRY = "by_country"
    }
}
