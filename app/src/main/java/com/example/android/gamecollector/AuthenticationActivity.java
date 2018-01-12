package com.example.android.gamecollector;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by shalom on 2018-01-11.
 * Activity that handles the login/sign-up activity
 */

public class AuthenticationActivity extends AppCompatActivity {
    public static final String LOG_TAG = AuthenticationActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    /*Google sign in button*/
    private SignInButton googleButton;
    private TextView welcomeTV;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleAccount;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        googleButton = (SignInButton) findViewById(R.id.signInButton);
        welcomeTV = (TextView) findViewById(R.id.welcome_tv);

        /*Get the shared instance of the FirebaseAuth object*/
        firebaseAuth = FirebaseAuth.getInstance();

        /*Set Typeface to Roboto Black*/
        Typeface robotoBlack = Typeface.createFromAsset(getAssets(), "roboto_black.ttf");
        welcomeTV.setTypeface(robotoBlack);

        /*Sets button to the wide size of the Google sign-in button*/
        googleButton.setSize(SignInButton.SIZE_STANDARD);
        /*Handle button click*/
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        /*Configure Google Sign in*/
//        TODO(1)  Pass server's client ID to the requestIdToken method from credentials page
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.gamecollector_client_id))
                .requestEmail()
                .build();

        /*Build a GoogleSignInClient with the options specified by googleSignInOptions*/
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        googleAccount = GoogleSignIn.getLastSignedInAccount(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        /*Check if user is signed in (non-null) and update UI accordingly*/
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)*/
        if (requestCode == RC_SIGN_IN) {
            /*Task returned from this call is always completed, therefore unecessary to attach a listener
             *The GoogleSignInAccount object contains information about the signed-in user, such as the user's name*/
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

//            TODO(1) If try is successful, sign in was succseful. Else sign in failed. Update ui accordingly
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                /*Sign in successful, so authenticate with Firebase*/
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                /*ApiException status code indicates the detailed failure reason.
                 *Refer to GoogleSignInStatusCodes class for more information*/
                Log.w(LOG_TAG, "signInResult:failed code=" + e.getStatusCode());
                updateUI(null);
            }
        }
    }


    /*Handles sign in button*/
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

//    TODO(1) If param is a Google account, start CollectionActvity; else if null, ask user to login
    /**
     * Handles presence of a googleAccount on UI
     *
     * @param currentUser If not null, user has already signed in
     */
    public void updateUI(FirebaseUser currentUser) {

    }

    /**
     * Handles successful sign in by exchanging it for a Firebase credential, and authenticating that with Firebase using the Firebase credential
     * @param account Account user is signed in with
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + account.getId());

        /*An instance of AuthCredential that wraps Google Sign-In ID or access tokens*/
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /*Sign in success, update UI with signed-in user's information*/
                            Log.d(LOG_TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            /*If sign in fails, display a message to user*/
                            Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.signInButton), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
