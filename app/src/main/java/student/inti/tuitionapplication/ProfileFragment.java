package student.inti.tuitionapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView textView_name;
    private EditText editText_name, editText_email, editText_phone, editText_gender;
    private String user_id, name, email, phone, gender;
    private ProgressDialog progressDialog;
    private Button buttonUpdate;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initProgressDialog();
        ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);

        //set user data
        textView_name = view.findViewById(R.id.textView_name);
        editText_name = view.findViewById(R.id.editText_name);
        editText_email = view.findViewById(R.id.editText_email);
        editText_phone = view.findViewById(R.id.editText_phone);
        editText_gender = view.findViewById(R.id.editText_gender);

        //get user data
        getUserInfo();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser(editText_name.getText().toString(), editText_email.getText().toString(), editText_phone.getText().toString(), editText_gender.getText().toString());
            }
        });

        return view;
    }


    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);
    }


    private void updateUser(String name, String email, String phone, String gender) {
        //loading
        progressDialog.show();

        FirebaseUser user = mAuth.getCurrentUser();
        user_id = user.getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("gender", gender);
        userMap.put("name", name);
        userMap.put("phone", phone);
        db.collection("users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    //set the navigation view
                    refreshNavView();
                    Toast.makeText(getActivity(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                    textView_name.setText(editText_name.getText().toString());
                    ((HomeActivity) getActivity()).login_username.setText(editText_name.getText().toString());
                    ((HomeActivity) getActivity()).login_email.setText(editText_email.getText().toString());
                    progressDialog.dismiss();
                } else {
                    String e = task.getException().getMessage();
                    Toast.makeText(getActivity(), e, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void refreshNavView() {
        ((HomeActivity) getActivity()).refreshNavInfo(name, email);
    }

    public void getUserInfo() {
        progressDialog.show();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {

                    if (task.getResult().exists() && task.getResult() != null) {

                        name = task.getResult().getString("name");
                        gender = task.getResult().getString("gender");
                        email = task.getResult().getString("email");
                        phone = task.getResult().getString("phone");

                        textView_name.setText(name);
                        editText_name.setText(name);
                        editText_email.setText(email);
                        editText_phone.setText(phone);
                        editText_gender.setText(gender);

                        editText_name.setSelection(editText_name.getText().length());
                        editText_email.setSelection(editText_email.getText().length());
                        editText_phone.setSelection(editText_phone.getText().length());
                        editText_gender.setSelection(editText_gender.getText().length());

                    }

                } else {
                    String e = task.getException().getMessage();
//                    GeneralStaticClass.toast(HomeActivity.this,"FireStore Retrieve Error: " + e);
                    Toast.makeText(getActivity(), "FireStore Retrieve Error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
