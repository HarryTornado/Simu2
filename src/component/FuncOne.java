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
public class FuncOne extends Component implements Editable {
	/**
	 * @uml.property name="fis"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	transient private FormulaInterpreter[] fis;
	private String[] formula;
	private InputConnector inputU;
	private OutputConnector output;
	private List<Parameter> params = new ArrayList<Parameter>();

	public FuncOne() {
		super(100, 40, "f(u)");
		inputU = new InputConnector("u", Component.Position.left);
		output = new OutputConnector("y", Component.Position.right);

		register(inputU);
		register(output);

		try {
			setDim(1);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void execute(double t) {
		Matrix u = inputU.getInput();
		// System.out.println("here");
		this.setUT(u, t);
		try {
			Matrix data = calculateOutput();
			if (data != null)
				output.setOutput(data);
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 * @uml.property name="output"
	 */
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
	public void reset() {
		try {
			output.setOutput(new ColumnMatrix(output.getDim()));
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		applyParams();
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

	@Override
	public void setDim(int dim) throws ConnectException {
		if (output.getDim() != dim) {
			try {
				setFormula(new String[dim]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OrderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			output.setDim(dim);
			for (InputConnector ic : output.getConnected()) {
				ic.getParent().setDim(dim);
			}
		}
	}

	/**
	 * @param formula
	 * @throws ParseException
	 * @throws OrderException
	 * @uml.property name="formula"
	 */
	public void setFormula(String[] formula) throws ParseException,
			OrderException {
		this.formula = formula;
		if (formula != null) {
			fis = new FormulaInterpreter[formula.length];
			for (int i = 0; i < fis.length; i++) {
				fis[i] = new FormulaInterpreter();
				fis[i].compile(formula[i]);
			}
		}
		reset();
	}

	private void setUT(Matrix u, double t) {
		if (fis != null) {
			for (int k = 0; k < fis.length; k++) {
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
		FuncOneEditor editor = new FuncOneEditor(getName(), "y", formula, this);
		editor.setParams(params);
		editor.setLocation(point);
		editor.showDialog();
		if (editor.isOk()) {
			setName(editor.getTitle());
			try {
				setFormula(editor.getFormula());
				params = editor.getParams();
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
		}
	}
}
