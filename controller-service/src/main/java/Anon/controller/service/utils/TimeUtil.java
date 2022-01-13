package Anon.controller.service.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Utility functions for time related tasks.
 * <p>
 */
class TimeUtil {
    /**
     * Convert UNIX time (a integer) to a Joda DateTime object.
     *
     * @param unixtime the unix time
     * @return a DateTime object converted from UNIX time stamp
     */
    public static DateTime getTimeFromUnix(long unixtime) {
        return new DateTime(unixtime * 1000);
    }

    /**
     * Get the local time. Current setting for time zone is Pacific time "America/Los_Angeles"
     *
     * @return the DateTime variable with local timezone setting.
     */
    public static DateTime getLocalTime() {
        return new DateTime().toDateTime(DateTimeZone.forID("America/Los_Angeles"));
    }
}
