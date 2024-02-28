package com.example.palisisflow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.logging.log4j.core.appender.RollingFileAppender
import org.apache.logging.log4j.core.config.Configurator
import org.tinylog.Logger
import org.tinylog.configuration.Configuration
import org.tinylog.core.TinylogLoggingProvider
import org.tinylog.writers.RollingFileWriter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.Properties

private const val TAG = "###"
private const val LOCATION_PERMISSION_REQUEST_CODE = 1
private const val REQUEST_ENABLE_BT = 2
private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 123

private var previousFileSize = 0L
private var previousModificationTime = 0L

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var settingBtn: Button
    private lateinit var showApis: Button
    private lateinit var registerBtn: Button
    private lateinit var showPrintersBtn: Button
    private lateinit var showDialogBtn: Button
    private lateinit var showMyListBtn: Button
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var showLoginPage: Button
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissions = arrayOf(
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Log.d("###", "onCreate: coming to isGranted in the permission Launcher")
                configureTinyLog()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.d("###", "onCreate: Coming to else of else")
                    checkExternalStoragePermission {
                        if (isGranted) {
                            // todo granted
                            configureTinyLog()
                        }
                    }
            }
        }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        showLoginPage = binding.goToLoginPage
        configureTinyLog()

//        if (!isPermissionsGranted()) {
//            requestPermissions()
//        } else {
//            // Permissions are already granted, proceed with your app logic
//            //check permission to get device details and ask permission when activity is start
//            try {
//                checkExternalStoragePermission { isGranted ->
//                    if (isGranted) {
//                        configureTinyLog()
//                    }
//                }
//            } catch (ex: Exception) {
//
//            }
//        }

//        when {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                // You can use the API that requires the permission.
//                Log.d("###", "onCreate: Coming to extrenal storage is granted")
//                configureTinyLog()
//
//            }
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
//                Log.d("###", "onCreate: Coming to rationale flow")
//                // In an educational UI, explain to the user why your app requires this
//                // permission for a specific feature to behave as expected, and what
//                // features are disabled if it's declined. In this UI, include a
//                // "cancel" or "no thanks" button that lets the user continue
//                // using your app without granting the permission.
//            }
//            else -> {
//                // You can directly ask for the permission.
//                // The registered ActivityResultCallback gets the result of this request.
//                Log.d("###", "onCreate: Coming to else")
//                requestPermissionLauncher.launch(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            }
//        }

//        val properties = Properties()
//        properties.load(FileInputStream("tinylog.properties"))
//        System.setProperty("tinylog.directory", directoryForLogs.getAbsolutePath());


        val directoryForLogs = getExternalFilesDir(null)
//        System.setProperty("tinylog.directory", directoryForLogs!!.absolutePath)
//        System.setProperty("writer", "rolling file")
//        System.setProperty("tinylog.writer", "rolling file")
//        System.setProperty("tinylog.writer.rollingfile.fileName", "/storage/emulated/0/Android/data/com.palisisag.pitapp/files1/logfile.log");
//        System.setProperty("tinylog.writer.rollingfile.keepFileOpen", "true");
//        System.setProperty("tinylog.writer.rollingfile.policies.size", "100");

        Log.d("###", "onCreate: Coming inside onCreate before logs")
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) =
//                PackageManager.PERMISSION_GRANTED
//        }
//        configureTinyLog()
//        Log.d("###", "onCreate: the path of the file " + directoryForLogs.absolutePath)

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            // Permission granted, proceed with logging
//            configureTinyLog()
//        } else {
//            // Request permission
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION)
//        }

//        System.setProperty("tinylog.directory")
//
//        ThreadContext.

        setContentView(binding.root)

        settingBtn = binding.settingsBtn
        registerBtn = binding.registerBtn
        showPrintersBtn = binding.showPrinters
        showDialogBtn = binding.showDialogBox
        showMyListBtn = binding.showMyList
        showApis = binding.doApiCallsBtn

        binding.registeredName.text = "No user registered"


        settingBtn.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
