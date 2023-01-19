package com.dapoi.pitjarustest.presentation.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dapoi.pitjarustest.R
import com.dapoi.pitjarustest.data.source.remote.model.StoresItem
import com.dapoi.pitjarustest.databinding.ActivityDetailShopBinding
import com.google.android.gms.location.*
import java.io.File

class DetailShopActivity : AppCompatActivity() {

    private lateinit var resultBitmap: Bitmap
    private lateinit var runnable: Runnable
    private lateinit var fusedLocation: FusedLocationProviderClient

    private var _binding: ActivityDetailShopBinding? = null
    private val binding get() = _binding!!
    private var getFile: File? = null
    private var handler = Handler(Looper.getMainLooper())
    private var myLat = 0.0
    private var myLon = 0.0
    private var hasVisited = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailShopBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            val dataShop = intent?.getParcelableExtra(EXTRA_DATA) as? StoresItem

            tvShopName.text = dataShop?.store_name
            tvShopAddress.text = dataShop?.address

            fabCam.setOnClickListener {
                val intent = Intent(this@DetailShopActivity, CameraActivity::class.java)
                launcherIntentCameraX.launch(intent)
            }

            btnVisit.setOnClickListener {
                if (getFile != null) {
                    dataShop?.has_visit = true
                    // back to previous activity with hasVisited = true
                    val intent = Intent(this@DetailShopActivity, ShopActivity::class.java)
                    intent.putExtra(HAS_VISITED, dataShop?.has_visit)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@DetailShopActivity, "Please take a photo", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f) // flip gambar
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }

    private val launcherIntentCameraX =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == CAMERA_X_RESULT) {
                val myFile = result.data?.getSerializableExtra("picture") as File
                val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) as Boolean

                getFile = myFile
                resultBitmap = rotateBitmap(
                    BitmapFactory.decodeFile(myFile.absolutePath),
                    isBackCamera
                )

                binding.apply {
                    btnVisit.isEnabled = true
                    tvEmpty.visibility = View.GONE
                    ivPhoto.visibility = View.VISIBLE
                    ivPhoto.setImageBitmap(resultBitmap)
                }
            }
        }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(Runnable {
            getLocation()
            handler.postDelayed(runnable, 1000)
        }.also { runnable = it }, 1000)
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (isLocationEnabled()) {
                binding.btnVisit.isEnabled = true
                fusedLocation.lastLocation.addOnCompleteListener(this) { task ->
                    val location = task.result
                    if (location != null) {
                        myLon = location.longitude
                        myLat = location.latitude
                        binding.tvLocation.text = "Lat: $myLat, Lon: $myLon"
                    } else {
                        requestNewLocationData()
                    }
                }
            } else {
                binding.btnVisit.isEnabled = false
                binding.tvLocation.text = resources.getString(R.string.turn_on_location)
                myLon = 0.0
                myLat = 0.0
            }
        } else {
            requestPermission()
        }
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 100
            fastestInterval = 3000
            numUpdates = 1
        }
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocation.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation!!
            myLon = location.longitude
            myLat = location.latitude
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_LOCATION
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    companion object {
        const val HAS_VISITED = "hasVisited"
        const val EXTRA_DATA = "extra_data"
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val PERMISSION_LOCATION = 1
    }
}