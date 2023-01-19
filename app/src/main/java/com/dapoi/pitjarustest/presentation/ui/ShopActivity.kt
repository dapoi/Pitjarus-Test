package com.dapoi.pitjarustest.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dapoi.pitjarustest.R
import com.dapoi.pitjarustest.data.source.remote.model.StoresItem
import com.dapoi.pitjarustest.databinding.ActivityShopBinding
import com.dapoi.pitjarustest.presentation.adapter.ShopAdapter
import com.dapoi.pitjarustest.presentation.viewmodel.ShopViewModel
import com.dapoi.pitjarustest.utils.Resource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityShopBinding
    private lateinit var shopAdapter: ShopAdapter

    private val shopViewModel: ShopViewModel by viewModels()
    private var shopLat = 0.0
    private var shopLng = 0.0
    private var hasVisit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setShopAdapter()
        setShopListViewModel()
    }

    private fun setShopAdapter() {
        shopAdapter = ShopAdapter()
        binding.rvShop.apply {
            setHasFixedSize(true)
            adapter = shopAdapter
        }
        shopAdapter.onClick = { storesItem ->
            Intent(this, DetailShopActivity::class.java).apply {
                putExtra(DetailShopActivity.EXTRA_DATA, storesItem)
                startActivity(this)
            }
        }
    }

    private fun setShopListViewModel() {
        shopViewModel.getShop().observe(this) { response ->
            binding.apply {
                when (response) {
                    is Resource.Loading -> {
                        progressBarShop.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        progressBarShop.visibility = View.GONE
                        response.data?.let {
                            shopAdapter.setShopList(it)
                        }
                    }
                    is Resource.Error -> {
                        progressBarShop.visibility = View.GONE
                        Toast.makeText(this@ShopActivity, response.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        shopViewModel.getShop().observe(this) { response ->
            binding.apply {
                when (response) {
                    is Resource.Loading -> {
                        progressBarMap.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        progressBarMap.visibility = View.GONE
                        setUpLocationShop(response.data)
                    }
                    is Resource.Error -> {
                        progressBarMap.visibility = View.GONE
                        Toast.makeText(this@ShopActivity, response.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun setUpLocationShop(stores: List<StoresItem>?) {
        stores?.let { storesItems ->
            val latLngBounds = LatLngBounds.Builder()
            storesItems.indices.forEach { location ->
                storesItems[location].apply {
                    shopLat = latitude.toDouble()
                    shopLng = longitude.toDouble()
                }
                try {
                    val latLng = LatLng(shopLat, shopLng)
                    mMap.apply {
                        addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(storesItems[location].store_name)
                        )
                        latLngBounds.include(latLng)
                        uiSettings.apply {
                            isZoomControlsEnabled = true
                            isCompassEnabled = true
                            isMapToolbarEnabled = true
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val bounds = latLngBounds.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val getHasVisited = intent.getBooleanExtra(DetailShopActivity.HAS_VISITED, false)
        hasVisit = getHasVisited
        if (hasVisit) {
            setShopListViewModel()
        }
    }
}