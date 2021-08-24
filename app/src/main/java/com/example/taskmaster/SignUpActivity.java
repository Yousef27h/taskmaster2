package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }

        EditText email = findViewById(R.id.signUpEmail);
        EditText username = findViewById(R.id.signUpUser);
        EditText password = findViewById(R.id.signUpPass);


        findViewById(R.id.signUpBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( email.getText().toString().trim().equals(""))
                {
                    email.setError( "Email is required!" );

                    email.setHint("please enter email");
                }else if(username.getText().toString().trim().equals("")){
                    username.setError( "Username is required!" );

                    username.setHint("please enter username");
                }else if(password.getText().toString().trim().equals("")){
                    password.setError( "Password is required!" );

                    password.setHint("please enter password");
                }else{
                    signingUp(email.getText().toString(),
                            username.getText().toString(),
                            password.getText().toString());

                    Intent intent = new Intent(SignUpActivity.this, ConfirmActivity.class);
                    intent.putExtra("username",username.getText().toString());
                    startActivity(intent);
                }

            }
        });
        findViewById(R.id.textView10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

    }

    public void signingUp(String email, String username, String password){
        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email)
                .build();

        Amplify.Auth.signUp(username,
                password,
                options,
                result -> Log.i("AuthQuickStart", "Result: " + result.toString()),
                error -> Log.e("AuthQuickStart", "Sign up failed", error)
        );
    }
}