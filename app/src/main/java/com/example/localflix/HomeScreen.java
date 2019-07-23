package com.example.localflix;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeScreen extends AppCompatActivity implements Movies.OnFragmentInteractionListener , Series.OnFragmentInteractionListener,Profile.OnFragmentInteractionListener{
    private TextView mTextMessage;

    private ActionBar toolbar;

    List<MoviesModel> moviesModels=new ArrayList<>();
    List<SeriesModel> seriesModels=new ArrayList<>();

    Movies movies;
    Series series;
    Profile profile;

    Fragment active;

    FragmentManager fm=getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //Toast.makeText(getApplicationContext(),"Movies",Toast.LENGTH_SHORT).show();
                    fm.beginTransaction().hide(active).show(movies).commit();
                    active=movies;
                    toolbar.setTitle("Movies");
                    //loadMovies(movies);

                    return true;
                case R.id.navigation_dashboard:
                    toolbar.setTitle("Series");
                    fm.beginTransaction().hide(active).show(series).commit();
                    active=series;

                   // loadFragment(series);
                    return true;

                case R.id.navigation_notifications:
                    toolbar.setTitle("Profile");
                    fm.beginTransaction().hide(active).show(profile).commit();
                    active=profile;
                   // mTextMessage.setText(R.string.title_notifications);
                    //Toast.makeText(HomeScreen.this,"Not available yet",Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_home_screen);
        toolbar = getSupportActionBar();



        movies=new Movies();
        series=new Series();
        profile=new Profile();
        init();

        // load the store fragment by default




    }
    private void init(){

        toolbar.setTitle("Movies");
        active=movies;
        loadFragment(movies);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Log.d("homescreen","here");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        moviesModels=new ArrayList<>();
        db.collection("movies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("homescreen", document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                MoviesModel moviesModel = new MoviesModel();
                                moviesModel.setName(data.get("name").toString());
                                moviesModel.setPath(data.get("path").toString());
                                moviesModel.setImage(data.get("image").toString());
                                moviesModels.add(moviesModel);
                            }
                            Movies.prepareMovies(moviesModels);
                        } else {
                            Log.d("homescreen", "Error getting documents: ", task.getException());
                        }
                    }
                });

        seriesModels=new ArrayList<>();
        db.collection("series")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("homescreen", document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                SeriesModel seriesModel = new SeriesModel();
                                seriesModel.setName(data.get("name").toString());
                                seriesModel.setSeasons(data.get("seasons").toString());
                                seriesModel.setImage(data.get("image").toString());
                                seriesModels.add(seriesModel);
                            }
                            Log.d("serieslist",""+seriesModels.size());
                            Series.prepareSeries(seriesModels);
                        } else {
                            Log.d("homescreen", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.frame_container,fragment);
        transaction.add(R.id.frame_container,series);
        transaction.add(R.id.frame_container,profile);
        transaction.hide(series);
        transaction.hide(profile);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private void loadMovies(Fragment fragment){
        Movies.prepareMovies(moviesModels);
        loadFragment(fragment);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing here.
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
