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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Encoding extends Field {
    public final char segmentDelimiter, repetitionDelimiter, fieldDelimiter, componentDelimiter, subComponentDelimiter, escapeCharacter, verticalTab, fileSeperator;
    public final String repetitionEscape, fieldEscape, componentEscape, subComponentEscape, escapeEscape;

    private final String repetitionDelimiterString, fieldDelimiterString, componentDelimiterString, subComponentDelimiterString, escapeCharacterString, value;

    public final MSH1 msh1;

    //                               '\r',                         '~',                          '|',                         '^',                             '&',                          '\\'
    public Encoding(final char segmentDelimiter, final char repetitionDelimiter, final char fieldDelimiter, final char componentDelimiter, final char subComponentDelimiter, final char escapeCharacter) {
        this(segmentDelimiter, repetitionDelimiter, fieldDelimiter, componentDelimiter, subComponentDelimiter, escapeCharacter, (char) 11, (char) 28);
    }

    public Encoding(final char segmentDelimiter, final char repetitionDelimiter, final char fieldDelimiter, final char componentDelimiter, final char subComponentDelimiter, final char escapeCharacter, final char verticalTab, final char fileSeperator) {
        this.segmentDelimiter = segmentDelimiter;
        this.repetitionDelimiter = repetitionDelimiter;
        this.fieldDelimiter = fieldDelimiter;
        this.componentDelimiter = componentDelimiter;
        this.subComponentDelimiter = subComponentDelimiter;
        this.escapeCharacter = escapeCharacter;
        this.verticalTab = verticalTab;
        this.fileSeperator = fileSeperator;

        this.repetitionEscape = escapeCharacter + "R" + escapeCharacter;
        this.fieldEscape = escapeCharacter + "F" + escapeCharacter;
        this.componentEscape = escapeCharacter + "S" + escapeCharacter;
        this.subComponentEscape = escapeCharacter + "T" + escapeCharacter;
        this.escapeEscape = escapeCharacter + "E" + escapeCharacter;

        this.repetitionDelimiterString = "" + repetitionDelimiter;
        this.fieldDelimiterString = "" + fieldDelimiter;
        this.componentDelimiterString = "" + componentDelimiter;
        this.subComponentDelimiterString = "" + subComponentDelimiter;
        this.escapeCharacterString = "" + escapeCharacter;

        this.value = componentDelimiterString + repetitionDelimiter + escapeCharacter + subComponentDelimiter;
        this.msh1 = new MSH1(this.fieldDelimiterString);
    }

    public String decode(final String s) {
        return s.replace(repetitionEscape, repetitionDelimiterString)
                .replace(fieldEscape, fieldDelimiterString)
                .replace(componentEscape, componentDelimiterString)
                .replace(subComponentEscape, subComponentDelimiterString)
                .replace(escapeEscape, escapeCharacterString);
    }

    public String encode(final String s) {
        return s.replace(escapeCharacterString, escapeEscape)
                .replace(repetitionDelimiterString, repetitionEscape)
                .replace(fieldDelimiterString, fieldEscape)
                .replace(componentDelimiterString, componentEscape)
                .replace(subComponentDelimiterString, subComponentEscape);
    }

    public Collection<String> splitSegment(final String s) {
        return split(s, segmentDelimiter);
    }

    public Collection<String> splitRepetition(final String s) {
        return split(s, repetitionDelimiter);
    }

    public Collection<String> splitField(final String s) {
        return split(s, fieldDelimiter);
    }

    public Collection<String> splitComponent(final String s) {
        return split(s, componentDelimiter);
    }

    public Collection<String> splitSubComponent(final String s) {
        return split(s, subComponentDelimiter);
    }

    public Collection<String> split(final String s, final char ch) {
        int off = 0;
        int next = 0;
        final List<String> list = new ArrayList<>();
        while ((next = s.indexOf(ch, off)) != -1) {
            list.add(s.substring(off, next));
            off = next + 1;
        }

        // Add remaining segment
        list.add(s.substring(off, s.length()));

        return list;
    }

    @Override
    public void encode(final StringBuilder sb, final Encoding enc) {
        sb.append(this.value);
        sb.append(enc.fieldDelimiter);
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public SubComponent value(final String value) {
        // do nothing
        return this;
    }

    @Override
    public boolean equals(final String s) {
        return value.equals(s);
    }

    @Override
    public String toString() {
        return value;
    }

    static class MSH1 extends Field {

        private final String value;

        private MSH1(final String fieldDelimiter) {
            this.value = fieldDelimiter;
        }

        @Override
        public void encode(final StringBuilder sb, final Encoding enc) {
            // do nothing
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public SubComponent value(final String value) {
            // do nothing
            return this;
        }

        @Override
        public boolean equals(final String s) {
            return value.equals(s);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
