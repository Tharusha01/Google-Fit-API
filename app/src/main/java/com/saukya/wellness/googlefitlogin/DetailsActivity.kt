package com.saukya.wellness.googlefitlogin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.fitness.FitnessOptions
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import java.util.*
import java.util.concurrent.TimeUnit


class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val back=findViewById<Button>(R.id.btn_back)
        val googleFit=findViewById<Button>(R.id.btn_googleFit)

        googleFit.setOnClickListener{

            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build()

            if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions)
            } else {
                accessGoogleFit()
            }
        }

        back.setOnClickListener{
            val intent = Intent(this,TestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun accessGoogleFit() {
        val cal = Calendar.getInstance()
        cal.time = Date()
        val endTime = cal.timeInMillis
        cal.add(Calendar.YEAR, -1)
        val startTime = cal.timeInMillis


        val readRequest = DataReadRequest.Builder()
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.HOURS)
            .aggregate(DataType.TYPE_LOCATION_SAMPLE, DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
            .build()



        GoogleSignIn.getLastSignedInAccount(this)?.let {
            Fitness.getHistoryClient(this, it)
                .readData(readRequest)
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "onSuccess()")
                }
                .addOnFailureListener {
                        e -> Log.e(LOG_TAG, "onFailure()", e)
                }
                .addOnCompleteListener {
                    Log.d(LOG_TAG, "onComplete()")
                }
        }
    }

    companion object {
        internal val LOG_TAG: String? = null
        internal const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    }


    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit()
            }
        }
    }
}
