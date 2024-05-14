package com.example.map

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map.adapter.AddressAdapter
import com.example.map.api.ApiConfig
import com.example.map.api.RetrofitConfig
import com.example.map.constant.AppConfig
import com.example.map.databinding.ActivityAddressSearchBinding
import com.example.map.model.Address
import kotlinx.coroutines.*



class AddressSearchActivity : AppCompatActivity() {
    private lateinit var activityAddressSearchBinding: ActivityAddressSearchBinding
    private var listAddress : MutableList<Address>? = null
    private var addressAdapter: AddressAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddressSearchBinding = ActivityAddressSearchBinding.inflate(layoutInflater)
        setContentView(activityAddressSearchBinding.root)
        initSearch()
        displayListProduct()
        hideSoftKeyboard()
        setEdtEmpty()

    }

    private fun initSearch() {
        activityAddressSearchBinding.rlLayout.setOnClickListener{hideSoftKeyboard()}
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        activityAddressSearchBinding.edtAddress.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(listAddress?.isNotEmpty() == true){
                    addressAdapter?.clearListAddress()
                }
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    delay(1000)
                    clickCallApi(s.toString())
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun clickCallApi(q : String) {
        GlobalScope.launch(Dispatchers.IO ) {
            listAddress = mutableListOf()
            val response =   RetrofitConfig.apiService.getListAddress(ApiConfig.API_KEY,q,ApiConfig.LIMIT,ApiConfig.COUNTRYCODE,ApiConfig.LANG)
            if(response.isSuccessful){
                for (address in response.body()?.items!!){
                    listAddress?.add(address.address)
                }

            }
            withContext(Dispatchers.Main){
                if(listAddress!!.isEmpty() && q.isNotEmpty()){
                    activityAddressSearchBinding.tvNoResult.visibility = View.VISIBLE

                }else{
                    addressAdapter?.addListAddress(listAddress!!,q)
                    activityAddressSearchBinding.tvNoResult.visibility = View.GONE
                }

            }
        }
    }

    private fun displayListProduct() {
        val linearLayoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        addressAdapter = AddressAdapter()
        activityAddressSearchBinding.rclvListAddress.apply {
            layoutManager=linearLayoutManager
            adapter=addressAdapter
            addItemDecoration(dividerItemDecoration)
        }
        getDirections()
    }
    private fun getDirections(){
        try {
            addressAdapter?.onClickAddress = {address->
                val uri = Uri.parse(AppConfig.DIRECTIONS + address)
                val intent = Intent(Intent.ACTION_VIEW,uri)
                intent.setPackage(AppConfig.MAPS_PACKAGE)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }catch (exception : ActivityNotFoundException){
            val uri = Uri.parse(AppConfig.GOOGLE_MAPS_STORE_URL)
            val intent = Intent(Intent.ACTION_VIEW,uri)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setEdtEmpty(){
        activityAddressSearchBinding.edtAddress.setOnTouchListener { _, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (activityAddressSearchBinding.edtAddress.right - activityAddressSearchBinding.edtAddress.compoundDrawables[drawableRight].bounds.width())) {
                    activityAddressSearchBinding.edtAddress.setText("")
                }
            }
            false
        }
    }

    private fun hideSoftKeyboard() {
        try {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

//    private fun getLocal() {
//        val task = fusedLocationProviderClient.lastLocation
//        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_CODE)
//            return
//        }
//        task.addOnSuccessListener {
//            if(it != null){
//                val geocoder = Geocoder(this, Locale.getDefault())
//                val addresses = geocoder.getFromLocation(it.latitude,it.longitude,1)
//                yourLocation = addresses!![0].getAddressLine(0)}
//                Toast.makeText(this,"${it.latitude} ${it.longitude}",Toast.LENGTH_LONG).show()
//                //Log.d("aaa","${it.latitude} ${it.longitude} ${addresses!![0].getAddressLine(0)}")
//            }
//        }

}