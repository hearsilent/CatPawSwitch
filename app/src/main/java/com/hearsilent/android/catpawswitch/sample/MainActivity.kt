package com.hearsilent.android.catpawswitch.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.hearsilent.android.catpawswitch.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setUpViews()
    }

    private fun setUpViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.catPawSwitch.apply {
            onPushToggle = {
                isChecked = false
            }
            onAnimationEnd = {
                val messages = listOf(
                    "No touchy! 🐾",
                    "I said NO! 😾",
                    "Where's my treats? No treats, no switch!",
                    "Sleeping here... go away! (╯°□°）╯",
                    "You're so persistent, aren't you?"
                )
                Toast.makeText(this@MainActivity, messages.random(), Toast.LENGTH_SHORT).show()
            }
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && binding.enableAnimationCheckBox.isChecked) {
                    postDelayed({
                        startPawAnimation()
                    }, 500L)
                }
            }
        }
    }
}