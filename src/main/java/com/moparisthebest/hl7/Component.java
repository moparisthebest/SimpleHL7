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

public class Component extends SubComponentContainer<SubComponent> {

    Component() {
        super();
    }

    Component(final String s) {
        super(s);
    }

    Component(final String s, final Encoding enc) {
        super(s, enc, enc.subComponentDelimiter);
    }

    @Override
    SubComponent newSubComponent() {
        return new SubComponentImpl();
    }

    @Override
    SubComponent newSubComponent(final String s) {
        return new SubComponentImpl(s);
    }

    @Override
    SubComponent newSubComponent(final String s, final Encoding enc) {
        return new SubComponentImpl(s, enc);
    }

    public void encode(final StringBuilder sb, final Encoding enc) {
        super.encode(sb, enc, enc.subComponentDelimiter, enc.componentDelimiter);
    }

    public SubComponent subComponent(final int index) {
        return super.subComponent1Based(index);
    }

    // shorter aliases

    public SubComponent sc(final int index) {
        return this.subComponent(index);
    }

    @Override
    public String toString() {
        return super.toString("SubComponent");
    }
}
