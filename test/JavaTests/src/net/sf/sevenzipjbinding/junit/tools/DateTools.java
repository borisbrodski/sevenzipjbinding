package net.sf.sevenzipjbinding.junit.tools;

import java.util.Date;

public class DateTools {
    public static final int WEEK = 1000 * 60 * 60 * 24 * 7; // Milliseconds in a week

    public static Date getDate(int period) {
        return new Date(new Date().getTime() - RandomTools.getRandom().nextInt(period) - period);
    }
}
