package com.example.localflix;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class EpisodesActivity extends AppCompatActivity {

    ArrayList<String> seasons=new ArrayList<>();
    //ArrayList<String>seasons_id=new ArrayList<>();

    Map<String,String> seasons_id=new HashMap<String,String>();

    ArrayList<EpisodeModel> episodeModels=new ArrayList<>();
    EpisodesAdapter episodesAdapter;

    RecyclerView recyclerView;

    String series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        Intent intent=getIntent();
        series=intent.getStringExtra("series");

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(""+series);

        recyclerView=findViewById(R.id.episodes_recyclerview);

        episodesAdapter=new EpisodesAdapter(getApplicationContext(),episodeModels);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(episodesAdapter);




        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),  android.R.layout.simple_spinner_item,seasons );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner=findViewById(R.id.seasons_spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setEnabled(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(""+series).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d("homescreen", document.getId() + " => " + document.getData());
                        Map<String, Object> data = document.getData();
                        Log.d("episodes", "" + data + "" + document.getId());
                        seasons.add(data.get("name").toString());
                        seasons_id.put(""+data.get("name").toString(),""+document.getId());

                    }
                    Collections.sort(seasons);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner.setSelection(i);
                loadEpisodes(seasons.get(i),seasons_id.get(""+seasons.get(i)));
                //Toast.makeText(getApplicationContext(),""+seasons.get(i)+" id="+seasons_id.get(""+seasons.get(i)),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void loadEpisodes(String seasons,String ref){

        episodeModels.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(""+series+"/"+ref+"/episodes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("episodesscreen", document.getId() + " => " + document.getData());
                            EpisodeModel episodeModel=new EpisodeModel();
                            episodeModel.setNo(document.get("no").toString());
                            episodeModel.setPath(document.get("path").toString());
                            episodeModel.setSubtitles(document.get("subtitles").toString());
                            episodeModel.setTitle(document.get("title").toString());
                            episodeModels.add(episodeModel);


                    }
                    Collections.sort(episodeModels);
                    episodesAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
