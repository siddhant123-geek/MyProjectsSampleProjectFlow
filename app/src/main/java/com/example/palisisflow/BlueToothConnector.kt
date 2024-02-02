//package com.example.palisisflow
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothClass
//import android.bluetooth.BluetoothDevice
//import android.bluetooth.BluetoothManager
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.IntentFilter
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.os.Handler
//import android.provider.Settings
//import android.util.Log
//import android.view.KeyEvent
//import android.view.View
//import android.widget.Toast
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.ActivityResultCallback
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.RequiresApi
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.registerReceiver
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import com.google.android.material.snackbar.Snackbar
//import com.palisisag.pitapp.PalisisInstance
//import com.palisisag.pitapp.R
//import com.palisisag.pitapp.api.BaseViewModelFactory
//import com.palisisag.pitapp.api.ErrorCallback
//import com.palisisag.pitapp.api.RetrofitBuilder
//import com.palisisag.pitapp.base.BaseActivity
//import com.palisisag.pitapp.databinding.ActivitySettingActivityBinding
//import com.palisisag.pitapp.modules.deviceRegistration.ui.DeviceRegistrationActivity
//import com.palisisag.pitapp.modules.operatorScreen.adapters.ScannerDialogList
//import com.palisisag.pitapp.modules.operatorScreen.models.ScannerListData
//import com.palisisag.pitapp.modules.setting.adapters.ScannerListAdapter
//import com.palisisag.pitapp.modules.setting.models.ScannerModelEnum
//import com.palisisag.pitapp.modules.setting.models.UploadLogCallbackRequest
//import com.palisisag.pitapp.modules.setting.repository.SettingRepository
//import com.palisisag.pitapp.modules.setting.viewModel.SettingViewModel
//import com.palisisag.pitapp.modules.startTicketing.models.DeviceRegistrationResponse
//import com.palisisag.pitapp.palisisUtils.CommonUtils
//import com.palisisag.pitapp.palisisUtils.Constants
//import com.palisisag.pitapp.palisisUtils.DeviceHelper
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import java.io.File
//
//class SettingActivity : BaseActivity() {
//    private val binding: ActivitySettingActivityBinding by lazy {
//        ActivitySettingActivityBinding.inflate(layoutInflater)
//    }
//    private val TAG = PalisisInstance.appContext.getString(R.string.settingactivity)
//    private val REQUEST_CODE_STORAGE_PERMISSION = 100
//    private lateinit var repo: SettingRepository
//    private lateinit var viewmodel: SettingViewModel
//    private val permissionRequestForReadMediaAndFiles =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                // PERMISSION GRANTED
//                PalisisInstance.NLOG.Debug(
//                    TAG,
//                    "READ_MEDIA_IMAGES ---> Granted",
//                    Throwable().stackTrace[0].lineNumber.toString()
//                )
//                // todo granted
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (!Environment.isExternalStorageManager()) {
//                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                        intent.data = Uri.fromParts("package", applicationContext.packageName, null)
//                        startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION)
//                    } else {
//                        // Permission granted, proceed with file operations
//                        PalisisInstance.NLOG.Debug(
//                            TAG, "Start Logging", Throwable().stackTrace[0].lineNumber.toString()
//                        )
//                    }
//                } else {
//                    // Permission not needed for lower API levels, proceed directly
//                    PalisisInstance.NLOG.Debug(
//                        TAG, "Start Logging", Throwable().stackTrace[0].lineNumber.toString()
//                    )
//                }
//
//
//                //Toast.makeText(this, "${storeExcelInStorage()}", Toast.LENGTH_SHORT).show()
//            } else {
//                // PERMISSION NOT GRANTED
//                Log.d("TAG", ": READ_MEDIA_IMAGES ---> Not Granted")
//                if (Build.VERSION.SDK_INT < 33) {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(
//                            this,
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                        )
//                    ) {
//                        checkForReadExternalStoragePermission { isGranted ->
//                            if (isGranted) {
//                                PalisisInstance.NLOG.Info(
//                                    TAG,
//                                    "Start Logging",
//                                    Throwable().stackTrace[0].lineNumber.toString()
//                                )
//                                //   startLogging()
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                                    if (!Environment.isExternalStorageManager()) {
//                                        val intent =
//                                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                                        intent.data = Uri.fromParts(
//                                            "package", applicationContext.packageName, null
//                                        )
//                                        startActivityForResult(
//                                            intent, REQUEST_CODE_STORAGE_PERMISSION
//                                        )
//                                    } else {
//                                        // Permission granted, proceed with file operations
//                                        PalisisInstance.NLOG.Debug(
//                                            TAG,
//                                            "Start Logging",
//                                            Throwable().stackTrace[0].lineNumber.toString()
//                                        )
//                                    }
//                                } else {
//                                    PalisisInstance.NLOG.Debug(
//                                        TAG,
//                                        "Start Logging",
//                                        Throwable().stackTrace[0].lineNumber.toString()
//                                    )
//                                }
//                            }
//                        }
//                    } else {
//                        CommonUtils.openPhoneSettings(
//                            this, getString(R.string.setting_permission_storage_message)
//                        )
//
//
//                    }
//                } else {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(
//                            this, Manifest.permission.READ_MEDIA_IMAGES
//                        )
//                    ) {
//                        checkForReadExternalStoragePermission { isGranted ->
//                            if (isGranted) {
//                                PalisisInstance.NLOG.Info(
//                                    TAG,
//                                    "Start Logging",
//                                    Throwable().stackTrace[0].lineNumber.toString()
//                                )
//                                //   startLogging()
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                                    if (!Environment.isExternalStorageManager()) {
//                                        val intent =
//                                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                                        intent.data = Uri.fromParts(
//                                            "package", applicationContext.packageName, null
//                                        )
//                                        startActivityForResult(
//                                            intent, REQUEST_CODE_STORAGE_PERMISSION
//                                        )
//                                    } else {
//                                        // Permission granted, proceed with file operations
//                                        PalisisInstance.NLOG.Debug(
//                                            TAG,
//                                            "Start Logging",
//                                            Throwable().stackTrace[0].lineNumber.toString()
//                                        )
//                                    }
//                                } else {
//                                    // Permission not needed for lower API levels, proceed directly
//                                    PalisisInstance.NLOG.Debug(
//                                        TAG,
//                                        "Start Logging",
//                                        Throwable().stackTrace[0].lineNumber.toString()
//                                    )
//                                }
//                            }
//                        }
//                    } else {
//                        CommonUtils.openPhoneSettings(
//                            this, getString(R.string.setting_permission_storage_message)
//                        )
//
//
//                    }
//                }
//            }
//        }
//    var xAmzSecurityToken: String = ""
//    var xAmzAlgorithm: String = ""
//    var xAmzDate: String = ""
//    var xAmzSignedHeaders: String = ""
//    var xAmzExpires: String = ""
//    var xAmzCredential: String = ""
//    var xAmzSignature: String = ""
//    private val registrationData =
//        registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) {
//            it?.let {
//                if (it.resultCode == Activity.RESULT_OK) {
//                    val data = it.data
//                    val qrData =
//                        data?.getParcelableExtra<DeviceRegistrationResponse>(Constants.REQUEST_KEY_REGISTRATION_DATA)
//                    // Do something with the QR data if needed
//                    val resultIntent = Intent()
//                    resultIntent.putExtra(Constants.REQUEST_KEY_REGISTRATION_DATA, qrData)
//                    setResult(Activity.RESULT_OK, resultIntent)
//                    finish()
//                }
//            }
//        }
//
//    private lateinit var targetDictionary: ArrayList<ScannerListData>
//    private lateinit var scannerListAdapter: ScannerListAdapter
//    private val REQ_CODE_FOR_R_AND_BELOW = 1200
//    private val REQ_CODE_FOR_S_AND_ABOVE = 1201
//    private val REQUIRED_PERMISSIONS_FOR_R_AND_BELOW = arrayOf(
//        Manifest.permission.BLUETOOTH,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    )
//
//    @RequiresApi(api = Build.VERSION_CODES.S)
//    private val REQUIRED_PERMISSIONS_FOR_S_AND_ABOVE = arrayOf(
//        Manifest.permission.BLUETOOTH,
//        Manifest.permission.BLUETOOTH_SCAN,
//        Manifest.permission.BLUETOOTH_CONNECT,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION
//    )
//
//
//    var devices = ArrayList<BluetoothDevice>()
//
//    private lateinit var bluetoothAdapter: BluetoothAdapter
//
//    @SuppressLint("MissingPermission")
//    private val bluetoothResult =
//        registerForActivityResult<Intent, ActivityResult>(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result != null) {
//                if (result.resultCode == RESULT_OK) {
//                    if (bluetoothAdapter != null) {
//                        val pairedDevices = bluetoothAdapter.bondedDevices
//                        if (pairedDevices.size > 0) {
//                            devices.clear()
//                            for (device in pairedDevices) {
//                                devices.add(device)
//                                val deviceName = device.name
//                                val deviceHardwareAddress = device.address // MAC address
//                                Log.d(
//                                    "PairedDevices",
//                                    "loadPairedBluetoothPrinters: $deviceName $deviceHardwareAddress"
//                                )
//                            }
//                        }
//                    } else {
//                        bluetoothNotSupported()
//                    }
//                }
//            }
//        }
//
//    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
//        @SuppressLint("MissingPermission")
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//            if (BluetoothDevice.ACTION_FOUND == action) {
//                // Discovery has found a device. Get the BluetoothDevice
//                // object and its info from the Intent.
//                val device =
//                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                var isAnyFound = false
//                for (i in devices.indices) {
//                    if (devices.get(i).getAddress()
//                            .trim { it <= ' ' } == device!!.address.trim { it <= ' ' }
//                    ) {
//                        isAnyFound = true
//                    }
//                }
//                if (!isAnyFound) {
//                    device?.let { devices.add(it) }
//                }
//                Log.d("BlueTooth", "onReceive: ${device?.name}")
//                showToast("${device?.name}")
//
//            } else if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
//                discoverDevices()
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//        targetDictionary = ArrayList()
//        targetDictionary.add(ScannerListData("Others", ScannerModelEnum.STANDARD))
//        targetDictionary.add(ScannerListData("Adyen", ScannerModelEnum.S1F2L))
//        targetDictionary.add(ScannerListData("Zebra", ScannerModelEnum.ZEBRA))
//        targetDictionary.add(ScannerListData("Famoco", ScannerModelEnum.FAMOCO))
//        scannerListAdapter = ScannerListAdapter()
//        val token = CommonUtils.getAuthentication()
//        PalisisInstance.NLOG.Info(TAG, "Token $token")
//        token.let {
//            val retrofitBuilder = RetrofitBuilder.getRetrofit(
//            )
//            repo = SettingRepository(retrofitBuilder)
//        }
//        val factory = BaseViewModelFactory(SettingViewModel::class.java, repo)
//        viewmodel = ViewModelProvider(this, factory)[SettingViewModel::class.java]
//
//        binding.fltStartScanning.setOnClickListener {
//            registrationData.launch(Intent(this, DeviceRegistrationActivity::class.java))
//        }
//
//        binding.fltStartUpload.setOnClickListener {
//            PalisisInstance.NLOG.Info(
//                TAG, message = "Start Uploading", Throwable().stackTrace[0].lineNumber.toString()
//            )
//            checkForReadExternalStoragePermission { isGranted ->
//                if (isGranted) {
//                    PalisisInstance.NLOG.Info(
//                        TAG, "Start Logging", Throwable().stackTrace[0].lineNumber.toString()
//                    )
//                    //   startLogging()
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        if (!Environment.isExternalStorageManager()) {
//                            val intent =
//                                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                            intent.data =
//                                Uri.fromParts("package", applicationContext.packageName, null)
//                            startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION)
//                        } else {
//                            // Permission granted, proceed with file operations
//                            PalisisInstance.NLOG.Debug(
//                                TAG,
//                                "Start Logging",
//                                Throwable().stackTrace[0].lineNumber.toString()
//                            )
//                        }
//                    } else {
//                        // Permission not needed for lower API levels, proceed directly
//                        PalisisInstance.NLOG.Debug(
//                            TAG, "Start Logging", Throwable().stackTrace[0].lineNumber.toString()
//                        )
//                    }
//                }
//            }
//        }
//
//        binding.ivBack.setOnClickListener {
//            onBackPressed()
//        }
//        binding.btnConfScanner.setOnClickListener {
//            selectScanner()
//        }
//        binding.btnConfPrinter.setOnClickListener {
//            loadPairedBluetoothPrinters()
//
//        }
//    }
//
//
//    private fun selectScanner() {
//        try {
//            val listDialog = object : ScannerDialogList(
//                this@SettingActivity,
//                targetDictionary, { target ->
//                    try {
//                        when (target.value) {
//
//                            ScannerModelEnum.STANDARD -> {
//                                PalisisInstance.sharedPreferences.saveData(
//                                    Constants.pit_Scanner_deviceType,
//                                    ScannerModelEnum.STANDARD.name
//                                )
//                            }
//
//                            else -> {
//                                PalisisInstance.sharedPreferences.saveData(
//                                    Constants.pit_Scanner_deviceType,
//                                    target.name
//                                )
//
//                            }
//                        }
//                    } catch (_: Exception) {
//
//                    } finally {
//                        // PalisisInstance.Instance.Scanner = null
//                        DeviceHelper().setDeviceAndPrinter()
//                    }
//                }
//            ) {
//
//            }
//            listDialog.show()
//        } catch (ex: Exception) {
//            PalisisInstance.NLOG.ERROR(
//                TAG,
//                "ScannerConfiguration_Clicked|${ex.message}",
//                lineNumber = Throwable().stackTrace[0].lineNumber.toString()
//            )
//
//        }
//    }
//
//    private fun uploadFile(fileName: String, filePath: File) {
//        val mainLogFilePath = "/data/data/com.PalisisAG.PalisisApp/files/logs/logfile.log"
//        //  val archiveBasePath = "/data/data/com.PalisisAG.PalisisApp/files/logs/archive/"
//        val archiveBasePath = getExternalFilesDir("logs/archive")?.path
//        val archiveDir = archiveBasePath?.let { File(it) }
//        val archivedLogFiles = archiveDir?.list() // Get a list of archive file names
//
//        lifecycleScope.launch {
//            if (archiveDir != null) {
//                viewmodel.uploadLogFile(archiveDir.name).observe(this@SettingActivity) {
//                    it.getValueOrNull()?.let { response ->
//                        if (response.isSuccessful) {
//                            response.body()?.let {
//                                PalisisInstance.NLOG.Info(
//                                    TAG,
//                                    "URL:${it.url}",
//                                    lineNumber = Throwable().stackTrace[0].lineNumber.toString()
//                                )
//                                it.url?.let { URL ->
//                                    val baseURL =
//                                        "https://pitteststorage.s3.eu-west-1.amazonaws.com/"
//                                    val retrofitBuilder = RetrofitBuilder.getRetrofit(
//                                        baseURL
//                                    )
//                                    repo = SettingRepository(retrofitBuilder)
//                                    viewmodel = SettingViewModel(repo)
//                                    val uri = Uri.parse(URL)
//                                    val queryParameters = uri.queryParameterNames
//                                    xAmzSecurityToken =
//                                        uri.getQueryParameter("X-Amz-Security-Token").toString()
//                                    xAmzAlgorithm =
//                                        uri.getQueryParameter("X-Amz-Algorithm").toString()
//                                    xAmzDate = uri.getQueryParameter("X-Amz-Date").toString()
//                                    xAmzSignedHeaders =
//                                        uri.getQueryParameter("X-Amz-SignedHeaders").toString()
//                                    xAmzExpires = uri.getQueryParameter("X-Amz-Expires").toString()
//                                    xAmzCredential =
//                                        uri.getQueryParameter("X-Amz-Credential").toString()
//                                    xAmzSignature =
//                                        uri.getQueryParameter("X-Amz-Signature").toString()
//
//
//                                    PalisisInstance.NLOG.Info(
//                                        TAG, "AWS INFO:$xAmzSecurityToken ,$xAmzDate"
//                                    )
//
//                                    val fileData = archiveDir.path.toByteArray()
//                                    PalisisInstance.NLOG.Info(TAG, "Byte Array:$fileData")
//                                    val endPoint = filePath.name
//                                    BaseActivity().showToast(endPoint)
//                                    val request = UploadLogCallbackRequest(
//                                        xAmzSecurityToken,
//                                        xAmzAlgorithm,
//                                        xAmzDate,
//                                        xAmzSignedHeaders,
//                                        xAmzExpires,
//                                        xAmzCredential,
//                                        xAmzSignature
//                                    )
//                                    uploadLogCallback(endPoint, request, fileData)
//                                }
//
//                            }
//                        }
//                    }
//
//                    it.getErrorIfExist()?.let {
//                        val error =
//                            ErrorCallback().handleApiError(this@SettingActivity, it)
//                        PalisisInstance.NLOG.ERROR(
//                            TAG,
//                            "registerDevice Error: ${error}",
//                            lineNumber = Throwable().stackTrace[0].lineNumber.toString()
//                        )
//                        showToast(error)
//                        hideLoading()
//                    }
//                    if (it.isLoading) {
//                        BaseActivity().showLoading(true)
//                    } else {
//                        BaseActivity().hideLoading()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun uploadLogCallback(
//        endPoint: String, request: UploadLogCallbackRequest?, fileData: ByteArray
//    ) {
//        lifecycleScope.launch {
//            request?.let {
//                viewmodel.uploadLogFileByUrl(
//                    endPoint,
//                    "application/zip",
//                    request.xAmzSecurityToken,
//                    request.xAmzAlgorithm,
//                    request.xAmzDate,
//                    request.xAmzSignedHeaders,
//                    request.xAmzExpires,
//                    request.xAmzCredential,
//                    request.xAmzSignature,
//                    fileData
//                ).observe(this@SettingActivity) {
//                    it.getValueOrNull()?.let { myResponse ->
//                        if (myResponse.isSuccessful) {
//                            myResponse.body()?.let {
//                                PalisisInstance.NLOG.Info(
//                                    TAG,
//                                    "URL:${it.success}",
//                                    lineNumber = Throwable().stackTrace[0].lineNumber.toString()
//                                )
//                            }
//                        }
//                    }
//                    it.getErrorIfExist()?.let {
//                        val error =
//                            ErrorCallback().handleApiError(this@SettingActivity, it)
//                        PalisisInstance.NLOG.ERROR(
//                            TAG,
//                            "registerDevice Error: ${error}",
//                            lineNumber = Throwable().stackTrace[0].lineNumber.toString()
//                        )
//                        showToast(error)
//                        hideLoading()
//                    }
//                    if (it.isLoading) {
//                        BaseActivity().showLoading(true)
//                    } else {
//                        BaseActivity().hideLoading()
//                    }
//                }
//            }
//        }
//    }
//
//
//    private fun checkForReadExternalStoragePermission(isPermissionGranted: (isGranted: Boolean) -> Unit) {
//        if (Build.VERSION.SDK_INT < 33) {
//            if (ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.READ_EXTERNAL_STORAGE
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                isPermissionGranted(true)
//            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this, Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//            ) {
//                MaterialAlertDialogBuilder(
//                    this
//                ).setMessage(getString(R.string.permission_storage_message)).setCancelable(false)
//                    .setPositiveButton(getString(R.string.okay)) { d: DialogInterface?, w: Int ->
//                        permissionRequestForReadMediaAndFiles.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//                    }
//                    .setNegativeButton(getString(R.string.cancel)) { d: DialogInterface, w: Int -> d.dismiss() }
//                    .show()
//            } else {
//                permissionRequestForReadMediaAndFiles.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
//        } else {
//            if (ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.READ_MEDIA_IMAGES
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                isPermissionGranted(true)
//            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this, Manifest.permission.READ_MEDIA_IMAGES
//                )
//            ) {
//
//                MaterialAlertDialogBuilder(
//                    this
//                ).setMessage(getString(R.string.permission_storage_message)).setCancelable(false)
//                    .setPositiveButton(getString(R.string.okay)) { d: DialogInterface?, w: Int ->
//                        permissionRequestForReadMediaAndFiles.launch(Manifest.permission.READ_MEDIA_IMAGES)
//                    }
//                    .setNegativeButton(getString(R.string.cancel)) { d: DialogInterface, w: Int -> d.dismiss() }
//                    .show()
//            } else {
//                permissionRequestForReadMediaAndFiles.launch(Manifest.permission.READ_MEDIA_IMAGES)
//            }
//        }
//    }
//
//    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
//        if (event?.keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
//            currentFocus?.clearFocus()
//        }
//        return false
//    }
//
//
//    private fun loadPairedBluetoothPrinters() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//            if (ActivityCompat.checkSelfPermission(
//                    this@SettingActivity,
//                    Manifest.permission.BLUETOOTH
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//                if (bluetoothAdapter == null) {
//                    bluetoothNotSupported()
//                    return
//                }
//                if (!bluetoothAdapter.isEnabled) {
//                    askForBluetoothConnection()
//                } else {
//                    val pairedDevices = bluetoothAdapter.bondedDevices
//                    if (pairedDevices.size > 0) {
//                        // There are paired devices. Get the name and address of each paired device.
//                        devices.clear()
//                        for (device in pairedDevices) {
//                            devices.add(device)
//                            val deviceName = device.name
//                            val deviceHardwareAddress = device.address // MAC address
//                            Log.d(
//                                "PairedDevices",
//                                "loadPairedBluetoothPrinters: $deviceName $deviceHardwareAddress"
//                            )
//                        }
//                    }
//                    discoverDevices()
//                }
//            }
//        } else {
//            if (ActivityCompat.checkSelfPermission(
//                    this@SettingActivity,
//                    Manifest.permission.BLUETOOTH_CONNECT
//                ) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(
//                    this@SettingActivity,
//                    Manifest.permission.BLUETOOTH_SCAN
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                val manager =
//                    this@SettingActivity.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
//                bluetoothAdapter = manager.adapter
//                if (bluetoothAdapter == null) {
//                    bluetoothNotSupported()
//                    return
//                }
//                if (!bluetoothAdapter.isEnabled) {
//                    askForBluetoothConnection()
//                } else {
//                    val pairedDevices = bluetoothAdapter.bondedDevices
//                    if (pairedDevices.size > 0) {
//                        // There are paired devices. Get the name and address of each paired device.
//                        devices.clear()
//                        for (device in pairedDevices) {
//                            devices.add(device)
//                            val deviceName = device.name
//                            val deviceHardwareAddress = device.address // MAC address
//                            Log.d(
//                                "PairedDevices",
//                                "loadPairedBluetoothPrinters: $deviceName $deviceHardwareAddress"
//                            )
//                        }
//                    }
//                    discoverDevices()
//                }
//            } else {
//                checkForBlueToothPermissions()
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun discoverDevices() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//            registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
//                addAction(BluetoothDevice.ACTION_FOUND)
//                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
//            })
//            bluetoothAdapter.startDiscovery()
//        } else {
//            if (ActivityCompat.checkSelfPermission(
//                    this@SettingActivity,
//                    Manifest.permission.BLUETOOTH_SCAN
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
//                    addAction(BluetoothDevice.ACTION_FOUND)
//                    addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
//                })
//                bluetoothAdapter.startDiscovery()
//            } else {
//                checkForBlueToothPermissions()
//            }
//        }
//    }
//
//    private fun checkForBlueToothPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            var showAlert = false
//            for (i in REQUIRED_PERMISSIONS_FOR_S_AND_ABOVE.indices) {
//                if (ContextCompat.checkSelfPermission(
//                        this@SettingActivity,
//                        REQUIRED_PERMISSIONS_FOR_S_AND_ABOVE[i]
//                    )
//                    == PackageManager.PERMISSION_GRANTED
//                ) {
//                    showAlert = false
//                } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        this@SettingActivity,
//                        REQUIRED_PERMISSIONS_FOR_S_AND_ABOVE[i]
//                    )
//                ) {
//                    showAlert = false
//                } else {
//                    ActivityCompat.requestPermissions(
//                        this@SettingActivity,
//                        REQUIRED_PERMISSIONS_FOR_S_AND_ABOVE,
//                        REQ_CODE_FOR_S_AND_ABOVE
//                    )
//                    showAlert = false
//                }
//            }
//            if (showAlert) {
//                MaterialAlertDialogBuilder(
//                    this@SettingActivity,
//                    R.style.CustomAlertDialog
//                )
//                    .setTitle("Permissions required")
//                    .setMessage("In order to connect with bluetooth printer we require these permissions.")
//                    .setNegativeButton(
//                        "No"
//                    ) { dialog, which -> dialog.dismiss() }
//                    .setPositiveButton(
//                        "Yes"
//                    ) { dialog, which ->
//                        ActivityCompat.requestPermissions(
//                            this@SettingActivity,
//                            REQUIRED_PERMISSIONS_FOR_S_AND_ABOVE,
//                            REQ_CODE_FOR_S_AND_ABOVE
//                        )
//                    }.show()
//            }
//        } else {
//            var showAlert = false
//            for (i in REQUIRED_PERMISSIONS_FOR_R_AND_BELOW.indices) {
//                if (ContextCompat.checkSelfPermission(
//                        this@SettingActivity,
//                        REQUIRED_PERMISSIONS_FOR_R_AND_BELOW[i]
//                    )
//                    == PackageManager.PERMISSION_GRANTED
//                ) {
//                    showAlert = false
//                } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        this@SettingActivity,
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
//            if (showAlert) {
//                MaterialAlertDialogBuilder(
//                    this@SettingActivity,
//                    R.style.CustomAlertDialog
//                )
//                    .setTitle("Permissions required")
//                    .setMessage("In order to connect with bluetooth printer we require these permissions.")
//                    .setNegativeButton(
//                        "No"
//                    ) { dialog, which -> dialog.dismiss() }
//                    .setPositiveButton(
//                        "Yes"
//                    ) { dialog, which ->
//                        ActivityCompat.requestPermissions(
//                            this@SettingActivity,
//                            REQUIRED_PERMISSIONS_FOR_R_AND_BELOW,
//                            REQ_CODE_FOR_R_AND_BELOW
//                        )
//                    }.show()
//            }
//        }
//    }
//
//    private fun askForBluetoothConnection() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            bluetoothResult.launch(enableBtIntent)
//        } else {
//            if (ContextCompat.checkSelfPermission(
//                    this@SettingActivity,
//                    Manifest.permission.BLUETOOTH_CONNECT
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                bluetoothResult.launch(enableBtIntent)
//            } else {
//                checkForBlueToothPermissions()
//            }
//        }
//    }
//
//    private fun bluetoothNotSupported() {
//        Toast.makeText(
//            this@SettingActivity,
//            getString(R.string.your_device_doesn_t_support_the_bluetooth),
//            Toast.LENGTH_LONG
//        ).show()
//        Handler().postDelayed({ this@SettingActivity.finish() }, 2000)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(receiver)
//    }
//}
//
//
//
