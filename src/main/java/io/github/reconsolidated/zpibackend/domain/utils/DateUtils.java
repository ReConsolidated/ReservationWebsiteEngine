package io.github.reconsolidated.zpibackend.domain.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtils {
    public static boolean areOnSameDay(Date date1, Date date2) {
        Instant instant1 = date1.toInstant()
                .truncatedTo(ChronoUnit.DAYS);
        Instant instant2 = date2.toInstant()
                .truncatedTo(ChronoUnit.DAYS);
        return instant1.equals(instant2);
    }
}
