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
import java.util.Set;

import javax.swing.JOptionPane;

import model.ConnectException;
import model.OrderException;
import Jama.ColumnMatrix;

import component.param.Parameter;

import connector.OutputConnector;
import formula.FormulaInterpreter;
import formula.parser_source.ParseException;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class Source extends Component implements Editable {
	/**
	 * @uml.property name="fis"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	transient private FormulaInterpreter[] fis;
	private String[] formula;

	private OutputConnector output;

	private List<Parameter> params = new ArrayList<Parameter>();

	public Source() {
		super(60, 40, "src");
		output = new OutputConnector("y", Component.Position.right);
		try {
			output.setDim(1);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		register(output);
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
	protected void drawComponentBody(Graphics2D g) {
		int[] xx = { x, x + width / 4, x + width - width / 4, x + width,
				x + width, x + width - width / 4, x + width / 4, x, };
		int[] yy = { y, y + height / 6, y + height / 6, y,
				y + height - height / 6, y + height, y + height,
				y + height - height / 6 };
		g.drawPolygon(xx, yy, xx.length);
		Stroke sSave = g.getStroke();
		g.setStroke(new BasicStroke(.5f));
		g.drawRect(x, y, width, height);
		g.setStroke(sSave);
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
			py = (int) (height - fm.getHeight() - height / 8) / 2;

			int xx = (int) (x);
			int yy = (int) (y);
			g.drawString(getName(), xx + px, yy + height / 8 + py
					+ fm.getAscent());
		}
	}

	@Override
	public void execute(double t) {
		if (fis != null) {
			double[] v = new double[fis.length];
			for (int i = 0; i < fis.length; i++) {
				fis[i].setVariable("t", t);
				v[i] = fis[i].evaluate();
			}
			try {
				output.setOutput(new ColumnMatrix(v));
			} catch (OrderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void recreate() {
		try {
			if (formula == null)
				formula = new String[] { "0" };
			setFormula(formula);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	@Override
	public void setDim(int dim) {

	}

	/**
	 * @param formula
	 * @throws ConnectException
	 * @throws ParseException
	 * @uml.property name="formula"
	 */
	public void setFormula(String[] formula) throws ConnectException,
			ParseException {
		this.formula = formula;
		output.setDim(formula.length);
		if (formula != null) {
			fis = new FormulaInterpreter[formula.length];
			for (int i = 0; i < fis.length; i++) {
				fis[i] = new FormulaInterpreter();
				fis[i].compile(formula[i]);
			}
		}
	}

	@Override
	public void showDialog(Point point) {
		BlockEditor editor = new BlockEditor(getName(), "y", formula, null,
				this);
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

					throw new Exception("variable " + vName + " unknown!");
				}
			}
			return true;
		}
		return true;
	}
}
