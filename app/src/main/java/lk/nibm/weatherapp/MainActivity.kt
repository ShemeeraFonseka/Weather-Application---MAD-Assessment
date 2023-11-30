package lk.nibm.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import com.google.android.gms.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.Builder
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {

    private lateinit var txtTemp: TextView
    private lateinit var txtSunny: TextView
    private lateinit var txtLocation: TextView
    private lateinit var txtWSpeed: TextView
    private lateinit var txtHumiditylevel: TextView
    private lateinit var txtUVIndex: TextView
    private lateinit var txtToday: TextView
    private lateinit var txtTodayTemp: TextView
    private lateinit var imgSunny: ImageView
    private lateinit var imgToday: ImageView

    private lateinit var txtTomoFor: TextView
    private lateinit var imgTomorrrow: ImageView
    private lateinit var txtTomoTemp: TextView

    private lateinit var txtDay01For: TextView
    private lateinit var imgDay01: ImageView
    private lateinit var txtDay01Temp: TextView
    private lateinit var txtDay01: TextView


    lateinit var locationRequest : LocationRequest

    val locationCient : FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    var currentLocation : ExerciseRoute.Location? = null
    lateinit var lblLocation : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lblLocation = findViewById(R.id.txtLocation)
        checkPermission()



        var clicktext = findViewById<TextView>(R.id.txtForecast)

        clicktext.setOnClickListener{
            var String = Intent(this,FutureForecast::class.java)
            startActivity(String)
        }

        var clickImageAdd = findViewById<ImageView>(R.id.imgAddLocation)
        clickImageAdd.setOnClickListener{
            var String = Intent(this,SearchLocation::class.java)
            startActivity(String)
        }



        txtTemp = findViewById(R.id.txtTemp)
        txtSunny = findViewById(R.id.txtSunny)
        txtLocation = findViewById(R.id.txtLocation)
        txtWSpeed = findViewById(R.id.txtWSpeed)
        txtHumiditylevel = findViewById(R.id.txtHumiditylevel)
        txtUVIndex = findViewById(R.id.txtUVIndex)
        txtToday = findViewById(R.id.txtToday)
        txtTodayTemp = findViewById(R.id.txtTodayTemp)
        imgSunny = findViewById(R.id.imgSunny)
        imgToday = findViewById(R.id.imgToday)

        txtTomoFor = findViewById(R.id.txtTomoFor)
        txtTomoTemp = findViewById(R.id.txtTomoTemp)
        imgTomorrrow = findViewById(R.id.imgTomorrrow)

        txtDay01For = findViewById(R.id.txtDay01For)
        txtDay01Temp = findViewById(R.id.txtDay01Temp)
        imgDay01 = findViewById(R.id.imgDay01)
        txtDay01 = findViewById(R.id.txtDay01)

    }

    private fun checkPermission() {
        if(ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            accessLocation()
        }
        else
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }
    @SuppressLint("MissingPermission")
    fun accessLocation() {
        run {
            locationRequest = Builder(PRIORITY_HIGH_ACCURACY,100).build()


            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    p0.locations.lastOrNull()?.let { location ->
                        loadCurrentWeatherData(location.latitude, location.longitude)
                        loadForcastWeatherData(location.latitude, location.longitude)
                    }
                }
            }
            locationCient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }



    private fun loadCurrentWeatherData(latitude: Double, longitude: Double) {
        val apiKey = "772320e4c397d11de2ad24fe5d2dae9a"
        val currentUrl =
            "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric"
        val currentJsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, currentUrl, null,
            Response.Listener { response ->
                val mainObject = response.getJSONObject("main")
                val temperatureCelsius = mainObject.getDouble("temp").toInt()
                val temperatureCelsiusMax = mainObject.getDouble("temp_min").toInt()
                val temperatureCelsiusMin = mainObject.getDouble("temp_max").toInt()
                val weatherCity = response.getString("name")
                val windObject = response.getJSONObject("wind")
                var currentWindSpeed = windObject.getDouble("speed") * 3.6
                var currentHumidity = mainObject.getInt("humidity")
                val timestamp = response.getLong("dt") * 1000
                val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getDefault()
                val formattedDate = dateFormat.format(Date(timestamp))

                val sunrise = response.getJSONObject("sys").getLong("sunrise") * 1000
                val sunset = response.getJSONObject("sys").getLong("sunset") * 1000

                val isDay = timestamp in sunrise until sunset

                val weatherArray = response.getJSONArray("weather")
                var currentWeather = ""
                if (weatherArray.length() > 0) {
                    val weatherObject = weatherArray.getJSONObject(0)
                    val weatherMain = weatherObject.getString("main")
                    currentWeather = weatherMain

                    val weatherImageResource = getWeatherImageResource(currentWeather, isDay)
                    imgSunny.setImageResource(weatherImageResource)

                    imgSunny.setImageResource(weatherImageResource)
                }

                txtTemp.text = "$temperatureCelsius°"
                txtSunny.text = "$currentWeather $temperatureCelsiusMin°/$temperatureCelsiusMax°"
                txtLocation.text = "$weatherCity"
                txtWSpeed.text = String.format("%.2f km/h", currentWindSpeed)
                txtHumiditylevel.text = ("$currentHumidity%")
            },
            Response.ErrorListener { error ->
                txtTemp.text = "Error: ${error.message}"
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(currentJsonObjectRequest)
    }


    private fun loadForcastWeatherData(latitude: Double, longitude: Double) {
        val apiKey = "772320e4c397d11de2ad24fe5d2dae9a"
        val loadForcastWeatherUrl = "https://api.openweathermap.org/data/3.0/onecall?lat=$latitude&lon=$longitude&appid=$apiKey&exclude=alerts,minutely,hourly&units=metric"
        val forcastWeatherDataJsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, loadForcastWeatherUrl, null,
            Response.Listener { forcastWeatherDataResponse ->
                val forcastWeatherCurrentObject = forcastWeatherDataResponse.getJSONObject("current")
                val forcastArray = forcastWeatherDataResponse.getJSONArray("daily")
                val uvIndex = forcastWeatherCurrentObject.getInt("uvi")

                for (i in 0 until minOf(forcastArray.length(), 7)) {
                    val forcastObject = forcastArray.getJSONObject(i)
                    val forcastTimestamp = forcastObject.getLong("dt") * 1000
                    val forcastDate = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(forcastTimestamp))
                    val forcastTemp = forcastObject.getJSONObject("temp")
                    val forcastMaxTemp = forcastTemp.getDouble("max").toInt()
                    val forcastMinTemp = forcastTemp.getDouble("min").toInt()
                    val forcastWeatherArray = forcastObject.getJSONArray("weather")
                    val forcastWeatherObject = forcastWeatherArray.getJSONObject(0)
                    val forcastWeatherMain = forcastWeatherObject.getString("main")

                    val isDayForcast = true
                    val weatherImageResource = getWeatherImageResource(forcastWeatherMain, isDayForcast)

                    when (i) {
                        0 -> {
                            txtToday.text = "$forcastWeatherMain"
                            txtTodayTemp.text = "$forcastMinTemp°/$forcastMaxTemp°"
                            imgToday.setImageResource(weatherImageResource)
                        }
                        1 -> {
                            txtTomoFor.text = "$forcastWeatherMain"
                            txtTomoTemp.text = "$forcastMinTemp°/$forcastMaxTemp°"
                            imgTomorrrow.setImageResource(weatherImageResource)
                        }
                        2 -> {
                            txtDay01For.text = "$forcastWeatherMain"
                            txtDay01Temp.text = "$forcastMinTemp°/$forcastMaxTemp°"
                            imgDay01.setImageResource(weatherImageResource)
                            txtDay01.text = "$forcastDate"
                        }
                    }
                }

                txtUVIndex.text = "$uvIndex"
            },
            Response.ErrorListener { error ->
                Log.e("MainActivity", "Error in second API call: ${error.message}")
            }
        )

        val requestQueueAdditionalData = Volley.newRequestQueue(this)
        requestQueueAdditionalData.add(forcastWeatherDataJsonObjectRequest)
    }

    private fun getWeatherImageResource(weatherCondition: String, isDay: Boolean): Int {
        return when {
            isDay && weatherCondition.equals("Rain", ignoreCase = true) -> R.drawable.rain
            isDay && weatherCondition.equals("Clouds", ignoreCase = true) -> R.drawable.cloudy
            isDay && weatherCondition.equals("Windy", ignoreCase = true) -> R.drawable.wind
            isDay && weatherCondition.equals("Sunny", ignoreCase = true) -> R.drawable.sun
            isDay && weatherCondition.equals("Thunderstorm", ignoreCase = true) -> R.drawable.storm
            isDay && weatherCondition.equals("Clear", ignoreCase = true) -> R.drawable.clearsky
            isDay && weatherCondition.equals("Snow", ignoreCase = true) -> R.drawable.snowy
            isDay && weatherCondition.equals("Drizzle", ignoreCase = true) -> R.drawable.drizzle
            !isDay && weatherCondition.equals("Rain", ignoreCase = true) -> R.drawable.rainynight
            !isDay && weatherCondition.equals("Clouds", ignoreCase = true) -> R.drawable.cloudynight
            !isDay && weatherCondition.equals("Windy", ignoreCase = true) -> R.drawable.windynight
            !isDay && weatherCondition.equals("Thunderstorm", ignoreCase = true) -> R.drawable.thundernight
            !isDay && weatherCondition.equals("Clear", ignoreCase = true) -> R.drawable.cloudynight1
            !isDay && weatherCondition.equals("Snow", ignoreCase = true) -> R.drawable.snownight

            else -> R.drawable.wind
        }
    }
}




