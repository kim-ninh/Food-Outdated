package com.example.foodoutdated;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodoutdated.custom.view.DateEditText;
import com.example.foodoutdated.custom.view.ImageHolderView;

public class AddProductActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText txtName;
    private DateEditText txtExpiry;
    private ImageHolderView productImageHolder;
    private Button btnCamera;
    private Button btnGallery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtName = findViewById(R.id.txtName);
        txtExpiry = findViewById(R.id.txtExpiry);
        productImageHolder = findViewById(R.id.productImageHolder);

        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_add:
                Toast.makeText(this, "Add click", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnCamera)
        {
            Toast.makeText(this, "Camera click", Toast.LENGTH_LONG).show();
        }else if (v == btnGallery){
            Toast.makeText(this, "Gallery click", Toast.LENGTH_LONG).show();
        }
    }
}
