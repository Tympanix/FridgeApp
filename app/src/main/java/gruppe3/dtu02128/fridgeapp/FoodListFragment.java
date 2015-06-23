package gruppe3.dtu02128.fridgeapp;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MathiasMacbookPro on 23/06/15.
 */
public class FoodListFragment extends ListFragment {

    private FridgeApp app;
    private ItemDatabaseHelper dbhelp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        app = (FridgeApp) getActivity().getApplication();
        dbhelp = app.getDBHelper();
        setListAdapter(app.getDBCursor());

        return super.onCreateView(inflater, container, savedInstanceState);

    }
}
