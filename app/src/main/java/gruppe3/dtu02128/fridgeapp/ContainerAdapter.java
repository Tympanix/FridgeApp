package gruppe3.dtu02128.fridgeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marcus on 19-06-2015.
 */
public class ContainerAdapter extends BaseAdapter{

    Context context;

    ArrayList<ContainerItem> items = new ArrayList<ContainerItem>();

    public ContainerAdapter(Context context){
        this.context = context;
    }

    public void add(String name, String type){
        items.add(new ContainerItem(name,type));
        notifyDataSetChanged();
    }

    public void add(ContainerItem item){
        items.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RelativeLayout layout = (RelativeLayout)
                LayoutInflater.from(this.context).inflate(R.layout.list_container, null);

        TextView nameView = (TextView) layout.findViewById(R.id.name);
        nameView.setText(items.get(position).getName());

        TextView typeView = (TextView) layout.findViewById(R.id.type);
        typeView.setText(items.get(position).getType());

        Button deleteButton = (Button) layout.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(position);
            }
        });

        return layout;
    }

    public void remove(int position) {
        items.remove(position);
        notifyDataSetChanged();
    }
}
