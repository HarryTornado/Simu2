package component;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

import model.ConnectException;
import model.OrderException;
import Jama.ColumnMatrix;
import Jama.Matrix;
import connector.InputConnector;
import connector.OutputConnector;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class Add extends Component {
	/**
	 * @uml.property name="input"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	private InputConnector[] input = new InputConnector[3];
	private OutputConnector output;

	public Add() {
		super(50, 50, "   ");
		input[0] = new InputConnector("1", Component.Position.left);
		input[1] = new InputConnector("2", Component.Position.top);
		input[2] = new InputConnector("3", Component.Position.bottom);
		output = new OutputConnector("y", Component.Position.right);

		register(input[0]);
		register(input[1]);
		register(input[2]);
		register(output);
	}

	@Override
	public void execute(double t) {
		Matrix y = new ColumnMatrix(output.getDim());
		for (InputConnector ic : input) {
			if (ic != null && ic.isConnected()) {
				y = y.plus(ic.getInput());
			}
		}
		try {
			output.setOutput(y);
		} catch (OrderException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		try {
			output.setOutput(new ColumnMatrix(output.getDim()));
		} catch (OrderException e) {
			e.printStackTrace();
		}
	}

	public void setDim(int dim) throws ConnectException {
		int maxDim = dim;
		for (InputConnector ic : input) {
			if (ic.isConnected()) {
				if (dim != 0 && ic.getSource().getDim() != dim)
					throw new ConnectException(ic.getSource(), dim);
				if (maxDim < ic.getDim())
					maxDim = ic.getDim();
			}
		}
		if (output.getDim() != maxDim) {
			output.setDim(maxDim);
			for (InputConnector ic : output.getConnected()) {
				ic.getParent().setDim(maxDim);
			}
		}
	}

	// @Override
	// public void showDialog(Point point) {
	// NameEditor editor = new NameEditor("Name of the Adder", "Name", this
	// .getName());
	// editor.setLocation(point);
	// editor.showDialog("i1 dim:" + input[0].getDim() + ", i2 dim:"
	// + input[1].getDim() + ", i3 dim:" + input[2].getDim()
	// + ", output dim:" + output.getDim());
	// if (editor.isOk()) {
	// setName(editor.getName());
	// }
	// }

	@Override
	protected void drawComponentBody(Graphics2D g) {
		g.drawOval(x, y, width, height);
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(1f));
		int sz = Math.min(width, height) * 1 / 3;
		g.drawLine(x + width / 2 - sz / 2, y + height / 2, x + width / 2 + sz
				/ 2, y + height / 2);
		g.drawLine(x + width / 2, y + height / 2 - sz / 2, x + width / 2, y
				+ height / 2 + sz / 2);
		g.setStroke(s);
	}

	@Override
	public void recreate() {
		// TODO Auto-generated method stub

	}
}
