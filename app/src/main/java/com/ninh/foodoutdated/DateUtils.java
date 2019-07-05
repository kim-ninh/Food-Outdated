package com.ninh.foodoutdated;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static int substract(Date d2, Date d1) {
        Calendar c1, c2;
        c1 = Calendar.getInstance();
        c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);

        return subtract(c2, c1);
    }

    public static int subtract(Calendar c2, Calendar c1) {
        int d = 0;

        Calendar tmpC1, tmpC2;
        if (c2.after(c1)) {
            tmpC1 = (Calendar) c1.clone();
            tmpC2 = (Calendar) c2.clone();
        } else {
            tmpC2 = (Calendar) c1.clone();
            tmpC1 = (Calendar) c2.clone();
        }

        int nLeapYear = countLeapYear(tmpC1.get(Calendar.YEAR), tmpC2.get(Calendar.YEAR));
        int nYear = tmpC2.get(Calendar.YEAR) - tmpC1.get(Calendar.YEAR);
        d = tmpC2.get(Calendar.DAY_OF_YEAR) - tmpC1.get(Calendar.DAY_OF_YEAR);
        d += (366 * nLeapYear + 365 * (nYear - nLeapYear));

        if (c1.after(c2)) {
            d *= -1;
        }
        return d;
    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0)
                || (year % 400 == 0);
    }

    private static int countLeapYear(int fromYear, int toYear) {
        int count = 0;
        for (int yyyy = fromYear; yyyy < toYear; yyyy++) {
            if (isLeapYear(yyyy))
                count++;
        }
        return count;
    }
}
