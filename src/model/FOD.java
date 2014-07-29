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
public class FOD implements Serializable {
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
	/**
	 * @uml.property name="order"
	 */
	private int order;

	public FOD(String[] formula, DynaSys parent) throws ParseException {
		setFormula(formula);
		this.parent = parent;
	}

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

	public boolean check(int iNum, int sNum) throws Exception {
		if (fis != null) {
			for (FormulaInterpreter f : fis) {
				@SuppressWarnings("rawtypes")
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
						showParams();
						throw ex;
					}
				}
			}
			return true;
		}
		return true;
	}

	private void showParams() {
		System.out.println("Avaliable parameters:");
		for (Parameter p : params)
			System.out.println(p);
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

	private Matrix evaluate(Matrix x, Matrix u, double t) {
		double[] v = new double[fis.length];
		for (int k = 0; k < fis.length; k++) {
			for (int i = 0; i < x.getRowDimension(); i++)
				if (fis[k].hasVariable("x" + (i + 1)))
					fis[k].setVariable("x" + (i + 1), x.get(i, 0));
			if (u != null)
				for (int i = 0; i < u.getRowDimension(); i++)
					if (fis[k].hasVariable("u" + (i + 1)))
						fis[k].setVariable("u" + (i + 1), u.get(i, 0));
			if (fis[k].hasVariable("t"))
				fis[k].setVariable("t", t);
			v[k] = fis[k].evaluate();
		}
		return new ColumnMatrix(v);
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

	/**
	 * @return
	 * @uml.property name="order"
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param formula
	 * @throws ParseException
	 * @uml.property name="formula"
	 */
	public void setFormula(String[] formula) throws ParseException {
		this.formula = formula;
		order = formula.length;
		fis = new FormulaInterpreter[formula.length];
		for (int i = 0; i < fis.length; i++) {
			fis[i] = new FormulaInterpreter();
			fis[i].compile(formula[i]);
		}
	}

	public Matrix solve(Matrix x, Matrix u, double t, double h) {
		Matrix k1 = evaluate(x, u, t);

		Matrix k2 = evaluate(x.plus(k1.times(h / 2)), u, t + h / 1);
		Matrix k3 = evaluate(x.plus(k2.times(h / 2)), u, t + h / 2);
		Matrix k4 = evaluate(x.plus(k3.times(h)), u, t + h);

		Matrix ret = k1.plus(k2.times(2)).plus(k3.times(2)).plus(k4).times(
				h / 6).plus(x);
		return ret;
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

	public void setParams(List<Parameter> params) {
		this.params = params;
	}
}