//            startActivityForResult(intent, 100)
            resultLauncher.launch(intent)
        }

        showApis.setOnClickListener {
            val intent = Intent(this, ProgressBarActivity::class.java)
            startActivity(intent)
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

        showLoginPage.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        showPrintersBtn.setOnClickListener {
//            val popupmenu = PopupMenu(this, showPrintersBtn)
//            popupmenu.menuInflater.inflate(R.menu.pop_up_menu, popupmenu.menu)
//
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "onCreate: Coming inside !granted")
//                ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
//                    LOCATION_PERMISSION_REQUEST_CODE)
//            } else {
//                Log.d(TAG, "onCreate: Coming inside granted else")
//                // Location permission granted flow
//                permissionRequestForLocation.launch(Manifest.permission.BLUETOOTH_CONNECT)
//                permissionRequestForLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                permissionRequestForLocation.launch(Manifest.permission.BLUETOOTH_SCAN)
//            }
//
//            popupmenu.setOnMenuItemClickListener {
//                Log.d("###", "onCreate: menuitem clicked $it")
//                val message = "You have clicked on $it"
////                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//                // Bluetooth Printers || USB Printers
//                if (it.toString() == "Bluetooth Printers") {
//                    Log.d(TAG, "onCreate: Coming to Bluetooth Printers case")
//                    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//                    Log.d(TAG, "onCreate: bluetoothAdapter created")
//                    Log.d(TAG, "onCreate: bluetoothAdapter $bluetoothAdapter")
//                    if(bluetoothAdapter == null) {
//                        Toast.makeText(this, "Your device does not support bluetooth", Toast.LENGTH_SHORT).show()
//                    }
//                    if (bluetoothAdapter?.isEnabled == false) {
//                        Log.d(TAG, "onCreate: COming to adapter is enabled as false")
//                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
////                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
//                        registerForResultFromBluetooth.launch(enableBtIntent)
//                    }
//
//                    else {
//                        Log.d(TAG, "onCreate: Coming to the bluetooth is enabled as true flow")
//                        discoverDevicesUsingBroadcast(bluetoothAdapter)
//                    }
//                }
//                else {
//
//                }
//                return@setOnMenuItemClickListener true
//            }
//
//            popupmenu.show()
//        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (isAllPermissionsGranted(grantResults)) {
                // All permissions are granted, proceed with your app logic
                configureTinyLog()
            } else {
                //check permission to get device details and ask permission when activity is start
                try {
                    checkExternalStoragePermission { isGranted ->
                        if (isGranted) {
                            configureTinyLog()
                        }
                    }
                } catch (ex: Exception) {

                }
            }
        }
    }

    private fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isPermissionsGranted(): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private inline fun checkExternalStoragePermission(isPermissionGranted: (isGranted: Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isPermissionGranted(true)
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity, Manifest.permission.READ_MEDIA_IMAGES
            )
        ) {
            Log.d("###", "checkExternalStoragePermission: Coming to custom dialog launcher")
            CoroutineScope(Dispatchers.Main).launch {
                MaterialAlertDialogBuilder(
                    this@MainActivity
                ).setMessage("Allow this permission")
                    .setCancelable(true)
                    .setPositiveButton("OK") { d: DialogInterface?, w: Int ->
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
            }

        } else {
//            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            Log.d("###", "checkExternalStoragePermission: coming to the last else")
            CoroutineScope(Dispatchers.Main).launch {
                MaterialAlertDialogBuilder(
                    this@MainActivity
                ).setMessage("Allow this permission")
                    .setCancelable(true)
                    .setPositiveButton("OK") { d: DialogInterface?, w: Int ->
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
            }
        }
    }


//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, proceed with logging
//                configureTinyLog()
//            } else {
//                // Permission denied, handle it gracefully
//                Log.d("###", "WRITE_EXTERNAL_STORAGE permission denied")
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureTinyLog() {
        val directoryForLogs = getExternalFilesDir(null)
//        System.setProperty("tinylog.directory", directoryForLogs!!.absolutePath)
//        System.setProperty("tinylog.archiveFormat", "/storage/emulated/0/Android/data/com.palisisag.pitapp/files/archive/{#}.txt")
//        Log.d("###", "configureTinyLog: my directory " + directoryForLogs?.absolutePath)
//        Log.d("###", "configureTinyLog: Coming inside configureTinyLogs")
//        Logger.info("onCreate: Coming inside the onCreate of MainActivity", null)
//        Logger.info("Hello World! first log", null)
//        Logger.info("Hello World! second log", null)
//        Logger.info("Hello World! third log", null)
        for (i in 1..20) {
            Logger.info("Hello World! log inside loop log number $i", null)
        }

        val lastCreatedFile = getLastCreatedFile("/storage/emulated/0/Android/data/com.palisisag.pitapp/files/archive/")
        Log.d("###", "configureTinyLog: lastCreatedFile " + lastCreatedFile)
        moveFileNIO2(lastCreatedFile.toString(), "/storage/emulated/0/Android/data/com.palisisag.pitapp/files/currentLogFile.txt")
        Log.d("###", "configureTinyLog: currTime " + LocalDateTime.now())
    }

    private fun getLastCreatedFile(directoryPath: String): File? {
        val directory = File(directoryPath)
        val files = directory.listFiles() ?: return null

        files.sortWith { file1, file2 ->
            file2.lastModified().compareTo(file1.lastModified())
        }

        return files[0]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun moveFileNIO2(sourcePath: String, destinationPath: String) {
        Log.d("###", "moveFileNIO2: Coming to moveFile")
        val source = Paths.get(sourcePath)
        val target = Paths.get(destinationPath)
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING)
            Log.d("###", "File moved successfully from: $sourcePath to: $destinationPath")
        } catch (e: IOException) {
            Log.w("###", "Failed to move file due to: ${e.message}")
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
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