/**
 * Приложение для организации совместных поездок на байк-фесты "МотоСтрана"
 * 2022 год. Разработчик - Дуюнов Павел
 */
package com.elpablo.motostrana.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.elpablo.motostrana.App
import com.elpablo.motostrana.App.Companion.cityName
import com.elpablo.motostrana.App.Companion.regionName
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.ActivityMainBinding
import com.elpablo.motostrana.model.Event
import com.elpablo.motostrana.utils.AppStates
import com.elpablo.motostrana.utils.CONST
import com.elpablo.motostrana.utils.CONST.ACTIVITY_CONTEXT
import com.elpablo.motostrana.utils.CONST.CONTACTS_PERMISSION_REQUEST_CODE
import com.elpablo.motostrana.utils.LocationPermission
import com.elpablo.motostrana.utils.checkPermission
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*

private const val READ_CONTACTS = Manifest.permission.READ_CONTACTS

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ACTIVITY_CONTEXT = this
        //Восстанавливаем переменные геопозиции
        if (savedInstanceState != null) {
            cityName = savedInstanceState.getString(CONST.KEY_CITY).toString()
            regionName = savedInstanceState.getString(CONST.KEY_STATE).toString()
        }

        //Проверяем пермишн геопозиции и получаем данные геопозиции
        checkPermissionAndLocation()

        //Убираем splash_screen
        setTheme(R.style.Theme_MotoStrana)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //настраиваем навигацию приложения
        initNavComponent()
        checkContactPermissions()

        //проверяем наличие google play services
        checkGooglePlayServices()
        if (!checkGooglePlayServices()) {
            Toast.makeText(
                applicationContext,
                "Из-за отсутствия Google Play Services оповещения о новых сообщениях не будут работать",
                Toast.LENGTH_SHORT
            ).show()
            Log.w(TAG, "Push notification не могут работать из-за отсутствия google play services")
        }

        //служебная функция
        //recordEventToDB()
    }

    private fun checkContactPermissions() {
        if (checkPermission(READ_CONTACTS, CONTACTS_PERMISSION_REQUEST_CODE)) {
            //чтение контактов
            initContacts()
        }
    }

    private fun initContacts() {
        //TODO("Not yet implemented")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(CONST.KEY_CITY, cityName)
        outState.putString(CONST.KEY_STATE, regionName)
        super.onSaveInstanceState(outState)
    }

    //NavComponent
    private fun initNavComponent() {
        val navView: BottomNavigationView = binding.navView
        navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment).navController
        navView.setupWithNavController(navController)
    }

    fun showBottomNav() {
        binding.navView.visibility = View.VISIBLE
    }

    fun hideBottomNav() {
        binding.navView.visibility = View.GONE
    }

    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Google play services не обновлены")
            false
        } else {
            Log.i(TAG, "Google play services обновлены")
            true
        }
    }

    private fun checkPermissionAndLocation() {
        when {
            LocationPermission.isAccessFineLocationGranted(this) -> {
                when {
                    LocationPermission.isLocationEnabled(this) -> {
                        lastKnownLocation()
                    }
                    else -> {
                        LocationPermission.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                LocationPermission.requestAccessFineLocationPermission(
                    this,
                    CONST.LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun lastKnownLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                decodingPosition(location)
            }
        }

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location ->
                decodingPosition(location)
            }
    }

    private fun decodingPosition(location: Location) {
        Thread {
            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            try {
                val list = geocoder.getFromLocation(
                    location.latitude, location.longitude, 1
                )
                if (list != null && list.size > 0) {
                    val address = list[0]
                    cityName = address.locality
                    regionName = address.adminArea
                }
            } catch (error: IOException) {
                throw error
            }
        }.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CONST.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        LocationPermission.isLocationEnabled(this) -> {
                            lastKnownLocation()
                        }
                        else -> {
                            LocationPermission.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            CONTACTS_PERMISSION_REQUEST_CODE -> {
                initContacts()
            }
        }
    }

    //Служебная функция для запись события в firestore
    private fun recordEventToDB() {
        val event = Event(
            "https://moto-magazine.ru/news/2022/07/cfmoto-presents-retro-style-700-cl-x-adv.jpg",
            "700CLX ADV: третья новинка от CFMoto",
            "2022-07-24",
            "Стартуют продажи adventure-версии китайского родстера в стилистике нео-ретро",
            "Представленная в конце 2020 года модель 700CLX стала фактически платформой, на базе которой компания CFMoto анонсировала сразу три версии мотоцикла в нео-ретро-стилистике. Первой в продаже появилась базовая модель 700CLX Heritage, чуть позже китайцы вывели на рынок версию Sport, а в ближайшее время начнутся продажи 700CLX ADV — этакого микса скремблера и туристического эндуро.\nОт базовой \"семисотки\" CFMoto CL-X ADV отличается спицованныи колёсами, обутыми в шины с внедорожным рисунком протектора, чуть более длинноходными подвесками, высоким передним крылом, ветровым стеклом и миниатюрными, но вполне функциональными багажниками. Поменялся и список режимов работы: вместо Eco и Sport у adventure-версии теперь доступны Street и Off-Road.\nПродажи CFMoto 700 CL-X ADV начнутся одновременно на рынках Европы, Азии и Австралии. Когда новинка появится в России, пока не известно."
        )
        App.remoteEventDB.collection(CONST.NEWS).add(event)
    }

    fun hideInputKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    override fun onResume() {
        AppStates.updateState(AppStates.ONLINE)
        super.onResume()
    }

    override fun onPause() {
        AppStates.updateState(AppStates.OFFLINE)
        super.onPause()
    }

    companion object {
        private const val TAG = "MAIN ACTIVITY"
    }
}