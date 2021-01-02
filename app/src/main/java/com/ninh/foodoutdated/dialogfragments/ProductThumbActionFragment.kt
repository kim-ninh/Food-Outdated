package com.ninh.foodoutdated.dialogfragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.editproduct.ProductImageAction
import com.ninh.foodoutdated.editproduct.ProductImageActionAdapter

class ProductThumbActionFragment: DialogFragment() {

    private val args: ProductThumbActionFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val argsPhotoFilePath: String? = args.photoFilePath

        val productThumbActions = mutableListOf(
            ProductImageAction(getString(R.string.action_take_photo), R.drawable.ic_camera_alt_black_24dp),
            ProductImageAction(getString(R.string.action_pick_from_gallery), R.drawable.ic_photo_black_24dp)
        )

        if (argsPhotoFilePath != null){
            productThumbActions.add(ProductImageAction(getString(R.string.action_remove_photo), R.drawable.ic_clear_black_24dp))
        }

        return MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Modify Image")
            .setAdapter(ProductImageActionAdapter(productThumbActions)){_, which ->
                val viewModelStore = findNavController().previousBackStackEntry?.viewModelStore!!
                ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory())
                findNavController().previousBackStackEntry?.savedStateHandle?.apply {
                    set(KEY_THUMB_ACTION_INDEX, which)
                }
            }
            .create()
    }

    companion object {
        const val KEY_THUMB_ACTION_INDEX = "THUMB_ACTION_INDEX"
    }
}