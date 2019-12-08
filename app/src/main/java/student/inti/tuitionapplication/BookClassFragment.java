package student.inti.tuitionapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookClassFragment extends Fragment {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseFirestore firebaseFirestore;
    private Dialog mPopupDetailsDialog;
    private int counter = 0;
    private String user_id;
    private Map<String, String> bookMap = new HashMap<>();
    private RecyclerView mRecyclerView;
    private ClassAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressDialog progressDialog;
    private List<Map<String, String>> mSeatRemained;
    private String mTitle[] = {
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_page, container, false);
        initProgressDialog();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = view.findViewById(R.id.recycler_view_class);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mPopupDetailsDialog = new Dialog(getActivity());
        computeSeatRemaining();

        return view;
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);
    }

    private void computeSeatRemaining() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        CollectionReference fclass = FirebaseFirestore.getInstance().collection("class");
        mSeatRemained = new ArrayList<>();
        counter = 0;

        for (final String s : mTitle) {
            fclass.document(s.toLowerCase()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        if (task.getResult().exists() && task.getResult() != null) {
                            int total = Integer.parseInt(task.getResult().getString("total"));
                            int count = Integer.parseInt(task.getResult().getString("count"));
                            int left = total - count;

                            Map<String, String> map = new HashMap();
                            map.put(s.toLowerCase(), String.valueOf(left));
                            mSeatRemained.add(map);

                            if (counter == 4) {
                                if (mAdapter == null) {
                                    mAdapter = new ClassAdapter(getActivity(), mSeatRemained, BookClassFragment.this);
                                    mRecyclerView.setAdapter(mAdapter);
                                } else {
                                    mAdapter.setData(mSeatRemained);
                                }
                                progressDialog.dismiss();
                            }

                            counter++;
                        }

                    } else {
                        String e = task.getException().getMessage();
                        Toast.makeText(getActivity(), "FireStore Retrieve Error: " + e, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    void showPopup(final String day, int seatRemain, int img, String timeslot) {

        if (seatRemain <= 0) {
            Toast.makeText(getActivity(), "Full slot for this class!", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView mbtnClose, dayTextview, timeslotTextview;
        ImageView slotImageview;
        final Button mbtnBook;

        mPopupDetailsDialog.setContentView(R.layout.popup_detail_class);
        mbtnClose = mPopupDetailsDialog.findViewById(R.id.btn_close);
        mbtnBook = mPopupDetailsDialog.findViewById(R.id.btn_book);
        dayTextview = mPopupDetailsDialog.findViewById(R.id.dayTextview);
        timeslotTextview = mPopupDetailsDialog.findViewById(R.id.timeslotTextview);
        slotImageview = mPopupDetailsDialog.findViewById(R.id.slotImageview);

        //set dynamic value
        dayTextview.setText(day + " Class");
        timeslotTextview.setText(timeslot);
        slotImageview.setImageResource(img);

        mbtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupDetailsDialog.dismiss();
            }
        });
        mPopupDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupDetailsDialog.show();

        SharedPreferences pref = getActivity().getPreferences(getContext().MODE_PRIVATE);
        int booked = pref.getInt(day.toLowerCase(), 1); // 0 = haven book, 1 = booked
        mbtnBook.setTag(booked);
        if (booked == 1) {
            mbtnBook.setText("Cancel");
        } else {
            mbtnBook.setText("Book");
        }

        mbtnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 0) {
                    //Book Class
                    mbtnBook.setText("Cancel");
                    bookClass(day.toLowerCase());
                    v.setTag(0);
                } else {
                    //Cancel class
                    mbtnBook.setText("Book");
                    cancelClass(day.toLowerCase());
                    v.setTag(1);
                }
                progressDialog.show();
            }
        });
    }

    private void bookClass(final String key) {
        FirebaseUser user = mAuth.getCurrentUser();
        user_id = user.getUid();

        // update with existing(if) booking data
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("bookings").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().exists() && task.getResult() != null && task.getResult().getString("monday") != null) {
                        bookMap.put("monday", task.getResult().getString("monday"));
                        bookMap.put("tuesday", task.getResult().getString("tuesday"));
                        bookMap.put("wednesday", task.getResult().getString("wednesday"));
                        bookMap.put("thursday", task.getResult().getString("thursday"));
                        bookMap.put("friday", task.getResult().getString("friday"));
                    } else {
                        bookMap.put("monday", "0");
                        bookMap.put("tuesday", "0");
                        bookMap.put("wednesday", "0");
                        bookMap.put("thursday", "0");
                        bookMap.put("friday", "0");
                    }
                    //set the day to occupy
                    if (bookMap.containsKey(key)) {
                        bookMap.put(key, "1");
                    }

                    //update to firestore
                    db.collection("bookings").document(user_id).set(bookMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                increment(key);
                                //store booking as shared preferences
                                SharedPreferences pref = getActivity().getPreferences(getContext().MODE_PRIVATE);
                                SharedPreferences.Editor edt = pref.edit();
                                edt.putInt(key, 1);
                                edt.commit();
                            } else {
                                String e = task.getException().getMessage();
                                Toast.makeText(getActivity(), e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    String e = task.getException().getMessage();
//                    GeneralStaticClass.toast(getActivity(),"FireStore Retrieve Error: " + e);
                    Toast.makeText(getActivity(), "FireStore Retrieve Error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void cancelClass(final String key) {
        FirebaseUser user = mAuth.getCurrentUser();
        user_id = user.getUid();

        // update with existing(if) booking data
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("bookings").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().exists() && task.getResult() != null && task.getResult().getString("monday") != null) {
                        bookMap.put("monday", task.getResult().getString("monday"));
                        bookMap.put("tuesday", task.getResult().getString("tuesday"));
                        bookMap.put("wednesday", task.getResult().getString("wednesday"));
                        bookMap.put("thursday", task.getResult().getString("thursday"));
                        bookMap.put("friday", task.getResult().getString("friday"));
                    } else {
                        bookMap.put("monday", "0");
                        bookMap.put("tuesday", "0");
                        bookMap.put("wednesday", "0");
                        bookMap.put("thursday", "0");
                        bookMap.put("friday", "0");
                    }
                    //set the day to occupy
                    if (bookMap.containsKey(key)) {
                        bookMap.put(key, "0");
                    }

                    //update to firestore
                    db.collection("bookings").document(user_id).set(bookMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                decrement(key);
                                //store as shared preferences
                                SharedPreferences pref = getActivity().getPreferences(getContext().MODE_PRIVATE);
                                SharedPreferences.Editor edt = pref.edit();
                                edt.putInt(key, 0);
                                edt.commit();
                            } else {
                                String e = task.getException().getMessage();
                                Toast.makeText(getActivity(), e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    String e = task.getException().getMessage();
//                    GeneralStaticClass.toast(getActivity(),"FireStore Retrieve Error: " + e);
                    Toast.makeText(getActivity(), "FireStore Retrieve Error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void increment(final String key) {

        firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseFirestore.collection("class").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    String count = "0";
                    String total = "0";

                    if (task.getResult().exists() && task.getResult() != null) {
                        count = String.valueOf(Integer.parseInt(task.getResult().getString("count")) + 1);
                        total = task.getResult().getString("total");
                    }

                    Map<String, String> classMap = new HashMap<>();
                    classMap.put("count", count);
                    classMap.put("total", total);

                    //update to firestore
                    db.collection("class").document(key).set(classMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (!task.isSuccessful()) {
                                String e = task.getException().getMessage();
                                Toast.makeText(getActivity(), e, Toast.LENGTH_SHORT).show();
                            } else {
                                computeSeatRemaining();
                                Toast.makeText(getActivity(), "Booked " + key + " successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    String e = task.getException().getMessage();
                    Toast.makeText(getActivity(), "FireStore Retrieve Error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void decrement(final String key) {

        firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseFirestore.collection("class").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    String count = "0";
                    String total = "0";

                    if (task.getResult().exists() && task.getResult() != null) {
                        count = String.valueOf(Integer.parseInt(task.getResult().getString("count")) - 1);
                        total = task.getResult().getString("total");
                    }

                    Map<String, String> classMap = new HashMap<>();
                    classMap.put("count", count);
                    classMap.put("total", total);

                    //update to firestore
                    db.collection("class").document(key).set(classMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (!task.isSuccessful()) {
                                String e = task.getException().getMessage();
                                Toast.makeText(getActivity(), e, Toast.LENGTH_SHORT).show();
                            } else {
                                computeSeatRemaining();
                                Toast.makeText(getActivity(), "Cancel " + key + " booking successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    String e = task.getException().getMessage();
                    Toast.makeText(getActivity(), "FireStore Retrieve Error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
