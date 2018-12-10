package com.example.lorenzo.meetup2

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity: AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private val TAG = "Sign In Activity"
    private val RC_SIGN_IN = 9001
    private lateinit var mAuth:FirebaseAuth
    private lateinit var mGoogleApiClient:GoogleApiClient
    private lateinit var mSignInButton:SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_sign_in)


        //Sign In Button Listener
        mSignInButton = findViewById(R.id.sign_in_button)
        mSignInButton.setOnClickListener(this)

        //Configure Google Sign In
        val googleSignInOptions:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()
        mAuth = FirebaseAuth.getInstance()

    }

    private fun signIn(){
        val signInIntent: Intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.sign_in_button -> signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val result:GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.d(TAG, resultCode.toString())
            if(result.isSuccess){
                val account:GoogleSignInAccount? = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else{
                Log.d(TAG, "Google Sign in failed")
            }
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed: " + result)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount){
        val cred: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(cred).addOnCompleteListener(this){
            Log.d(TAG, "SignInWithCredential:onComplete: " + it.isSuccessful)
            if(!it.isSuccessful){
                Log.w(TAG, "signInWithCredentail", it.exception)
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }else{
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }

}