package component;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Arrays;

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
public class Mult extends Component {
	/**
	 * @uml.property name="inputs"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	private InputConnector[] inputs = new InputConnector[3];
	private OutputConnector output;

	public Mult() {
		super(50, 50, "   ");
		inputs[0] = new InputConnector("1", Component.Position.left);
		inputs[1] = new InputConnector("2", Component.Position.top);
		inputs[2] = new InputConnector("3", Component.Position.bottom);
		output = new OutputConnector("y", Component.Position.right);

		register(inputs[0]);
		register(inputs[1]);
		register(inputs[2]);
		register(output);
	}

	@Override
	public void execute(double t) {
		double[] v = new double[output.getDim()];
		Arrays.fill(v, 1);
		Matrix y = new ColumnMatrix(v);
		for (InputConnector ic : inputs) {
			if (ic != null && ic.isConnected() && ic.getSource() != null) {
				y = y.arrayTimes(ic.getInput());
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
		for (InputConnector ic : inputs) {
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
	// NameEditor editor = new NameEditor("Name of the Multiplyer", "Name",
	// this.getName());
	// editor.setLocation(point);
	// editor.showDialog("i1 dim:" + inputs[0].getDim() + ", i2 dim:"
	// + inputs[1].getDim() + ", i3 dim:" + inputs[2].getDim()
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
		g.drawLine(x + width / 2 - sz / 2, y + height / 2 - sz / 2, x + width
				/ 2 + sz / 2, y + height / 2 + sz / 2);
		g.drawLine(x + width / 2 + sz / 2, y + height / 2 - sz / 2, x + width
				/ 2 - sz / 2, y + height / 2 + sz / 2);
		g.setStroke(s);
	}

	@Override
	public void recreate() {
		// TODO Auto-generated method stub
	}
}
