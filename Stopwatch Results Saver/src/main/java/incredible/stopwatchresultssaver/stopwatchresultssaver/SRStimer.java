package incredible.stopwatchresultssaver.stopwatchresultssaver;

import android.os.Handler;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SRStimer extends Timer {

    protected String    b_string, s_string, prog;
    protected int       spl, counter, sec, min, hour = 0;
    protected boolean   split;
    protected Handler   handler;
    protected TextView  b, s;
    protected TimerTask task;

    public SRStimer(Handler h, TextView big, TextView small) {

        handler = h;
        b = big; s = small;

    }

    public void start(String program) {

        prog=program;

        task = new TimerTask() {

            @Override
            public void run() {

                if (prog.equals("stopwatch")) {

                    counter++;
                    if (counter == 100) { counter = 0; sec++; }
                    if (sec     == 60)  { sec     = 0; min++; }
                    if (min     == 60)  { min     = 0; hour++;}
                    if (hour    == 24)  { hour    = 0;        }

                }

                if (prog.equals("timer")) {

                    counter--;
                    if (counter < 0)  { counter = 99; sec--; }
                    if (sec     < 0)  { sec     = 59; min--; }
                    if (min     < 0)  { min     = 59; hour--;}
                    if (hour    < 0)  { return;              }

                }

                if (split) { // Split-time, nachvilu sa zastavi predavanie stringov do UI, ale cas bezi

                    spl++;
                    if (spl>75) { split=false; }
                
                }

                else { // Formatovnie stringov a postnutie do UI threadu

                    format_text();

                }
            }
        };

        super.scheduleAtFixedRate(task, 10, 10);

    }

    public void stop() {

        if (task!=null) { task.cancel(); }
        super.purge();


    }

    public void set_timer_time(int h, int m, int s, int c) {

        hour    = h;
        min     = m;
        sec     = s;
        counter = c;
        format_text();
    }

    public void split() {

        split = true;
        spl   = 0;

    }

    public void pause() {

        task.cancel();
        super.purge();

    }

    public void resume() {

        start(prog);

    }

    public String get_b_time() {

        return b_string;

    }

    public String get_s_time() {

        return s_string;

    }

    public void format_text() {

        String space = "";
        if (hour < 10) { space = "0"; } else { space = ""; }
        b_string = (space + Integer.toString(hour) + ":");

        if (min < 10) { space = "0"; } else { space = "";  }
        b_string = (b_string + space + Integer.toString(min) + ":");

        if (sec < 10) { space = "0"; } else { space = ""; }
        b_string = (b_string + space + Integer.toString(sec));

        if (counter < 10) { space = "0"; } else { space = ""; }
        s_string = ("." + space + Integer.toString(counter));

        handler.post(new Runnable() {

            @Override
            public void run() { b.setText(b_string); s.setText(s_string);
            }

        });
    }

    public int getCounter() {

        return counter;
    }

    public int getSec() {

        return sec;
    }

    public int getMin() {

        return min;
    }

    public int getHour() {

        return hour;
    }
}

