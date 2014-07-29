package component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import model.ConnectException;
import model.NameEditor;
import model.OrderException;
import Jama.ColumnMatrix;
import Jama.Matrix;
import connector.InputConnector;
import connector.OutputConnector;

@SuppressWarnings("serial")
public abstract class Stub extends Component implements Editable {
	protected InputConnector input;
	protected OutputConnector output;

	public Stub(int width, int height, String name) {
		super(width, height, name);

		input = new InputConnector("i", Component.Position.left);
		output = new OutputConnector("y", Component.Position.right);

		register(input);
		register(output);
	}

	@Override
	protected void drawComponentName(Graphics2D g) {
		if (getName() != null) {
			g.setColor(Color.blue);
			int fontSize = (int) ((width) / getName().length());
			if (fontSize > 16)
				fontSize = 16;
			g.setFont(new Font(Font.DIALOG, Font.PLAIN, fontSize));
			float px, py;
			FontMetrics fm = g.getFontMetrics();
			px = (int) (width - fm.stringWidth(getName())) / 2;
			py = (int) (height - fm.getHeight()) / 2;

			int xx = (int) (x);
			int yy = (int) (y);
			g.drawString(getName(), xx + px, yy + py + fm.getAscent());
		}
	}

	@Override
	protected void drawComponentBody(Graphics2D g) {
		int[] xx = { x, x + width / 4, x + width - width / 4, x + width,
				x + width, x + width - width / 4, x + width / 4, x, };
		int[] yy = { y, y + height / 6, y + height / 6, y, y + height,
				y + height - height / 6, y + height - height / 6, y + height };
		g.drawPolygon(xx, yy, xx.length);
		Stroke sSave = g.getStroke();
		g.setStroke(new BasicStroke(.5f));
		g.drawRect(x, y, width, height);
		g.setStroke(sSave);
	}

	@Override
	public void execute(double t) {
		Matrix data = new ColumnMatrix(output.getDim());
		if (input.isConnected() && input.getSource() != null) {
			data = input.getInput();
		}
		try {
			output.setOutput(data);
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
		try {
			output.setOutput(new ColumnMatrix(output.getDim()));
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setDim(int dim) {
		try {
			output.setDim(dim);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void showDialog(Point point) {
		NameEditor editor = new NameEditor("Name of the Input Stub", "Name",
				this.getName());
		editor.setLocation(point);
		editor.showDialog("iStud dim:" + input.getDim() + ", output dim:"
				+ output.getDim());
		if (editor.isOk()) {
			setName(editor.getName());
		}
	}

	public InputConnector getInput() {
		return input;
	}

	public OutputConnector getOutput() {
		return output;
	}
}
