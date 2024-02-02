package com.example.palisisflow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.palisisag.pitapp.R
import com.palisisag.pitapp.databinding.ActivityMainBinding
import java.security.Permission
import java.util.UUID
import kotlin.math.log

private const val TAG = "###"
private const val LOCATION_PERMISSION_REQUEST_CODE = 1
private const val REQUEST_ENABLE_BT = 2

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var settingBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var showPrintersBtn: Button
    private lateinit var showDialogBtn: Button
    private lateinit var showMyListBtn: Button
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val deviceList = arrayListOf<BluetoothDevice>()

    private val REQ_CODE_FOR_R_AND_BELOW = 1200
    private val REQ_CODE_FOR_S_AND_ABOVE = 1201
    private val REQUIRED_PERMISSIONS_FOR_R_AND_BELOW = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @RequiresApi(api = Build.VERSION_CODES.S)
    private val REQUIRED_PERMISSIONS_FOR_S_AND_ABOVE = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //permission launcher to read phone state for get device config details
    private val permissionRequestForLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d(TAG, "isGranted value : $isGranted")
            if (isGranted) {
                Log.d(TAG, ": Coming to isGranted flow")
            } else {
                TODO("To implement this case")
            }
        }

    private val permissionRequestForBluetooth =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d(TAG, "isGranted value for enable bluetooth request: $isGranted")
            if (isGranted) {
                Log.d(TAG, ": Coming to isGranted flow for bluetooth")
            } else {
                TODO("To implement this case")
            }
        }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)

        settingBtn = binding.settingsBtn
        registerBtn = binding.registerBtn
        showPrintersBtn = binding.showPrinters
        showDialogBtn = binding.showDialogBox
        showMyListBtn = binding.showMyList

        binding.registeredName.text = "No user registered"


        settingBtn.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
//            startActivityForResult(intent, 100)
            resultLauncher.launch(intent)
        }

        showMyListBtn.setOnClickListener {
            val intent = Intent(this, MyListActivity::class.java)
            startActivity(intent)
        }

        registerBtn.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
//            startActivityForResult(intent, 100)
            resultLauncher.launch(intent)
        }

        val typesOfPrinters = listOf("Bluetooth", "Usb")

//        showPrintersBtn.setOnClickListener {
//            val alertDialogBuilder = AlertDialog.Builder(this)
//            alertDialogBuilder.setTitle("Please select the type of printers you want to use")
////            alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert)
//            alertDialogBuilder.setItems(typesOfPrinters) { dialog, which ->
//                val selectedItem = typesOfPrinters[which]
//                Toast.makeText(this, "Selected item: $selectedItem", Toast.LENGTH_SHORT).show()
//            }
//
//        }

        showDialogBtn.setOnClickListener {
            val devicesList = ArrayList<String> ()
            devicesList.add("device1")
            devicesList.add("device2")
            devicesList.add("device3")
            val listDialog = object : DialogList(
                this@MainActivity,
                devicesList
            ) {
            }

            listDialog.show()
        }

        showPrintersBtn.setOnClickListener {
            val popupmenu = PopupMenu(this, showPrintersBtn)
            popupmenu.menuInflater.inflate(R.menu.pop_up_menu, popupmenu.menu)

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onCreate: Coming inside !granted")
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                    LOCATION_PERMISSION_REQUEST_CODE)
            } else {
                Log.d(TAG, "onCreate: Coming inside granted else")
                // Location permission granted flow
                permissionRequestForLocation.launch(Manifest.permission.BLUETOOTH_CONNECT)
                permissionRequestForLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                permissionRequestForLocation.launch(Manifest.permission.BLUETOOTH_SCAN)
            }

            popupmenu.setOnMenuItemClickListener {
                Log.d("###", "onCreate: menuitem clicked $it")
                val message = "You have clicked on $it"
//                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                // Bluetooth Printers || USB Printers
                if (it.toString() == "Bluetooth Printers") {
                    Log.d(TAG, "onCreate: Coming to Bluetooth Printers case")
                    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    Log.d(TAG, "onCreate: bluetoothAdapter created")
                    Log.d(TAG, "onCreate: bluetoothAdapter $bluetoothAdapter")
                    if(bluetoothAdapter == null) {
                        Toast.makeText(this, "Your device does not support bluetooth", Toast.LENGTH_SHORT).show()
                    }
                    if (bluetoothAdapter?.isEnabled == false) {
                        Log.d(TAG, "onCreate: COming to adapter is enabled as false")
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                        registerForResultFromBluetooth.launch(enableBtIntent)
                    }

                    else {
                        Log.d(TAG, "onCreate: Coming to the bluetooth is enabled as true flow")
                        discoverDevicesUsingBroadcast(bluetoothAdapter)
                    }
                }
                else {

                }
                return@setOnMenuItemClickListener true
            }

            popupmenu.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun discoverDevicesUsingBroadcast(bluetoothAdapter: BluetoothAdapter) {
        Log.d(TAG, "discoverDevicesUsingBroadcast: Coming ot discover devices and show method")
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.devices_available_activity, null)

        val recyclerView = bottomSheetView.findViewById<RecyclerView>(R.id.devicesList)
