package component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import model.NameEditor;
import net.effortech.harry.graph.Figure;
import net.effortech.harry.graph.FigurePanel;
import Jama.Matrix;
import connector.InputConnector;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class StateSpaceTrajectory extends Component implements Editable {
	class StateTrajectory {
		List<Double> x = new ArrayList<Double>();
	}

	private InputConnector input;

	private double maxv = 0;

	transient private JDialog result;

	/**
	 * @uml.property name="xs"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	transient private StateTrajectory[] xs;

	public StateSpaceTrajectory() {
		super(70, 50, "traj");

		input = new InputConnector("u", Component.Position.bottom);

		register(input);
		reset();
	}

	@Override
	protected void drawComponentBody(Graphics2D g) {
		int w = 0, h = 0;
		g.drawRect(x, y, width, height);
		w = width / 10;
		h = height / 10;
		Stroke save = g.getStroke();
		g.setStroke(new BasicStroke(.3f));
		g.drawRect(x + 2, y + 2, width - 4, height - 4);
		g.drawOval(x - 3 + (int) w, y + (int) h, (int) (width - 2 * w),
				(int) (height - 3.6 * h));
		g.drawOval(x + (int) w, y + (int) h, (int) (width - 2 * w),
				(int) (height - 3.6 * h));
		g.drawOval(x + 3 + (int) w, y + (int) h, (int) (width - 2 * w),
				(int) (height - 3.6 * h));
		g.setStroke(save);
	}

	@Override
	protected void drawComponentName(Graphics2D g) {
		String name = getName();
		if (name != null) {
			g.setColor(Color.blue);
			int fontSize = (int) ((width) / name.length());
			if (fontSize > 16)
				fontSize = 16;
			g.setFont(new Font(Font.DIALOG, Font.PLAIN, fontSize));
			float px, py;
			FontMetrics fm = g.getFontMetrics();
			int h = height / 10;
			px = (int) (width - fm.stringWidth(name)) / 2;
			py = h + (int) (height - 3.6 * h - fm.getHeight()) / 2;

			int xx = (int) (x);
			int yy = (int) (y);
			g.drawString(name, xx + px, yy + py + fm.getAscent());
		}
	}

	@Override
	public void execute(double t) {
		if (input.isConnected()) {
			Matrix u = input.getInput();
			for (int i = 0; i < input.getDim(); i++) {
				xs[i].x.add(u.get(i, 0));
				double v = Math.abs(u.get(i, 0));
				if (maxv < v)
					maxv = v;
			}
		}
	}

	private JTabbedPane getFP() {
		JTabbedPane tabbedPhases = new JTabbedPane();
		for (int i = 0; i < xs.length; i++) {
			for (int j = i + 1; j < xs.length; j++) {
				String name = "x" + (j + 1) + "-x" + (i + 1);
				FigurePanel phase = new FigurePanel(getName() + " " + name, "x"
						+ (j + 1), "x" + (i + 1));
				phase.setMark(true);
				if (xs[j].x.size() > 0) {
					Figure f = new Figure(xs[j].x, xs[i].x);
					f.setLineWidth(0.2f);
					phase.add(f);
				}
				tabbedPhases.add(phase, name);
			}
		}
		return tabbedPhases;
	}

	@Override
	public void recreate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		if (input != null) {
			if (input.getSource() != null)
				this.setName("trajectory of "
						+ input.getSource().getParent().getName());

			xs = new StateTrajectory[input.getDim()];
			for (int i = 0; i < xs.length; i++)
				xs[i] = new StateTrajectory();
		}
		result = null;
		maxv = 0;
	}

	@Override
	public void setDim(int dim) {
		reset();
	}

	@Override
	public void showDialog(Point point) {
		NameEditor editor = new NameEditor("Name of the PhasePlane", "Name",
				this.getName());
		editor.showDialog("input dim: " + input.getDim() + ", input max norm: "
				+ maxv);
		if (editor.isOk()) {
			setName(editor.getName());
		}
	}

	public void showResult() {
		if (result == null) {
			result = new JDialog();
			result.setTitle("State Space Trajectory");
			result.setModal(false);
			result.add(getFP());
			result.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			result.setSize(800, 600);
			result.setLocationRelativeTo(null);
		}
		result.setVisible(true);
	}
}
