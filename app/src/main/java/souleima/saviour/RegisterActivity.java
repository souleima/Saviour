package souleima.saviour;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity  {

    private EditText etRegisterMail;
    private EditText etRegisterPassword;
    private EditText etRegisterConfirmationPassword;
    private Button btRegister;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        etRegisterMail = (EditText) findViewById(R.id.etRegisterMail);
        etRegisterPassword = (EditText) findViewById(R.id.etRegisterPassword);
        etRegisterConfirmationPassword = (EditText) findViewById(R.id.etRegisterConfirmationPassword);
        btRegister = (Button) findViewById(R.id.btRegister);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {

        //getting email and password from edit texts
        String email = etRegisterMail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String passwordConfirmation= etRegisterConfirmationPassword.getText().toString().trim();

        if (!validate(email,password,passwordConfirmation)) {
            //validation of email, password, passwordConfirmation failed
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
            return;
        }

        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            //display some message here
                            Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                        } else {
                            //display some message here
                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    public boolean validate(String email,String password,String passwordConfirmation) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegisterMail.setError("enter a valid email address");
            valid = false;
        } else {
            etRegisterMail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etRegisterPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etRegisterPassword.setError(null);
        }

        if(!TextUtils.equals(password,passwordConfirmation)){
            etRegisterConfirmationPassword.setError("password doesn't match");
            valid = false;
        }else {
            etRegisterConfirmationPassword.setError(null);
        }

        return valid;
    }

}