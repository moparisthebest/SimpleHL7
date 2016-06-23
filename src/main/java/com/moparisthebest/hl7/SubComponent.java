/*
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * Copyright (c) 2016 Travis Burtrum.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the "GPL"), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 */

package com.moparisthebest.hl7;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;

public interface SubComponent {

    DateTimeFormatter LOCAL_DATE = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .parseStrict()
            .toFormatter(Locale.US)
            .withResolverStyle(ResolverStyle.STRICT);

    DateTimeFormatter LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .appendValue(HOUR_OF_DAY, 2)
            .appendValue(MINUTE_OF_HOUR, 2)
            .parseStrict()
            .toFormatter(Locale.US)
            .withResolverStyle(ResolverStyle.STRICT);

    DateTimeFormatter INSTANT = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .appendValue(HOUR_OF_DAY, 2)
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendLiteral('.')
            .appendValue(MILLI_OF_SECOND, 3)
            .appendOffset("+HHMM", "0000")
            .parseStrict()
            .toFormatter(Locale.US)
            .withZone(ZoneId.systemDefault())
            .withResolverStyle(ResolverStyle.STRICT);

    SubComponent value(final String value);

    String value();

    boolean equals(final String s);

    void encode(final StringBuilder sb, final Encoding enc);

    default boolean isEmpty() {
        return this.equals("");
    }

    default void date(final LocalDate date) {
        this.value(LOCAL_DATE.format(date));
    }

    default void date(final LocalDateTime date) {
        this.value(LOCAL_DATE_TIME.format(date));
    }

    default void date(final Instant date) {
        this.value(INSTANT.format(date));
    }

    // shorter aliases

    default SubComponent v(final String value) {
        return this.value(value);
    }

    default String v() {
        return this.value();
    }

    default void d(final LocalDate date) {
        this.date(date);
    }

    default void d(final LocalDateTime date) {
        this.date(date);
    }

    default void d(final Instant date) {
        this.date(date);
    }
}
