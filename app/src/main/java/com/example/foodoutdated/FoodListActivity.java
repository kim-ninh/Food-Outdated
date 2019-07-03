package com.example.foodoutdated;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Product> myDataset = new ArrayList<>();
    private static final int ADD_PRODUCT_REQUEST = 1;
    private static final int PERMISSION_REQUEST = 484;
    String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent =  new Intent(this, AddProductActivity.class);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, ADD_PRODUCT_REQUEST);

            }
        });


        recyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)

        mAdapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);

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
                    myDataset.add(newProduct);
                    recyclerView.notify();

                    Snackbar.make(recyclerView, "Add successful!", Snackbar.LENGTH_LONG)
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
