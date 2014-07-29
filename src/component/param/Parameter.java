package component.param;

import java.io.Serializable;
import java.util.List;

import formula.FormulaInterpreter;

@SuppressWarnings("serial")
public class Parameter implements Serializable {
	transient private FormulaInterpreter fi;
	private String formula;
	private String name;
	private double value;

	public Parameter(String name) throws Exception {
		setName(name);
	}

	public Parameter(String name, String formula) throws Exception {
		setName(name);
		setFormula(formula);
	}

	public void evaluate(List<Parameter> params) {
		if (fi == null)
			try {
				setFormula(formula);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		for (Parameter p : params) {
			if (fi.hasVariable(p.name)) {
				fi.setVariable(p.name, p.value);
			}
		}
		value = fi.evaluate();
	}

	public void setFormula(String formula) throws Exception {
		if (formula == null)
			throw new Exception("formula should not be null");
		this.formula = formula = formula.trim();
		if (this.formula.length() < 1)
			throw new Exception("formula should not be empty");
		fi = new FormulaInterpreter();
		fi.compile(formula);
	}

	@Override
	public String toString() {
		return name + " = " + formula + " = " + value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws Exception {
		if (name == null)
			throw new Exception("Name should not be null!");
		String s = name.replaceAll("\\W", "");
		if (!s.equals(name))
			throw new Exception("name cannot contain non-word chars");
		if (name.length() < 1)
			throw new Exception("name must contain at least one chars");
		if (Character.isDigit(name.charAt(0)))
			throw new Exception("name can not begin with digits");
		this.name = name;
	}

	public String getFormula() {
		return formula;
	}

	public double getValue() {
		return value;
	}
}
