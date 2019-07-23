package com.example.localflix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText email,username,pass,cpass;
    Button register;

    SharedPreferences preferences;

    private FirebaseAuth mAuth;
// ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email=findViewById(R.id.Register_email);
        pass=findViewById(R.id.Register_Password);
        cpass=findViewById(R.id.Register_Confirmpass);
        register=findViewById(R.id.Register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailst=email.getText().toString();
                final String passst=pass.getText().toString();
                String cpassst=cpass.getText().toString();

                if(emailst.length()!=0 && passst.length()!=0 && cpassst.length()!=0){
                    if(passst.equals(cpassst)){
                        if(passst.length()>6){
                        mAuth.createUserWithEmailAndPassword(emailst,passst).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("reglog", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                        preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor data = preferences.edit();
                                        data.putString("email", emailst);
                                        data.putString("password", passst);
                                        data.commit();

                                        Intent intent=new Intent(RegisterActivity.this,HomeScreen.class);
                                        ActivityCompat.finishAffinity(RegisterActivity.this);
                                        startActivity(intent);


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("reglog", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }

                            }


                        });
                        }else{
                            Toast.makeText(getApplicationContext(),"Min password length is 6",Toast.LENGTH_SHORT).show();
                        }


                    }else
                    {
                        Toast.makeText(getApplicationContext(),"Passwords do no match!",Toast.LENGTH_SHORT).show();
                    }


                }else{
                    Toast.makeText(getApplicationContext(),"Fill up the details",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }
}
