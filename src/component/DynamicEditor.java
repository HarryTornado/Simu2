package component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.MatrixDialog;
import net.effortech.harry.swing.BlankPanel;
import Jama.ColumnMatrix;
import Jama.Matrix;

import component.param.Parameter;
import component.param.ParameterManager;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class DynamicEditor extends JDialog implements ActionListener {
	private JButton cancelBtn = new JButton("cancel");
	private FormulaTools fodTools;
	private int inputDim;
	/**
	 * @uml.property name="ok"
	 */
	private boolean ok;
	private JButton okBtn = new JButton("ok");
	private FormulaTools outTools;
	private JButton paramBtn = new JButton("params");
	private List<Parameter> params = new ArrayList<Parameter>();
	private JButton setInitBtn = new JButton("initial state");
	private JButton tfBtn = new JButton("tf");
	private Component parent;
	/**
	 * @uml.property name="title"
	 */
	private String title;
	private JTextField titleField = new JTextField(10);
	private JTextField varField = new JTextField("0us", 3);
	/**
	 * @uml.property name="x0"
	 */
	private Matrix x0;
	/**
	 * @uml.property name="xFormula"
	 */
	private String[] xFormula;

	/**
	 * @uml.property name="yFormula"
	 */
	private String[] yFormula;

	public DynamicEditor(DynaSys dyn) {
		this.setModal(true);

		this.parent = dyn;

		xFormula = dyn.getXFormula();
		yFormula = dyn.getYFormula();
		title = dyn.getName();
		x0 = dyn.getX0();
		inputDim = dyn.getInputU().getDim();

		this.setTitle("Editing [" + title + "]");
		fodTools = new FormulaTools("State Equations", "f", xFormula, true,
				params, dyn.getPanel().getParams());
		outTools = new FormulaTools("Output Equations", "y", yFormula, true,
				params, dyn.getPanel().getParams());

		addHelpMenu();

		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel("Name:"));
		topPanel.add(titleField);
		topPanel.add(new BlankPanel(10, true));
		topPanel.add(new JLabel("available inputs::"));
		topPanel.add(varField);
		varField.setEditable(false);
		topPanel.add(new BlankPanel(10, true));
		topPanel.add(setInitBtn);
		topPanel.add(new BlankPanel(10, true));
		topPanel.add(tfBtn);
		topPanel.add(new BlankPanel(10, true));
		topPanel.add(paramBtn);
		topPanel.add(new BlankPanel(10, true));
		topPanel.add(okBtn);
		topPanel.add(cancelBtn);

		paramBtn.addActionListener(this);
		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		tfBtn.addActionListener(this);

		setInitBtn.addActionListener(this);
		titleField.addActionListener(this);

		JPanel window = new JPanel();
		window.setLayout(new BoxLayout(window, BoxLayout.X_AXIS));

		window.add(fodTools);
		window.add(outTools);

		add(topPanel, BorderLayout.NORTH);
		add(window);

		pack();
		this.setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfBtn) {
			editTf();
		} else if (e.getSource() == cancelBtn) {
			ok = false;
			setVisible(false);
		} else if (e.getSource() == paramBtn) {
			ParameterManager pm = new ParameterManager(params);
			pm.setVisible(true);
			params = pm.getParams();
		} else if (e.getSource() == okBtn) {
			xFormula = fodTools.getFormula();
			yFormula = outTools.getFormula();
			title = titleField.getText();
			ok = true;
			setVisible(false);
		} else if (e.getSource() == titleField) {
			title = titleField.getText();
			setTitle("Editing " + title);
		} else if (e.getSource() == setInitBtn) {
			if (x0 == null
					|| x0.getRowDimension() != fodTools.getFormula().length)
				x0 = new ColumnMatrix(fodTools.getFormula().length);
			MatrixDialog sd = new MatrixDialog(x0, "Initial State", "x");
			if (sd.isOk()) {
				x0 = sd.getDataMatrix();
			}
		}
	}

	private void addHelpMenu() {
		JMenuBar jb = this.getJMenuBar();
		if (jb == null)
			jb = new JMenuBar();

		JMenu menu = new JMenu("help");
		menu.add(ElementLists.dynamic_system.getHelpItem());

		jb.add(menu);
		this.setJMenuBar(jb);
	}

	private void editTf() {
		int dim = fodTools.getFormula().length;
		int oNum = outTools.getFormula().length;
		TfDialog tf = new TfDialog(title, dim, oNum, params, parent.getPanel()
				.getParams());
		if (tf.isOk()) {
			String[] xf = makeSysEqn(dim, tf);
			fodTools.setFormula(xf);
			String[] of = makeOutputEqn(dim, oNum, tf);
			outTools.setFormula(of);
		}
	}

	/**
	 * @return
	 * @uml.property name="fodTools"
	 */
	public FormulaTools getFodTools() {
		return fodTools;
	}

	/**
	 * @return
	 * @uml.property name="outTools"
	 */
	public FormulaTools getOutTools() {
		return outTools;
	}

	public List<Parameter> getParams() {
		return params;
	}

	/**
	 * @return
	 * @uml.property name="title"
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return
	 * @uml.property name="x0"
	 */
	public Matrix getX0() {
		return x0;
	}

	/**
	 * @return
	 * @uml.property name="xFormula"
	 */
	public String[] getXFormula() {
		return xFormula;
	}

	/**
	 * @return
	 * @uml.property name="yFormula"
	 */
	public String[] getYFormula() {
		return yFormula;
	}

	/**
	 * @return
	 * @uml.property name="ok"
	 */
	public boolean isOk() {
		return ok;
	}

	private String[] makeOutputEqn(int dim, int oNum, TfDialog tf) {
		String[] of = new String[oNum];
		for (int k = 0; k < oNum; k++) {
			String bn = tf.getB(k, dim);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < dim; i++) {
				sb.append("(" + tf.getB(k, i) + "-(" + bn + ")*(" + tf.getA(i)
						+ "))*x" + (i + 1));
				sb.append("+");
			}
			sb.append("(" + bn + ")*u" + (k + 1));
			of[k] = sb.toString();
		}
		return of;
	}

	private String[] makeSysEqn(int dim, TfDialog tf) {
		String[] xf = new String[dim];
		for (int i = 0; i < dim - 1; i++)
			xf[i] = "x" + (i + 2);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < dim; i++) {
			sb.append("-(" + tf.getA(i) + ")*x" + (i + 1));
			if (i == dim - 1)
				sb.append("+");
		}
		sb.append("u1");
		xf[dim - 1] = sb.toString();
		return xf;
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
		fodTools.setParams(params);
		outTools.setParams(params);
	}

	public void showDialog() {
		setTitle("Editing " + title);
		titleField.setText(title);
		varField.setText(inputDim + "us");
		super.setVisible(true);
	}

	@Override
	public void setLocation(Point p) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dm = tk.getScreenSize();
		if (p.x + this.getWidth() > dm.width)
			p.x = dm.width - this.getWidth();
		if (p.y + this.getHeight() > dm.height)
			p.y = dm.height - this.getHeight();
		super.setLocation(p);
	}
}
