package lk.nibm.weatherapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class SearchLocation : AppCompatActivity() {

    private lateinit var txtsearchlocation: TextView
    private lateinit var imgSearchWeather: ImageView
    private lateinit var txtSearchTemp: TextView
    private lateinit var txtPressureLevel: TextView
    private lateinit var txtFeelslikeLevel: TextView
    private lateinit var txtSearchWeatherType: TextView
    private lateinit var txtHumidityLevel: TextView
    private lateinit var searchView: SearchView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_location)

        var clickImageBack = findViewById<ImageView>(R.id.imgbacktomain)

        clickImageBack.setOnClickListener{
            var String = Intent(this,MainActivity::class.java)
            startActivity(String)
        }

        txtsearchlocation = findViewById(R.id.txtsearchlocation)
        imgSearchWeather = findViewById(R.id.imgSearchWeather)
        txtSearchTemp =  findViewById(R.id.txtSearchTemp)
        txtSearchWeatherType = findViewById(R.id.txtSearchWeatherType)
        txtPressureLevel = findViewById(R.id.txtPressureLevel)
        txtFeelslikeLevel = findViewById(R.id.txtFeelslikeLevel)
        txtHumidityLevel = findViewById(R.id.txtHumidityLevel)
        searchView = findViewById(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    loadWeatherDataByCity(newText)
                }
                return true
            }
        })
        searchView.setOnCloseListener {
            false
        }

        loadWeatherDataByCity("Colombo")
    }

    private fun loadWeatherDataByCity(cityName: String) {
        val apiKey = "772320e4c397d11de2ad24fe5d2dae9a"
        val geocodingUrl = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$apiKey&units=metric"

        val geocodingJsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, geocodingUrl, null,
            Response.Listener { response ->
                val coordinates = response.getJSONObject("coord")
                val latitude = coordinates.getDouble("lat")
                val longitude = coordinates.getDouble("lon")
                loadCurrentWeatherData(latitude, longitude)
            },
            Response.ErrorListener { error ->
                Log.e("SearchLocationActivity", "Error in second API call: ${error.message}")
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(geocodingJsonObjectRequest)
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
                val temperatureFeelslike = mainObject.getDouble("feels_like").toInt()
                val temperatureMin = mainObject.getDouble("temp_min").toInt()
                val temperatureMax = mainObject.getDouble("temp_max").toInt()
                val weatherCity = response.getString("name")
                val windObject = response.getJSONObject("wind")
                var currentPressure = mainObject.getInt("pressure")
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
                    imgSearchWeather.setImageResource(weatherImageResource)
                }

                txtsearchlocation.text = "$weatherCity"
                txtSearchTemp.text = "$temperatureCelsius°"
                txtFeelslikeLevel.text = "$temperatureFeelslike°"
                txtPressureLevel.text = "$currentPressure mbar"
                txtHumidityLevel.text = "$currentHumidity%"
                txtSearchWeatherType.text = "$currentWeather $temperatureMin°/$temperatureMax°"

            },
            Response.ErrorListener { error ->
                Log.e("SearchLocationActivity", "${error.message}")
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(currentJsonObjectRequest)
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
            !isDay && weatherCondition.equals("Clear Sky", ignoreCase = true) -> R.drawable.clearskynight
            !isDay && weatherCondition.equals("Snow", ignoreCase = true) -> R.drawable.snownight

            else -> R.drawable.wind
        }
    }


}
