package com.edwinacubillos.exloggingoogle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    TextView mStatusUser, mStatusEmail;
    Button signOutButton;
    String TAG = "MainActivity";
    private static final int RC_SIGN_IN=9001;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusEmail = (TextView) findViewById(R.id.id_tvStatusEmail);
        mStatusUser = (TextView) findViewById(R.id.id_tvStatusUser);
        signOutButton = (Button) findViewById(R.id.id_sign_out_button);
        SignInButton signInButton = (SignInButton) findViewById(R.id.id_sign_in_button);

        signOutButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_sign_in_button:
                Sign_in();
                break;
            case R.id.id_sign_out_button:
                Sign_out();
                break;
        }
    }

    private void Sign_out() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    updateUI(false);

                }
            });
    }

    private void Sign_in() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusUser.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            mStatusEmail.setText(getString(R.string.signed_in_fmt, acct.getEmail()));
            updateUI(true);
           /* Intent startapplication = new Intent (MainActivity.this, index.class); //Abriria una nueva actividad
            startActivity();*/
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if(signedIn){
            Toast.makeText(MainActivity.this, "In",Toast.LENGTH_SHORT).show();
            findViewById(R.id.id_sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.id_sign_out_button).setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(MainActivity.this, "Out",Toast.LENGTH_SHORT).show();
            findViewById(R.id.id_sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.id_sign_out_button).setVisibility(View.GONE);
            mStatusUser.setText("^-^");
            mStatusEmail.setText("...");
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed"+connectionResult);
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if(opr.isDone()){
            Log.d(TAG, "Sign-In");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
        else {
            Log.d(TAG, "Nadie logeado");
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
}
