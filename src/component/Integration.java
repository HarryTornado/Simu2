package component;

import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JOptionPane;

import model.ConnectException;
import model.OrderException;
import Jama.ColumnMatrix;
import Jama.Matrix;
import connector.InputConnector;
import connector.OutputConnector;

@SuppressWarnings("serial")
public class Integration extends Component implements Editable {
	private InputConnector inputU;
	private OutputConnector outputY;
	private int dim;
	transient private Matrix sum;
	private double dt;
	transient private Matrix last;

	public Integration() {
		super(60, 40, "int");
		inputU = new InputConnector("u", Component.Position.left);
		outputY = new OutputConnector("y", Component.Position.right);

		register(inputU);
		register(outputY);
	}

	@Override
	protected void drawComponentBody(Graphics2D g) {
		int rd = Math.max(width, height) / 4;
		g.drawRoundRect(x, y, width, height, rd, rd);
	}

	public void setDt(double dt) {
		this.dt = dt;
	}

	@Override
	public void execute(double t) {
		if (sum == null)
			reset();
		Matrix u = inputU.getInput();

		if (u == null)
			return;

		if (last != null) {
			Matrix dSum = last.plus(u);
			sum = sum.plus(dSum.times(dt / 2));
		}

		last = u;
		try {
			outputY.setOutput(sum);
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void recreate() {
	}

	@Override
	public void reset() {
		sum = new ColumnMatrix(dim);
		last = null;
	}

	@Override
	public void setDim(int dim) throws ConnectException {
		this.dim = dim;
		outputY.setDim(dim);
		reset();
	}

	@Override
	public void showDialog(Point point) {
		JOptionPane.showMessageDialog(null, "Integration value: " + sum);
	}
}
