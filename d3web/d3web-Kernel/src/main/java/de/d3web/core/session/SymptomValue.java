/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.core.session;

import de.d3web.core.inference.Rule;

/**
 * Type definition for a tupel of rule and values (extensible) There will be
 * saved a rule and the values it has overwritten by firing Creation date:
 * (18.08.2000 17:38:21)
 * 
 * @author Norman Brümmer
 */
public class SymptomValue {
    private Object[] values = null;
    private Rule rule = null;

    /**
     * Creates a new SymptomValue-Object - a tupel of an overwritten value-array
     * and the rule that has overwritten it
     * 
     * @param values
     *            overwritten question-value-array
     * @param rule
     *            rule that has overwritten it
     * 
     */
    public SymptomValue(Object[] values, Rule rule) {
        this.values = values;
        this.rule = rule;
    }

    /**
     * 
     * Creation date: (17.08.2000 14:36:58)
     * 
     * @return rule of this tupel
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * Creation date: (17.08.2000 14:45:39)
     * 
     * @return value-array of this tupel
     */
    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public String toString() {
        return rule.getId() + ":" + de.d3web.core.utilities.Utils.createVector(values);
    }
}