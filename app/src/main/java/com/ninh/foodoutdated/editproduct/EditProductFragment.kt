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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.ninh.foodoutdated.AlarmUtils
import com.ninh.foodoutdated.MyApplication
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.data.models.ProductAndRemindInfo
import com.ninh.foodoutdated.data.models.RemindInfo
import com.ninh.foodoutdated.data.models.RepeatingType
import com.ninh.foodoutdated.databinding.FragmentEditProductBinding
import com.ninh.foodoutdated.dialogfragments.DatePickerFragment
import com.ninh.foodoutdated.dialogfragments.NumberPickerFragment
import com.ninh.foodoutdated.dialogfragments.ProductThumbActionFragment
import com.ninh.foodoutdated.dialogfragments.ReminderPickerFragment
import com.ninh.foodoutdated.extensions.*
import com.ninh.foodoutdated.viewmodels.ProductViewModel
import com.ninh.foodoutdated.viewmodels.ProductsViewModel
import com.orhanobut.logger.Logger
import java.io.File
import java.io.FileOutputStream
import java.util.*

open class EditProductFragment : Fragment(R.layout.fragment_edit_product),
    View.OnFocusChangeListener,
    Toolbar.OnMenuItemClickListener{

    private val args: EditProductFragmentArgs by navArgs()

    private var _binding: FragmentEditProductBinding? = null
    protected val binding
        get() = _binding!!

    protected val productsViewModel: ProductsViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
    }

    protected val productViewModel: ProductViewModel by viewModels()

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
    protected var productNameChangeConfirmed = false

    protected var _loadedProduct = ProductAndRemindInfo(Product(), RemindInfo())
    protected val loadedProduct
        get() = _loadedProduct

    private var actionMode: ActionMode? = null

    private var actionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
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
            if (binding.name.isFocused) {
                binding.name.clearFocus()
            }
        }
    }

    open fun inflateToolbarMenu() {
        binding.toolbar.inflateMenu(R.menu.delete_product)
    }

    open fun loadProductFromDB() {
        Log.i(TAG, "onViewCreated: received product id: ${args.productId}")

        productsViewModel.load(args.productId)
            .observe(viewLifecycleOwner) {
                _loadedProduct = it.copy()
                productViewModel.setProduct(it)
            }
    }

    protected open val actionToDatePickerFragmentFunc =
        (EditProductFragmentDirections)::actionEditProductFragmentToDatePickerFragment

    private fun getActionToDatePickerFragment(productViewModel: ProductViewModel): NavDirections =
        with(productViewModel){
            actionToDatePickerFragmentFunc.invoke(expiry.value!!)
        }

    protected open val actionToNumberPickerFragmentFunc =
        (EditProductFragmentDirections)::actionEditProductFragmentToNumberPickerFragment

    private fun getActionToNumberPickerFragment(productViewModel: ProductViewModel): NavDirections =
        with(productViewModel){
            actionToNumberPickerFragmentFunc.invoke(1, 10, quantity.value!!)
        }

    protected open val actionToReminderPickerFragmentFunc =
        (EditProductFragmentDirections)::actionEditProductFragmentToReminderPickerFragment

    private fun getActionToReminderPickerFragment(productViewModel: ProductViewModel): NavDirections =
        with(productViewModel){
            val expiry = expiry.value!!
            val triggerDate = reminder.value!!
            val repeatingType = productAndRemindInfo.value!!.remindInfo.repeating

            Logger.d("TriggerTime: ${triggerDate.hour}:${triggerDate.minute}")
            actionToReminderPickerFragmentFunc.invoke(expiry, triggerDate, repeatingType)
        }

    protected open val actionToProductThumbActionFragmentFunc =
        (EditProductFragmentDirections)::actionEditProductFragmentToProductThumbActionFragment

    private fun getActionToProductThumbActionFragment(productViewModel: ProductViewModel): NavDirections =
        with(productViewModel) {
            val filePath = thumb.value?.absolutePath
            actionToProductThumbActionFragmentFunc.invoke(filePath)
        }


    protected open fun getThisBackStackEntry(navController: NavController) =
        navController.getBackStackEntry(R.id.editProductFragment)

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

            val actions = arrayOf(
                this@EditProductFragment::getActionToProductThumbActionFragment,
                this@EditProductFragment::getActionToNumberPickerFragment,
                this@EditProductFragment::getActionToDatePickerFragment,
                this@EditProductFragment::getActionToReminderPickerFragment
            )

            arrayOf(thumbnail, content.quantity, content.expiry, content.reminder)
                .forEachIndexed { index, view ->
                    view.setOnClickListener {
                        val action = actions[index].invoke(productViewModel)
                        it.findNavController().navigate(action)
                    }
                }

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

            productViewModel.name.observe(viewLifecycleOwner) {
                if (it.isNotEmpty() && it.isNotBlank()) {
                    collapsingToolbarLayout.title = it
                    name.setText(it)
                } else {
                    collapsingToolbarLayout.title = "Untitled"
                    name.setText("")
                }
                name.alpha = 0f
            }

            productViewModel.quantity.observe(viewLifecycleOwner) {
                content.quantity.text = it.toString()
            }

            productViewModel.expiry.observe(viewLifecycleOwner) {
                content.expiry.text = DateFormat.format(getString(R.string.date_pattern_vn), it)
            }

            productViewModel.thumb.observe(viewLifecycleOwner) {
                loadProductImage(it)
            }

            productViewModel.reminder.observe(viewLifecycleOwner) {
                content.reminder.text =
                    DateFormat.format(getString(R.string.reminder_date_time), it)
            }

            Unit
        }

        val navBackStackEntry = getThisBackStackEntry(navController)

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                with(navBackStackEntry.savedStateHandle) {
                    if (contains(DatePickerFragment.KEY_PICKED_DATE)) {
                        val pickedDate = pop<Calendar>(DatePickerFragment.KEY_PICKED_DATE)!!
                        productViewModel.setExpiry(pickedDate)
                    }

                    if (contains(ProductThumbActionFragment.KEY_THUMB_ACTION_INDEX)) {
                        val actionIndex =
                            pop<Int>(ProductThumbActionFragment.KEY_THUMB_ACTION_INDEX)!!

                        val productThumbActions = mutableListOf(
                            this@EditProductFragment::dispatchTakePictureIntent,
                            this@EditProductFragment::selectImage,
                            this@EditProductFragment::removePhotoFile
                        )
                        productThumbActions[actionIndex].invoke()
                    }

                    if (contains(NumberPickerFragment.KEY_SELECTED_VALUE)) {
                        val quantity = pop<Int>(NumberPickerFragment.KEY_SELECTED_VALUE)!!
                        productViewModel.setQuantity(quantity)
                    }

                    if (contains(ReminderPickerFragment.KEY_REMIND_INFO)) {
                        val remindInfo = pop<RemindInfo>(ReminderPickerFragment.KEY_REMIND_INFO)!!
                        productViewModel.setReminder(remindInfo)
                    }
                }
            }
        }

        navBackStackEntry.lifecycle.addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })

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

                if (actionMode == null) {
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
                            productViewModel.setName(name.tag.toString())
                        }
                        .show()
                }
                name.hideSoftKeyboard()
                productViewModel.setName(name.text.toString())
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
                productViewModel.setThumb(photoFile)
            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val fullSizePhotoFile: File? = this.tempFile
            val resizedPhotoFile = createImageFile()

            val photoUri = this.photoUri!!
            decodeBitmapAndSave(photoUri, resizedPhotoFile) {
                fullSizePhotoFile?.delete()
                this.photoFile = resizedPhotoFile
                productViewModel.setThumb(resizedPhotoFile)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_PHOTO_URI, photoUri)
    }

    open fun onDestroyImp() {
        if (productViewModel.productAndRemindInfo.value != loadedProduct) {
            AlarmUtils.update(
                requireContext(),
                productViewModel.productAndRemindInfo.value!!.remindInfo
            )
            productsViewModel.update(productViewModel.productAndRemindInfo.value!!)
        }
    }

    override fun onDestroy() {
        onDestroyImp()
        super.onDestroy()
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
        productViewModel.setThumb(photoFile)
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

    companion object {
        private val TAG = EditProductFragment::class.java.simpleName
        private const val REQUEST_TAKE_PHOTO = 0
        private const val REQUEST_IMAGE_GET = 1

        private const val KEY_PHOTO_URI = "KEY_PHOTO_URI"
    }
}