package incredible.stopwatchresultssaver.stopwatchresultssaver;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SRSSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABAZE_NAME = "SRSdatabaze";
    private static final int DATABAZE_VERSION = 1;
    Context myContext;

    private static final String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS SRSEvents (Event_ID INTEGER PRIMARY KEY AUTOINCREMENT, Date_time TEXT, Activity TEXT, Duration TEXT, Program TEXT)";
    private static final String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS SRSValues (Event INTEGER, Value TEXT, FOREIGN KEY (Event) REFERENCES SRSEvents(Event_ID))";


    public SRSSQLiteHelper(Context context) {
        super(context, DATABAZE_NAME, null, DATABAZE_VERSION);
        myContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE1);
        db.execSQL(CREATE_TABLE2);
        Toast.makeText(myContext,"Creating tables", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public void insert(Context context,ListView listview, SQLiteDatabase db,String program, String duration) {

        Adapter adapter = listview.getAdapter();
        int     count = adapter.getCount();
        String  date_time = this.getDate_Time();
        String  activity = "Running";

        db.execSQL("INSERT INTO SRSEvents (Date_time, Activity, Duration, Program) VALUES ('"+date_time+"','"+activity+"','"+duration+"','"+program+"')");
        Toast.makeText(context, "Saving results...",Toast.LENGTH_LONG).show();

        Cursor cursor = db.rawQuery("SELECT seq FROM sqlite_sequence WHERE name = 'SRSEvents'",null);
        cursor.moveToFirst();
        int last_id = cursor.getInt(0);

        int i=0;
        while (i<count) {
            String time = adapter.getItem(i).toString();
            db.execSQL("INSERT INTO SRSValues (Event, Value) VALUES ("+last_id+",'"+time+"')");
            i++;

        }

    }

    public Cursor get_eventCursor(SQLiteDatabase db, String table, String request){

       if      (request.equals("@ALL"))       {Cursor cursor = db.rawQuery("SELECT * FROM " + table, null); return cursor;}
       else if (request.equals("@TIMER"))     {Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE Program='timer'", null); return cursor;}
       else if (request.equals("@STOPWATCH")) {Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE Program='stopwatch'", null); return cursor;}
       else {
           return null;
       }
    }

    public Cursor get_valueCursor(SQLiteDatabase db, int event) {

        Cursor cursor = db.rawQuery("SELECT * FROM SRSValues WHERE Event = "+Integer.toString(event),null);
        return cursor;
    }

    public String getDate_Time(){

        Date dnow = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        return df.format(dnow);

    }

    public void drop_tables(SQLiteDatabase db, Context context) {
        db.execSQL("DROP TABLE IF EXISTS StopwatchEvents");
        db.execSQL("DROP TABLE IF EXISTS StopwatchSplits");
        db.execSQL("DROP TABLE IF EXISTS TimerEvents");
        db.execSQL("DROP TABLE IF EXISTS TimerPauses");

        Toast.makeText(context, "Dropping tables",Toast.LENGTH_SHORT).show();
        onCreate(db);

    }
}
