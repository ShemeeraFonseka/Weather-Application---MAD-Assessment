package lk.nibm.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FutureForecast : AppCompatActivity() {

    private lateinit var txtForecastWeatherToday: TextView
    private lateinit var todayTemp: TextView
    private lateinit var imageToday: ImageView

    private lateinit var txtForecastWeatherTomo: TextView
    private lateinit var tomoTemp: TextView
    private lateinit var imageTomo: ImageView

    private lateinit var txtForecastWeatherDay1: TextView
    private lateinit var day1Temp: TextView
    private lateinit var Day1: TextView
    private lateinit var imageDay1: ImageView

    private lateinit var txtForecastWeatherDay2: TextView
    private lateinit var day2Temp: TextView
    private lateinit var Day2: TextView
    private lateinit var imageDay2: ImageView

    private lateinit var txtForecastWeatherDay3: TextView
    private lateinit var day3Temp: TextView
    private lateinit var Day3: TextView
    private lateinit var imageDay3: ImageView

    private lateinit var txtForecastWeatherDay4: TextView
    private lateinit var day4Temp: TextView
    private lateinit var Day4: TextView
    private lateinit var imageDay4: ImageView

    private lateinit var txtForecastWeatherDay5: TextView
    private lateinit var day5Temp: TextView
    private lateinit var Day5: TextView
    private lateinit var imageDay5: ImageView

    private lateinit var txtSunset: TextView
    private lateinit var txtSunrise: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_future_forecast)

        var clickImage = findViewById<ImageView>(R.id.imgBack)

        clickImage.setOnClickListener{
            var String = Intent(this,MainActivity::class.java)
            startActivity(String)
        }

        txtForecastWeatherToday = findViewById(R.id.txtForecastWeatherToday)
        todayTemp = findViewById(R.id.todayTemp)
        imageToday = findViewById(R.id.imageToday)

        txtForecastWeatherTomo = findViewById(R.id.txtForecastWeatherTomo)
        tomoTemp = findViewById(R.id.tomoTemp)
        imageTomo = findViewById(R.id.imageTomo)

        txtForecastWeatherDay1 = findViewById(R.id.txtForecastWeatherDay1)
        day1Temp = findViewById(R.id.day1Temp)
        Day1 = findViewById(R.id.Day1)
        imageDay1 = findViewById(R.id.imageDay1)

        txtForecastWeatherDay2 = findViewById(R.id.txtForecastWeatherDay2)
        day2Temp = findViewById(R.id.day2Temp)
        Day2 = findViewById(R.id.Day2)
        imageDay2 = findViewById(R.id.imageDay2)

        txtForecastWeatherDay3 = findViewById(R.id.txtForecastWeatherDay3)
        day3Temp = findViewById(R.id.day3Temp)
        Day3 = findViewById(R.id.Day3)
        imageDay3 = findViewById(R.id.imageDay3)

        txtForecastWeatherDay4 = findViewById(R.id.txtForecastWeatherDay4)
        day4Temp = findViewById(R.id.day4Temp)
        Day4 = findViewById(R.id.Day4)
        imageDay4 = findViewById(R.id.imageDay4)

        txtForecastWeatherDay5 = findViewById(R.id.txtForecastWeatherDay5)
        day5Temp = findViewById(R.id.day5Temp)
        Day5 = findViewById(R.id.Day5)
        imageDay5 = findViewById(R.id.imageDay5)

        txtSunrise = findViewById(R.id.txtSunrise)
        txtSunset = findViewById(R.id.txtSunset)

        loadForcastWeatherData()
    }

    private fun loadForcastWeatherData() {
        val loadForcastWeatherUrl = "https://api.openweathermap.org/data/3.0/onecall?lat=6.9271&lon=79.8612&exclude=alerts,minutely,hourly&units=metric&appid=772320e4c397d11de2ad24fe5d2dae9a"
        val forcastWeatherDataJsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, loadForcastWeatherUrl, null,
            Response.Listener { forcastWeatherDataResponse ->
                val forcastArray = forcastWeatherDataResponse.getJSONArray("daily")

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
                            txtForecastWeatherToday.text = "$forcastWeatherMain"
                            todayTemp.text = "$forcastMinTemp°/$forcastMaxTemp°"
                            imageToday.setImageResource(weatherImageResource)
                        }
                        1 -> {
                            txtForecastWeatherTomo.text = "$forcastWeatherMain"
                            tomoTemp.text = "$forcastMinTemp°/$forcastMaxTemp°"
                            imageTomo.setImageResource(weatherImageResource)
                        }
                        2 -> {
                            txtForecastWeatherDay1.text = "$forcastWeatherMain"
                            day1Temp.text = "$forcastMinTemp°/$forcastMaxTemp°"
                            Day1.text = "$forcastDate"
                            imageDay1.setImageResource(weatherImageResource)
                        }
                        3 -> {
                            txtForecastWeatherDay2.text = "$forcastWeatherMain"
                            day2Temp.text = "$forcastMinTemp°/$forcastMaxTemp°"
                            Day2.text = "$forcastDate"
                            imageDay2.setImageResource(weatherImageResource)
                        }
                        4 -> {
                            txtForecastWeatherDay3.text = "$forcastWeatherMain"
                            day3Temp.text = "$forcastMinTemp°/$forcastMaxTemp°"
                            Day3.text = "$forcastDate"
                            imageDay3.setImageResource(weatherImageResource)
                        }
                        5 -> {
                            txtForecastWeatherDay4.text = "$forcastWeatherMain"
                            day4Temp.text = "$forcastMinTemp°/$forcastMaxTemp°"
                            Day4.text = "$forcastDate"
                            imageDay4.setImageResource(weatherImageResource)
                        }
                        6 -> {
                            txtForecastWeatherDay5.text = "$forcastWeatherMain"
                            day5Temp.text = "$forcastMinTemp°/$forcastMaxTemp°"
                            Day5.text = "$forcastDate"
                            imageDay5.setImageResource(weatherImageResource)
                        }
                    }

                    val currentObject = forcastWeatherDataResponse.getJSONObject("current")
                    val sunriseTimestamp = currentObject.getLong("sunrise")* 1000L
                    val sunsetTimestamp = currentObject.getLong("sunset")* 1000L

                    val sunriseDate = Date(sunriseTimestamp)
                    val sunriseDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    sunriseDateFormat.timeZone = TimeZone.getDefault()
                    val formattedSunrise = sunriseDateFormat.format(sunriseDate)

                    val sunsetDate = Date(sunsetTimestamp)
                    val sunsetDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    sunsetDateFormat.timeZone = TimeZone.getDefault()
                    val formattedSunset = sunsetDateFormat.format(sunsetDate)

                    txtSunrise.text = "$formattedSunrise Sunrise"
                    txtSunset.text = "$formattedSunset Sunset"

                }

            },
            Response.ErrorListener { error ->
                Log.e("FutureForecastActivity", "Error in second API call: ${error.message}")
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
            !isDay && weatherCondition.equals("Rain", ignoreCase = true) -> R.drawable.rain
            !isDay && weatherCondition.equals("Clouds", ignoreCase = true) -> R.drawable.cloudynight
            !isDay && weatherCondition.equals("Windy", ignoreCase = true) -> R.drawable.wind
            else -> R.drawable.wind
        }
    }
}