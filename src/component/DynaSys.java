package component;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.ConnectException;
import model.FOD;
import model.OrderException;
import model.Output;
import Jama.ColumnMatrix;
import Jama.Matrix;

import component.param.Parameter;

import connector.InputConnector;
import connector.OutputConnector;
import formula.parser_source.ParseException;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class DynaSys extends Component implements Editable {
	private double dt;

	private FOD fod;

	private InputConnector inputU;

	private Output output;

	private OutputConnector outputX;

	private OutputConnector outputY;

	private int stateNum;

	private List<Parameter> params = new ArrayList<Parameter>();

	/**
	 * @uml.property name="x0"
	 */
	private Matrix x0;

	public DynaSys() {
		super(130, 50, "dynamic");
		inputU = new InputConnector("u", Component.Position.left);
		outputY = new OutputConnector("y", Component.Position.right);
		outputX = new OutputConnector("x", Component.Position.bottom);

		try {
			this.setXFormula(new String[] { "0" });
			this.setYFormula(new String[] { "0" });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		register(inputU);
		register(outputY);
		register(outputX);
	}

	@Override
	protected void drawComponentBody(Graphics2D g) {
		int rd = Math.max(width, height) / 4;
		g.drawRoundRect(x, y, width, height, rd, rd);
	}

	@Override
	public void execute(double t) {
		Matrix u = new ColumnMatrix(inputU.getDim());
		if (inputU.isConnected())
			u = inputU.getInput();
		Matrix x = outputX.getOutput();
		x = fod.solve(x, u, t, dt);
		t += dt;
		output.setXUT(x, u, t);
		try {
			outputY.setOutput((ColumnMatrix) output.getOutput());
			outputX.setOutput(x);
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void execute2(Matrix u, double t) {
		Matrix x = outputX.getOutput();
		x = fod.solve(x, u, t, dt);
		t += dt;
		output.setXUT(x, u, t);
		try {
			outputY.setOutput((ColumnMatrix) output.getOutput());
			outputX.setOutput(x);
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 * @uml.property name="inputU"
	 */
	public InputConnector getInputU() {
		return inputU;
	}

	public String[] getStateNames() {
		String[] names = new String[stateNum];
		for (int i = 0; i < names.length; i++) {
			names[i] = "x" + (i + 1);
		}
		return names;
	}

	/**
	 * @return
	 * @uml.property name="x0"
	 */
	public Matrix getX0() {
		return x0;
	}

	public String[] getXFormula() {
		return fod.getFormula();
	}

	public String[] getYFormula() {
		return output.getFormula();
	}

	public Matrix getYMatrix() {
		return output.getOutput();
	}

	@Override
	public void recreate() {
		fod.reset();
		output.reset();
	}

	@Override
	public void reset() {
		try {
			fod.reset();
			output.reset();
			outputX.setOutput(x0);

			output.setXUT(x0, new ColumnMatrix(inputU.getDim()), 0);

			outputY.setOutput(output.getOutput());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setDim(int dim) {
	}

	/**
	 * @param dt
	 * @uml.property name="dt"
	 */
	public void setDt(double dt) {
		this.dt = dt;
	}

	/**
	 * @param x0
	 * @uml.property name="x0"
	 */
	public void setX0(Matrix x0) {
		this.x0 = x0;
	}

	public void setParams() {
		fod.setParams(params);
		output.setParams(params);
		try {
			fod.check(inputU.getDim(), stateNum);
			output.check(inputU.getDim(), stateNum);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public void setXFormula(String[] formula) throws OrderException,
			ParseException, ConnectException {
		stateNum = formula.length;
		outputX.setDim(stateNum);
		if (fod == null)
			fod = new FOD(formula, this);
		else
			fod.setFormula(formula);
		if (x0 == null || x0.getRowDimension() != stateNum)
			x0 = new ColumnMatrix(stateNum);
	}

	public void setYFormula(String[] formula) throws ParseException,
			ConnectException {
		if (output == null)
			output = new Output(formula, this);
		else
			output.setFormula(formula);
		outputY.setDim(formula.length);
	}

	@Override
	public void showDialog(Point point) {
		DynamicEditor de = new DynamicEditor(this);
		de.setParams(params);
		de.setLocation(point);
		de.showDialog();
		if (de.isOk()) {
			this.setX0(de.getX0());
			this.setName(de.getTitle());
			this.params = de.getParams();
			try {
				this.setXFormula(de.getXFormula());
				this.setYFormula(de.getYFormula());
				this.setParams();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getClass()
						.getSimpleName()
						+ ":\n" + e.getMessage());
			}
		}
	}
}
