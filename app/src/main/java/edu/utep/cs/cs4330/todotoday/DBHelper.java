package edu.utep.cs.cs4330.todotoday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
/**
 * Created by juanrazo on 4/21/16.
 */
public class DBHelper extends  SQLiteOpenHelper{

    //TASK 1: DEFINE THE DATABASE TABLE
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "toDo_Today";
    private static final String DATABASE_TABLE = "to_Do_Items";

    //TASK 2: DEFINE THE COLUMN NAMES FOR THE TABLE
    private static final String KEY_TASK_ID = "id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IS_DONE = "is_done";

    private int taskCount; //COUNTS THE NUMBER OF TASKS ON THE LIST

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlStatement = "CREATE TABLE" + DATABASE_TABLE + "("
                + KEY_TASK_ID + " INTEGER PRIMARY KEY, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_IS_DONE + " INTEGER" + ")";
        db.execSQL(sqlStatement);
        taskCount = 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    //*********** DATABASE OPERATIONS: ADD, EDIT, DELETE

    //ADD A ToDo TASK TO THE DATABSE
    public void addToDoItem(ToDo_Item task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        taskCount++;
        //ADD KEY-VALUE PAIR INFORMATION FOR THE TASK DESCRIPTION
        values.put(KEY_TASK_ID, taskCount);

        //ADD KEY-VALUE PAIR INFORMATION FOR THE TASK DESCRIPTION
        values.put(KEY_DESCRIPTION, task.getDescription()); //task name

        //ADD KEY-VALUE PAIR INFORMATION FOR IS_DONE
        // 0- NOT DONE, 1 - IS DONE
        values.put(KEY_IS_DONE, task.getIs_done());

        // INSERT THE ROW IN THE TABLE
        db.insert(DATABASE_TABLE, null, values);

        //CLOSE THE DATABASE CONNECTION
        db.close();
    }

    // EDIT A TODO TASK IN THE DATABASE
    public void editTaskItem(ToDo_Item task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_IS_DONE, task.getIs_done());

        db.update(DATABASE_TABLE, values, KEY_TASK_ID + " = ?",
                new String[]{
                        String.valueOf(task.getId())
                });
        db.close();
    }

    // RETURN A SPECIFIC TODO TASK IN THE DATABASE
    public ToDo_Item getToDo_Task(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                DATABASE_TABLE,
                new String[]{KEY_TASK_ID, KEY_DESCRIPTION, KEY_IS_DONE},
                KEY_TASK_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);
        ToDo_Item task = new ToDo_Item(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2));

        db.close();
        return task;
    }

    // DELETE A SPECIFIC TODO TASK FROM THE DATABASE
    public  void deleteTaskItem (ToDo_Item task){
        SQLiteDatabase database = this.getReadableDatabase();

        // DELETE THE TABLE ROW
        database.delete(DATABASE_TABLE, KEY_TASK_ID + " = ?",
                new String[]{String.valueOf(task.getId())});

        database.close();;
    }

    public int getTaskCount() {
        return  taskCount;
    }

    // ADD A TODO TASK TO THE DATABASE
    public ArrayList<ToDo_Item> getAllTaskItems(){
        ArrayList<ToDo_Item> taskList = new ArrayList<ToDo_Item>();
        String queryList = "SELECT * FROM " + DATABASE_TABLE;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryList, null);

        // COLLECT EACH ROW IN THE TABLE
        if (cursor.moveToFirst()){
            do{
                ToDo_Item task = new ToDo_Item();
                task.setId(cursor.getInt(0));
                task.setDescription(cursor.getString(1));
                task.setIs_done(cursor.getInt(2));

                //ADD TO THE QUERY LIST
                taskList.add(task);
            }while(cursor.moveToNext());
        }
        return  taskList;
    }


}
