package com.ninh.foodoutdated.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.Utils
import com.ninh.foodoutdated.custom.view.CloseableImageView
import com.ninh.foodoutdated.custom.view.DateEditText
import com.ninh.foodoutdated.models.Product
import com.ninh.foodoutdated.viewmodels.ProductViewModel
import com.orhanobut.logger.Logger
import java.io.File
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddProductFragment : Fragment(R.layout.fragment_add_product) {

    private lateinit var productEditText: EditText
    private lateinit var expiryDateEditText: DateEditText
    private lateinit var productImageView: CloseableImageView
    private lateinit var buttonCamera: Button
    private lateinit var buttonGallery: Button

    private lateinit var productViewModel: ProductViewModel

    private var photoUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        productEditText = view.findViewById(R.id.product_edit_text)
        expiryDateEditText = view.findViewById(R.id.expiry_date_edit_text)
        productImageView = view.findViewById(R.id.product_image_view)
        buttonCamera = view.findViewById(R.id.button_camera)
        buttonGallery = view.findViewById(R.id.button_gallery)
        buttonGallery.setOnClickListener(this::selectImage)
        buttonCamera.setOnClickListener(this::dispatchTakePictureIntent)

        productViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )
            .get(ProductViewModel::class.java)

        productImageView.setOnCloseListener {
            photoUri = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri = data!!.data!!
            photoUri = imageUri

            Glide.with(this)
                .load(photoUri)
                .into(productImageView.internalImageView)
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            // photoUri already used when open Camera Activity
            Glide.with(this)
                .load(photoUri)
                .into(productImageView.internalImageView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_product, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_add -> {
                if (validate()) {
                    product?.let {
                        productViewModel.insert(it)
                    }

                    val action = AddProductFragmentDirections
                        .actionAddProductFragmentToProductsFragment()
                    findNavController().navigate(action)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val product: Product?
        get() {
            var product: Product? = null
            try {
                val productName = productEditText.text.toString()
                val expiryDateStr = expiryDateEditText.text.toString()
                val photoUri = this.photoUri
                val expiryDate = SimpleDateFormat(Utils.DATE_PATTERN_VN).parse(expiryDateStr)
                product = Product(name = productName, expiry = expiryDate, uri = photoUri)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return product
        }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    private fun dispatchTakePictureIntent(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) == null) {
            return
        }

        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Logger.e(ex, "CreateImageFile failed!")
            null
        }

        photoFile?.also {
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.ninh.foodoutdated",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    private fun selectImage(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        val productName = productEditText.text.toString()
        val expiryDate = expiryDateEditText.text.toString()
        val photoUri = this.photoUri

        productEditText.error = null
        if (productName.isEmpty() && isValid) {
            Toast.makeText(requireContext(), R.string.error_message_empty_name, Toast.LENGTH_LONG)
                .show()
            isValid = false
        }

        if (expiryDate.isEmpty() && isValid) {
            Toast.makeText(requireContext(), R.string.error_message_empty_date, Toast.LENGTH_LONG)
                .show()
            isValid = false
        }

        if (photoUri == null && isValid) {
            Toast.makeText(requireContext(), R.string.error_message_empty_photo, Toast.LENGTH_LONG)
                .show()
            isValid = false
        }
        return isValid
    }

    companion object {
        private const val REQUEST_TAKE_PHOTO = 0
        private const val REQUEST_IMAGE_GET = 1

        @JvmStatic
        fun newInstance() = AddProductFragment()
    }
}