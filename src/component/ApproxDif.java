package component;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;

import model.ConnectException;
import model.OrderException;
import net.effortech.harry.swing.LVP;
import net.effortech.harry.swing.SetupDialog;
import Jama.ColumnMatrix;
import Jama.Matrix;
import connector.InputConnector;
import connector.OutputConnector;

@SuppressWarnings("serial")
public class ApproxDif extends Component implements Editable {
	private double alpha = 10;
	private int order = 1;
	transient private DynaSys[] dynaSys = new DynaSys[order];
	private InputConnector inputU;
	private OutputConnector outputY;
	private int dim;

	public ApproxDif() {
		super(80, 40, "dif(1)");
		inputU = new InputConnector("u", Component.Position.left);
		outputY = new OutputConnector("y", Component.Position.right);
		register(inputU);
		register(outputY);
	}

	@Override
	public void showDialog(Point p) {
		LVP<Double> alphaLVP = new LVP<Double>("the value of alpha: ", alpha,
				2.0, 1000000.0);
		LVP<Integer> orderLVP = new LVP<Integer>("order: ", order, 1, 10);
		SetupDialog sd = new SetupDialog("ApproxDiff Editor", new LVP[] {
				alphaLVP, orderLVP });

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dm = tk.getScreenSize();

		if (p.x + sd.getWidth() > dm.width)
			p.x = dm.width - sd.getWidth();
		if (p.y + sd.getHeight() > dm.height)
			p.y = dm.height - sd.getHeight();

		sd.setLocation(p);
		sd.setVisible(true);
		setAlphaAndOrder(alphaLVP.getValue(), orderLVP.getValue());
	}

	@Override
	protected void drawComponentBody(Graphics2D g) {
		int rd = Math.max(width, height) / 4;
		g.drawRoundRect(x, y, width, height, rd, rd);
	}

	public void setDt(double dt) {
		for (DynaSys ds : dynaSys)
			if (ds != null)
				ds.setDt(dt);
	}

	@Override
	public void execute(double t) {
		if (dynaSys != null) {
			Matrix u = new ColumnMatrix(inputU.getDim());
			if (inputU.isConnected())
				u = inputU.getInput();
			Matrix y = new ColumnMatrix(outputY.getDim());
			y = putTogether(u, 0, y);
			int index = 1;
			for (DynaSys ds : dynaSys) {
				if (ds != null) {
					ds.execute2(u, t);
					u = ds.getYMatrix();
					y = putTogether(u, index, y);
					index++;
				}
			}
			try {
				outputY.setOutput(y);
			} catch (OrderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Matrix putTogether(Matrix u, int index, Matrix y) {
		double[] ya = y.getColumnPackedCopy();
		double[] ua = u.getColumnPackedCopy();
		for (int i = 0; i < u.getRowDimension(); i++)
			ya[index * u.getRowDimension() + i] = ua[i];
		return new ColumnMatrix(ya);
	}

	@Override
	public void recreate() {
		makeDynaSys();
	}

	@Override
	public void reset() {
		for (DynaSys ds : dynaSys)
			if (ds != null)
				ds.reset();
	}

	private String[] getXFormula() {
		String[] fms = new String[dim];
		for (int i = 0; i < fms.length; i++) {
			fms[i] = "-" + alpha + " * x" + (i + 1) + " + u" + (i + 1);
		}
		return fms;
	}

	private String[] getYFormula() {
		String[] fms = new String[dim];
		for (int i = 0; i < fms.length; i++) {
			fms[i] = "- " + alpha * alpha + " * x" + (i + 1) + " + " + alpha
					+ " * u" + (i + 1);
		}
		return fms;
	}

	@Override
	public void setDim(int dim) throws ConnectException {
		this.dim = dim;
		makeDynaSys();
		outputY.setDim(dim * (order + 1));
	}

	private void makeDynaSys() {
		try {
			outputY.setDim(dim * (order + 1));
		} catch (ConnectException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		dynaSys = new DynaSys[order];
		for (int i = 0; i < dynaSys.length; i++) {
			DynaSys ds = new DynaSys();
			try {
				ds.setXFormula(getXFormula());
				ds.setYFormula(getYFormula());
				ds.reset();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dynaSys[i] = ds;
		}
	}

	public double getAlpha() {
		return alpha;
	}

	private void setAlphaAndOrder(double alpha, int order) {
		this.alpha = alpha;
		this.order = order;
		setName("dif(" + order + ")");
		makeDynaSys();
	}
}
