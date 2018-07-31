package android.brennangamwell.com.simpletodo.data.task;

import android.provider.BaseColumns;

public final class TaskContract {

  TaskContract() {}

  public static final class TaskEntry implements BaseColumns {

    public final static String TABLE_NAME = "tasks";

    /**
     * Unique ID number for the task (only for use in the database table).
     *
     * Type: INTEGER
     */
    public final static String _ID = BaseColumns._ID;

    /**
     * Name of the TASK.
     *
     * Type: TEXT
     */
    public final static String COLUMN_TASK_TEXT ="name";

    /**
     * ID of the ASSIGNEE.
     *
     * Type: INTEGER
     */
    public final static String COLUMN_ASSIGNEE_ID ="assignee_id";
  }
}
