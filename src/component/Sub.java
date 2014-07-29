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
public class Sub extends Component {
	private InputConnector mi;

	private OutputConnector output;

	private InputConnector pi;

	public Sub() {
		super(50, 50, "   ");
		pi = new InputConnector("p", Component.Position.left);
		mi = new InputConnector("m", Component.Position.bottom);
		output = new OutputConnector("y", Component.Position.right);

		register(pi);
		register(mi);
		register(output);
	}

	@Override
	protected void drawComponentBody(Graphics2D g) {
		g.drawOval(x, y, width, height);
		int sz = Math.min(width, height) * 1 / 3;
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(1f));

		g.drawLine(x + width / 2 - sz / 2, y + height / 2, x + width / 2 + sz
				/ 2, y + height / 2);
		// g.drawLine(x + width / 2, y + height / 2 - sz / 2, x + width / 2, y
		// + height / 2 + sz / 2);
		g.setStroke(s);
	}

	@Override
	public void execute(double t) {
		Matrix y = new ColumnMatrix(output.getDim());
		if (pi.isConnected()) {
			if (mi.isConnected()) {
				y = mi.getInput().times(-1);
				y = y.plus(pi.getInput());
			} else
				y = pi.getInput();
		} else {
			if (mi.isConnected())
				y = mi.getInput().times(-1);
		}
		try {
			output.setOutput(y);
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
			e.printStackTrace();
		}
	}

	public void setDim(int dim) throws ConnectException {
		int maxDim = dim;
		for (InputConnector ic : new InputConnector[] { pi, mi }) {
			if (ic.isConnected()) {
				if (dim != 0 && ic.getSource().getDim() != dim)
					throw new ConnectException(ic, dim);
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
	// NameEditor editor = new NameEditor("Name of the Subtract", "Name", this
	// .getName());
	// editor.showDialog("plus dim:" + pi.getDim() + ", minus dim:"
	// + mi.getDim() + ", output dim:" + output.getDim());
	// if (editor.isOk()) {
	// setName(editor.getName());
	// }
	// }

	@Override
	public void recreate() {
		// TODO Auto-generated method stub

	}
}
