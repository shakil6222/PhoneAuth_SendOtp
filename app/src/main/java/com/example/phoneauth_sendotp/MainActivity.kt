package com.example.phoneauth_sendotp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var buttonSendOTP: Button
    private lateinit var editTextOTP: EditText
    private lateinit var buttonVerifyOTP: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        buttonSendOTP = findViewById(R.id.buttonSendOTP)
        editTextOTP = findViewById(R.id.editTextOTP)
        buttonVerifyOTP = findViewById(R.id.buttonVerifyOTP)

        buttonSendOTP.setOnClickListener {
            val phoneNumber = editTextPhoneNumber.text.toString()

            if (phoneNumber.isNotEmpty()) {
                sendOTP(phoneNumber)
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }

        buttonVerifyOTP.setOnClickListener {
            val otp = editTextOTP.text.toString().trim()

            if (otp.isNotEmpty()) {
                verifyOTP(otp)
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOTP(phoneNumber: String) {
        val formattedPhoneNumber = "+91$phoneNumber"

        PhoneAuthProvider.getInstance().verifyPhoneNumber(formattedPhoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e(
                        "com.example.airbase_test otp.MainActivity",
                        "Verification failed: ${e.message}"
                    )
                    Toast.makeText(
                        this@MainActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCodeSent(
                    verificationId: String, token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)
                    this@MainActivity.verificationId = verificationId
                    Toast.makeText(this@MainActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun verifyOTP(otp: String) {
        if (verificationId.isNotEmpty()) {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            signInWithPhoneAuthCredential(credential)
        } else {
            Toast.makeText(this, "Verification ID is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SendingNumberActivity::class.java))
                } else {
                    Log.e(
                        "com.example.phoneauth_sendotp.MainActivity",
                        "Authentication failed: ${task.exception?.message}"
                    )
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
