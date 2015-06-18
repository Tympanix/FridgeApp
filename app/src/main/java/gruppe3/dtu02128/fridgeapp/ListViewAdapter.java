package gruppe3.dtu02128.fridgeapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Morten on 16-Jun-15.
 */
public class ListViewAdapter extends BaseAdapter {
    //Todo: Create local variables
    Context mContext;

    ArrayList<ListItem> list = new ArrayList<ListItem>();

    public ListViewAdapter(Context cont) {
        mContext = cont;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void add() {
        Calendar cal = Calendar.getInstance();

        list.add(new ListItem("Item nr: "+ String.valueOf(getCount()),cal.getTimeInMillis()));
        Log.i("test", "Added item");
        notifyDataSetChanged();
    }

    @Override
    public ListItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;

        LinearLayout text = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_item, null);

        TextView txt = (TextView) text.findViewById(R.id.title);
        txt.setText("Random" + getItem(position).getValue());

        TextView txt2 = (TextView) text.findViewById(R.id.title2);
        txt2.setText(getItem(position).getName());

        Log.i("test","Getting view");
        //Configure configure view to delete itself if button is pressed
        Button butt = (Button) text.findViewById(R.id.remove_button);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(pos);
            }
        });

        return text;
    }
}
