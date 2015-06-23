package gruppe3.dtu02128.fridgeapp;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by MathiasMacbookPro on 23/06/15.
 */
public class SingleFoodListFragment extends ListFragment {

    private FridgeApp app;
    private ItemDatabaseHelper dbhelp;
    private String name;
    private CursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        name = bundle.getString("name");

        app = (FridgeApp) getActivity().getApplication();
        dbhelp = app.getDBHelper();

        adapter = app.getAdapterDetail(name);
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public void update(){
        adapter.changeCursor(dbhelp.getFoodList(name));
    }



}
