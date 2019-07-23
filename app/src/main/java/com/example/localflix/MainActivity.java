package com.example.localflix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    static SharedPreferences sharedPreferences;
    static Context mContext;

    private DatabaseReference mDatabase;
// ...
    static String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         sharedPreferences=getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
         mContext=getApplicationContext();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("link");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Object object = dataSnapshot.getValue();
                link=object.toString()+"/";
                init();
                Log.d("linkdata",""+link);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d("linkdata", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };



        mDatabase.addValueEventListener(postListener);


    }
    public static String getLink(){
        sharedPreferences=mContext.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean flag=sharedPreferences.getBoolean("local",false);
        if(flag){
            return "http://192.168.0.3:50000/";
        }
        else {
            return link;
        }

    }

    public void init(){
        sharedPreferences=getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        String email=sharedPreferences.getString("email","0");
        String password=sharedPreferences.getString("password","0");

        if (email == "0" && password == "0" || (email.length()==0 || password.length()==0) || !sharedPreferences.contains("email")) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            //Toast.makeText(this, "" + email + "" + password, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this,HomeScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.finishAffinity(this);
            startActivity(i);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
