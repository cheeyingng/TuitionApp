package student.inti.tuitionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    Context context;
    private MyBookingsFragment myBookingsFragment;
    private List<String> mVisibilityArray;
    private List<String> mTimeArray;
    private List<Integer> mImageArray;

    public BookingAdapter(Context c, List<String> mVisibility, MyBookingsFragment myBookingsFragment) {
        context = c;
        mVisibilityArray = new ArrayList<>();
        mTimeArray = new ArrayList<>();
        mImageArray = new ArrayList<>();
        this.myBookingsFragment = myBookingsFragment;
        this.mVisibilityArray = mVisibility;

        for (String string : mVisibility) {
            switch (string) {
                case "Monday":
                    mTimeArray.add("7.00pm - 9.00pm");
                    mImageArray.add(R.drawable.icon_mon);
                    break;

                case "Tuesday":
                    mTimeArray.add("7.30pm - 9.30pm");
                    mImageArray.add(R.drawable.icon_tues);
                    break;

                case "Wednesday":
                    mTimeArray.add("8.00pm - 10.00pm");
                    mImageArray.add(R.drawable.icon_wed);
                    break;

                case "Thursday":
                    mTimeArray.add("7.30pm - 9.30pm");
                    mImageArray.add(R.drawable.icon_thurs);
                    break;

                case "Friday":
                    mTimeArray.add("8.00pm - 10.00pm");
                    mImageArray.add(R.drawable.icon_fri);
                    break;
            }
        }
    }


    @NonNull
    //@Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_bookings_item, parent, false);
        return new BookingViewHolder(view);
    }

    //@Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.titleBooking.setText(mVisibilityArray.get(position));
        holder.time.setText(mTimeArray.get(position));
        holder.icon.setImageResource(mImageArray.get(position));
    }

    //@Override
    public int getItemCount() {
        return mVisibilityArray.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView titleBooking;
        TextView time;
        ImageView icon;

        public BookingViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            titleBooking = (TextView) itemLayoutView.findViewById(R.id.title_textview_bookings);
            time = (TextView) itemLayoutView.findViewById(R.id.time_textview_bookings);
            icon = (ImageView) itemLayoutView.findViewById(R.id.image_bookings);
        }
    }

}
