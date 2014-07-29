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

@SuppressWarnings("serial")
public class Multiplexer extends Component {
	private InputConnector input1, input2, input3;
	private OutputConnector outputY;

	public Multiplexer() {
		super(50, 50, "   ");
		input1 = new InputConnector("1", Component.Position.left);
		input2 = new InputConnector("2", Component.Position.top);
		input3 = new InputConnector("3", Component.Position.bottom);
		outputY = new OutputConnector("y", Component.Position.right);

		register(input1);
		register(input2);
		register(input3);
		register(outputY);
	}

	@Override
	protected void drawComponentBody(Graphics2D g) {
		g.drawRect(x, y, width, height);
		Stroke save = g.getStroke();
		g.setStroke(new BasicStroke(.3f));
		int[] xx = { x + 2, x + width / 4, x + width - width / 4,
				x + width - 2, x + width - 2, x + width - width / 4,
				x + width / 4, x + 2 };
		int[] yy = { y + height / 4, y + 2, y + 2, y + height / 4,
				y + height - height / 4, y + height - 2, y + height - 2,
				y + height - height / 4 };
		g.drawPolygon(xx, yy, xx.length);
		g.setStroke(new BasicStroke(1f));
		int sz = width / 6;
		int x0 = x + width / 2;
		int y0 = y + height / 2;
		int hf = sz * 5 / 12;
		g.drawPolyline(new int[] { x0 - sz, x0, x0, x0 + sz }, new int[] {
				y0 - sz, y0 - sz, y0 - hf, y0 - hf }, 4);
		g.drawPolyline(new int[] { x0 - sz, x0 + sz }, new int[] { y0, y0 }, 2);
		g.drawPolyline(new int[] { x0 - sz, x0, x0, x0 + sz }, new int[] {
				y0 + sz, y0 + sz, y0 + hf, y0 + hf }, 4);
		// g.drawLine(x0 - sz, y0 - sz, x0, y0 - sz);
		// g.drawLine(x0, y0 - sz, x0, y2);
		g.setStroke(save);
	}

	@Override
	public void execute(double t) {
		Matrix u1 = input1.getInput();
		Matrix u2 = input2.getInput();
		Matrix u3 = input3.getInput();

		double[] y = new double[input1.getDim() + input2.getDim()
				+ input3.getDim()];
		if (u1 != null)
			for (int i = 0; i < input1.getDim(); i++)
				y[i] = u1.get(i, 0);
		if (u2 != null)
			for (int i = 0; i < input2.getDim(); i++)
				y[input1.getDim() + i] = u2.get(i, 0);
		if (u3 != null)
			for (int i = 0; i < input3.getDim(); i++)
				y[input1.getDim() + input2.getDim() + i] = u3.get(i, 0);
		try {
			outputY.setOutput(new ColumnMatrix(y));
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
	}

	@Override
	public void setDim(int dim) throws ConnectException {
		outputY.setDim(input1.getDim() + input2.getDim() + input3.getDim());
	}
}
