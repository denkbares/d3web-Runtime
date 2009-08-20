package de.d3web.kernel.domainModel;

/**
 * Type definition for a tupel of rule and values (extensible) There will be
 * saved a rule and the values it has overwritten by firing Creation date:
 * (18.08.2000 17:38:21)
 * 
 * @author Norman Brümmer
 */
public class SymptomValue {
    private Object[] values = null;
    private RuleComplex rule = null;

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
    public SymptomValue(Object[] values, RuleComplex rule) {
        this.values = values;
        this.rule = rule;
    }

    /**
     * 
     * Creation date: (17.08.2000 14:36:58)
     * 
     * @return rule of this tupel
     */
    public RuleComplex getRule() {
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
        return rule.getId() + ":" + de.d3web.kernel.utilities.Utils.createVector(values);
    }
}