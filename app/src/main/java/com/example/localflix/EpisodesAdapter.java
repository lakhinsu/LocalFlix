package com.example.localflix;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder> {

    private List<EpisodeModel> episodeModelList;
    //final String link="https://a6e3fa80.ngrok.io/";
    private Context mContext;


    MyTask temp;

    public EpisodesAdapter(Context mContext,List<EpisodeModel> episodeModels){
        this.mContext=mContext;
        episodeModelList=episodeModels;
    }

    public class EpisodesViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public ImageButton download;

        public EpisodesViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.episodes_title);
            thumbnail = (ImageView) view.findViewById(R.id.episodes_thumbnail);
            download=(ImageButton) view.findViewById(R.id.episodes_download);

        }


    }
    @Override
    public EpisodesAdapter.EpisodesViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.episodes_card, parent, false);

        Log.d("linkdata","here"+MainActivity.getLink());

        return new EpisodesAdapter.EpisodesViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final EpisodesAdapter.EpisodesViewHolder holder, int position) {

        final EpisodeModel episodeModel = episodeModelList.get(position);
        holder.title.setText(episodeModel.getNo()+"."+episodeModel.getTitle());
        //holder.seasons.setText(episodeModel.getSeasons()+" Seasons");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,""+episodeModel.getTitle(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mContext,VlcPlayer.class);
                intent.putExtra("url",""+MainActivity.getLink()+episodeModel.getPath());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });


        try {

            MyTask myTask=new MyTask(MainActivity.getLink()+episodeModel.getPath(),holder.thumbnail);
            temp=myTask;
            myTask.execute();
            //holder.thumbnail.setImageBitmap(retriveVideoFrameFromVideo(link + episodeModel.getPath()));

        }catch (Throwable e){
            e.printStackTrace();
        }

        holder.thumbnail.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_movie_filter_black_24dp));

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,""+episodeModel.getTitle(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mContext,VlcPlayer.class);
                intent.putExtra("url",""+MainActivity.getLink()+episodeModel.getPath());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });




        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"Not Available Yet",Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public int getItemCount() {
        return episodeModelList.size();
    }




    private class MyTask extends AsyncTask<Void, Void, Void> {
        String videoPath;
        ImageView imageView;
        Bitmap bitmap = null;

        //initiate vars
        public MyTask(String url, ImageView imageView) {
            super();
            //my params here
            this.videoPath=url;
            this.imageView=imageView;
        }

        protected Void doInBackground(Void... params) {
            //do stuff

            MediaMetadataRetriever mediaMetadataRetriever = null;
            try
            {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                else
                    mediaMetadataRetriever.setDataSource(videoPath);
                bitmap = mediaMetadataRetriever.getFrameAtTime();

            } catch (Throwable e) {
                e.printStackTrace();

            } finally {
                if (mediaMetadataRetriever != null) {
                    Log.d("taskrel","here");
                    mediaMetadataRetriever.release();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //do stuff
            //myMethod(myValue);
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if(temp.getStatus().equals(AsyncTask.Status.RUNNING))
        {
            temp.cancel(true);
        }

    }
}
