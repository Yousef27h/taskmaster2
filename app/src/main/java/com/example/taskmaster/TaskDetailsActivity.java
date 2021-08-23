package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class TaskDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Intent intent = getIntent();

        TextView textTitle = findViewById(R.id.taskName);
        TextView textState = findViewById(R.id.taskState);
        TextView textDescription = findViewById(R.id.taskBody);

        textTitle.setText(intent.getExtras().getString("taskName"));
        textState.setText( intent.getExtras().getString("taskState"));
        textDescription.setText( intent.getExtras().getString("taskBody"));
    }
}