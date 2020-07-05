package com.ninh.foodoutdated;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.Iterator;

public class FoodListActivity extends AppCompatActivity {

    private ActionMode actionMode;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SelectionTracker tracker;
    private boolean actionModeVisible = false;

    private ProductDAO productDAO;

    private static final int ADD_PRODUCT_REQUEST = 1;
    private static final int PERMISSION_REQUEST = 484;
    String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.delete_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.item_delete:
                    //TODO: delete selected item
                    Selection selection = tracker.getSelection();

                    Iterator it = selection.iterator();
                    while (it.hasNext()) {
                        long id = (long) it.next();
                        int adapterIndex = productDAO.deleteById(id);
                        if (adapterIndex != -1) {
                            recyclerView.getAdapter().notifyItemRemoved(adapterIndex);
                        }
                    }

                    Logger.i("%s", selection.toString());
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            actionModeVisible = false;
            clearAllSelection();
        }
    };

    private void clearAllSelection() {
        if (tracker.getSelection().size() != 0) {
            tracker.clearSelection();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        Logger.addLogAdapter(new AndroidLogAdapter());
        final Intent intent =  new Intent(this, AddProductActivity.class);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, ADD_PRODUCT_REQUEST);

            }
        });
        productDAO = new ProductDAO(FoodListActivity.this);

        recyclerView = findViewById(R.id.my_recycler_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(productDAO.loadAll());
        ((MyAdapter) mAdapter).setContext(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProvider(recyclerView),
                new MyDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.<Long>createSelectAnything())
                .build();

        tracker.addObserver(new SelectionTracker.SelectionObserver() {


            @Override
            public void onItemStateChanged(@NonNull Object key, boolean selected) {
                super.onItemStateChanged(key, selected);

                int totalSelection = tracker.getSelection().size();

                if (!actionModeVisible && totalSelection == 1) {
                    actionMode = startSupportActionMode(actionModeCallBack);
                    actionModeVisible = true;
                }


                if (actionMode == null)
                    return;

                if (totalSelection == 0) {
                    actionMode.finish();
                    return;
                }
                actionMode.setTitle(String.format("%d selected", totalSelection));
            }

        });

        ((MyAdapter) mAdapter).setTracker(tracker);

        if (! checkPermissionGranted())
        {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {
        if (requestCode == ADD_PRODUCT_REQUEST){
            if (resultCode == RESULT_OK){
                Product newProduct = null;
                if (data != null) {
                    newProduct = data.getParcelableExtra("new_product");
                    productDAO.add(newProduct);
                    int size = productDAO.size();
                    recyclerView.getAdapter().notifyItemInserted(size - 1);
                    Snackbar.make(recyclerView, "Add successful!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                }

            }
        }
    }

    private boolean checkPermissionGranted(){

        boolean arePermissionsGranted = true;
        for (String permission: permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED)
            {
                arePermissionsGranted =false;
            }
        }

        return arePermissionsGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST)
            return;

        boolean arePermissionsGranted = true;
        for (int grantResult: grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED)
            {
                arePermissionsGranted = false;
            }
        }

        if (! arePermissionsGranted && Build.VERSION.SDK_INT >= 23)
        {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
        }
    }
}
