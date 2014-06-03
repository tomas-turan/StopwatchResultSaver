package incredible.stopwatchresultssaver.stopwatchresultssaver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;

public class SettingsActivityTimer extends Activity {

    NumberPicker hourpicker, minpicker, secpicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_timer);

        this.setFinishOnTouchOutside(false);

        Intent i = getIntent();
        int rths = i.getIntExtra("RTHS", 0);
        int rtms = i.getIntExtra("RTMS", 0);
        int rtss = i.getIntExtra("RTSS", 10);

        hourpicker = (NumberPicker)findViewById(R.id.hourpicker);
        minpicker  = (NumberPicker)findViewById(R.id.minpicker);
        secpicker  = (NumberPicker)findViewById(R.id.secpicker);

        hourpicker.setMinValue(0); hourpicker.setMaxValue(23); hourpicker.setValue(rths);
        minpicker.setMinValue (0);  minpicker.setMaxValue(59);  minpicker.setValue(rtms);
        secpicker.setMinValue (0);  secpicker.setMaxValue(59);  secpicker.setValue(rtss);


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
        int rths = hourpicker.getValue();
        int rtms = minpicker.getValue();
        int rtss = secpicker.getValue();

        i.putExtra("RTHS",rths);
        i.putExtra("RTMS",rtms);
        i.putExtra("RTSS",rtss);
        i.putExtra("requestCode",2);

        setResult(1, i);

        this.finish();
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent();
        i.putExtra("requestCode", 2);
        setResult(0, i);
        super.onBackPressed();
    }


}
