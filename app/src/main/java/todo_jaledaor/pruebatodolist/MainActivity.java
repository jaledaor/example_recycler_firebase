package todo_jaledaor.pruebatodolist;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private EditText addTaskBox;
    private FirebaseAuth mAuth_control;
    private FirebaseDatabase database_control;
    private DatabaseReference reference_control;
    private List<Task> allTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        allTask = new ArrayList<Task>();
        mAuth_control = FirebaseAuth.getInstance();
        database_control = FirebaseDatabase.getInstance();
        reference_control = database_control.getReference("Task");
        addTaskBox = (EditText)findViewById(R.id.add_task_box);
        recyclerView = (RecyclerView)findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        Button addTaskButton = (Button)findViewById(R.id.add_task_button);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredTask = addTaskBox.getText().toString();
                if(TextUtils.isEmpty(enteredTask)){
                    Toast.makeText(MainActivity.this, "You must enter a task first", Toast.LENGTH_LONG).show();
                    return;
                }
                if(enteredTask.length() < 6){
                    Toast.makeText(MainActivity.this, "Task count must be more than 6", Toast.LENGTH_LONG).show();
                    return;
                }
                Task taskObject =new Task(enteredTask);
                reference_control.push().setValue(taskObject);
                addTaskBox.setText("");
            }
        });
        reference_control.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getAllTask(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getAllTask(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                taskDeletion(dataSnapshot);
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void getAllTask(DataSnapshot dataSnapshot){
        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
            String taskTitle = singleSnapshot.getValue(String.class);
            allTask.add(new Task(taskTitle));
            recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, allTask);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }
    private void taskDeletion(DataSnapshot dataSnapshot){
        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            String taskTitle = singleSnapshot.getValue(String.class);
            for(int i = 0; i < allTask.size(); i++){
                if(allTask.get(i).getTask().equals(taskTitle)){
                    allTask.remove(i);
                }
            }
            Log.d(TAG, "Task tile " + taskTitle);
            recyclerViewAdapter.notifyDataSetChanged();
            recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, allTask);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }
}