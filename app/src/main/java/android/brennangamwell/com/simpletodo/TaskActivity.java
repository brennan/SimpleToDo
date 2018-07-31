package android.brennangamwell.com.simpletodo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;

import android.brennangamwell.com.simpletodo.data.task.TaskDbHelper;
import android.brennangamwell.com.simpletodo.data.task.TaskContract.TaskEntry;
import android.brennangamwell.com.simpletodo.data.assignee.AssigneeDbHelper;
import android.brennangamwell.com.simpletodo.data.assignee.AssigneeContract.AssigneeEntry;

public class TaskActivity extends AppCompatActivity{

  private ArrayList<String> assigneesArray;
  private Intent intent;
  private Spinner assigneesSpinner;
  private String selectedAssignee = null;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task);

    intent = getIntent();

    EditText task = findViewById(R.id.task);
    task.setText(intent.getStringExtra("text"));
    assigneesArray = new ArrayList<>();
    assigneesArray.addAll(intent.getStringArrayListExtra("assignees"));
    assigneesArray.remove(0);
    assigneesArray.add(0, "[Select an assignee to associate the the task...]");

    ArrayAdapter<String> assigneesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, assigneesArray);
    assigneesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    assigneesSpinner = findViewById(R.id.assigneesSpinnerTaskView);
    assigneesSpinner.setAdapter(assigneesAdapter);
    setupListeners();
  }

  private void setupListeners() {
    assigneesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> parent, View view,
          int pos, long id) {
        selectedAssignee = assigneesArray.get(pos);
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {
        // noop
      }
    });
  }

  public void saveTask(View v) {
    EditText task = findViewById(R.id.task);
    String newText = task.getText().toString();
    int taskId = intent.getIntExtra("id", 0);

    updateTask(taskId, newText, selectedAssignee);
  }

  private void updateTask(int taskId, String taskText, @Nullable String assigneeName) {
    int assigneeId = -1;

    if (assigneeName != null) {
      assigneeId = getAssigneeId(assigneeName);
    }

    TaskDbHelper dbWriteHelper = new TaskDbHelper(this);
    SQLiteDatabase db = dbWriteHelper.getWritableDatabase();

    String where = TaskEntry._ID + " = ? ";

    String[] whereArgs = {
        String.valueOf(taskId)
    };

    ContentValues values = new ContentValues();
    values.put(TaskEntry.COLUMN_TASK_TEXT, taskText);

    if (assigneeId > 0) {
      values.put(TaskEntry.COLUMN_ASSIGNEE_ID, assigneeId);
    }

    db.update(TaskEntry.TABLE_NAME, values, where, whereArgs);
  }

  private int getAssigneeId(String assigneeName) {
    AssigneeDbHelper dbReadHelper = new AssigneeDbHelper(this);
    SQLiteDatabase db = dbReadHelper.getReadableDatabase();
    int assigneeId = -1;

    String[] columns = {
        AssigneeEntry._ID
    };

    String where = AssigneeEntry.COLUMN_ASSIGNEE_NAME + " = ? ";

    String[] whereArgs = {
        assigneeName
    };

    Cursor cursor = db.query(
        AssigneeEntry.TABLE_NAME,
        columns,
        where,
        whereArgs,
        null,
        null,
        null);

    try {
      int assigneeColumnId = cursor.getColumnIndex(TaskEntry._ID);

      while (cursor.moveToNext()) {
        assigneeId = cursor.getInt(assigneeColumnId);
      }
    }
    catch (Error e) {
      e.printStackTrace();
    }
    finally {
      cursor.close();
    }
    return assigneeId;
  }
}
