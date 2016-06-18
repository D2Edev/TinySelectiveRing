package io.github.d2edev.distinctivering.ui;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import io.github.d2edev.distinctivering.R;
import io.github.d2edev.distinctivering.adapters.NameNumPicListAdapter;
import io.github.d2edev.distinctivering.db.DataContract;
import io.github.d2edev.distinctivering.logic.DataSetWatcher;
import io.github.d2edev.distinctivering.util.Utility;


public class DeleteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DataSetWatcher {
    public static final String TAG = "TAG_" + DeleteFragment.class.getSimpleName();
    public static final String KEY_SORT_ORDER = "kso";
    public static final int ALLOWEDLIST_CURSOR_LOADER = 0;
    private String[] sortBy;
    private String[] sortOrder;
    private ListView mDeleteView;
    private TextView lHeaderTextSortBy;
    private TextView lHeaderSortOrder;
    private int sortTypeIndex;
    private boolean sortAsc;
    private NameNumPicListAdapter nameNumPicListAdapter;


    public DeleteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sortBy = getResources().getStringArray(R.array.sortBy);
        sortOrder = getResources().getStringArray(R.array.sortOrder);
        sortTypeIndex = Utility.getSortTypeIndex(getActivity());
        sortAsc = Utility.isSortOrderAscending(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_delete, container, false);
        mDeleteView = (ListView) rootView.findViewById(R.id.listview_numbers_to_delete);
        ViewGroup headerView = (ViewGroup) inflater.inflate(R.layout.list_numbers_header_item, mDeleteView, false);
        //header SortBy part init
        lHeaderTextSortBy = (TextView) headerView.findViewById(R.id.header_text_sort_by);
        lHeaderTextSortBy.setText(sortBy[sortTypeIndex]);
        lHeaderTextSortBy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                headerSortByClicked();
            }
        });
        //header soerOrder part init
        lHeaderSortOrder = (TextView) headerView.findViewById(R.id.header_text_sort_order);
        lHeaderSortOrder.setText(sortOrder[Utility.getSortOrderIndex(sortAsc)]);
        lHeaderSortOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headerSortOrderClicked();
            }
        });

        mDeleteView.addHeaderView(headerView);


        nameNumPicListAdapter = new NameNumPicListAdapter(getContext(), null, 0);
        mDeleteView.setAdapter(nameNumPicListAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        rebuildList();
        super.onActivityCreated(savedInstanceState);
    }

    private void headerSortOrderClicked() {
        Log.d(TAG, "order clicked");
        sortAsc = sortAsc ? false : true;
        lHeaderSortOrder.setText(sortOrder[Utility.getSortOrderIndex(sortAsc)]);
        rebuildList();

    }

    private void headerSortByClicked() {
        if (sortTypeIndex == 2) {
            sortTypeIndex = 0;
        } else {
            sortTypeIndex++;
        }
        Log.d(TAG, "criteria clicked " + sortTypeIndex);
        lHeaderTextSortBy.setText(sortBy[sortTypeIndex]);
        rebuildList();
    }

    private void rebuildList() {
        String sortOrder = Utility.getSortColumnName(sortTypeIndex) + (sortAsc?" ASC":" DESC");
        Bundle bundle=new Bundle();
        bundle.putString(KEY_SORT_ORDER,sortOrder);
        if(getLoaderManager().getLoader(ALLOWEDLIST_CURSOR_LOADER)!=null) getLoaderManager().destroyLoader(ALLOWEDLIST_CURSOR_LOADER);
        getLoaderManager().initLoader(ALLOWEDLIST_CURSOR_LOADER,bundle,this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.delete_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.action_help: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortSQL=null;
        if (args!=null&&args.containsKey(KEY_SORT_ORDER))sortSQL=args.getString(KEY_SORT_ORDER);
        return new CursorLoader(getActivity(), DataContract.All.CONTENT_URI, null, null, null, sortSQL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        nameNumPicListAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameNumPicListAdapter.swapCursor(null);
    }

    @Override
    public void onPause() {
        Utility.setSortTypeIndex(getActivity(), sortTypeIndex);
        Utility.setSortOrderAscending(getContext(), sortAsc);
        super.onPause();
    }


    @Override
    public void dataSetChanged() {
        Log.d(TAG, "dataSetChanged: ");
        String sortOrder = Utility.getSortColumnName(sortTypeIndex) + (sortAsc?" ASC":" DESC");
        Bundle bundle=new Bundle();
        bundle.putString(KEY_SORT_ORDER,sortOrder);
        getLoaderManager().restartLoader(ALLOWEDLIST_CURSOR_LOADER, bundle, this);
        
    }
}
