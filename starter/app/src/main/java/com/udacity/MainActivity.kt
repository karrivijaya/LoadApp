package com.udacity

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var url: String = ""
    lateinit var downloadManager: DownloadManager

    private val WRITE_TO_STORAGE_PERMISSION_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel(
                getString(R.string.loadapp_notification_channel_id),
                getString(R.string.loadapp_notification_channel_name)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        if(url.isEmpty())
            printToastMessage(getString(R.string.toast_message))

        custom_button.setOnClickListener {
            url = getURL(radio_repositories.checkedRadioButtonId)
            if(url.isEmpty()) printToastMessage(getString(R.string.toast_message))
            else {
                checkForPermission()
            }
        }
    }

    private fun printToastMessage(toastMessage: String) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
    }

    private fun getURL(checkedRadioButtonId: Int) : String {

        return when(checkedRadioButtonId){
            R.id.radio_glide -> GLIDE_URL
            R.id.radio_udacity -> UDACITY_URL
            R.id.radio_retrofit -> RETROFIT_URL
            else -> ""
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id == downloadID){
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

                var status: Int = -1

                if(cursor != null && cursor.moveToNext()){
                    status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                }
                cursor.close()

                custom_button.buttonState = ButtonState.Completed

                sendNotification(getStatus(status))
            }
        }
    }



    private fun getStatus(intExtra: Int?) : String{
        return when(intExtra){
            DownloadManager.STATUS_SUCCESSFUL -> "SUCCESS"
            DownloadManager.STATUS_FAILED -> "FAILED"
            DownloadManager.STATUS_PAUSED -> "PAUSED"
            DownloadManager.STATUS_PENDING -> "PENDING"
            else -> "RUNNING"
        }
    }

    private fun sendNotification(status: String) {
        val notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java)
                as NotificationManager
        notificationManager.sendNotification(
                getText(R.string.notification_description).toString(),
                this, getFileDescription(url), status)
    }

    private fun download(url: String) {

        custom_button.buttonState = ButtonState.Loading

        val request =
                DownloadManager.Request(Uri.parse(url))
                        .setTitle(getFileDescription(url))
                        .setDescription(getFileDescription(url))
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "")
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    private fun checkForPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            download(url)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.permission_needed))
                        .setMessage(getString(R.string.permission_message))
                        .setPositiveButton(getString(R.string.permission_OK), DialogInterface.OnClickListener { dialogInterface, i ->
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_TO_STORAGE_PERMISSION_CODE)
                        })
                        .setNegativeButton(getString(R.string.permission_cancel), DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                        .create().show()
            }
            else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_TO_STORAGE_PERMISSION_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == WRITE_TO_STORAGE_PERMISSION_CODE){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this," Permission granted", Toast.LENGTH_SHORT).show()
               download(url)
            }
            else{
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getFileDescription(url: String): String{
        return when(url){
            GLIDE_URL -> getString(R.string.radio_button_glide_desc)
            UDACITY_URL -> getString(R.string.radio_button_udacity_desc)
            else -> getString(R.string.radio_button_retrofit_desc)
        }
    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
                "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL =
                "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }



    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.loadapp_notification_channel_name)


            val notificationManager =
                    getSystemService(
                            NotificationManager::class.java
                    )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
