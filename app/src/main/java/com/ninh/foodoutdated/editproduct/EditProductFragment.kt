package com.ninh.foodoutdated.editproduct

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ninh.foodoutdated.MyApplication
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.databinding.FragmentEditProductBinding
import com.ninh.foodoutdated.extensions.hideSoftKeyboard
import com.ninh.foodoutdated.extensions.hideTitle
import com.ninh.foodoutdated.viewmodels.ProductViewModel
import com.orhanobut.logger.Logger
import java.io.File
import java.io.FileOutputStream
import java.util.*

open class EditProductFragment : Fragment(R.layout.fragment_edit_product),
    View.OnFocusChangeListener,
    Toolbar.OnMenuItemClickListener {

    private val TAG = EditProductFragment::class.java.simpleName

    private val args: EditProductFragmentArgs by navArgs()

    private var _binding: FragmentEditProductBinding? = null
    protected val binding
        get() = _binding!!

    protected val productViewModel: ProductViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
    }

    protected val executorService by lazy {
        (requireActivity().application as MyApplication)
            .workerExecutor
    }

    protected val placeholderThumbnail: Drawable by lazy {
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_waste)!!
        drawable.alpha = 255 / 10
        drawable
    }

    protected var tempFile: File? = null
    protected var photoUri: Uri? = null
    protected var photoFile: File? = null
        set(value) {
            field = value
            loadProductImage(value)
        }
    protected var productNameChangeConfirmed = false

    protected val quantityArr = generateSequence(1) { it + 1 }
        .map { it.toString(10) }
        .take(10)
        .toList()
        .toTypedArray()

    private var actionMode: ActionMode? = null

    private var actionModeCallback: ActionMode.Callback = object : ActionMode.Callback{
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when(item.itemId){
                R.id.confirm_button -> {
                    productNameChangeConfirmed = true
                    actionMode?.finish()
                    true
                }
                else -> false
            }
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.edit_name, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.title = "Edit name"
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            if (binding.name.isFocused){
                binding.name.clearFocus()
            }
        }
    }

    open fun inflateToolbarMenu() {
        binding.toolbar.inflateMenu(R.menu.delete_product)
    }

    open fun loadProductFromDB() {
        Log.i(TAG, "onViewCreated: received product id: ${args.productId}")

        productViewModel.loadProductAndRemindInfo(args.productId)
            .observe(viewLifecycleOwner) {
                updateUIs(it.product)
                binding.content.reminder.apply {
                    expiryDate = it.product.expiryDate
                    remindInfo = it.remindInfo
                }
            }
    }

    protected fun updateUIs(product: Product) = with(binding) {
        val quantityIndex = quantityArr.indexOf(product.quantity.toString())

        collapsingToolbarLayout.title = product.name
        name.setText(product.name)
        content.quantity.currentValue = quantityIndex
        content.expiry.datePicked = product.expiryDate

        loadProductImage(product.file)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProductBinding.bind(view)

        savedInstanceState?.let {
            photoUri = it.getParcelable(KEY_PHOTO_URI)
        }

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        with(binding) {
            toolbar.setupWithNavController(navController, appBarConfiguration)
            toolbar.setOnMenuItemClickListener(this@EditProductFragment)
            inflateToolbarMenu()

            thumbnail.setOnClickListener { showImageActionDialog() }
            name.setOnEditorActionListener { _, actionId, _ ->
                return@setOnEditorActionListener when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        productNameChangeConfirmed = true
                        name.clearFocus()
                        actionMode?.finish()
                        false
                    }
                    else -> false
                }
            }

            arrayOf(name, content.expiry, thumbnail, content.quantity, content.reminder)
                .forEach { focusableView ->
                    focusableView.onFocusChangeListener = this@EditProductFragment
                }

            content.quantity.displayedValues = quantityArr
            content.expiry.onDatePickChanged = { datePicked ->
                content.reminder.expiryDate = datePicked
            }
        }

        loadProductFromDB()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) = with(binding) {
        if (v == name) {
            if (hasFocus) {
                productNameChangeConfirmed = false
                name.tag = collapsingToolbarLayout.title.toString()
                collapsingToolbarLayout.hideTitle()
                v.alpha = 1f

                if (actionMode == null){
                    actionMode = (requireActivity() as AppCompatActivity).startSupportActionMode(
                        actionModeCallback
                    )
                }
                actionMode?.invalidate()
            } else {
                if (!productNameChangeConfirmed) {
                    Logger.i("Text ${name.tag} saved!.")
                    Snackbar.make(binding.root, "Updated name", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            Logger.i("Undo text to ${name.tag}.")
                            name.setText(name.tag.toString())
                            collapsingToolbarLayout.title = name.text.toString()
                        }
                        .show()
                }
                name.hideSoftKeyboard()
                updateEditTextAndToolBar()
            }
        }

        if (hasFocus && v != name) {
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

            decodeBitmapAndSave(imageUri, photoFile) {
                this.photoFile = photoFile
            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val fullSizePhotoFile: File? = this.tempFile
            val resizedPhotoFile = createImageFile()

            val photoUri = this.photoUri!!
            decodeBitmapAndSave(photoUri, resizedPhotoFile) {
                fullSizePhotoFile?.delete()
                this.photoFile = resizedPhotoFile
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_PHOTO_URI, photoUri)
    }

    protected fun loadProductImage(file: File?) {
        Glide.with(this)
            .load(file)
            .fallback(placeholderThumbnail)
            .centerCrop()
            .into(binding.thumbnail)
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
        get() = with(binding) {
            val productName = name.text.toString()
            val quantity = quantityArr[content.quantity.currentValue].toInt()
            val expiryDate = content.expiry.datePicked

            Product(productName, quantity, expiryDate, photoFile)
        }

    private fun createImageFile(): File {
        val timeStamp = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance())
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
            tempFile = photoFile
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

    private fun updateEditTextAndToolBar() = with(binding) {
        if (name.text!!.isNotEmpty() && name.text!!.isNotBlank()) {
            collapsingToolbarLayout.title = name.text
        } else {
            collapsingToolbarLayout.title = name.tag.toString()
        }
        name.alpha = 0f
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
                ProductImageActionAdapter(productImageActions)
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