//        val devicesAdapter = DevicesAdapter(deviceList)
//        recyclerView.adapter = devicesAdapter
//        deviceList.add("Sample Device")
//        devicesAdapter.notifyItemInserted(deviceList.lastIndex)
        Log.d(TAG, "discoverDevicesUsingBroadcast: Coming before broadCastReceiver initilialization")
        Log.d(TAG, "discoverDevicesUsingBroadcast: permissions val " + (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED)
        )

        Log.d(TAG, "discoverDevicesUsingBroadcast: permissions val " + (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
        )
        discoverDevices()

//        Log.d(TAG, "discoverDevicesUsingBroadcast: Coming before startDiscovery")
//        Log.d(TAG, "discoverDevicesUsingBroadcast: is BlueToothAdapter Enabled " + bluetoothAdapter.isEnabled)
//
//        Log.d(TAG, "discoverDevicesUsingBroadcast: discovery Started " + bluetoothAdapter.startDiscovery())
//
//        registerReceiver(broadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
//        registerReceiver(broadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
//
//        bottomSheetDialog.setContentView(bottomSheetView)
//        bottomSheetDialog.show()
//        println(deviceList)

    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        //            @SuppressLint("MissingPermission", "NotifyDataSetChanged")
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive: Coming to onReceive of broadCast Receiver")
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    Log.d(TAG, "onReceive: Coming inside action found")
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    var isAnyFound = false
                    for (i in deviceList.indices) {
                        if (deviceList[i].address
                                .trim { it <= ' ' } == device!!.address.trim { it <= ' ' }
                        ) {
                            isAnyFound = true
                        }
                    }
                    if (!isAnyFound) {
                        device?.let { deviceList.add(it) }
                    }
                    Log.d("###", "onReceive: ${device?.name}")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d(TAG, "onReceive: Coming to BlueToothActionFinished")
                    discoverDevices()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun discoverDevices() {
        Log.d(TAG, "discoverDevices: Coming to the discoverDevices")
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Log.d(TAG, "discoverDevices: Coming to Build.VERSION.SDK_INT < Build.VERSION_CODES.S")
            registerReceiver(broadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            })
            bluetoothAdapter.startDiscovery()
        } else {
            Log.d(TAG, "discoverDevices: Coming to else of the build version")
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "discoverDevices: Coming to permissions as true in else of build codes")
                registerReceiver(broadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
                    addAction(BluetoothDevice.ACTION_FOUND)
                    addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                })
                Log.d(TAG,
                    "discoverDevices: bluetooth Adapter value before startDiscover in else of build codes $bluetoothAdapter"
                )
                bluetoothAdapter.startDiscovery()
            } else {
                Log.d(TAG, "discoverDevices: Coming before checkBlueToothPermissions in the sle of discoverServices()")
//                checkForBlueToothPermissions()
            }
        }
    }

