package student.inti.tuitionapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String user_id;
    private BookingAdapter mAdapter;
    private String mTitle[] = {
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday"
    };
    private List<String> mVisibility;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_bookings, container, false);
        initProgressDialog();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = view.findViewById(R.id.recycler_view_class_mybookings);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mAdapter = new BookingAdapter(getActivity());
        //mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.setAdapter(mAdapter);
        getMyBookings();
        return view;
    }

    public void getMyBookings() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        FirebaseUser user = mAuth.getCurrentUser();
        user_id = user.getUid();

        // update with existing(if) booking data
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        CollectionReference fbooking = FirebaseFirestore.getInstance().collection("bookings");
        mVisibility = new ArrayList<>();


        fbooking.document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().exists() && task.getResult() != null) {

                        for (String s : mTitle) {
                            String result = task.getResult().getString(s.toLowerCase());
                            if (String.valueOf(result).equals("1")) {
                                mVisibility.add(s);
                            }
                        }

                        if (mAdapter == null) {
                            mAdapter = new BookingAdapter(getActivity(), mVisibility, MyBookingsFragment.this);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        progressDialog.dismiss();

                    }

                } else {
                    String e = task.getException().getMessage();
                    Toast.makeText(getActivity(), "FireStore Retrieve Error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);
    }
}
