package android.brennangamwell.com.simpletodo.data.task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.brennangamwell.com.simpletodo.data.task.TaskContract.TaskEntry;

public class TaskDbHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "tasks.db";

  /**
   * Database version. If you change the database schema, you must increment the database version.
   */
  private static final int DATABASE_VERSION = 1;

  /**
   * Constructs a new instance of {@link TaskDbHelper}.
   *
   * @param context of the app
   */
  public TaskDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String SQL_CREATE_ASSIGNEES_TABLE =  "CREATE TABLE " + TaskEntry.TABLE_NAME + " ("
        + TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + TaskEntry.COLUMN_TASK_TEXT + " TEXT NOT NULL, "
        + TaskEntry.COLUMN_ASSIGNEE_ID + ");";

    db.execSQL(SQL_CREATE_ASSIGNEES_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // do nothing for now
  }
}
