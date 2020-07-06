package com.ninh.foodoutdated.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.Utils
import com.ninh.foodoutdated.custom.view.CloseableImageView
import com.ninh.foodoutdated.custom.view.DateEditText
import com.ninh.foodoutdated.models.Product
import com.orhanobut.logger.Logger
import java.text.ParseException
import java.text.SimpleDateFormat

class AddProductFragment : Fragment() {

    private lateinit var productEditText: EditText
    private lateinit var expiryDateEditText: DateEditText
    private lateinit var productImageView: CloseableImageView
    private lateinit var buttonCamera: Button
    private lateinit var buttonGallery: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Logger.i("onViewCreated")
        productEditText = view.findViewById(R.id.product_edit_text)
        expiryDateEditText = view.findViewById(R.id.expiry_date_edit_text)
        productImageView = view.findViewById(R.id.product_image_view)
        buttonCamera = view.findViewById(R.id.button_camera)
        buttonGallery = view.findViewById(R.id.button_gallery)
    }

    private val product: Product?
        get() {
            var product: Product? = null
            try{
                val productName=  productEditText.text.toString()
                val expiryDateStr = expiryDateEditText.text.toString()
                val photoUri = productImageView.uri
                val expiryDate = SimpleDateFormat(Utils.DATE_PATTERN_VN).parse(expiryDateStr)
                product = Product(name = productName, expiry = expiryDate, uri = photoUri)
            }catch (e: ParseException){
                e.printStackTrace()
            }
            return product
        }

    companion object {
        @JvmStatic
        fun newInstance() = AddProductFragment()
    }
}