package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    lateinit var downloadManager: DownloadManager


    private var downloadStatus= "Succeeded"

    private var selectedURL: String? = null
    private var selectedFileName: String? = null


    private lateinit var customRadioGroup: RadioGroup





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        customRadioGroup = findViewById(R.id.radioGroupId)

        loadingButtonId.setOnClickListener {
           handleLoadingButtonClick( )
        }
        createChannel()
    }


    private  fun requestPermission(): Boolean {
        return if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PermissionInfo.PROTECTION_DANGEROUS)
            false
        }
    }
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(downloadID == id){

                downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)

                if(cursor.moveToFirst()){
                    downloadStatus = when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.status_success)
                        else -> getString(R.string.status_fail)
                    }
                    val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
                    notificationManager.sendNotification(
                        CHANNEL_ID,
                        getString(R.string.notification_description),
                        applicationContext,
                        downloadStatus,
                        selectedFileName!!
                    )
                    loadingButtonId.buttonState = ButtonState.Completed
                }
            }
        }
    }


    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(selectedURL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit"

        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "channelName"
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.RED
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun handleRadioButtonSelection() {
         if (radioGlide.isChecked) {
          selectedURL =   GLIDE_URL
             selectedFileName = getString(R.string.glide)
        } else if (radioLoadApp.isChecked) {
           selectedURL = LOAD_APP_URL
             selectedFileName = getString(R.string.loadApp)
        } else if(radioRetrofit.isChecked){
          selectedURL =  RETROFIT_URL
             selectedFileName = getString(R.string.retrofit)
        }else{
            selectedURL = null
        }
    }

    private  fun  handleLoadingButtonClick(){
        if(!requestPermission()){
            return
        }else {
            loadingButtonId.buttonState = ButtonState.Clicked
            handleRadioButtonSelection()
            if (selectedURL == null) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.empty_choice),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                loadingButtonId.buttonState = ButtonState.Loading
                download()
            }
        }

    }

}
