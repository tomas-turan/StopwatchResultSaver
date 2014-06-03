package incredible.stopwatchresultssaver.stopwatchresultssaver;


import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class BrowseResults extends ListActivity {

    SRSSQLiteHelper datahelper; SQLiteDatabase database; SRSAdapter adapter;
    Button buttAll, buttStopwatch, buttTimer; Spinner spinActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_results);

        adapter = new SRSAdapter(this,R.layout.result_row, R.id.eventLine);
        setListAdapter(adapter);
        loadDatas("@ALL");

    }

    public void button_pressed(View view) {

       ToggleButton buttAll       = (ToggleButton) findViewById(R.id.ButtonAllResults);
       ToggleButton buttStopwatch = (ToggleButton) findViewById(R.id.ButtonAllResults);
       ToggleButton buttTimer     = (ToggleButton) findViewById(R.id.ButtonAllResults);

       if (view.getId() == R.id.ButtonAllResults)        {loadDatas("@ALL");       buttAll.setChecked(true); buttStopwatch.setChecked(false); buttTimer.setChecked(false);     }
       if (view.getId() == R.id.ButtonStopwatchResults)  {loadDatas("@STOPWATCH"); buttAll.setChecked(false); buttStopwatch.setChecked(true); buttTimer.setChecked(false);}
       if (view.getId() == R.id.ButtonTimerResults)      {loadDatas("@TIMER");     buttAll.setChecked(false); buttStopwatch.setChecked(false); buttTimer.setChecked(true);    }

    }

    private void loadDatas(String request) {      //nacita data do zoznamu podla parametru request

        Cursor eventCursor;
        datahelper=new SRSSQLiteHelper(this);
        database=datahelper.getReadableDatabase();

        adapter.clear();

        eventCursor = datahelper.get_eventCursor(database, "SRSEvents", request);
        loadList(eventCursor);

    }

    private void loadList(Cursor eventCursor) {

        Cursor valueCursor;
        String eventString, valueString; int i=0;
        while (eventCursor.moveToPosition(i)) {

            int event = eventCursor.getInt(0);
            eventString = eventCursor.getString(1) + " " +
                    eventCursor.getString(2) + " Dur: " +
                    eventCursor.getString(3) + " Mode: " +
                    eventCursor.getString(4);

            valueCursor = datahelper.get_valueCursor(database, event);
            int j = 0; valueString = "";
            while (valueCursor.moveToPosition(j)) {

                valueString = valueString + valueCursor.getString(1) + "\n";
                j++;
            }

            String[] s = {eventString, valueString};
            adapter.add(s);
            adapter.notifyDataSetChanged();
            i++;
        }
    }



}
