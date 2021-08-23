package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;

import java.util.ArrayList;
import java.util.List;

public class AddTaskActivity extends AppCompatActivity {
    private Spinner spinner;
    private Spinner teamSpinner;
    private String[] state = new String[]{"new", "assigned", "in progress", "complete"};
    private List<Team> teams =  new ArrayList<>();
    private Handler handler;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }




        handler = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message message) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                        return false;
                    }
                });

//        Amplify.API.query(ModelQuery.list(Team.class),
//                response -> {
//                    for (Team team : response.getData()) {
//                            this.teams.add(team);
//                        Log.i("API", "Query: " + team);
//                    }
//                    handler.sendEmptyMessage(1);
//                }, failure -> Log.e("Add", "Could not query DataStore", failure)
//        );

        Button saveBtn = findViewById(R.id.saveBtn);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                state);

        spinner = findViewById(R.id.spinner);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Team 1", "Team 2", "Team 3"});
        teamSpinner = findViewById(R.id.teamSpinner);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(dataAdapter2);

        getTeams();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText taskTitleInput = findViewById(R.id.inputTaskTitle);
                EditText taskDescriptionInput = findViewById(R.id.inputTaskDescription);


//                rg = findViewById(R.id.radioGroup);
//                int selectedId = rg.getCheckedRadioButtonId();
//                radioButton = findViewById(selectedId);
//                Log.i("radio", "onClick: "+ radioButton.getTransitionName());
//                Log.i("radio", "onClick2: "+ radioButton.toString());
//                Log.i("radio", "onClick3: "+ radioButton.getId());
//                Log.i("radio", "onClick4: "+ radioButton.getTransitionName());

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
            }

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
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}