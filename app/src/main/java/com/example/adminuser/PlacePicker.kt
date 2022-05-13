package com.example.adminuser

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.util.Log

import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.adevinta.leku.*
import com.adevinta.leku.locale.SearchZoneRect
import com.google.android.gms.maps.model.LatLng


class PlacePicker : AppCompatActivity() {

    var btn_PickLocation: Button? = null
    var tv_MyLocation: TextView? = null
    private val PLACE_PICKER_REQUEST = 999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_picker)

        btn_PickLocation = findViewById(R.id.BtnPickLocation);
        tv_MyLocation = findViewById(R.id.MyLocation);

        btn_PickLocation!!.setOnClickListener { openPlacePicker() }
    }

    private fun openPlacePicker() {
        val locationPickerIntent = LocationPickerActivity.Builder()
            .withLocation(41.4036299, 2.1743558)
            .withGeolocApiKey("<PUT API KEY HERE>")
            .withSearchZone("es_ES")
            .withSearchZone(
                SearchZoneRect(
                    LatLng(26.525467, -18.910366),
                    LatLng(43.906271, 5.394197)
                )
            )
            .withDefaultLocaleSearchZone()
            .shouldReturnOkOnBackPressed()
            .withStreetHidden()
            .withCityHidden()
            .withZipCodeHidden()
            .withSatelliteViewHidden()
            //.withGooglePlacesEnabled()
            .withGoogleTimeZoneEnabled()
            .withVoiceSearchHidden()
            .withUnnamedRoadHidden()
            .withMapStyle(R.raw.map_style)
            // .withSearchBarHidden()
            .build(applicationContext)


        startActivityForResult(locationPickerIntent, PLACE_PICKER_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RESULT****", "OK")
            if (requestCode == 1) {
                val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                Log.d("LATITUDE****", latitude.toString())
                val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                Log.d("LONGITUDE****", longitude.toString())
                val address = data.getStringExtra(LOCATION_ADDRESS)
                Log.d("ADDRESS****", address.toString())
                val postalcode = data.getStringExtra(ZIPCODE)
                Log.d("POSTALCODE****", postalcode.toString())
                val bundle = data.getBundleExtra(TRANSITION_BUNDLE)
                if (bundle != null) {
                    bundle.getString("test")?.let { Log.d("BUNDLE TEXT****", it) }
                }
                val fullAddress = data.getParcelableExtra<Address>(ADDRESS)
                if (fullAddress != null) {
                    Log.d("FULL ADDRESS****", fullAddress.toString())
                }
                val timeZoneId = data.getStringExtra(TIME_ZONE_ID)
                if (timeZoneId != null) {
                    Log.d("TIME ZONE ID****", timeZoneId)
                }
                val timeZoneDisplayName = data.getStringExtra(TIME_ZONE_DISPLAY_NAME)
                if (timeZoneDisplayName != null) {
                    Log.d("TIME ZONE NAME****", timeZoneDisplayName)
                }
            } else if (requestCode == 2) {
                val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                Log.d("LATITUDE****", latitude.toString())
                val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                Log.d("LONGITUDE****", longitude.toString())
                val address = data.getStringExtra(LOCATION_ADDRESS)
                Log.d("ADDRESS****", address.toString())
                val lekuPoi = data.getParcelableExtra<LekuPoi>(LEKU_POI)
                Log.d("LekuPoi****", lekuPoi.toString())
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("RESULT****", "CANCELLED")
        }
    }
}