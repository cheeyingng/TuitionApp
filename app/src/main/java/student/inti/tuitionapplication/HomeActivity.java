package student.inti.tuitionapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    public TextView login_username, login_email;
    private FirebaseFirestore db;
    private Map<String, String> bookMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        mDrawerLayout = findViewById(R.id.navigation_drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        login_username = navigationView.getHeaderView(0).findViewById(R.id.login_user_name);
        login_email = navigationView.getHeaderView(0).findViewById(R.id.login_user_email);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new BookClassFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_book_class);
        }
        db = FirebaseFirestore.getInstance();

        getUserInfo();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void getUserInfo() {
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().exists() && task.getResult() != null) {

                        String name = task.getResult().getString("name");
                        String gender = task.getResult().getString("gender");
                        String email = task.getResult().getString("email");
                        String phone = task.getResult().getString("phone");

                        //Toast.makeText(HomeActivity.this, " Data Exist " + name + image, Toast.LENGTH_LONG).show();

                        login_username.setText(name);
                        login_email.setText(email);


                    } else {
                        // If data doesn't exist
                        Toast.makeText(HomeActivity.this, "Please Update Profile", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    String e = task.getException().getMessage();
//                    GeneralStaticClass.toast(HomeActivity.this,"FireStore Retrieve Error: " + e);
                    Toast.makeText(HomeActivity.this, "FireStore Retrieve Error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void refreshNavInfo(String name, String email) {
        login_username.setText(name);
        login_email.setText(email);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_my_bookings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyBookingsFragment()).commit();
                getSupportActionBar().setTitle("My Booking");
                break;
            case R.id.nav_book_class:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BookClassFragment()).commit();
                getSupportActionBar().setTitle("Book A Class");
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                getSupportActionBar().setTitle("View Profile");
                break;
            case R.id.nav_logout:
                confirmLogout();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void confirmLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Do you want to logout ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mAuth.getInstance().signOut();
                redirectToMain();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void redirectToMain() {
        Intent i = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

}
