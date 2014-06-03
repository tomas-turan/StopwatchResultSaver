package incredible.stopwatchresultssaver.stopwatchresultssaver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

public class SettingsActivityStopwatch extends Activity {

    NumberPicker splitpicker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_stopwatch);

        this.setFinishOnTouchOutside(false);

        Intent i = getIntent();
        int splits = i.getIntExtra("SPLITS", 1);

        splitpicker = (NumberPicker)findViewById(R.id.splitpicker);
        splitpicker.setMinValue(1);
        splitpicker.setMaxValue(99);
        splitpicker.setValue(splits);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
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

    public void ok_button (View viev) {

        Intent i = new Intent();
        int spl = splitpicker.getValue();
        i.putExtra("spl",spl);
        i.putExtra("requestCode", 1);
        setResult(1, i);
        this.finish();
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent();
        i.putExtra("requestCode", 1);
        setResult(0, i);
        super.onBackPressed();
    }

}
