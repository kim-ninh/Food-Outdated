package com.ninh.foodoutdated.custom.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.DatePicker;

import com.ninh.foodoutdated.Utils;

import java.util.Calendar;

public class DateEditText extends androidx.appcompat.widget.AppCompatEditText
    implements DatePickerDialog.OnDateSetListener
{
    private DatePickerDialog datePickerDialog;
    private String dateFormat = Utils.DATE_PATTERN_VN;

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public DateEditText(Context context) {
        super(context);
        createDatePickerDialog();
    }

    public DateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        createDatePickerDialog();
    }

    public DateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createDatePickerDialog();
    }


    private void createDatePickerDialog(){
        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this.getContext(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year,month, dayOfMonth);
        this.setText(DateFormat.format(dateFormat, c));
    }


    @Override
    public boolean performClick() {
        datePickerDialog.show();
        return super.performClick();
    }
}
