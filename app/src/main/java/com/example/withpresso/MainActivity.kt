package com.example.withpresso

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.withpresso.adapter.CafeRecyclerViewAdapter
import com.example.withpresso.service.Cafe
import com.example.withpresso.service.CafeRecommendService
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    private val LOCATION_ACCESS_CODE = 1002

    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var BASE_URL: String

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* location permission */
        setupLocationPermission()

        /* 위치 정보 요청할 준비 */
        initLocation()

        /* init */
        pref = getSharedPreferences("user_info", 0)
        BASE_URL = "http://ec2-3-34-119-217.ap-northeast-2.compute.amazonaws.com"
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        cafes_recycle.layoutManager = GridLayoutManager(this, 2)


        /* setOnClickListener */
        my_page_button.setOnClickListener {
            if(pref.contains("email") && pref.contains("password")) {
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, LogInActivity::class.java)
                startActivity(intent)
            }
        }


    }

    override fun onResume() {
        super.onResume()

        /* 프로필 이미지 그리기 */
        val userUniqNum = pref.getInt("uniq_num", 0)
        val profileName = pref.getString("profile", "")
        val profileUrl = "${BASE_URL}/profiles/${userUniqNum}/${profileName}"
        drawProfile(this, profileUrl, my_page_button)

        /* 위치 권한이 중간에 막히면 requestLocationUpdate가 null을 반환할 수 있음 */
        val latlong = requestLocationUpdate()
        latlong?.let{
            val lat = latlong[0]
            val long = latlong[1]
            val uniq_num = pref.getInt("uniq_num", 0)
            val cafeRecommendService = retrofit.create(CafeRecommendService::class.java)
            cafeRecommendService.requestCafeRecommendList(lat, long, uniq_num).enqueue(
                object :Callback<ArrayList<Cafe>> {
                    /* 통신에 실패하면 실행 */
                    override fun onFailure(call: Call<ArrayList<Cafe>>, t: Throwable) {
                        Log.e("cafe recommend", "network error")
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("카페 추천 실패")
                            .setMessage("통신 오류")
                            .show()
                    }
                    /* 통신이 성공하면 실행 */
                    override fun onResponse(
                        call: Call<ArrayList<Cafe>>,
                        response: Response<ArrayList<Cafe>>
                    ) {
                        val dataList = response.body()

                        if(dataList.isNullOrEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "모든 카페를 추천해드렸습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else {
                            cafes_recycle.adapter = CafeRecyclerViewAdapter(
                                this@MainActivity, dataList)
                            Toast.makeText(
                                this@MainActivity,
                                "다음 카페 추천",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            )
        } ?: AlertDialog.Builder(this)
            .setTitle("위치 정보 가져오기 실패")
            .setMessage("위치 정보를 가져오지 못했습니다.")
            .show()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /* 프로필 이미지 그리기 */
    private fun drawProfile(context: Context, profileUri: String?, des: ImageView) {
        if(profileUri.isNullOrBlank()) {
            Glide.with(this)
                .load(R.drawable.default_profile)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(des)
        }
        else {
            Glide.with(context)
                .load(profileUri)
                .error(R.drawable.default_profile)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(des)
        }
    }

    /* location permission */
    private fun setupLocationPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )

        /* 이미 위치 정보 접근을 허용 했음 */
        if(permission == PermissionChecker.PERMISSION_GRANTED){
            return true
        }
        /* 위치 정보 접근이 허용된 적 없음 */
        else {
            val isAccessDenied = ActivityCompat.shouldShowRequestPermissionRationale(
                this, ACCESS_FINE_LOCATION
            )

            /* 최초로 위치 정보에 접근 시도 */
            if(!isAccessDenied) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(ACCESS_FINE_LOCATION),
                    LOCATION_ACCESS_CODE
                )
            }
            /* 위치 정보 접근 권한 제한됨 */
            else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("위치 정보 접근 권한 제한됨")
                dialog.setMessage("  위치 정보 접근을 허용해 주세요.\n\n" +
                        "  app info -> Permissions -> location")
                dialog.show()
            }

            return false
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            /* 위치 정보에 접근이 허용되면 실행 */
            LOCATION_ACCESS_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {

                }
            }
        }

        return
    }

    private fun initLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if(location == null) {
                    Log.e("location error", "location get fail")
                } else {
                    Log.d("location", "${location.latitude} , ${location.longitude}")
                }
            }
            .addOnFailureListener {
                Log.e("location error", "location error is ${it.message}")
                it.printStackTrace()
            }
    }

    /* 위치를 얻을 수 있으면 위도, 경도를 반환. 위도, 경도를 얻을 수 없으면 null을 반환 */
    private fun requestLocationUpdate(): ArrayList<Double>? {
        /* 위치 정보 권한 검사 */
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION),
                LOCATION_ACCESS_CODE
            )

            return null
        }

        locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 60 * 1000
        }

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for((i, location) in it.locations.withIndex()) {
                        Log.d("history", "#$i ${location.latitude} , ${location.longitude}")
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        val latitude = fusedLocationClient.lastLocation.result.latitude
        val longitude = fusedLocationClient.lastLocation.result.longitude

        return arrayListOf(latitude, longitude)
    }
}