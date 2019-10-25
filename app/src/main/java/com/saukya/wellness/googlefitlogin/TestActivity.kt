package com.saukya.wellness.googlefitlogin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.DataType.TYPE_DISTANCE_DELTA
import com.google.android.gms.fitness.data.Field


@Suppress("IMPLICIT_CAST_TO_ANY")
class TestActivity : AppCompatActivity() {

    private lateinit var steps: TextView
    private lateinit var distance: TextView
    private lateinit var calories: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val testBTN = findViewById<Button>(R.id.btn_Test)

        steps = findViewById(R.id.tv_StepCount)
        distance = findViewById(R.id.tv_DistanceCount)
        calories = findViewById(R.id.tv_CaloriesCount)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION)
        }

            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .addDataType(DataType.TYPE_DISTANCE_DELTA)
                .addDataType(DataType.TYPE_DISTANCE_CUMULATIVE)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                .build()
            if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions
                )
            } else {
                subscribe()
            }
            initializeLogging()
            testBTN.setOnClickListener {

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe()
            }
        }
    }

    /** Records step data by requesting a subscription to background step data.  */
    fun subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Successfully subscribed for Steps API!")
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .subscribe(DataType.TYPE_DISTANCE_CUMULATIVE)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Successfully subscribed for distance API!")
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .subscribe(DataType.TYPE_CALORIES_EXPENDED)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Successfully subscribed for Calories API!")
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private fun readDataStep() {

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total = (if (dataSet.isEmpty)
                    0
                else
                    dataSet.dataPoints[0].getValue(Field.FIELD_STEPS).asInt()).toLong()
                Toast.makeText(this,"Total steps: $total", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Total steps: $total")
                steps.text = "$total"
            }
            .addOnFailureListener { e -> Log.w(TAG, "There was a problem getting the step count.", e) }
    }

    private fun readDataDistance() {

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .readDailyTotal(TYPE_DISTANCE_DELTA)
            .addOnSuccessListener { dataSet ->
                val total = (if (dataSet.isEmpty)
                    0
                else
                    dataSet.dataPoints[0].getValue(Field.FIELD_DISTANCE).asFloat().toInt())
                Toast.makeText(this,"Total Distance: $total", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Total Distance: $total")
                val disTotal = total * 0.001

                val dis = "%.2f".format(disTotal).toDouble().toString()  + "km"

                distance.text = dis
            }
            .addOnFailureListener { e -> Log.w(TAG, "There was a problem getting the Distance count.", e) }
    }

    private fun readDataCalories() {

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
            .addOnSuccessListener { dataSet ->
                val total = (if (dataSet.isEmpty)
                    0
                else
                    dataSet.dataPoints[0].getValue(Field.FIELD_CALORIES).asFloat().toInt())
                Toast.makeText(this,"Total Calories: $total", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Total Calories: $total")
                val cal = total.toString() + " cal"
                calories.text = cal
            }
            .addOnFailureListener { e -> Log.w(TAG, "There was a problem getting the Calories count.", e) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_read_data) {
            readDataStep()
            return true
        }else if(id == R.id.action_read_distance){
            readDataDistance()
            return true
        }else if(id == R.id.action_read_calories){
            readDataCalories()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /** Initializes a custom log class that outputs both to in-app targets and logcat.  */
    private fun initializeLogging() {
        Log.i(TAG, "Ready")
    }

    companion object {

        val TAG = "StepCounter"
        private val REQUEST_OAUTH_REQUEST_CODE = 0x1001
        private val MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 155
    }

}
