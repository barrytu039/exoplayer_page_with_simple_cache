package com.barry.exoplayerpage

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.barry.exoplayerpage.VideoActivity.Companion.BUNDLE_VIDEO_URL
import com.barry.kotlin_code_base.base.BaseActivity
import com.barry.kotlin_code_base.tools.DatePickerUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        playTextView.setOnClickListener {

            val intent = Intent(this, VideoActivity::class.java)

            intent.putExtra(BUNDLE_VIDEO_URL,"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4")

            startActivity(intent)

        }

    }

}