//    @SuppressLint("InflateParams", "MissingPermission")
//    private fun discoverAndShowDevices(bluetoothAdapter: BluetoothAdapter) {
//        val bottomSheetDialog = BottomSheetDialog(this)
//        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.devices_available_activity, null)
//
//        val recyclerView = bottomSheetView.findViewById<RecyclerView>(R.id.devicesList)
////        val devicesAdapter = DevicesAdapter(deviceList)
////        recyclerView.adapter = devicesAdapter
//
//        val scanFilters = listOf(
//            ScanFilter.Builder().setDeviceName("MyDevice").build())// Filter by device name
////            ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))).build() // Filter by service UUID
//
//        val scanSettings = ScanSettings.Builder()
//            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(500) // Adjust scan mode as needed
//            .build()
//
//        val scanCallback = object : ScanCallback() {
//            override fun onScanResult(callbackType: Int, result: ScanResult) {
//                val device = result.device
//                if (device != null) {
//                    deviceList.add(device.name)
//                    devicesAdapter.notifyItemInserted(deviceList.lastIndex)
//                    devicesAdapter.notifyDataSetChanged()
//                }
//            }
//        }
//
//        // Start scanning for Bluetooth devices
//        bluetoothAdapter.bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)
//
//        bottomSheetDialog.setContentView(bottomSheetView)
//        bottomSheetDialog.show()
//    }

    private val registerForResultFromBluetooth = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Coming inside ok from bluetooth permissions:")
            val intent = result.data
            // Handle the Intent
        }
    }

//    @Deprecated(message= "This method is deprecated")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        Log.d(TAG, "onActivityResult: coming inside onActivityResult")
//        Log.d(TAG, "onActivityResult: requestCode $requestCode")
//        Log.d(TAG, "onActivityResult: resultCode $resultCode")
//
//        if(requestCode == 100) {
//
//            Log.d(TAG, "onActivityResult: Coming inside both req and resultCode")
//
//            if(data?.getStringExtra("name") != null) {
//                Log.d(TAG, "onCreate: Coming inside currOperator != null")
//                binding.registeredName.text = data.getStringExtra("name")
//            }
//            else {
//                binding.registeredName.text = "No Operator Assigned"
//            }
//        }
//    }

    // Result code for registering from register button - 101
    // Result code for registering from settings button - 100

    @SuppressLint("SetTextI18n")
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        Log.d(TAG, "coming inside the resultLauncher in MainActivity: ")
        Log.d(TAG, "resultCode in the mainActivity : ${it.resultCode}")
        if (it.data != null) {
            val data = it.data
            Log.d(TAG, "successCode received in the mainActivity: " + it.resultCode)
            if(it.resultCode == 100) {
                binding.registeredName.text = data?.getStringExtra("name") + " registered using the settings flow"
            }
            else {
                binding.registeredName.text = data?.getStringExtra("name") + " registered using the register flow"

            }

        }
    }

//    private fun checkForBlueToothPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            for (i in REQUIRED_PERMISSIONS_FOR_S_AND_ABOVE.indices) {
//                if (ContextCompat.checkSelfPermission(
//                        this@MainActivity,
//                        REQUIRED_PERMISSIONS_FOR_S_AND_ABOVE[i]
//                    )
//                    == PackageManager.PERMISSION_GRANTED
//                ) {
//                    showAlert = false
//                } else {
//                    ActivityCompat.requestPermissions(
//                        this@MainActivity,
//                        REQUIRED_PERMISSIONS_FOR_S_AND_ABOVE,
//                        REQ_CODE_FOR_S_AND_ABOVE
//                    )
//                }
//            }
//        } else {
//            var showAlert = false
//            for (i in REQUIRED_PERMISSIONS_FOR_R_AND_BELOW.indices) {
//                if (ContextCompat.checkSelfPermission(
//                        this@MainActivity,
//                        REQUIRED_PERMISSIONS_FOR_R_AND_BELOW[i]
//                    )
//                    == PackageManager.PERMISSION_GRANTED
//                ) {
//                    showAlert = false
//                } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        this@MainActivity,
//                        REQUIRED_PERMISSIONS_FOR_R_AND_BELOW[i]
//                    )
//                ) {
//                    showAlert = true
//                } else {
//                    ActivityCompat.requestPermissions(
//                        this@SettingActivity,
//                        REQUIRED_PERMISSIONS_FOR_R_AND_BELOW,
//                        REQ_CODE_FOR_R_AND_BELOW
//                    )
//                    showAlert = false
//                }
//            }
//        }
//    }



//    @Deprecated("Deprecated in Java")
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_ENABLE_BT && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            enableBluetooth()
//        } else {
//            // Handle permission denial (e.g., display a message)
//        }
//    }


//    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Location permission granted. Use the location data as needed.
//            } else {
//                // Permission denied. Handle the denial as needed.
//            }
//        }
//    }
}