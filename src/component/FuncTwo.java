package component;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import model.ConnectException;
import model.OrderException;
import Jama.ColumnMatrix;
import Jama.Matrix;

import component.param.Parameter;

import connector.InputConnector;
import connector.OutputConnector;
import formula.FormulaInterpreter;
import formula.parser_source.ParseException;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class FuncTwo extends Component implements Editable {
	/**
	 * @uml.property name="fis"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	transient private FormulaInterpreter[] fis;
	private String[] formula;
	private InputConnector inputU;
	private InputConnector inputX;
	private List<Parameter> params = new ArrayList<Parameter>();

	private OutputConnector output;

	public FuncTwo() {
		super(130, 50, "f(x,u)");
		inputU = new InputConnector("u", Component.Position.left);
		inputX = new InputConnector("x", Component.Position.bottom);
		output = new OutputConnector("y", Component.Position.right);

		register(inputU);
		register(inputX);
		register(output);

		try {
			setFormula(new String[] { "0" });
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ColumnMatrix calculateOutput() {
		if (fis != null) {
			double[] v = new double[fis.length];
			for (int k = 0; k < fis.length; k++) {
				v[k] = fis[k].evaluate();
			}
			return new ColumnMatrix(v);
		}
		return (ColumnMatrix) null;
	}

	@Override
	public void execute(double t) {
		Matrix x = null, u = null;
		if (inputX.isConnected())
			x = inputX.getInput();
		if (inputU != null)
			u = inputU.getInput();
		this.setUXT(u, x, t);
		try {
			output.setOutput(calculateOutput());
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		try {
			output.setOutput(new ColumnMatrix(output.getDim()));
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		applyParams();
	}

	@Override
	public void setDim(int dim) throws ConnectException {
	}

	/**
	 * @param formula
	 * @throws ParseException
	 * @throws OrderException
	 * @throws ConnectException
	 * @uml.property name="formula"
	 */
	public void setFormula(String[] formula) throws ParseException,
			OrderException, ConnectException {
		this.formula = formula;
		output.setDim(formula.length);
		if (formula != null) {
			fis = new FormulaInterpreter[formula.length];
			for (int i = 0; i < fis.length; i++) {
				fis[i] = new FormulaInterpreter();
				fis[i].compile(formula[i]);
			}
		}
		reset();
	}

	private void applyParams() {
		if (fis != null) {
			for (int k = 0; k < fis.length; k++) {
				if (getPanel() != null)
					for (Parameter p : getPanel().getParams()) {
						if (fis[k].hasVariable(p.getName())) {
							fis[k].setVariable(p.getName(), p.getValue());
						}
					}

				for (Parameter p : params) {
					if (fis[k].hasVariable(p.getName())) {
						fis[k].setVariable(p.getName(), p.getValue());
					}
				}
			}
		}
	}

	private void setUXT(Matrix u, Matrix x, double t) {
		if (fis != null) {
			for (int k = 0; k < fis.length; k++) {
				if (x != null) {
					for (int i = 0; i < x.getRowDimension(); i++)
						if (fis[k].hasVariable("x" + (i + 1)))
							fis[k].setVariable("x" + (i + 1), x.get(i, 0));
				}
				if (u != null) {
					for (int i = 0; i < u.getRowDimension(); i++)
						if (fis[k].hasVariable("u" + (i + 1)))
							fis[k].setVariable("u" + (i + 1), u.get(i, 0));
				}
				if (fis[k].hasVariable("t"))
					fis[k].setVariable("t", t);
			}
		}
	}

	@Override
	public void showDialog(Point point) {
		BlockEditor editor = new BlockEditor(getName(), "y", formula, inputU
				.getDim()
				+ "us," + inputX.getDim() + "xs", this);
		editor.setParams(params);
		editor.setLocation(point);
		editor.showDialog();
		if (editor.isOk()) {
			setName(editor.getTitle());
			try {
				setFormula(editor.getFormula());
				check();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public boolean check() throws Exception {
		if (fis != null) {
			for (FormulaInterpreter f : fis) {
				Set vs = f.getAllVariables();
				for (Object obj : vs) {
					String vName = (String) obj;

					if (vName.equals("t"))
						continue;

					boolean found = false;
					for (Parameter p : params) {
						if (p.getName().equals(vName)) {
							found = true;
							break;
						}
					}

					if (found)
						continue;

					for (Parameter p : getPanel().getParams()) {
						if (p.getName().equals(vName)) {
							found = true;
							break;
						}
					}

					if (found)
						continue;

					int num = 0;
					num = Integer.parseInt(vName.substring(1));
					if (vName.startsWith("u") && num >= 1
							&& num <= inputU.getDim())
						continue;
					if (vName.startsWith("x") && num >= 1
							&& num <= inputX.getDim())
						continue;
					throw new Exception("variable " + vName + " unknown!");
				}
			}
			return true;
		}
		return true;
	}

	@Override
	protected void drawComponentBody(Graphics2D g) {
		g.drawRect(x, y, width, height);
	}

	@Override
	public void recreate() {
		try {
			setFormula(formula);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
