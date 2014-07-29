package model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import Jama.ColumnMatrix;
import Jama.Matrix;

import component.DynaSys;
import component.param.Parameter;

import formula.FormulaInterpreter;
import formula.parser_source.ParseException;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class Output implements Serializable {
	/**
	 * @uml.property name="fis"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	transient private FormulaInterpreter[] fis;
	private List<Parameter> params;
	private DynaSys parent;
	/**
	 * @uml.property name="formula"
	 */
	private String[] formula;

	private void applyParams() {
		if (fis != null && params != null) {
			for (int k = 0; k < fis.length; k++) {
				if (parent != null && parent.getPanel() != null)
					for (Parameter p : parent.getPanel().getParams()) {
						if (fis[k].hasVariable(p.getName())) {
							fis[k].setVariable(p.getName(), p.getValue());
						}
					}

				for (Parameter p : params) {
					p.evaluate(params);
					if (fis[k].hasVariable(p.getName())) {
						fis[k].setVariable(p.getName(), p.getValue());
					}
				}
			}
		}
	}

	public void reset() {
		try {
			setFormula(formula);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		applyParams();
	}

	public Output(String[] formula, DynaSys parent) throws ParseException {
		setFormula(formula);
		this.parent = parent;
	}

	public boolean connectedToInput(int m) {
		if (fis != null) {
			for (FormulaInterpreter f : fis)
				for (int i = 0; i < m; i++)
					if (f.hasVariable("u" + (i + 1)))
						return true;
		}
		return false;
	}

	/**
	 * @return
	 * @uml.property name="fis"
	 */
	public FormulaInterpreter[] getFis() {
		return fis;
	}

	/**
	 * @return
	 * @uml.property name="formula"
	 */
	public String[] getFormula() {
		return formula;
	}

	public Matrix getOutput() {
		if (fis != null) {
			double[] v = new double[fis.length];
			for (int k = 0; k < fis.length; k++) {
				v[k] = fis[k].evaluate();
			}
			return new ColumnMatrix(v);
		}
		return null;
	}

	/**
	 * @param formula
	 * @throws ParseException
	 * @uml.property name="formula"
	 */
	public void setFormula(String[] formula) throws ParseException {
		this.formula = formula;
		if (formula != null) {
			fis = new FormulaInterpreter[formula.length];
			for (int i = 0; i < fis.length; i++) {
				fis[i] = new FormulaInterpreter();
				fis[i].compile(formula[i]);
			}
		}
	}

	public void setXUT(Matrix x, Matrix u, double t) {
		if (fis != null) {
			for (int k = 0; k < fis.length; k++) {
				for (int i = 0; i < x.getRowDimension(); i++)
					if (fis[k].hasVariable("x" + (i + 1)))
						fis[k].setVariable("x" + (i + 1), x.get(i, 0));
				for (int i = 0; i < u.getRowDimension(); i++)
					if (fis[k].hasVariable("u" + (i + 1)))
						fis[k].setVariable("u" + (i + 1), u.get(i, 0));
				if (fis[k].hasVariable("t"))
					fis[k].setVariable("t", t);
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String s : formula) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public boolean check(int iNum, int sNum) throws Exception {
		if (fis != null) {
			for (FormulaInterpreter f : fis) {
				Set vs = f.getAllVariables();
				for (Object obj : vs) {
					String vName = (String) obj;
					
					if(vName.equals("t")) continue;

					boolean found = false;
					for (Parameter p : params) {
						if (p.getName().equals(vName)) {
							found = true;
							break;
						}
					}

					if (found)
						continue;

					for (Parameter p : parent.getPanel().getParams()) {
						if (p.getName().equals(vName)) {
							found = true;
							break;
						}
					}

					if (found)
						continue;

					try {
						int num = 0;
						num = Integer.parseInt(vName.substring(1));
						if (vName.startsWith("u") && num >= 1 && num <= iNum)
							continue;
						if (vName.startsWith("x") && num >= 1 && num <= sNum)
							continue;
						throw new Exception("variable " + vName + " unknown!");
					} catch (Exception ex) {
						throw ex;
					}
				}
			}
			return true;
		}
		return true;
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
	}
}
