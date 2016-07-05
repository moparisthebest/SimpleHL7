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

public class Line extends SubComponentContainer<Field> {

    public Line(final String s) {
        super(s);
    }

    public Line(final Line toClone) {
        super(toClone);
    }

    Line(final String s, final Encoding enc) {
        super(s, enc, enc.fieldDelimiter);
    }

    @Override
    Field newSubComponent() {
        return new Field();
    }

    @Override
    Field newSubComponent(final String s) {
        return new Field(s);
    }

    @Override
    Field newSubComponent(final String s, final Encoding enc) {
        return new Field(s, enc);
    }

    @Override
    Field newSubComponent(final Field s) {
        // no need to clone Encoding or Encoding.MSH1, they are immutable
        return s instanceof Encoding || s instanceof Encoding.MSH1 ? s : new Field(s);
    }

    public void encode(final StringBuilder sb, final Encoding enc) {
        super.encode(sb, enc, enc.fieldDelimiter, enc.segmentDelimiter);
    }

    public Field field(final int index) {
        return super.subComponent0Based(index);
    }

    public Repetition repetition(final int fieldIndex, final int repetitionIndex) {
        return this.field(fieldIndex).repetition(repetitionIndex);
    }

    public Component component(final int fieldIndex, final int repetitionIndex, final int componentIndex) {
        return this.field(fieldIndex).repetition(repetitionIndex).component(componentIndex);
    }

    public Component component(final int fieldIndex, final int componentIndex) {
        return this.field(fieldIndex).component(componentIndex);
    }

    public SubComponent subComponent(final int fieldIndex, final int repetitionIndex, final int componentIndex, final int subComponentIndex) {
        return this.field(fieldIndex).repetition(repetitionIndex).component(componentIndex).subComponent(subComponentIndex);
    }

    public SubComponent subComponent(final int fieldIndex, final int componentIndex, final int subComponentIndex) {
        return this.field(fieldIndex).component(componentIndex).subComponent(subComponentIndex);
    }

    // shorter aliases

    public Field f(final int index) {
        return this.field(index);
    }

    public Repetition r(final int fieldIndex, final int repetitionIndex) {
        return this.repetition(fieldIndex, repetitionIndex);
    }

    public Component c(final int fieldIndex, final int repetitionIndex, final int componentIndex) {
        return this.component(fieldIndex, repetitionIndex, componentIndex);
    }

    public Component c(final int fieldIndex, final int componentIndex) {
        return this.component(fieldIndex, componentIndex);
    }

    public SubComponent sc(final int fieldIndex, final int repetitionIndex, final int componentIndex, final int subComponentIndex) {
        return this.subComponent(fieldIndex, repetitionIndex, componentIndex, subComponentIndex);
    }

    public SubComponent sc(final int fieldIndex, final int componentIndex, final int subComponentIndex) {
        return this.subComponent(fieldIndex, componentIndex, subComponentIndex);
    }

    @Override
    public String toString() {
        return super.toString("Line");
    }
}
