package incredible.stopwatchresultssaver.stopwatchresultssaver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Tomas on 5.5.2014.
 */
public class SRSAdapter extends ArrayAdapter {
      TextView valueView;

    public SRSAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);


    }

    public View getView(int pos, View view, ViewGroup parent) {

        String[] s = (String[]) getItem(pos);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.result_row, parent, false);
        }

        TextView eventLine = (TextView) view.findViewById(R.id.eventLine);
        TextView valueLine = (TextView) view.findViewById(R.id.valueLine);

        eventLine.setText(s[0]);
        valueLine.setText(s[1]);

        return view;
    }


}