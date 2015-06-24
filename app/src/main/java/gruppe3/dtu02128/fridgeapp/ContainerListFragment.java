package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MathiasMacbookPro on 23/06/15.
 */
public class ContainerListFragment extends ListFragment {

    private FridgeApp app;
    private ItemDatabaseHelper dbhelp;
    private String name;
    private ContainerCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        app = (FridgeApp) getActivity().getApplication();
        dbhelp = app.getDBHelper();

        adapter = app.getContainerAdapter();
        adapter = new ContainerCursorAdapter(getActivity(), dbhelp.getContainerListFromDB(), dbhelp);
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public void update() {
        adapter.changeCursor(dbhelp.getContainerListFromDB());
    }


    public ContainerCursorAdapter getAdapter() {
        return adapter;
    }
}
