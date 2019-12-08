package student.inti.tuitionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private Context context;
    private BookClassFragment bookClassFragment;

    private String mTitle[] = {
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday"
    };

    private List<String> mSeatRemainedArray;

    private String mTimeslot[] = {
            "7.00pm - 9.00pm",
            "7.30pm - 9.30pm",
            "8.00pm - 10.00pm",
            "7.30pm - 9.30pm",
            "8.00pm - 10.00pm"
    };

    private int image[] = {
            R.drawable.icon_mon,
            R.drawable.icon_tues,
            R.drawable.icon_wed,
            R.drawable.icon_thurs,
            R.drawable.icon_fri,
    };

    public ClassAdapter(Context c, List<Map<String, String>> mSeatRemained, BookClassFragment bookClassFragment) {
        context = c;
        mSeatRemainedArray = new ArrayList<>();
        this.bookClassFragment = bookClassFragment;

        for (String s : mTitle) {
            for (int i = 0; i < mSeatRemained.size(); i++) {
                if (mSeatRemained.get(i).containsKey(s.toLowerCase())) {
                    mSeatRemainedArray.add(mSeatRemained.get(i).get(s.toLowerCase()));
                }
            }
        }
    }

    public void setData(List<Map<String, String>> mSeatRemained) {
        mSeatRemainedArray.clear();
        for (String s : mTitle) {
            for (int i = 0; i < mSeatRemained.size(); i++) {
                if (mSeatRemained.get(i).containsKey(s.toLowerCase())) {
                    mSeatRemainedArray.add(mSeatRemained.get(i).get(s.toLowerCase()));
                }
            }
        }
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_item, parent, false);

        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, final int position) {

        holder.titleClass.setText(mTitle[position]);
        holder.seatRemained.setText(R.string.seat_remained);
        holder.seatRemainedNum.setText(mSeatRemainedArray.get(position));
        holder.icon.setImageResource(image[position]);
        holder.bookclass_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookClassFragment.showPopup(mTitle[position], Integer.valueOf(mSeatRemainedArray.get(position)), image[position], mTimeslot[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTitle.length;
    }

    public class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView titleClass;
        //TextView timeslotTextview;
        TextView seatRemained;
        TextView seatRemainedNum;
        ImageView icon;
        LinearLayout bookclass_item;

        public ClassViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            titleClass = (TextView) itemLayoutView.findViewById(R.id.title_textview);
            //timeslotTextview = (TextView)itemLayoutView.findViewById(R.id.timeslotTextview);
            seatRemained = (TextView) itemLayoutView.findViewById(R.id.seat_remained_textview);
            seatRemainedNum = (TextView) itemLayoutView.findViewById(R.id.seat_remained_num);
            icon = (ImageView) itemLayoutView.findViewById(R.id.image);
            bookclass_item = (LinearLayout) itemLayoutView.findViewById(R.id.bookclass_item);
        }
    }

}
