/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mediapipe.examples.handlandmarker

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.mediapipe.examples.handlandmarker.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private var job: Job? = null
    var timlist: MutableList<String> =  mutableListOf("")
    var startTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        activityMainBinding.navigation.setupWithNavController(navController)
        activityMainBinding.navigation.setOnNavigationItemReselectedListener {
            }
        var yy=""
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {

                var output = HandLandmarkerHelper.outr.substringAfter('(').substringBefore(',')
                timlist.add(output)

                val endTime = System.currentTimeMillis()
                val elapsedTime = endTime - startTime

                if (elapsedTime >= 2400) {
                    yy += findMostFrequentElement(timlist)
                    timlist.clear()
                    startTime = System.currentTimeMillis()

                }
                val arabicTypeface = Typeface.createFromAsset(assets, "big.ttf")
                activityMainBinding.textView.typeface = arabicTypeface

                activityMainBinding.textView.append(yy)
                yy=""



                delay(1)
            }
        }
    }
    fun findMostFrequentElement(list: List<String>): String {
        val sortedList = list.sorted()

        var maxCount = 0
        var currentCount = 1
        var mostFrequentElement: String = ""

        for (i in 3 until sortedList.size) {
            if (sortedList[i] == sortedList[i - 1]) {
                currentCount++
            } else {
                if (currentCount > maxCount) {
                    maxCount = currentCount
                    mostFrequentElement = sortedList[i - 1]
                }
                currentCount = 1
            }
        }

        if (currentCount > maxCount) {
            maxCount = currentCount
            mostFrequentElement = sortedList.last()
        }

        return mostFrequentElement
    }

    override fun onBackPressed() {
       finish()
    }
}
