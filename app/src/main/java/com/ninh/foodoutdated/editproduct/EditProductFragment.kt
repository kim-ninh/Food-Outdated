package com.ninh.foodoutdated.editproduct

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ninh.foodoutdated.*
import com.ninh.foodoutdated.customview.DatePickerTextView
import com.ninh.foodoutdated.customview.NumberPickerTextView
import com.ninh.foodoutdated.customview.ReminderPickerTextView
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.extensions.CalendarExtension
import com.ninh.foodoutdated.extensions.findViewById
import com.ninh.foodoutdated.extensions.hideSoftKeyboard
import com.ninh.foodoutdated.extensions.hideTitle

import com.ninh.foodoutdated.viewmodels.ProductViewModel
import com.ninh.foodoutdated.viewmodels.RemindInfoViewModel
import com.orhanobut.logger.Logger
import java.io.File
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

open class EditProductFragment : Fragment(R.layout.fragment_edit_product),
    View.OnFocusChangeListener,
    AdapterView.OnItemSelectedListener,
    Toolbar.OnMenuItemClickListener {

    private val TAG = EditProductFragment::class.java.simpleName

    protected val productEditText: EditText by lazy { findViewById<EditText>(R.id.product_edit_text) }
    protected val expiryDateTextView: DatePickerTextView by lazy {
        findViewById<DatePickerTextView>(
            R.id.expiry_date_edit_text
        )
    }
    protected val productImageView: ImageView by lazy { findViewById<ImageView>(R.id.product_image_view) }
    protected val reminderPickerTextView: ReminderPickerTextView by lazy {
        findViewById<ReminderPickerTextView>(
            R.id.textViewReminderPicker
        )
    }
    protected val quantityTextView: NumberPickerTextView by lazy {
        findViewById<NumberPickerTextView>(
            R.id.textViewQuantity
        )
    }

    protected val toolBar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolBar) }
    protected val collapsingToolbarLayout: CollapsingToolbarLayout
            by lazy { findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbarLayout) }
    protected val coordinatorLayout: CoordinatorLayout by lazy {
        requireActivity().findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
    }

    protected lateinit var productViewModel: ProductViewModel

    protected var photoUri: Uri? = null
    protected var photoFile: File? = null
    protected var productNameChangeConfirmed = false

    protected val executorService by lazy {
        (requireActivity().application as MyApplication)
            .workerExecutor
    }

    protected val quantityArr = generateSequence(1) { it + 1 }
        .map { it.toString(10) }
        .take(10)
        .toList()
        .toTypedArray()

    private val args: EditProductFragmentArgs by navArgs()

    open fun inflateToolbarMenu() {
        toolBar.inflateMenu(R.menu.delete_product)
    }

    open fun loadProductFromDB() {
        Log.i(TAG, "onViewCreated: received product id: ${args.productId}")

        productViewModel.loadProductAndRemindInfo(args.productId)
            .observe(viewLifecycleOwner) {
                updateUIs(it.product)
                reminderPickerTextView.expiryDate = it.product.expiryDate
                reminderPickerTextView.remindInfo = it.remindInfo
            }
    }

    protected fun updateUIs(product: Product) {
        val quantityIndex = quantityArr.indexOf(product.quantity.toString())

        collapsingToolbarLayout.title = product.name
        productEditText.setText(product.name)
        quantityTextView.currentValue = quantityIndex
        if (product.file == null) {
            loadPlaceholderImage()
        } else {
            this.photoFile = product.file
            loadProductImage(product.file!!)
        }
        expiryDateTextView.datePicked = product.expiryDate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            photoUri = it.getParcelable(KEY_PHOTO_URI)
        }

        productViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )
            .get(ProductViewModel::class.java)


        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolBar.setupWithNavController(navController, appBarConfiguration)
        toolBar.setOnMenuItemClickListener(this)
        inflateToolbarMenu()

        productImageView.setOnClickListener { showImageActionDialog() }
        productEditText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    productNameChangeConfirmed = true
                    productEditText.clearFocus()
                    updateEditTextAndToolBar()
                    false
                }
                else -> false
            }
        }

        arrayOf(
            productEditText,
            expiryDateTextView,
            productImageView,
            quantityTextView,
            reminderPickerTextView
        )
            .forEach { focusableView ->
                focusableView.onFocusChangeListener = this@EditProductFragment
            }

        quantityTextView.displayedValues = quantityArr
        expiryDateTextView.onDatePickChanged = { datePicked ->
            reminderPickerTextView.expiryDate = datePicked
        }

        loadProductFromDB()
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (v == productEditText) {
            if (hasFocus) {
                productNameChangeConfirmed = false
                productEditText.tag = collapsingToolbarLayout.title.toString()
                collapsingToolbarLayout.hideTitle()
                v.alpha = 1f
            } else {
                if (!productNameChangeConfirmed) {
                    Logger.i("Text ${productEditText.tag} saved!.")
                    Snackbar.make(coordinatorLayout, "Updated name", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            Logger.i("Undo text to ${productEditText.tag}.")
                            productEditText.setText(productEditText.tag.toString())
                            collapsingToolbarLayout.title = productEditText.text.toString()
                        }
                        .show()
                }
                productEditText.hideSoftKeyboard()
                updateEditTextAndToolBar()
            }
        }

        if (hasFocus && v != productEditText) {
            v.performClick()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_delete -> {
                // TODO: Implement this method
                true
            }
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri = data!!.data!!
            val photoFile = createImageFile()
            this.photoFile = photoFile

            decodeBitmapAndSave(imageUri, photoFile) {
                loadProductImage(photoFile)
            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val fullSizePhotoFile: File? = this.photoFile
            val resizedPhotoFile = createImageFile()

            val photoUri = this.photoUri!!
            decodeBitmapAndSave(photoUri, resizedPhotoFile) {
                loadProductImage(resizedPhotoFile)

                fullSizePhotoFile?.delete()
                this.photoFile = resizedPhotoFile
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_PHOTO_URI, photoUri)
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) = Unit


    private fun loadProductImage(file: File) {
        productImageView.alpha = 1f
        Glide.with(this)
            .load(file)
            .centerCrop()
            .into(productImageView)
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

    protected val product: Product
        get() {
            val product: Product
            val productName = productEditText.text.toString()
            val quantity = quantityArr[quantityTextView.currentValue].toInt()
            val expiryDate = expiryDateTextView.datePicked
            product = Product(
                name = productName,
                quantity = quantity,
                expiryDate = expiryDate,
                file = photoFile
            )

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

    private fun removePhotoFile() {
        photoFile?.delete()
        photoFile = null

        loadPlaceholderImage()
    }

    private fun loadPlaceholderImage() {
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

    private fun updateEditTextAndToolBar() {
        if (productEditText.text.isNotEmpty() && productEditText.text.isNotBlank()) {
            collapsingToolbarLayout.title = productEditText.text
        } else {
            collapsingToolbarLayout.title = productEditText.tag.toString()
        }
        productEditText.alpha = 0f
    }

    private fun showImageActionDialog() {
        val productImageActions = mutableListOf(
            ProductImageAction(
                "Take photo",
                R.drawable.ic_camera_alt_black_24dp,
                this::dispatchTakePictureIntent
            ),
            ProductImageAction(
                "Pick from gallery", R.drawable.ic_photo_black_24dp, this::selectImage
            )
        ).apply {
            if (photoFile != null) {
                add(
                    0, ProductImageAction(
                        "Remove photo",
                        R.drawable.ic_clear_black_24dp,
                        this@EditProductFragment::removePhotoFile
                    )
                )
            }
        }

        val simpleDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Modify Image")
            .setAdapter(
                ProductImageActionAdapter(
                    productImageActions
                )
            ) { _, which ->
                productImageActions[which].action.invoke()
            }
        simpleDialog.show()
    }

    companion object {
        private const val REQUEST_TAKE_PHOTO = 0
        private const val REQUEST_IMAGE_GET = 1

        private const val KEY_PHOTO_URI = "KEY_PHOTO_URI"
    }
}