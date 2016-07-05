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

import java.util.Objects;

class SubComponentImpl implements SubComponent {

    protected String value;

    SubComponentImpl() {
        this("");
    }

    SubComponentImpl(final String value) {
        value(value);
    }

    SubComponentImpl(final SubComponent value) {
        this.value = value.value();
    }

    SubComponentImpl(final String s, final Encoding enc) {
        this.value = enc.decode(s);
    }

    public void encode(final StringBuilder sb, final Encoding enc) {
        sb.append(enc.encode(this.value));
        sb.append(enc.subComponentDelimiter);
    }

    String encode(final Encoding enc) {
        return enc.encode(this.value);
    }

    @Override
    public SubComponent value(final String value) {
        this.value = value == null ? "" : value;
        return this;
    }

    @Override
    public boolean equals(final String s) {
        return Objects.equals(value, s);
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
