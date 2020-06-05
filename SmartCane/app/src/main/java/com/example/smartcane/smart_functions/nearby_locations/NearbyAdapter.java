package com.example.smartcane.smart_functions.nearby_locations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcane.R;
import com.example.smartcane.smart_functions.api_google.model.GooglePlace;

import java.util.ArrayList;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.NearbyViewHolder> {
    private ArrayList<GooglePlace> mDataset;
    private OnItemClickListener OnItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        OnItemClickListener = listener;
    }

    static class NearbyViewHolder extends RecyclerView.ViewHolder {

        TextView mTvName;
        TextView mTvAddress;
        TextView mTvRating;

        NearbyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvAddress = itemView.findViewById(R.id.tv_address);
            mTvRating = itemView.findViewById(R.id.tv_rating);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    NearbyAdapter(ArrayList<GooglePlace> myDataset) {
        mDataset = myDataset;
    }


    @NonNull
    @Override
    public NearbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_item, parent, false);
        return new NearbyViewHolder(v, OnItemClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull NearbyViewHolder holder, int position) {
        holder.mTvName.setText(mDataset.get(position).getName());
        holder.mTvAddress.setText(mDataset.get(position).getVicinity());
        holder.mTvRating.setText((mDataset.get(position).getRating().equals(" ")) ? "Ainda não tem avaliações" : "Avaliação: " + mDataset.get(position).getRating() + " estrelas");
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}