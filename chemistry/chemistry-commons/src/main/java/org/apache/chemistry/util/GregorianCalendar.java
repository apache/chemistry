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
import java.util.Locale;
import java.util.TimeZone;

/**
 * A GregorianCalendar with a better toString method.
 * <p>
 * This way you don't have to pull you hair when debugging dates, or maps
 * containing dates.
 */
public class GregorianCalendar extends java.util.GregorianCalendar {

    private static final long serialVersionUID = 1L;

    public static Calendar getInstance() {
        return new GregorianCalendar();
    }

    public static Calendar getInstance(TimeZone zone) {
        return new GregorianCalendar(zone);
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
        buf.append(get(YEAR));
        buf.append('-');
        int f = get(MONTH);
        if (f < 9) {
            buf.append('0');
        }
        buf.append(f + 1);
        buf.append('-');
        f = get(DATE);
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        buf.append('T');
        f = get(HOUR_OF_DAY);
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        buf.append(':');
        f = get(MINUTE);
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        buf.append(':');
        f = get(SECOND);
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        buf.append('.');
        f = get(MILLISECOND);
        if (f < 100) {
            buf.append('0');
        }
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        char sign;
        int offset = getTimeZone().getOffset(getTimeInMillis()) / 60000;
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
        f = offset % 60;
        if (f < 10) {
            buf.append('0');
        }
        buf.append(f);
        buf.append(')');
        return buf.toString();
    }

}
