package incredible.stopwatchresultssaver.stopwatchresultssaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends ActionBarActivity {

    TextView small_text, large_text, setting_1, setting_2;
    Button   start, reset, button_stopwatch, button_timer, button_results;
    ListView times_list;

    String   program, mode;  // program: stopwatch, timer.     mode: reseted, running, stopped, paused, saved.
    int      remainingSplits, splitsCount, pauses,  tHour, tMin, tSec;  long time;
    SRStimer timer; Handler handler; ArrayAdapter adapter; SRSSQLiteHelper datahelper; SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        small_text       = (TextView) findViewById(R.id.stopwatch_small_text);
        large_text       = (TextView) findViewById(R.id.stopwatch_large_text);
        setting_1        = (TextView) findViewById(R.id.setting1);
        setting_2        = (TextView) findViewById(R.id.setting2);

        button_stopwatch = (Button) findViewById(R.id.button_stopwatch);
        button_timer     = (Button) findViewById(R.id.button_timer);
        button_results   = (Button) findViewById(R.id.button_results);
        start            = (Button) findViewById(R.id.button_start);
        reset            = (Button) findViewById(R.id.button_set);

        times_list       = (ListView) findViewById(R.id.times_list);

        handler          = new Handler(Looper.getMainLooper());
        timer            = new SRStimer(handler, large_text, small_text);
        adapter          = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        times_list.setAdapter(adapter);

        if (savedInstanceState != null) {

            program         = savedInstanceState.getString ("PROG" );
            mode            = savedInstanceState.getString ("MODE" );
            splitsCount     = savedInstanceState.getInt    ("SPLC" );
            remainingSplits = savedInstanceState.getInt    ("RSPL" );
            pauses          = savedInstanceState.getInt    ("PSES" );

            tHour           = savedInstanceState.getInt    ("THOUR");
            tMin            = savedInstanceState.getInt    ("TMIN" );
            tSec            = savedInstanceState.getInt    ("TSEC" );
            time            = savedInstanceState.getInt    ("TIME" );

            timer.set_timer_time( savedInstanceState.getInt ("HOUR"),
                                  savedInstanceState.getInt ("MIN" ),
                                  savedInstanceState.getInt ("SEC" ),
                                  savedInstanceState.getInt ("MILL"));

            if (mode.equals("running")) { if (program.equals("stopwatch") && remainingSplits> 1) { start.setText("SPLIT"); }
                                          if (program.equals("stopwatch") && remainingSplits==1) { start.setText("STOP" ); }
                                          if (program.equals("timer")   )                        { start.setText("PAUSE"); }
                                          timer.start(program);          reset.setText("RESET");                           }

            if (mode.equals("reseted")) { start.setText("START");        reset.setText("SET.."); }

            if (mode.equals("stopped")) { start.setText("Save Results"); reset.setText("RESET"); }

            if (mode.equals("paused"))  { start.setText("Continue");     reset.setText("RESET"); }

            if (mode.equals("saved"))   { start.setText("Saved");        reset.setText("RESET"); }

        }

        else {

            program = "stopwatch"; mode = "reseted";
            pauses  = 0; splitsCount = 3; remainingSplits = splitsCount;
            tHour   = 0; tMin = 10; tSec = 0;

            if (program.equals("stopwatch")) { timer.set_timer_time(0, 0, 0, 0);           }
            if (program.equals("timer"))     { timer.set_timer_time(tHour, tMin, tSec, 0); }

        }

        if (program.equals("stopwatch")) { button_timer.setTextColor(button_results.getTextColors());
                                           button_stopwatch.setTextColor(0xffffc000);               }

        if (program.equals("timer"))     { button_timer.setTextColor(0xffffc000);
                                           button_stopwatch.setTextColor(button_results.getTextColors()); }

        update_settings();

        timer.format_text();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void stopwatch_button(View view) {

        Intent i = new Intent(this, SettingsActivityStopwatch.class);
        i.putExtra("SPLITS", splitsCount);

        startActivityForResult(i, 1);

    }

    public void timer_button(View view) {

        Intent i = new Intent(this, SettingsActivityTimer.class);
        i.putExtra("RTHS", tHour);
        i.putExtra("RTMS", tMin);
        i.putExtra("RTSS", tSec);

        startActivityForResult(i, 2);

    }

    public void results_button(View view) {

        Intent i = new Intent(this, BrowseResults.class);
        startActivity(i);
    }

    public void start_split(View view) {

        if (mode.equals("reseted")) {  // resetnuty stav, spusti sa casovac

            mode="running";
            reset.setText("RESET");

            if      (program.equals("stopwatch") && remainingSplits >1) { start.setText("SPLIT");   }
            else if (program.equals("stopwatch") && remainingSplits==1) { start.setText("STOP");    }
            else if (program.equals("timer"))                           { start.setText("PAUSE");   }

            if (program.equals("timer"))     { timer.set_timer_time(tHour,tMin,tSec,0);  }
            if (program.equals("stopwatch")) { timer.set_timer_time(0, 0, 0, 0);         }

            timer.start(program);

        }

        else if (mode.equals("running")) { // casovac bezi, nastane pauza alebo split

            if (program.equals("stopwatch")) {

                remainingSplits--;

                adapter.insert(Integer.toString(splitsCount-remainingSplits) + ": " + timer.get_b_time() + timer.get_s_time(), 0);
                adapter.notifyDataSetChanged();

                if (remainingSplits >1) { timer.split();                                                        }
                if (remainingSplits==1) { timer.split(); start.setText("STOP");                                 }
                if (remainingSplits==0) { timer.stop();  start.setText("Save results"); mode="stopped"; return; }

            }

            else if (program.equals("timer")) {

                mode="paused"; pauses++;
                start.setText("Continue");

                timer.pause();

                adapter.insert(Integer.toString(pauses) + ": Pause at: " + timer.get_b_time(), 0);
                adapter.notifyDataSetChanged();

                time = System.currentTimeMillis();

            }
        }

        else if (mode.equals("stopped")) { // casovac stopnuty, ulozia sa namerane vysledky

            datahelper = new SRSSQLiteHelper(getApplicationContext());
            database = datahelper.getWritableDatabase();
           // datahelper.drop_tables(database, getApplicationContext());

            String duration = "00:00:15";
            datahelper.insert(getApplicationContext(), times_list, database,  program, duration);

            database.close();
            mode="saved";
            start.setText("Saved");

        }

        else if (mode.equals("paused")) { // pauza, casovac bude pokracovat

            mode="running";
            start.setText("Pause..");

            Long n_time         = System.currentTimeMillis();
            Long pause_duration = n_time-time;

            int hours   = (int) (pause_duration/(1000*60*60));
            int minutes = (int) (pause_duration/(1000*60))-(hours*60);
            int seconds = (int) (pause_duration/(1000))-(minutes*60)-(hours*60*60);

            String string=adapter.getItem(0).toString();

            adapter.remove(adapter.getItem(0));
            adapter.insert(string + " duration: " + hours + ":" + minutes + ":" + seconds, 0);  // treba naformatovat

            timer.resume();
        }

    update_settings();

    }

    public void set_reset(View view) {

        if (mode.equals("reseted")) {

            reset.setText("SET..");

            if (program.equals("stopwatch")) {

                Intent i = new Intent(this, SettingsActivityStopwatch.class);
                i.putExtra("SPLITS", splitsCount);
                startActivityForResult(i, 1);

            }

            if (program.equals("timer")) {

                Intent i = new Intent(this, SettingsActivityTimer.class);
                i.putExtra("RTHS", tHour);
                i.putExtra("RTMS", tMin);
                i.putExtra("RTSS", tSec);

                startActivityForResult(i, 2);

            }
        }

        else {

            reset();
        }
    }

    private void reset() {

        mode="reseted";
        remainingSplits=splitsCount; pauses=0;
        timer.stop();
        reset.setText("SET..");
        start.setText("START");

        if (program.equals("stopwatch")) { timer.set_timer_time(0, 0, 0, 0);           }

        if (program.equals("timer"))     { timer.set_timer_time(tHour, tMin, tSec, 0); }

        timer.format_text();
        adapter.clear();

        update_settings();

    }

    private void update_settings() {

        if (program.equals("stopwatch")) { setting_1.setText("SPLITS: "    + splitsCount);
                                           setting_2.setText("  REMAINING: " + remainingSplits); }

        if (program.equals("timer"))     { setting_1.setText("FROM: "  + tHour + ":" + tMin + ":" + tSec);  // need formatting
                                           setting_2.setText("  PAUSE: " + pauses); }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent x) {

        super.onActivityResult(requestCode, resultCode, x);

        if (requestCode==1) {

            if (resultCode==1) {

                splitsCount = x.getIntExtra("spl", 1);

                button_timer.setTextColor(button_results.getTextColors());
                button_stopwatch.setTextColor(0xffffc000);

                program = "stopwatch";

                reset();
                update_settings();

            }
        }

        if (requestCode==2) {

            if (resultCode==1) {

                tHour = x.getIntExtra("RTHS", 0);
                tMin  = x.getIntExtra("RTMS", 0);
                tSec  = x.getIntExtra("RTSS", 1);

                button_timer.    setTextColor(0xffffc000);
                button_stopwatch.setTextColor(button_results.getTextColors());

                program = "timer";

                reset();
                update_settings();

            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString ("PROG" , program            );
        savedInstanceState.putString ("MODE" , mode               );
        savedInstanceState.putInt    ("SPLC" , splitsCount        );
        savedInstanceState.putInt    ("RSPL" , remainingSplits    );
        savedInstanceState.putInt    ("PSES" , pauses             );

        savedInstanceState.putInt    ("THOUR", tHour              );
        savedInstanceState.putInt    ("TMIN" , tMin               );
        savedInstanceState.putInt    ("TSEC" , tSec               );
        savedInstanceState.putLong   ("TIME" , time               );

        savedInstanceState.putInt    ("HOUR" , timer.getHour()    );
        savedInstanceState.putInt    ("MIN"  , timer.getMin()     );
        savedInstanceState.putInt    ("SEC"  , timer.getSec()     );
        savedInstanceState.putInt    ("MILL" , timer.getCounter() );

        //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putInt("", newHighScore);
        //editor.commit();


        super.onSaveInstanceState(savedInstanceState);
    }

}
