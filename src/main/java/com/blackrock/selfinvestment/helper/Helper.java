package com.blackrock.selfinvestment.helper;

import java.time.LocalDateTime;

public class Helper {
    public static boolean isBetweenInclusive(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return (target.equals(start) || target.isAfter(start)) &&
                (target.equals(end) || target.isBefore(end));
    }

    public static double taxPerSalary(double salary) {
        if (salary <= 700000) {
            return 0;
        } else if (salary <= 1000000) {
            return 0.10;
        } else if (salary <= 1200000) {
            return 0.15;
        } else if (salary <= 1500000) {
            return 0.20;
        } else {
            return 0.30;
        }
    }
}
