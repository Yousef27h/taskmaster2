package com.example.taskmaster;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;

//import android.content.Context;
//import android.net.ConnectivityManager;
import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
import android.util.Log;
//import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//import com.amplifyframework.AmplifyException;
//import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
//import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;

import java.util.ArrayList;
import java.util.List;

public class AddTaskActivity extends AppCompatActivity {
    private Spinner spinner;
    private Spinner teamSpinner;
    private final String[] state = new String[]{"new", "assigned", "in progress", "complete"};
    private final List<Team> teams =  new ArrayList<>();
//    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);



        Button saveBtn = findViewById(R.id.saveBtn);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                state);

        spinner = findViewById(R.id.spinner);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Team 1", "Team 2", "Team 3"});
        teamSpinner = findViewById(R.id.teamSpinner);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(dataAdapter2);

        getTeams();

        saveBtn.setOnClickListener(view -> {

            EditText taskTitleInput = findViewById(R.id.inputTaskTitle);
            EditText taskDescriptionInput = findViewById(R.id.inputTaskDescription);

            switch(teamSpinner.getSelectedItem().toString()){
                case "Team 1":
                    Task task = Task.builder().title(taskTitleInput.getText().toString()).body(taskDescriptionInput.getText().toString()).state(spinner.getSelectedItem().toString()).team(teams.get(0)).build();
                    saveToAPI(task);
                    saveToDatastore(task);
                    break;
                case "Team 2":
                    Task task2 = Task.builder().title(taskTitleInput.getText().toString()).body(taskDescriptionInput.getText().toString()).state(spinner.getSelectedItem().toString()).team(teams.get(1)).build();
                    saveToAPI(task2);
                    saveToDatastore(task2);
                    break;
                case "Team 3":
                    Task task3 = Task.builder().title(taskTitleInput.getText().toString()).body(taskDescriptionInput.getText().toString()).state(spinner.getSelectedItem().toString()).team(teams.get(2)).build();
                    saveToAPI(task3);
                    saveToDatastore(task3);
                    break;
            }

                Toast toast = Toast.makeText(getApplicationContext(),"Submitted!", Toast.LENGTH_SHORT);
                toast.show();
        });
    }

    private void getTeams() {
        Amplify.API.query(
                ModelQuery.list(Team.class),
                response -> {
                    for (Team team : response.getData()) {
                        this.teams.add(team);
                        Log.i("MyAmplifyApp", team.getName());
                    }
                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );
    }

    public void saveToAPI(Task task){
        Amplify.API.mutate(ModelMutation.create(task),
                success -> Log.i("Tutorial", "Saved item: " + success.getData()),
                error -> Log.e("Tutorial", "Could not save item to API", error)
        );
    }

    public void saveToDatastore(Task task){
        Amplify.DataStore.save(task,
                success -> Log.i("Tutorial", "Saved item: " + success.item()),
                error -> Log.e("Tutorial", "Could not save item to DataStore", error)
        );
    }

}