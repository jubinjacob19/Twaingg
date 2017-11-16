package com.example.jubinjacob.twaingg

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.util.Log
import android.view.animation.*
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.resetButton).setOnClickListener {
            val fragment = fragmentManager.findFragmentById(R.id.knob_collection)
            if (fragment is KnobCollectionFragment) {
                fragment.reset()
            }
        }
    }
}
