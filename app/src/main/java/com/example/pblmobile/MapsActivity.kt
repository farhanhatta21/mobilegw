package com.example.pblmobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.FetchPlaceRequest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var cardViewPanel: CardView
    private lateinit var locationTitle: TextView
    private lateinit var searchView: SearchView
    private lateinit var tvCityDescription: TextView
    private var searchResults: List<AutocompletePrediction> = emptyList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setupButtonAddLocationListener()

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check and request location permissions
        checkLocationPermission()

        // Initialize Google Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(this)

        // Initialize view elements
        cardViewPanel = findViewById(R.id.cardViewPanel)
        locationTitle = findViewById(R.id.tvRestaurantAddress)
        tvCityDescription = findViewById(R.id.tvCityDescription)
        searchView = findViewById(R.id.search_view)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupSearchViewListener()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Permission denied. Cannot fetch current location.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Try to get the current location and center the map there
        getCurrentLocation()

        // Set a default fallback location (e.g., a city center)
        val defaultLocation = LatLng(-6.200000, 106.816666) // Jakarta, Indonesia
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

        // Allow user to select a location by tapping on the map
        mMap.setOnMapClickListener { latLng ->
            mMap.clear() // Remove previous markers
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            fetchAddressFromLocation(latLng.latitude, latLng.longitude)
        }
    }


    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    mMap.clear()
                    mMap.addMarker(MarkerOptions().position(userLocation).title("You are here"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                    // Optionally, update the UI with the current location address
                    fetchAddressFromLocation(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, "Location not available. Please select a location manually.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Location permission required to fetch your current location.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = android.location.Geocoder(this)
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            // Use safe call to check if addresses is not null and contains elements
            addresses?.firstOrNull()?.let { address ->
                locationTitle.text = address.getAddressLine(0)
                tvCityDescription.text = "${address.locality}, ${address.adminArea}"
            } ?: run {
                locationTitle.text = "Unknown Location"
                tvCityDescription.text = "Coordinates: $latitude, $longitude"
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            locationTitle.text = "Unknown Location"
            tvCityDescription.text = "Coordinates: $latitude, $longitude"
            Toast.makeText(this, "Unable to get address: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupSearchViewListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) performSearch(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean = true
        })
    }

    private fun performSearch(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                searchResults = response.autocompletePredictions
                if (searchResults.isNotEmpty()) fetchPlaceDetails(searchResults[0].placeId)
                else Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Search failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchPlaceDetails(placeId: String) {
        val request = FetchPlaceRequest.builder(
            placeId,
            listOf(
                com.google.android.libraries.places.api.model.Place.Field.LAT_LNG,
                com.google.android.libraries.places.api.model.Place.Field.NAME,
                com.google.android.libraries.places.api.model.Place.Field.ADDRESS
            )
        ).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val latLng = response.place.latLng
                if (latLng != null) {
                    mMap.clear()
                    mMap.addMarker(MarkerOptions().position(latLng).title(response.place.name))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                    locationTitle.text = response.place.name
                    parseAndDisplayLocationDetails(response.place.address)

                    cardViewPanel.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Unable to find location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun parseAndDisplayLocationDetails(address: String?) {
        if (address != null) {
            val addressParts = address.split(",")
            if (addressParts.size >= 2) {
                val kecamatan = addressParts[1].trim()
                val kota = addressParts[2].trim()
                tvCityDescription.text = "$kecamatan, $kota"
            } else {
                tvCityDescription.text = "Lokasi tidak lengkap"
            }
        } else {
            tvCityDescription.text = "Data tidak tersedia"
        }
    }

    private fun setupButtonAddLocationListener() {
        findViewById<Button>(R.id.buttonAddLocation).setOnClickListener {
            val latLng = mMap.cameraPosition.target // Get the map's current position
            val latitude = latLng.latitude
            val longitude = latLng.longitude

            // Log coordinates
            android.util.Log.d("MapsActivity", "Selected Latitude: $latitude, Longitude: $longitude")

            if (latitude != 0.0 || longitude != 0.0) {
                val locationDetails = "${locationTitle.text}, ${tvCityDescription.text}"
                val resultIntent = Intent()
                resultIntent.putExtra("LOCATION_DETAILS", locationDetails)
                resultIntent.putExtra("LATITUDE", latitude)
                resultIntent.putExtra("LONGITUDE", longitude)
                setResult(RESULT_OK, resultIntent)
                finish() // Return to the previous activity
            } else {
                Toast.makeText(this, "Invalid location. Please select a valid location.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
