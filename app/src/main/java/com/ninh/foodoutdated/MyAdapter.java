package com.ninh.foodoutdated;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Product> mDataset;
    private static Context context;
    private SelectionTracker<Long> tracker = null;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Product> myDataset) {
        mDataset = myDataset;
        setHasStableIds(true);
    }

    public void setTracker(SelectionTracker tracker) {
        this.tracker = tracker;
    }

    public void setContext(Context context) {
        MyAdapter.context = context;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Product product = mDataset.get(position);
        if (tracker != null) {
            holder.bind(product, tracker.isSelected((long) position));
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);
        //...
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout linearLayout;
        public MyViewHolder(LinearLayout v) {
            super(v);
            linearLayout = v;
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            ItemDetailsLookup.ItemDetails<Long> obj = new ItemDetailsLookup.ItemDetails<Long>() {
                @Override
                public int getPosition() {
                    return getAdapterPosition();
                }

                @Nullable
                @Override
                public Long getSelectionKey() {
                    return getItemId();
                }
            };

            return obj;
        }

        public void bind(Product product, boolean isActived) {
            TextView txtName = linearLayout.findViewById(R.id.product_name);
            TextView txtEpiry = linearLayout.findViewById(R.id.product_expiry);
            ImageView imageThumbnail = linearLayout.findViewById(R.id.product_thumbnail);

            txtName.setText(product.getName());
            txtEpiry.setText(DateFormat.format(Utils.DATE_PATTERN_VN, product.getExpiry()));
            imageThumbnail.setImageURI(Uri.parse(product.getThumbnail()));
            linearLayout.setActivated(isActived);

            int greenColor = context.getResources().getColor(R.color.green);
            int redColor = context.getResources().getColor(R.color.red);
            int yellowColor = context.getResources().getColor(R.color.yellow);
            switch (product.getState()) {
                case NEW:
                    txtEpiry.setTextColor(greenColor);
                    break;
                case EXPIRIED:
                    txtEpiry.setTextColor(redColor);
                    break;
                case NEARLY_EXPIRY:
                    txtEpiry.setTextColor(yellowColor);
                    break;
            }
        }
    }
}