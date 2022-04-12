package com.barry.exoplayerpage

import android.content.Intent
import android.os.Bundle
import com.barry.exoplayerpage.VideoActivity.Companion.BUNDLE_VIDEO_URL
import com.barry.kotlin_code_base.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        playTextView.setOnClickListener {

            val intent = Intent(this, VideoActivity::class.java)

            intent.putExtra(BUNDLE_VIDEO_URL,"https://kps-dev.thekono.com/api/v1/projects/61398d3c62cbe46b8b9e58af/streams/62551f8105b865000ead4f93/playlist.m3u8")

            startActivity(intent)

        }

    }

}