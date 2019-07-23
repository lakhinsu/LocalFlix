package com.example.localflix;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {

    private List<SeriesModel> seriesModelList;
    private Context mContext;

    public SeriesAdapter(Context mContext,List<SeriesModel> seriesModels){
        this.mContext=mContext;
        seriesModelList=seriesModels;
    }

    public class SeriesViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public TextView seasons;

        public SeriesViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.series_title);
            thumbnail = (ImageView) view.findViewById(R.id.series_thumbnail);
            seasons=(TextView) view.findViewById(R.id.series_season);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),""+title.getText().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(view.getContext(),EpisodesActivity.class);
                    intent.putExtra("series",""+title.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }


    }

    @Override
    public SeriesViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.series_card, parent, false);


        return new SeriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {

        final SeriesModel seriesModel = seriesModelList.get(position);
        holder.title.setText(seriesModel.getName());
        holder.seasons.setText(seriesModel.getSeasons()+" Seasons");
        Picasso.get().load(MainActivity.getLink()+seriesModel.getImage()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return seriesModelList.size();
    }
}
