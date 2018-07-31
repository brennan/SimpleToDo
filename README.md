# A Brief Tour

This simple to-do app lets you: 
- Add tasks
- Delete tasks
- Edit tasks
- Add task assignees
- Associate tasks with assignees
- Filter tasks by assignee

To add a task, simply add your task to the "Enter a task" field and click "Add item".

To delete a task, long touch a task.

To edit a task, tap a task. This will open a new view, where you can edit the task text or 
associate the task with an assignee. 

To add an assignee, enter the assignee's name in the "Enter a new assignee" field.

To view an assignee's tasks, simply select their name in the spinner.

This app persists data in the device's SQL lite database.

## Areas for Improvement

- Validating queries and error handling: In most cases, errors thrown when accessing the database
don't interrupt code execution, which could cause the app to crash. In future iterations, error 
checking and handling will be added in all cases.

- Making db queries more efficient: Instead making sure the view is in sync with the db without
pulling new data by clearing the view and re-querying.

- Ability to disassociate users from tasks.

- Ability to reset the main screen to view all tasks after filtering by a single assignee's name.

- The UI - it leaves much to be desired. :)

- DRY up the code - many of the methods are repeated in the app's MainActivity and 
TaskActivity - this could could be shared across the activities utilizing fragments or otherwise.

- String values, colors, etc., should go in the `values` folder for straightforward localization.

- Unit tests.