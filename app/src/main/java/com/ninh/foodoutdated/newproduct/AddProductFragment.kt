package com.ninh.foodoutdated.newproduct

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ninh.foodoutdated.MyApplication
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.customview.DateEditText
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.viewmodels.ProductViewModel
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService

class AddProductFragment : Fragment(R.layout.fragment_add_product) {

    private lateinit var productEditText: EditText
    private lateinit var expiryDateEditText: TextView
    private lateinit var productImageView: ImageView

    private lateinit var productViewModel: ProductViewModel

    private var photoUri: Uri? = null
    private var photoFile: File? = null

    private lateinit var executorService: ExecutorService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar: Toolbar = view.findViewById(R.id.toolBar)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolBar.setupWithNavController(navController, appBarConfiguration)

        savedInstanceState?.let {
            photoUri = it.getParcelable(KEY_PHOTO_URI)
        }

        setHasOptionsMenu(true)
        productEditText = view.findViewById(R.id.product_edit_text)
        expiryDateEditText = view.findViewById(R.id.expiry_date_edit_text)
        productImageView = view.findViewById(R.id.product_image_view)

        val collapsingToolbarLayout: CollapsingToolbarLayout = view.findViewById(R.id.collapsingToolbarLayout)

        productViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )
            .get(ProductViewModel::class.java)

        val productImageActions = arrayOf(
            ProductImageAction("Remove photo", R.drawable.ic_clear_black_24dp, this::removePhotoFile),
            ProductImageAction("Take photo", R.drawable.ic_camera_alt_black_24dp, this::dispatchTakePictureIntent),
            ProductImageAction("Pick from gallery", R.drawable.ic_photo_black_24dp, this::selectImage)
        )

        val simpleDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Title")
            .setAdapter(ProductImageActionAdapter(productImageActions)){_, which ->
                productImageActions[which].action.invoke()
            }

        productImageView.setOnClickListener{
            simpleDialog.show()
        }

        executorService = (requireActivity().application as MyApplication).workerExecutor

        productEditText.setOnClickListener{
            collapsingToolbarLayout.title = ""
            it.alpha = 1f
        }

        productEditText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when(actionId){
                EditorInfo.IME_ACTION_DONE -> {
                    collapsingToolbarLayout.title = productEditText.text
                    productEditText.alpha = 0f
                    false
                }
                else -> false
            }
        }

        productEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                collapsingToolbarLayout.title = productEditText.text
                productEditText.alpha = 0f
            }
        }

        toolBar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.item_add ->{
                    Toast.makeText(requireContext(),"Item add touched!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        val builder = MaterialDatePicker.Builder.datePicker().apply {

        }

        val picker = builder.build().apply {
            addOnPositiveButtonClickListener {
                expiryDateEditText.text = this.headerText
            }
        }

        expiryDateEditText.setOnClickListener {
            picker.show(childFragmentManager, picker.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri = data!!.data!!
            val photoFile = createImageFile()
            this.photoFile = photoFile

            decodeBitmapAndSave(imageUri, photoFile) {
                updateProductImageView(photoFile)
            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val fullSizePhotoFile: File? = this.photoFile
            val resizedPhotoFile = createImageFile()

            val photoUri = this.photoUri!!
            decodeBitmapAndSave(photoUri, resizedPhotoFile) {
                updateProductImageView(resizedPhotoFile)

                fullSizePhotoFile?.delete()
                this.photoFile = resizedPhotoFile
            }
        }
    }

    private fun updateProductImageView(file: File){
        productImageView.alpha = 1f

        Glide.with(this)
            .load(file)
            .centerCrop()
            .into(productImageView)
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

                    val action =
                        AddProductFragmentDirections.actionAddProductFragmentToProductsFragment()
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
                val expiryDate =
                    SimpleDateFormat(getString(R.string.date_pattern_vn)).parse(expiryDateStr)
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

    private fun removePhotoFile(){
        photoFile?.delete()
        photoFile = null

        productImageView.alpha = 0.1f
        Glide.with(requireContext())
            .load(R.drawable.ic_waste)
            .fitCenter()
            .into(productImageView)
    }

    private fun dispatchTakePictureIntent() {
        removePhotoFile()
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

    private fun selectImage() {
        removePhotoFile()
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