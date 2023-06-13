package orielmoznino.example.alonmanes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import orielmoznino.example.alonmanes.activities.ShowElderlyActivity;
import orielmoznino.example.alonmanes.model.Elderly;

public class ElderlyAdapter extends RecyclerView.Adapter<ElderlyAdapter.MiniElderlyHolder> implements RecyclerView.OnItemTouchListener {

    public ArrayList<Elderly> mList;
    private final Context context;
    private Activity activity;

    public ElderlyAdapter(Activity activity, Context context, ArrayList<Elderly> mList) {
        this.activity= activity;
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MiniElderlyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.elderly_tile, parent, false);
        return new MiniElderlyHolder(v);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MiniElderlyHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(mList.get(position).url).into(holder.ivImage);
        holder.tvName.setText(String.valueOf(mList.get(position).userName));
        holder.tvCity.setText(String.valueOf(mList.get(position).city));
        final int finalI = position;
        holder.lBackground.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ShowElderlyActivity.class);
            intent.putExtra(Constants.HASH_MAP_INTENT_FROM_ELDERLY_MAIN_TO_SHOW_ELDERLY, mList.get(finalI).toMap());
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MiniElderlyHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvName;
        TextView tvCity;
        LinearLayout lBackground;

        public MiniElderlyHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvCity = itemView.findViewById(R.id.tvCity);
            lBackground = itemView.findViewById(R.id.lBackground);

        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}