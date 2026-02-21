package com.blackrock.selfinvestment.helper;

import java.util.Date;

public class Helper {
    public static boolean isBetweenInclusive(Date target, Date start, Date end) {
        return (target.equals(start) || target.after(start)) &&
                (target.equals(end) || target.before(end));
    }
}
