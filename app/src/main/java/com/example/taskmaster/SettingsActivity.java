package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;


public class SettingsActivity extends AppCompatActivity {

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        spinner = findViewById(R.id.chooseTeamSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Team 1", "Team 2", "Team 3"});
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

//        TextView email = findViewById(R.id.textView14);
//
//        Amplify.Auth.fetchUserAttributes(
//                attributes -> {
////                    Log.i("AuthDemo", "User attributes = " + attributes.get(2).getValue().toString());
//                    email.setText(attributes.get(2).getValue().toString());
//                },
//                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)
//        );

        findViewById(R.id.saveUserBtn).setOnClickListener(v -> {
            EditText username = findViewById(R.id.editTextPersonName);
            preferenceEditor.putString("username", username.getText().toString());
//            preferenceEditor.apply();

            switch(spinner.getSelectedItem().toString()){
                case "Team 1":
                    preferenceEditor.putString("teamName","Team1");
                    preferenceEditor.apply();
                    break;
                case "Team 2":
                    preferenceEditor.putString("teamName","Team2");
                    preferenceEditor.apply();
                    break;
                case "Team 3":
                    preferenceEditor.putString("teamName","Team3");
                    preferenceEditor.apply();
                    break;
            }

            Toast.makeText(getApplicationContext(),"Saved Username!",Toast.LENGTH_SHORT).show();

        });


        findViewById(R.id.homeBtn).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        });
    }
}