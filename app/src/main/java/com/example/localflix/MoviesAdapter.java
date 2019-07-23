package com.example.localflix;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private List<MoviesModel> moviesModelList;
    private Context mContext;
    

    public MoviesAdapter(Context mContext,List<MoviesModel> moviesModels){
        this.mContext=mContext;
        moviesModelList=moviesModels;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movies_card, parent, false);

        Log.d("linkdata","here"+MainActivity.getLink());

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MoviesModel moviesModel = moviesModelList.get(position);
        holder.title.setText(moviesModel.getName());


        // loading album cover using Picasso library
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,""+moviesModel.getName(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mContext,VlcPlayer.class);
                intent.putExtra("url",MainActivity.getLink()+moviesModel.getPath());
                mContext.startActivity(intent);
            }
        });
        Log.d("moviesadapter",""+MainActivity.getLink()+moviesModel.getImage());
        Picasso.get().load(MainActivity.getLink()+moviesModel.getImage()).into(holder.thumbnail);



    }

    @Override
    public int getItemCount() {
        return moviesModelList.size();
    }



}
