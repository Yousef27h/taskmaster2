package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amplifyframework.core.Amplify;

public class ConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        EditText confirmCode = findViewById(R.id.editTextNumber);

        findViewById(R.id.verifyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAcc(username, confirmCode.getText().toString());
            }
        });

    }

    public void confirmAcc(String username, String confirmCode){
        Amplify.Auth.confirmSignUp(
                username,
                confirmCode,
                result -> {
                    if (result.isSignUpComplete()){
                       Intent intent = new Intent(ConfirmActivity.this, SignInActivity.class);
                       startActivity(intent);
                    }
                },
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }
}