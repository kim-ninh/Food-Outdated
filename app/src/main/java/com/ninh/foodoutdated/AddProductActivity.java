package com.ninh.foodoutdated;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ninh.foodoutdated.custom.view.DateEditText;
import com.ninh.foodoutdated.custom.view.ImageHolderView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProductActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText txtName;
    private DateEditText txtExpiry;
    private ImageHolderView productImageHolder;
    private Button btnCamera;
    private Button btnGallery;
    private Uri photoURI;

    private final int TAKE_PICTURE_REQUEST = 111;
    private final int OPEN_GALLERY_REQUEST = 222;

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
                if (validate()){
                    openPreviousActivity();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openPreviousActivity() {
        Intent data = new Intent();
        data.putExtra("new_product", getProduct());
        setResult(Activity.RESULT_OK, data);
        AddProductActivity.this.finish();
    }


    @Override
    public void onClick(View v) {
        if (v == btnCamera)
        {
            dispatchTakePictureIntent();
        }else if (v == btnGallery){
            openGallery();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("CreateImageFile", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.ninh.foodoutdated",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, OPEN_GALLERY_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if (requestCode == OPEN_GALLERY_REQUEST && resultCode == RESULT_OK){
           Uri imageUri = Uri.parse(data.getDataString());
           String str = imageUri.getPath();
           productImageHolder.setImageUri(imageUri);
       }

       if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK){
           productImageHolder.setImageUri(photoURI);
       }

    }

    private Product getProduct(){
        Product product = null;
        try {
            String productName = txtName.getText().toString();
            String expiryDate = txtExpiry.getText().toString();
            String photoUri = productImageHolder.getImageUri();
            Date expiry = new SimpleDateFormat(Utils.DATE_PATTERN_VN).parse(expiryDate);
            product = new Product(productName, expiry, photoUri);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return product;
    }

    private boolean validate(){
        boolean isValid = true;

        String ProductName = txtName.getText().toString();
        String expiryDate = txtExpiry.getText().toString();
        String photoUri = productImageHolder.getImageUri();

        txtName.setError(null);

        if (ProductName.isEmpty() && isValid){
            Toast.makeText(this, R.string.error_message_empty_name, Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (expiryDate.isEmpty() && isValid){
            Toast.makeText(this, R.string.error_message_empty_date,Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (photoUri.isEmpty() && isValid){
            Toast.makeText(this, R.string.error_message_empty_photo, Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }
}
