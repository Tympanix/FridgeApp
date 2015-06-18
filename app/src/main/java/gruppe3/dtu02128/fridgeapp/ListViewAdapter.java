package gruppe3.dtu02128.fridgeapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Morten on 16-Jun-15.
 */
public class ListViewAdapter extends BaseAdapter {
    //Todo: Create local variables
    Context mContext;

    ArrayList<ListItem> list2 = new ArrayList<ListItem>();
    ArrayList<String> list = new ArrayList<String>();

    public ListViewAdapter(Context cont) {
        mContext = cont;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void add() {
        list.add(String.valueOf(getCount()));
        list2.add(new ListItem("Item nr: "+String.valueOf(getCount()),getCount()));
        Log.i("test", "Added item");
        notifyDataSetChanged();
    }

    @Override
    public ListItem getItem(int position) {
        return list2.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        list.remove(position);
        list2.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout text = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
        TextView txt = (TextView) text.findViewById(R.id.title);
        txt.setText("Random" + getItem(position).getValue());
        TextView txt2 = (TextView) text.findViewById(R.id.title2);
        txt2.setText(getItem(position).getName());
        Log.i("test","Getting view");

        return text;
    }
}
