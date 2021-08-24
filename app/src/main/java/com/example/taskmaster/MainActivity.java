package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Task> tasks = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private Handler handler;
    private SharedPreferences sharedPreferences;
    private String teamName = "Team1";

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }



//        Team team = Team.builder().name("Team1").build();
//        Team team2 = Team.builder().name("Team2").build();
//        Team team3 = Team.builder().name("Team3").build();
//
//        Amplify.API.mutate(ModelMutation.create(team1),
//                success -> Log.i("Tutorial", "Deleted item API: " + success.getData()),
//                error -> Log.e("Tutorial", "Could not save item to DataStore", error)
//        );
//        Amplify.API.mutate(ModelMutation.create(team2),
//                success -> Log.i("Tutorial", "Deleted item API: " + success.getData()),
//                error -> Log.e("Tutorial", "Could not save item to DataStore", error)
//        );
//        Amplify.API.mutate(ModelMutation.create(team3),
//                success -> Log.i("Tutorial", "Deleted item API: " + success.getData()),
//                error -> Log.e("Tutorial", "Could not save item to DataStore", error)
//        );

        handler = new Handler(Looper.getMainLooper(),
                message -> {
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                    return false;
                });

        Button addActivityBtn = findViewById(R.id.addActivityBtn);
        addActivityBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,AddTaskActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        teamName = sharedPreferences.getString("teamName","Team1");

        TextView username = findViewById(R.id.usernameTitle);
        username.setText(sharedPreferences.getString("username","User")+" Tasks");

        if (isNetworkAvailable(getApplicationContext()) && tasks.isEmpty()){
            Amplify.API.query(ModelQuery.list(Task.class),
                    response -> {
                        for (Task task : response.getData()) {
                            if ( !this.tasks.contains(task) && task != null && task.getTeam() != null ) {
                                if (task.getTeam().getName().equals(teamName)){
                                    this.tasks.add(task);
                                }
                            }
                            Log.i("API", "Query: " + task);
                        }
                        handler.sendEmptyMessage(1);
                    }, failure -> Log.e("Add", "Could not query DataStore", failure)
            );
        }else {
            Amplify.DataStore.query(Task.class,tasks -> {
                        while (tasks.hasNext()) {
                            Task task = tasks.next();
                            if (task.getTitle() != null && !this.tasks.contains(task)) {this.tasks.add(task);}
                            Log.i("Datastore", "Query: " + task);
                        }
                    }, failure -> Log.e("Add", "Could not query DataStore", failure)
                    );
        }
        setTaskAdapter();
    }

    public void setTaskAdapter(){
        recyclerView = findViewById(R.id.recyclerView);
        taskAdapter = new TaskAdapter(tasks, new TaskAdapter.OnTaskListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                intent.putExtra("taskName", tasks.get(position).getTitle());
                intent.putExtra("taskBody", tasks.get(position).getBody());
                intent.putExtra("taskState", tasks.get(position).getState());
                startActivity(intent);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDeleteClick(int position) {
                Task task = tasks.remove(position);

                Amplify.API.mutate(ModelMutation.delete(task),
                        success -> Log.i("Tutorial", "Deleted item API: " + success.getData()),
                        error -> Log.e("Tutorial", "Could not save item to DataStore", error)
                );

                Amplify.DataStore.delete(task,
                        success -> Log.i("Tutorial", "Deleted item DataStore: " + success.item().getTitle()),
                        error -> Log.e("Tutorial", "Could not save item to DataStore", error)
                );

                taskAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Item Deleted!", Toast.LENGTH_SHORT).show();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(taskAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsItem) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}