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
import java.util.List;

abstract class SubComponentContainer<T extends SubComponent> implements SubComponent {
    
    protected final List<T> subComponents = new ArrayList<>();

    protected SubComponentContainer() {
        subComponents.add(newSubComponent());
    }

    protected SubComponentContainer(final String s) {
        subComponents.add(newSubComponent(s));
    }

    protected SubComponentContainer(final String s, final Encoding enc, final char thisDelimiter) {
        for(final String line : enc.split(s, thisDelimiter))
            subComponents.add(newSubComponent(line, enc));
        if(subComponents.isEmpty())
            subComponents.add(newSubComponent());
    }
    
    abstract T newSubComponent();
    abstract T newSubComponent(final String s);
    abstract T newSubComponent(final String s, final Encoding enc);

    protected final void encode(final StringBuilder sb, final Encoding enc, final char thisDelimiter, final char parentDelimiter) {
        for(final SubComponent e : subComponents)
            e.encode(sb, enc);
        if(!subComponents.isEmpty()){
            int index = 1, count = -1;
            while (++count < subComponents.size() && index > 0 && sb.charAt(index = sb.length() - 1) == thisDelimiter)
                sb.setLength(index);
        }
        sb.append(parentDelimiter);
    }

    protected T subComponent0Based(final int index) {
        while(subComponents.size() <= index)
            subComponents.add(newSubComponent());
        return subComponents.get(index);
    }

    protected T subComponent1Based(final int index) {
        while(subComponents.size() < index)
            subComponents.add(newSubComponent());
        return subComponents.get(index - 1);
    }

    @Override
    public SubComponent value(final String value) {
        if(subComponents.size() == 1) {
            subComponents.get(0).value(value);
        } else {
            subComponents.clear();
            subComponents.add(newSubComponent(value));
        }
        return this;
    }

    @Override
    public String value() {
        // todo: this isn't ALWAYS correct, it just returns the first, ignoring any other field/repetition/component/subcomponent/whatever
        return subComponents.isEmpty() ? "" : subComponents.get(0).value();
    }

    @Override
    public boolean equals(final String s) {
        // more complicated than you'd think
        if("".equals(s)) {
            // special case for empty string
            if(subComponents.isEmpty())
                return true;
            // otherwise every one needs to be empty
            for(final SubComponent sc : subComponents)
                if(!sc.isEmpty())
                    return false;
        } else {
            if(subComponents.isEmpty())
                return false;
            boolean first = true;
            // first has to match, and every OTHER one needs to be empty now...
            for(final SubComponent sc : subComponents) {
                if (!(first ? sc.equals(s) : sc.isEmpty()))
                    return false;
                first = false;
            }
        }
        return true;
    }

    protected final String toString(final String name) {
        return subComponents.size() == 1 ? subComponents.get(0).toString() : name + subComponents;
    }
}
