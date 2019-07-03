package com.example.foodoutdated.custom.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.foodoutdated.R;

public class ImageHolderView extends RelativeLayout
    implements View.OnClickListener {

    private ImageView imageView;
    private ImageView imageViewDelete;

    public ImageHolderView(Context context) {
        super(context);
        initView(context);
    }

    public ImageHolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ImageHolderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.image_holder_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageView = this.findViewById(R.id.imageView);
        imageViewDelete = this.findViewById(R.id.imageViewDelete);
        imageViewDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imageViewDelete)
        {
            this.setVisibility(GONE);
        }
    }

    public void setImageUri(Uri uri)
    {
        imageView.setImageURI(uri);
        imageView.setTag(uri.toString());
    }

    public String getImageUri(){
        return imageView.getTag().toString();
    }
}
