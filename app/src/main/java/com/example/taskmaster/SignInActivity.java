package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;

public class SignInActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        EditText username = findViewById(R.id.editTextTextEmailAddress);
        EditText password = findViewById(R.id.editTextTextPassword);
        username.setHint("Username");
        password.setHint("Password");
        findViewById(R.id.signinBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String  userString = username.getText().toString();
                String passwordString = password.getText().toString();
                
                if( userString.trim().equals(""))
                {
                    username.setError( "Username is required!" );


                }else if(passwordString.trim().equals("")){

                    password.setError( "Password is required!" );


                }else{
                    signing(userString,passwordString);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    username.getText().clear();
                    password.getText().clear();
                }

            }
        });
    }

    public void signing(String username, String password){
        Amplify.Auth.signIn(
                username,
                password,
                result -> {
                    if (result.isSignInComplete()){
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                },
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }
}