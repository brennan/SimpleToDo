package android.brennangamwell.com.simpletodo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;

import android.brennangamwell.com.simpletodo.data.task.TaskDbHelper;
import android.brennangamwell.com.simpletodo.data.task.TaskContract.TaskEntry;
import android.brennangamwell.com.simpletodo.data.assignee.AssigneeDbHelper;
import android.brennangamwell.com.simpletodo.data.assignee.AssigneeContract.AssigneeEntry;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
  private ArrayAdapter<String> tasksAdapter;
  private ListView lvTasks;
  private ArrayList<String> tasksArray = new ArrayList<>();
  private ArrayList<Integer> taskIdArray = new ArrayList<>();
  private ArrayList<String> assigneesArray = new ArrayList<>();
  private int selectedItemId = -1;
  private ArrayAdapter<String>  assigneesAdapter;
  private Spinner assigneesSpinner;
  private Set<String> assigneeSet = new HashSet<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    getAssignees();

    assigneesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, assigneesArray);
    assigneesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    assigneesSpinner = findViewById(R.id.assigneesSpinner);
    assigneesSpinner.setAdapter(assigneesAdapter);

    getTasks();

    tasksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasksArray);
    lvTasks = findViewById(R.id.lvItems);
    lvTasks.setAdapter(tasksAdapter);

    setupListeners();
  }

  private void setupListeners() {
    lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override public boolean onItemLongClick(AdapterView<?> adapter, View task, int pos, long id) {
          deleteTask(pos);
          tasksArray.clear();
          taskIdArray.clear();
          getTasks();
          tasksAdapter.notifyDataSetChanged();
          return true;
      }
    });

    lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        String text = tasksArray.get(pos);
        selectedItemId = taskIdArray.get(pos);

        Intent intent = new Intent(MainActivity.this, TaskActivity.class);
        intent.putExtra("text", text);
        intent.putExtra("id", selectedItemId);
        intent.putExtra("assignees", assigneesArray);
        startActivity(intent);
      }
    });

    assigneesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> parent, View view,
          int pos, long id) {
        if (assigneesArray.size() > 0) {
          filterTasks(assigneesArray.get(pos));
        }
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {
        // noop
      }
    });
  }

  public void onAddItem(View view) {
    EditText etNewItem = findViewById(R.id.etNewItem);
    String taskText = etNewItem.getText().toString();
    int updated;

    if (taskText.length() <= 0) {
      Toast.makeText(this, "Tasks cannot be blank.", Toast.LENGTH_SHORT).show();
      return;
    }

    updated = addTask(taskText);
    if (updated > 0) {
      tasksArray.add(taskText);
      taskIdArray.add(updated);
      tasksAdapter.notifyDataSetChanged();
      etNewItem.setText("");
    } else {
      Toast.makeText(this, "There was a problem adding this task.", Toast.LENGTH_SHORT).show();
    }
  }

  private void getTasks() {
    TaskDbHelper dbReadHelper = new TaskDbHelper(this);
    SQLiteDatabase db = dbReadHelper.getReadableDatabase();

    String[] columns = {
        TaskEntry._ID, TaskEntry.COLUMN_TASK_TEXT,
    };

    Cursor cursor = db.query(
        TaskEntry.TABLE_NAME,
        columns,
        null,
        null,
        null,
        null,
        null);

    try {
      int taskColumnId = cursor.getColumnIndex(TaskEntry._ID);
      int taskColumnText = cursor.getColumnIndex(TaskEntry.COLUMN_TASK_TEXT);

      while (cursor.moveToNext()) {
        int currentId = cursor.getInt(taskColumnId);
        String currentTask = cursor.getString(taskColumnText);
        taskIdArray.add(currentId);
        tasksArray.add(currentTask);
      }
    }
    catch (Error e) {
      e.printStackTrace();
    }
    finally {
      cursor.close();
      db.close();
    }
  }

  private int addTask(String text) {
    TaskDbHelper dbWriteHelper = new TaskDbHelper(this);
    SQLiteDatabase db = dbWriteHelper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(TaskEntry.COLUMN_TASK_TEXT, text);

    int newTaskId = (int) db.insert(TaskEntry.TABLE_NAME, null, values);

    if (newTaskId < 0) {
      Toast.makeText(this, "There was a problem inserting this value", Toast.LENGTH_LONG).show();
    }

    return newTaskId;
  }

  private void deleteTask(int pos) {
    int id = taskIdArray.get(pos);
    String text = tasksArray.get(pos);

    TaskDbHelper dbWriteHelper = new TaskDbHelper(this);
    SQLiteDatabase db = dbWriteHelper.getWritableDatabase();

    String[] columns = {
        TaskEntry._ID, TaskEntry.COLUMN_TASK_TEXT,
    };

    String where = TaskEntry._ID + " = ? AND " + TaskEntry.COLUMN_TASK_TEXT + " = ? ";

    String[] whereArgs = {
        String.valueOf(id), text
    };

    Cursor cursor = db.query(
        TaskEntry.TABLE_NAME,
        columns,
        where,
        whereArgs,
        null,
        null,
        null);

    try {
      int idColumnIndex = cursor.getColumnIndex(TaskEntry._ID);
      int textColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_TASK_TEXT);
      int currentId = -1;
      String currentText = "";

      if (cursor.getCount() == 1) {
        while (cursor.moveToNext()) {
          currentId = cursor.getInt(idColumnIndex);
          currentText = cursor.getString(textColumnIndex);
        }

        if (id == currentId && text.equals(currentText)) {
          db.delete(
            TaskEntry.TABLE_NAME,
            where,
            whereArgs
          );
        }
      }
    }
    catch (Error e) {
      e.printStackTrace();
    }
    finally {
      cursor.close();
      db.close();
    }
  }

  private void getAssignees() {
    AssigneeDbHelper dbReadHelper = new AssigneeDbHelper(this);
    SQLiteDatabase db = dbReadHelper.getReadableDatabase();

    String[] columns = {
        AssigneeEntry.COLUMN_ASSIGNEE_NAME,
    };

    Cursor cursor = db.query(AssigneeEntry.TABLE_NAME,
        columns,
        null,
        null,
        null,
        null,
        null);

    try {
      int nameColumnIndex = cursor.getColumnIndex(AssigneeEntry.COLUMN_ASSIGNEE_NAME);
      String currentAssignee;

      while (cursor.moveToNext()) {
        currentAssignee = cursor.getString(nameColumnIndex);
        assigneesArray.add(currentAssignee);
      }
      assigneeSet.addAll(assigneesArray);
      assigneesArray.clear();
      assigneesArray.addAll(assigneeSet);
      Collections.sort(assigneesArray);
      assigneesArray.add(0, "[Filter by an assignee's name...]");
    }
    catch (Error e) {
      e.printStackTrace();
    }
    finally {
      cursor.close();
      db.close();
    }
  }

  public void onAddAssignee(View v) {
    EditText etNewAssignee = findViewById(R.id.etNewAssignee);
    String assigneeName = etNewAssignee.getText().toString();

    if (assigneeName.length() <= 0) {
      Toast.makeText(this, "Assignee names cannot be blank.", Toast.LENGTH_SHORT).show();
      return;
    }
    createAssignee(assigneeName);
    assigneesArray.add(assigneeName);
    assigneesAdapter.notifyDataSetChanged();
    etNewAssignee.setText("");
  }

  private void createAssignee(String assigneeName) {
    AssigneeDbHelper dbWriteHelper = new AssigneeDbHelper(this);
    SQLiteDatabase db = dbWriteHelper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(AssigneeEntry.COLUMN_ASSIGNEE_NAME, assigneeName);

    int newAssigneeId = (int) db.insert(AssigneeEntry.TABLE_NAME, null, values);

    if (newAssigneeId < 0) {
      Toast.makeText(this, "There was a problem inserting this value", Toast.LENGTH_LONG).show();
    }
  }

  public void filterTasks(String assigneeName) {
    int assigneeId = getAssigneeId(assigneeName);

    if (assigneeId <= 0) {
      Toast.makeText(this, "This isn't a valid assignee.", Toast.LENGTH_SHORT).show();
      return;
    }

    taskIdArray.clear();
    tasksArray.clear();

    TaskDbHelper dbReadHelper = new TaskDbHelper(this);
    SQLiteDatabase db = dbReadHelper.getReadableDatabase();

    String[] columns = {
        TaskEntry.COLUMN_TASK_TEXT, TaskEntry.COLUMN_ASSIGNEE_ID, TaskEntry._ID
    };

    // TO DO: This is an inefficient query, but the "correct" way was bugging out and not
    // returning any values. Revisit and correct later.
    Cursor cursor = db.query(TaskEntry.TABLE_NAME,
        columns,
        null,
        null,
        null,
        null,
        null
    );

    try {
      int idColumnIndex = cursor.getColumnIndex(TaskEntry._ID);
      int taskColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_TASK_TEXT);
      int assigneeColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_ASSIGNEE_ID);

      while (cursor.moveToNext()) {
        if (assigneeId == cursor.getInt(assigneeColumnIndex)) {
          int currentId = cursor.getInt(idColumnIndex);
          String currentTask = cursor.getString(taskColumnIndex);
          taskIdArray.add(currentId);
          tasksArray.add(currentTask);
        }
      }
      tasksAdapter.notifyDataSetChanged();
    }
    catch (Error e) {
      e.printStackTrace();
    }
    finally {
      cursor.close();
      db.close();
    }
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
      db.close();
    }
    return assigneeId;
  }

  public void resetFilters(View v) {
    taskIdArray.clear();
    tasksArray.clear();
    getTasks();
    tasksAdapter.notifyDataSetChanged();
  }
}
