package com.anwesh.uiprojects.linkedhoritriincview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.horitriincview.HoriTriIncView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HoriTriIncView.create(this)
    }
}
