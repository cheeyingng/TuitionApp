package student.inti.tuitionapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button mLoginButton, mSignupButton;
    private FirebaseAuth mAuth;
    private ProgressBar loginProgress;

    public LoginActivity() {
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Checking whether logged in or not, if yes go back to MainActivity
        if (currentUser != null) {
            redirectToMain();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loginProgress = (ProgressBar) findViewById(R.id.login_progressBar);

        mLoginButton = findViewById(R.id.login_btn);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input
                EditText mEmailInput = (EditText) findViewById(R.id.email_input);
                String email_input = mEmailInput.getText().toString().trim();

                EditText mPasswordInput = (EditText) findViewById(R.id.password_input);
                String password_input = mPasswordInput.getText().toString().trim();

                //handling inputs
                TextView username_error = (TextView) findViewById(R.id.username_input_error);
                if (email_input.isEmpty())
                    username_error.setVisibility(View.VISIBLE);
                else
                    username_error.setVisibility(View.GONE);

                TextView password_error = (TextView) findViewById(R.id.password_input_error);
                if (password_input.isEmpty())
                    password_error.setVisibility(View.VISIBLE);
                else
                    password_error.setVisibility(View.GONE);

                //activity_login if both fields not empty
                if (!email_input.isEmpty() && !password_input.isEmpty()) {
                    loginProgress.setVisibility(View.VISIBLE);
                    mLoginButton.setText("Logging In");
                    loginUser(email_input, password_input);
                }
            }
        });


        mSignupButton = findViewById(R.id.signup_btn);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginUser(String email, String password) {
//        progressbar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("success", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            redirectToMain();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("warn", "signInWithEmail:failure", task.getException());
                            mLoginButton.setText("Log In");
                            Toast.makeText(LoginActivity.this, "Credentials Invalid Or Not Found.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        loginProgress.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void redirectToMain() {
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
