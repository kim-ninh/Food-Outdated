package com.ninh.foodoutdated

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.ninh.foodoutdated.custom.view.DateEditText
import com.ninh.foodoutdated.custom.view.CloseableImageView
import java.io.File
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddProductActivity : AppCompatActivity(), View.OnClickListener {
    private var txtName: EditText? = null
    private var txtExpiry: DateEditText? = null
    private var productCloseableImage: CloseableImageView? = null
    private var btnCamera: Button? = null
    private var btnGallery: Button? = null
    private lateinit var photoURI: Uri
    private val TAKE_PICTURE_REQUEST = 111
    private val OPEN_GALLERY_REQUEST = 222
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        txtName = findViewById(R.id.txtName)
        txtExpiry = findViewById(R.id.txtExpiry)
        productCloseableImage = findViewById(R.id.productImageHolder)
        btnCamera = findViewById(R.id.btnCamera)
        btnGallery = findViewById(R.id.btnGallery)
        btnCamera?.setOnClickListener(this)
        btnGallery?.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_product, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_add -> {
                if (validate()) {
                    openPreviousActivity()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openPreviousActivity() {
        val data = Intent()
        data.putExtra("new_product", product)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onClick(v: View) {
        if (v === btnCamera) {
            dispatchTakePictureIntent()
        } else if (v === btnGallery) {
            openGallery()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
        )
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Log.e("CreateImageFile", ex.message)
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.ninh.foodoutdated",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, OPEN_GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUri = Uri.parse(data!!.dataString)
            val str = imageUri.path
            productCloseableImage!!.setImageUri(imageUri)
        }
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == Activity.RESULT_OK) {
            productCloseableImage!!.setImageUri(photoURI)
        }
    }

    private val product: Product?
        private get() {
            var product: Product? = null
            try {
                val productName = txtName!!.text.toString()
                val expiryDate = txtExpiry!!.text.toString()
                val photoUri = productCloseableImage!!.imageUri
                val expiry = SimpleDateFormat(Utils.DATE_PATTERN_VN).parse(expiryDate)
                product = Product(name = productName, expiry = expiry!!, thumbnail = photoUri)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return product
        }

    private fun validate(): Boolean {
        var isValid = true
        val ProductName = txtName!!.text.toString()
        val expiryDate = txtExpiry!!.text.toString()
        val photoUri = productCloseableImage!!.imageUri
        txtName!!.error = null
        if (ProductName.isEmpty() && isValid) {
            Toast.makeText(this, R.string.error_message_empty_name, Toast.LENGTH_LONG).show()
            isValid = false
        }
        if (expiryDate.isEmpty() && isValid) {
            Toast.makeText(this, R.string.error_message_empty_date, Toast.LENGTH_LONG).show()
            isValid = false
        }
        if (photoUri.isEmpty() && isValid) {
            Toast.makeText(this, R.string.error_message_empty_photo, Toast.LENGTH_LONG).show()
            isValid = false
        }
        return isValid
    }
}