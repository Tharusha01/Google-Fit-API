package com.saukya.wellness.googlefitlogin

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val testBTN = findViewById<Button>(R.id.btn_Test)

        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
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
        testBTN.setOnClickListener{

            val intent = Intent(this, DetailsActivity::class.java)
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
                    Log.i(TAG, "Successfully subscribed!")
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private fun readData() {
        var steps = findViewById<TextView>(R.id.tv_StepCount)
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total = (if (dataSet.isEmpty)
                    0
                else
                    dataSet.dataPoints[0].getValue(Field.FIELD_STEPS).asInt()).toLong()
                Toast.makeText(this,"Total steps: $total", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Total steps: $total")
                steps.setText("$total")
            }
            .addOnFailureListener { e -> Log.w(TAG, "There was a problem getting the step count.", e) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_read_data) {
            readData()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /** Initializes a custom log class that outputs both to in-app targets and logcat.  */
    private fun initializeLogging() {
        // Wraps Android's native log framework.
        //        LogWrapper logWrapper = new LogWrapper();
        //        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        //        Log.setLogNode(logWrapper);
        //        // Filter strips out everything except the message text.
        //        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        //        logWrapper.setNext(msgFilter);
        //        // On screen logging via a customized TextView.
        //        LogView logView = (LogView) findViewById(R.id.sample_logview);
        //
        //        // Fixing this lint error adds logic without benefit.
        //        // noinspection AndroidLintDeprecation
        //        logView.setTextAppearance(R.style.Log);
        //
        //        logView.setBackgroundColor(Color.WHITE);
        //        msgFilter.setNext(logView);
        Log.i(TAG, "Ready")
    }

    companion object {

        val TAG = "StepCounter"
        private val REQUEST_OAUTH_REQUEST_CODE = 0x1001
    }

}
