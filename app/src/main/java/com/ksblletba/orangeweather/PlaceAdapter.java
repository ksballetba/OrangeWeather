package com.ksblletba.orangeweather;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Administrator on 2018/4/12.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private Context mContext;
    private List<Place> mPlaceList;
    private OnItemClickListener mOnItemClickListener = null;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView placeImage;
        TextView placeName;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            placeName = view.findViewById(R.id.place_name);
            placeImage = view.findViewById(R.id.place_image);
        }
    }


    public PlaceAdapter(List<Place> placeList){
        mPlaceList = placeList;
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.place_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Place place = mPlaceList.get(position);
        holder.placeName.setText(place.getName());
        Glide.with(mContext).load(place.getImageId()).into(holder.placeImage);
        if(mOnItemClickListener!=null){
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.cardView,pos);
                }
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.cardView,pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPlaceList.size();
    }
}
