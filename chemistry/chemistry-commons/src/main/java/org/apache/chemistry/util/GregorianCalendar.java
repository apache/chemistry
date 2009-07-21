/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A GregorianCalendar with a better toString method.
 * <p>
 * It also provides a static {@link #fromAtomPub} method for construction.
 */
public class GregorianCalendar extends java.util.GregorianCalendar {

    private static final long serialVersionUID = 1L;

    public static GregorianCalendar getInstance() {
        return new GregorianCalendar();
    }

    public static GregorianCalendar getInstance(TimeZone zone) {
        return new GregorianCalendar(zone);
    }

    private static final Pattern ATOMPUB_PATTERN = Pattern.compile( //
    "(\\d{4})-(\\d{2})-(\\d{2})[Tt]"
            + "(\\d{2}):(\\d{2}):(\\d{2})(?:\\.(\\d{3}))?"
            + "(?:[Zz]|([+-]\\d{2}:\\d{2}))");

    /**
     * Factory from an AtomPub date string representation.
     * <p>
     * Basically parses {@code YYYY-HH-MMThh:mm:ss.sss+hh:mm}, or a {@code Z}
     * for the timezone, and with {@code .sss} being optional.
     *
     * @param date the string representation in AtomPub format
     * @return the created instance
     */
    public static GregorianCalendar fromAtomPub(String date) {
        Matcher m = ATOMPUB_PATTERN.matcher(date);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid date format: " + date);
        }
        String tz = m.group(8);
        GregorianCalendar cal = getInstance(TimeZone.getTimeZone("GMT"
                + (tz == null ? "" : tz)));
        cal.set(YEAR, Integer.parseInt(m.group(1)));
        cal.set(MONTH, Integer.parseInt(m.group(2)) - 1);
        cal.set(DATE, Integer.parseInt(m.group(3)));
        cal.set(HOUR_OF_DAY, Integer.parseInt(m.group(4)));
        cal.set(MINUTE, Integer.parseInt(m.group(5)));
        cal.set(SECOND, Integer.parseInt(m.group(6)));
        String ms = m.group(7);
        cal.set(MILLISECOND, ms == null ? 0 : Integer.parseInt(ms));
        return cal;
    }

    /**
     * Serializer of a Calendar to the AtomPub representation.
     *
     * @param cal a {@link Calendar}
     * @return the AtomPub string representation
     */
    public static String toAtomPub(Calendar cal) {
        StringBuilder buf = new StringBuilder(28);
        toAtomPub(cal, buf);
        return buf.toString();
    }

    /**
     * Serializer of a Date to the AtomPub representation.
     *
     * @param date a {@link Date}
     * @return the AtomPub string representation
     */
    public static String toAtomPub(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        return toAtomPub(cal);
    }

    protected static void toAtomPub(Calendar cal, StringBuilder buf) {
        buf.append(cal.get(YEAR));
        buf.append('-');
        int f = cal.get(MONTH);
        if (f < 9) {
            buf.append('0');
        }
        buf.append(f + 1);
        buf.append('-');
        f = cal.get(DATE);
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        buf.append('T');
        f = cal.get(HOUR_OF_DAY);
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        buf.append(':');
        f = cal.get(MINUTE);
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        buf.append(':');
        f = cal.get(SECOND);
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        buf.append('.');
        f = cal.get(MILLISECOND);
        if (f < 100) {
            buf.append('0');
        }
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis()) / 60000;
        if (offset == 0) {
            buf.append('Z');
        } else {
            char sign;
            if (offset < 0) {
                offset = -offset;
                sign = '-';
            } else {
                sign = '+';
            }
            buf.append(sign);
            f = offset / 60;
            if (f < 10) {
                buf.append('0');
            }
            buf.append(f);
            buf.append(':');
            f = offset % 60;
            if (f < 10) {
                buf.append('0');
            }
            buf.append(f);
        }
    }

    /**
     * @see java.util.GregorianCalendar#GregorianCalendar()
     */
    public GregorianCalendar() {
        super();
    }

    /**
     * @see java.util.GregorianCalendar#GregorianCalendar(TimeZone)
     */
    public GregorianCalendar(TimeZone zone) {
        super(zone);
    }

    /**
     * @see java.util.GregorianCalendar#GregorianCalendar(Locale)
     */
    public GregorianCalendar(Locale aLocale) {
        super(aLocale);
    }

    /**
     * @see java.util.GregorianCalendar#GregorianCalendar(TimeZone, Locale)
     */
    public GregorianCalendar(TimeZone zone, Locale aLocale) {
        super(zone, aLocale);
    }

    /**
     * @see java.util.GregorianCalendar#GregorianCalendar(int, int, int)
     */
    public GregorianCalendar(int year, int month, int dayOfMonth) {
        super(year, month, dayOfMonth);
    }

    /**
     * @see java.util.GregorianCalendar#GregorianCalendar(int, int, int, int,
     *      int)
     */
    public GregorianCalendar(int year, int month, int dayOfMonth,
            int hourOfDay, int minute) {
        super(year, month, dayOfMonth, hourOfDay, minute);
    }

    /**
     * @see java.util.GregorianCalendar#GregorianCalendar(int, int, int, int,
     *      int, int)
     */
    public GregorianCalendar(int year, int month, int dayOfMonth,
            int hourOfDay, int minute, int second) {
        super(year, month, dayOfMonth, hourOfDay, minute, second);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(47);
        buf.append("GregorianCalendar(");
        toAtomPub(this, buf);
        buf.append(')');
        return buf.toString();
    }

}
