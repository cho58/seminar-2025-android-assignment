package com.example.seminar_assignment_2025.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.seminar_assignment_2025.R

class ProfileActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // res/layout/activity_profile.xml 사용

        // GitHub TextView
        val githubText = findViewById<TextView>(R.id.githubLink)
        // 클릭 시 GitHub 페이지 열기
        githubText.setOnClickListener {
            val url = "https://github.com/cho58"   // 본인 깃허브 주소
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        val url = intent.getStringExtra("workspaceUrl") ?: ""
        val urlText = findViewById<TextView>(R.id.urlTextView)

        val display = if (url.isNotBlank()) "https://$url" else "-"
        urlText.text = display

        urlText.apply {
            paint.isUnderlineText = true
            setTextColor(ContextCompat.getColor(this@ProfileActivity, android.R.color.holo_blue_dark))
            setOnClickListener {
                val open = Intent(Intent.ACTION_VIEW, Uri.parse("https://$url"))
                startActivity(open)
            }
        }
    }
}