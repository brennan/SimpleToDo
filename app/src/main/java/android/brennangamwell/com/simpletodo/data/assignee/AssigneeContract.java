package android.brennangamwell.com.simpletodo.data.assignee;

import android.provider.BaseColumns;

public final class AssigneeContract {

  AssigneeContract() {}

  public static final class AssigneeEntry implements BaseColumns {

    public final static String TABLE_NAME = "assignees";

    /**
     * Unique ID number for the assignee (only for use in the database table).
     *
     * Type: INTEGER
     */
    public final static String _ID = BaseColumns._ID;

    /**
     * Name of the ASSIGNEE.
     *
     * Type: TEXT
     */
    public final static String COLUMN_ASSIGNEE_NAME ="name";
  }
}
