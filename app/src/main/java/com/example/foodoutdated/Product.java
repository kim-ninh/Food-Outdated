package com.example.foodoutdated;

import android.os.Parcel;
import android.os.Parcelable;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Product implements Parcelable {
    private String name;
    private Date expiry;        //date format: yyyy-mm-dd
    private String thumbnail;

    // constructor
    public Product() {
        this.name = "";
        this.expiry = new Date();
        this.thumbnail = "";
    }

    public Product(String name, Date expiry, String thumbnail) {
        this.name = name;
        this.expiry = expiry;
        this.thumbnail = thumbnail;
    }


    // getter and setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }


    // Parcelable implement
    protected Product(Parcel in) {
        try {
            name = in.readString();
            thumbnail = in.readString();
            expiry = new SimpleDateFormat(Utils.DATE_PATTERN_VN).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(thumbnail);
        dest.writeString(new SimpleDateFormat(Utils.DATE_PATTERN_VN).format(expiry));
    }
}
