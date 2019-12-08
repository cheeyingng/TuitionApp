package student.inti.tuitionapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Button mLoginButton;
    private Button mSignupButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String email_input, password_input, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mSignupButton = findViewById(R.id.signup_btn);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input
                EditText mEmailInput = (EditText) findViewById(R.id.email_input);
                email_input = mEmailInput.getText().toString().trim();

                EditText mPasswordInput = (EditText) findViewById(R.id.password_input);
                password_input = mPasswordInput.getText().toString().trim();

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
                    registerUser(email_input, password_input);
                }
            }
        });
        mLoginButton = findViewById(R.id.login_btn);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void registerUser(String email, String password) {
//        progressbar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("success", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user_id = user.getUid();
                            //start
// Create a new user with a first and last name
                            Map<String, Object> user_new = new HashMap<>();
                            user_new.put("email", email_input);
                            user_new.put("gender", "Not Set");
                            user_new.put("name", "Not Set");
                            user_new.put("password", password_input);
                            user_new.put("phone", "Not Set");

// Add a new document with a generated ID
                            db.collection("users").document(user_id).set(user_new).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                                        redirectToMain();
                                    } else {
                                        String e = task.getException().getMessage();
                                        Toast.makeText(RegisterActivity.this, e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            //end
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("warn", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Failed To Register User",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            redirectToMain();
        }

    }

    private void redirectToMain() {
        Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
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
                            Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("warn", "signInWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Credentials Invalid Or Not Found.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
