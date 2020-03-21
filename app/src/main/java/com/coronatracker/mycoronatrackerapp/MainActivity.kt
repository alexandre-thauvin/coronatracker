package com.coronatracker.mycoronatrackerapp

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.coronatracker.mycoronatrackerapp.adapter.ConfirmedAdapter
import com.coronatracker.mycoronatrackerapp.adapter.DeathsAdapter
import com.coronatracker.mycoronatrackerapp.adapter.RecoveredAdapter
import com.coronatracker.mycoronatrackerapp.network.ApiServiceImp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Field


class MainActivity : AppCompatActivity() {
    private var disposable = CompositeDisposable()
    private val REQUEST_CODE = 10001
    private lateinit var confirmedAdapter: ConfirmedAdapter
    private lateinit var deathsAdapter: DeathsAdapter
    private lateinit var recoveredAdapter: RecoveredAdapter
    private var isDataReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
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
        initView()
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
                .subscribe({
                    renderRefreshing()
                    tvNumberTotalConfirmed.text = it.cases.toString()
                    tvNumberTotalDeaths.text = it.deaths.toString()
                    tvNumberTotalRecovered.text = it.recovered.toString()
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
            .subscribe({
                renderRefreshing()
                confirmedAdapter.update(it)
                deathsAdapter.update(it)
                recoveredAdapter.update(it)
                isDataReady = true
                if (!tvNumberTotalRecovered.text.toString().isNullOrEmpty()){
                    ivLogo.clearAnimation()
                    ivLogo.visibility = View.GONE
                    clAll.visibility = View.VISIBLE
                }
            }, Throwable::printStackTrace)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
