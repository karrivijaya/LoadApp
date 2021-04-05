package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.utils.DOWNLOAD_STATUS
import com.udacity.utils.URL_EXTRA
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import java.io.File


val NOTIFICATION_ID_STRING = "NOTIFICATION_ID"

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        manager.cancel(intent.getIntExtra(NOTIFICATION_ID_STRING, com.udacity.utils.NOTIFICATION_ID))

        val url = intent.getStringExtra(URL_EXTRA)
        tv_fileNameValue.setText(url)

        val status = intent.getStringExtra(DOWNLOAD_STATUS)

        tv_fileStatusValue.setText(status)

        if(status.equals("SUCCESS")){
            val spannableString = SpannableString(status)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    openDirectory()
                }
            }

            status?.length?.let { spannableString.setSpan(clickableSpan, 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }

            tv_fileStatusValue.setText(spannableString)
            tv_fileStatusValue.setMovementMethod(LinkMovementMethod.getInstance())
        }

        button_detail_screen.setOnClickListener {
            finish() // going back to main activity
        }
    }

    private fun openDirectory() {
        val downloadedFile = Uri.parse(Environment.DIRECTORY_DOWNLOADS
                + File.separator + "repos" + File.separator)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(downloadedFile, "*/*")
        startActivity(intent)
    }
}

