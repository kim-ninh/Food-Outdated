package com.ninh.foodoutdated.newproduct

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ninh.foodoutdated.*
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.viewmodels.ProductViewModel
import com.orhanobut.logger.Logger
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddProductFragment : Fragment(R.layout.fragment_add_product),
    View.OnFocusChangeListener,
    DatePickerDialog.OnDateSetListener,
    AdapterView.OnItemSelectedListener {

    private val TAG = AddProductFragment::class.java.simpleName

    private val productEditText: EditText by lazy { findViewById<EditText>(R.id.product_edit_text) }
    private val expiryDateTextView: TextView by lazy { findViewById<TextView>(R.id.expiry_date_edit_text) }
    private val productImageView: ImageView by lazy { findViewById<ImageView>(R.id.product_image_view) }
    private val spinnerReminder: Spinner by lazy { findViewById<Spinner>(R.id.spinner_reminder) }
    private val quantityTextView: TextView by lazy { findViewById<TextView>(R.id.textViewQuantity) }
    private val numberPickerQuantity: NumberPicker by lazy { NumberPicker(requireContext()) }

    private val collapsingToolbarLayout: CollapsingToolbarLayout
            by lazy { findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbarLayout) }
    private val coordinatorLayout: CoordinatorLayout by lazy {
        requireActivity().findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
    }

    private val datePickerDialog: DatePickerDialog by lazy {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(), this,
            year, month, day
        )
    }

    private val quantityPickerDialog by lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Quantity")
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                quantityTextView.text = quantityArr[numberPickerQuantity.value]
            }
            .setView(numberPickerQuantity)
            .create()
    }

    private lateinit var productViewModel: ProductViewModel

    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private var productNameChangeConfirmed = false

    private val executorService by lazy {
        (requireActivity().application as MyApplication)
            .workerExecutor
    }

    private val quantityArr = generateSequence(1) { it + 1 }
        .map { it.toString(10) }
        .take(10)
        .toList()
        .toTypedArray()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar: Toolbar = view.findViewById(R.id.toolBar)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolBar.setupWithNavController(navController, appBarConfiguration)

        savedInstanceState?.let {
            photoUri = it.getParcelable(KEY_PHOTO_URI)
        }

        productViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )
            .get(ProductViewModel::class.java)

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

        toolBar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.item_add -> {
                    if (validate()) {
                        product?.let {
                            productViewModel.insert(it)
                        }
                    }
                    val action =
                        AddProductFragmentDirections.actionAddProductFragmentToProductsFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        arrayOf(productEditText, expiryDateTextView, productImageView, quantityTextView)
            .forEach { focusableView ->
                focusableView.onFocusChangeListener = this@AddProductFragment
            }

        productImageView.setOnClickListener { showImageActionDialog() }
        expiryDateTextView.setOnClickListener { datePickerDialog.show() }

        val spinnerReminderAdapter = ArrayAdapter
            .createFromResource(
                requireContext(),
                R.array.spinner_reminder_items,
                android.R.layout.simple_spinner_dropdown_item
            )
        spinnerReminder.adapter = spinnerReminderAdapter
        spinnerReminder.onItemSelectedListener = this

        numberPickerQuantity.minValue = 0
        numberPickerQuantity.maxValue = quantityArr.size - 1
        numberPickerQuantity.displayedValues = quantityArr

        quantityTextView.setOnClickListener { quantityPickerDialog.show() }
        setCurrentDateForTextView(expiryDateTextView)
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

    private fun updateProductImageView(file: File) {
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

    private val product: Product?
        get() {
            var product: Product? = null
            try {
                val productName = productEditText.text.toString()
                val expiryDateStr = expiryDateTextView.text.toString()
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

    private fun removePhotoFile() {
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

        val productName = productEditText.text
        if (productName.isEmpty() || productName.isBlank()){
            isValid = false
        }

        return isValid
    }

    private fun updateEditTextAndToolBar() {
        if (productEditText.text.isNotEmpty() && productEditText.text.isNotBlank()) {
            collapsingToolbarLayout.title = productEditText.text
        }else{
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
                        this@AddProductFragment::removePhotoFile
                    )
                )
            }
        }

        val simpleDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Modify Image")
            .setAdapter(ProductImageActionAdapter(productImageActions)) { _, which ->
                productImageActions[which].action.invoke()
            }
        simpleDialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        setDateForTextView(year, month, dayOfMonth, expiryDateTextView)
    }

    private fun setDateForTextView(
        year: Int, month: Int, dayOfMonth: Int,
        textView: TextView
    ) {
        val c = Calendar.getInstance()
        c[year, month] = dayOfMonth
        textView.text = DateFormat.format(getString(R.string.date_pattern_vn), c)
    }

    private fun setCurrentDateForTextView(textView: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        setDateForTextView(year, month, day, textView)
    }

    companion object {
        private const val REQUEST_TAKE_PHOTO = 0
        private const val REQUEST_IMAGE_GET = 1

        private const val KEY_PHOTO_URI = "KEY_PHOTO_URI"

        @JvmStatic
        fun newInstance() = AddProductFragment()

    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) =
        Unit
}