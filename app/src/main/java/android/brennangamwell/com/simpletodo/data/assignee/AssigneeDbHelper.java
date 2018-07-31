package android.brennangamwell.com.simpletodo.data.assignee;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.brennangamwell.com.simpletodo.data.assignee.AssigneeContract.AssigneeEntry;

public class AssigneeDbHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "assignees.db";

  /**
   * Database version. If you change the database schema, you must increment the database version.
   */
  private static final int DATABASE_VERSION = 1;

  /**
   * Constructs a new instance of {@link AssigneeDbHelper}.
   *
   * @param context of the app
   */
  public AssigneeDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String SQL_CREATE_ASSIGNEES_TABLE =  "CREATE TABLE " + AssigneeEntry.TABLE_NAME + " ("
        + AssigneeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + AssigneeEntry.COLUMN_ASSIGNEE_NAME + " TEXT NOT NULL);";

    db.execSQL(SQL_CREATE_ASSIGNEES_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // do nothing for now
  }
}
