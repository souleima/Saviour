package souleima.saviour;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity
        implements  GoogleApiClient.OnConnectionFailedListener,
                    View.OnClickListener {
    private Button btLoginCreateAccount;
    private Button btLoginForgotPassword;
    private Button btLogin;
    private EditText etLoginMail;
    private EditText etLoginPassword;
    private LoginButton loginButton;
    private static int GOOGLE_SIGN_IN = 0;
    private static String TAG = "LOGIN_ACTIVITY" ;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mcallbackManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*This function initializes the Facebook SDK, the behavior of
        Facebook SDK functions are undetermined if this function is not called.
        It should be called as early as possible.*/
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        btLogin = (Button) findViewById(R.id.btLogin);
        btLoginCreateAccount= (Button) findViewById(R.id.btLoginCreateAccount);
        btLoginForgotPassword = (Button) findViewById(R.id.btLoginForgotPassword);
        etLoginMail = (EditText) findViewById(R.id.etLoginMail);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        //Facebook LoginButton
        loginButton = (LoginButton) findViewById(R.id.login_button);

        progressDialog = new ProgressDialog(this);

        //open SignUp Activity (RegisterActivity)
        btLoginCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInMail();
            }
        });


    if ( getIntent().hasExtra("logout") ){
        LoginManager.getInstance().logOut();
    }

    mAuth=FirebaseAuth.getInstance();
        //The CallbackManager manages the callbacks into the FacebookSdk from an Activity's or Fragment's onActivityResult() method.
    mcallbackManager = CallbackManager.Factory.create();
        // Set the permissions to use when the user logs in.
    loginButton.setReadPermissions("email", "public_profile");
        //Registers a login callback to the given callback manager.
        //mcallbackManager will encapsulate the callback.
        //FacebookCallback The login callback that will be called on login completion.
    loginButton.registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            //exchange user token with firebase credential
            handleFacebookAccessToken(loginResult.getAccessToken());
            Log.d("","facebook:onSuccess" + loginResult);
        }

        @Override
        public void onCancel() {
            Log.d("TAG", "facebook:onCancel");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("TAG", "facebook:onError",error);
        }
    });

    //se when we log in and out
    mAuthListener = new FirebaseAuth.AuthStateListener() {
        /*This method gets invoked in the UI thread on changes in the authentication state:
            Right after the listener has been registered
            When a user is signed in
            When the current user is signed out
            When the current user changes
            When there is a change in the current user's token*/
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user= firebaseAuth.getCurrentUser();
            if( user != null)
            {
                Log.d("AUTH","user logged in" + user.getEmail());
                   //we can open an activity here
            }
            else
            {
                Log.d("AUTH","user logged out");
            }
        }
    };
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //Specifies that an ID token for authenticated users is requested
            .requestIdToken(getString(R.string.default_web_client_id))
            //Specifies that email info is requested by your application.
            .requestEmail()
            //builds the GoogleSignInOptions object
            .build();

        //GoogleApiClient the main entry point for Google Play services integration.
    googleApiClient = new GoogleApiClient.Builder(this)
            //Enables automatic lifecycle management
            .enableAutoManage(this, this)
            //Specify which Apis are requested by your app.
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();

    findViewById(R.id.sign_in_button).setOnClickListener(this);

        //findViewById(R.id.btSignout).setOnClickListener(this);

}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //facebook
        mcallbackManager.onActivityResult(requestCode, resultCode,data);
        //google
        if(requestCode == GOOGLE_SIGN_IN){
            GoogleSignInResult result =Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                Log.d(TAG, "Google Login Failed");
            }
        }
    }

    private  void handleFacebookAccessToken(AccessToken token){
        Log.d("","handleFacebookAccessToken" + token);
        /*an access token for the signed-in user, exchange it for a Firebase credential,
        and authenticate with Firebase using the Firebase credential:
                 */
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "signInWithCredential", task.getException());
                        if( !task.isSuccessful() ){
                            Log.v("" , "signInWithCredential" , task.getException());
                            Toast.makeText(LoginActivity.this, " Authentication failed ", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void firebaseAuthWithGoogle (GoogleSignInAccount acct){
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("AUTH", "signInWithCredential:oncomplete"+ task.isSuccessful());
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signInGoogle(){
        Intent singInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(singInIntent, GOOGLE_SIGN_IN);
    }
    private void signInMail(){
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
        String email = etLoginMail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "authentification failed",
                                    Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(LoginActivity.this, "authentification success",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.sign_in_button: signInGoogle();
                break;
            //in case I wanted to use a signout button
            /*case R.id.btSignout: signOut();
                break;*/
        }
    }
}
