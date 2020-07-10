package com.ninh.foodoutdated.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
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
import com.ninh.foodoutdated.MyApplication
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.Utils
import com.ninh.foodoutdated.custom.view.CloseableImageView
import com.ninh.foodoutdated.custom.view.DateEditText
import com.ninh.foodoutdated.models.Product
import com.ninh.foodoutdated.viewmodels.ProductViewModel
import com.orhanobut.logger.Logger
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService

class AddProductFragment : Fragment(R.layout.fragment_add_product) {

    private lateinit var productEditText: EditText
    private lateinit var expiryDateEditText: DateEditText
    private lateinit var productImageView: CloseableImageView
    private lateinit var buttonCamera: Button
    private lateinit var buttonGallery: Button

    private lateinit var productViewModel: ProductViewModel

    private var photoUri: Uri? = null
    private var photoFile: File? = null

    private lateinit var executorService: ExecutorService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            photoUri = it.getParcelable(KEY_PHOTO_URI)
        }

        setHasOptionsMenu(true)
        productEditText = view.findViewById(R.id.product_edit_text)
        expiryDateEditText = view.findViewById(R.id.expiry_date_edit_text)
        productImageView = view.findViewById(R.id.product_image_view)
        buttonCamera = view.findViewById(R.id.button_camera)
        buttonGallery = view.findViewById(R.id.button_gallery)
        buttonGallery.setOnClickListener(this::handleRequestImage)
        buttonCamera.setOnClickListener(this::handleRequestImage)

        productViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )
            .get(ProductViewModel::class.java)

        productImageView.setOnCloseListener {
            photoFile?.delete()
            photoFile = null
        }

        executorService = (requireActivity().application as MyApplication).workerExecutor
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri = data!!.data!!
            val photoFile = createImageFile()
            this.photoFile = photoFile

            decodeBitmapAndSave(imageUri, photoFile){
                Glide.with(this)
                    .load(photoFile)
                    .centerCrop()
                    .into(productImageView.internalImageView)
            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val fullSizePhotoFile: File? = this.photoFile
            val resizedPhotoFile = createImageFile()

            val photoUri = this.photoUri!!
            decodeBitmapAndSave(photoUri, resizedPhotoFile){
                Glide.with(this)
                    .load(resizedPhotoFile)
                    .centerCrop()
                    .into(productImageView.internalImageView)

                fullSizePhotoFile?.delete()
                this.photoFile = resizedPhotoFile
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_PHOTO_URI, photoUri)
    }

    private fun decodeBitmapAndSave(uri: Uri, file: File, uiCallback: () -> Unit) {
        executorService.submit {
            val futureTarget = Glide.with(this)
                .asBitmap()
                .load(uri)
                .centerInside()
                .submit(1080, 1080)
            val bitmap: Bitmap = futureTarget.get()

            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
            }

            Glide.with(this).clear(futureTarget)
            requireActivity().runOnUiThread(uiCallback)
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
                val expiryDate = SimpleDateFormat(Utils.DATE_PATTERN_VN).parse(expiryDateStr)
                product = Product(name = productName, expiry = expiryDate, file = photoFile)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return product
        }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val productDir = File(storageDir, "products")
        if (!productDir.exists()) {
            productDir.mkdirs()
        }

        return File(productDir, "JPEG_$timeStamp.jpg")
    }

    private fun handleRequestImage(view: View){
        photoFile?.delete()
        photoFile = null

        when(view){
            buttonGallery -> selectImage(view)
            buttonCamera -> dispatchTakePictureIntent(view)
        }
    }

    private fun dispatchTakePictureIntent(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) == null) {
            return
        }

        intent.also {
            val photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(
                requireContext(), "com.ninh.foodoutdated", photoFile
            )
            it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(it, REQUEST_TAKE_PHOTO)
            this.photoFile = photoFile
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

        if (photoFile == null && isValid) {
            Toast.makeText(requireContext(), R.string.error_message_empty_photo, Toast.LENGTH_LONG)
                .show()
            isValid = false
        }
        return isValid
    }

    companion object {
        private const val REQUEST_TAKE_PHOTO = 0
        private const val REQUEST_IMAGE_GET = 1
        private const val KEY_PHOTO_URI = "KEY_PHOTO_URI"

        @JvmStatic
        fun newInstance() = AddProductFragment()
    }
}