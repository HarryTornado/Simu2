package component;

import help.Help;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.effortech.harry.swing.BlankPanel;

import component.param.Parameter;
import component.param.ParameterManager;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class BlockEditor extends JDialog implements ActionListener {
	private JButton cancelBtn = new JButton("cancel");
	/**
	 * @uml.property name="formula"
	 */
	private String[] formula;
	private JTextField iNumField;
	/**
	 * @uml.property name="ok"
	 */
	private boolean ok;
	private JButton okBtn = new JButton("ok");
	private JButton paramBtn = new JButton("params");
	private List<Parameter> params = new ArrayList<Parameter>();

	/**
	 * @uml.property name="title"
	 */
	private String title;

	private JTextField titleField = new JTextField(6);

	private FormulaTools tools;

	public BlockEditor(String title, String prefix, String[] formula,
			String is, Component parent) {
		this.setTitle(title);
		this.title = title;
		this.setModal(true);

		addHelpMenu();

		if (is != null) {
			iNumField = new JTextField(is);
			iNumField.setEditable(false);
		}

		if (formula == null)
			formula = new String[] { "0" };
		this.formula = formula;
		tools = new FormulaTools(title + " equations", prefix, formula, true,
				params, parent.getPanel().getParams());

		add(tools);
		add(getCtlPanel(), BorderLayout.NORTH);

		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		this.setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == paramBtn) {
			ParameterManager pm = new ParameterManager(params);
			pm.setVisible(true);
			params = pm.getParams();
		} else if (e.getSource() == cancelBtn) {
			tools.setFormula(formula);
			ok = false;
			setVisible(false);
		} else if (e.getSource() == okBtn) {
			formula = tools.getFormula();
			title = titleField.getText();
			BlockEditor.this.setTitle(title);

			ok = true;
			setVisible(false);
		} else {
			title = titleField.getText();
			BlockEditor.this.setTitle(title);
		}
	}

	private void addHelpMenu() {
		JMenuBar jb = this.getJMenuBar();
		if (jb == null)
			jb = new JMenuBar();

		jb.add(Help.makeHelpMenu());

		this.setJMenuBar(jb);
	}

	private JPanel getCtlPanel() {
		titleField.addActionListener(this);
		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		paramBtn.addActionListener(this);

		JPanel panel = new JPanel();

		panel.add(new JLabel("Title: "));
		panel.add(titleField);
		panel.add(new BlankPanel(10, true));

		if (iNumField != null) {
			panel.add(new JLabel("available inputs:"));
			iNumField.setEditable(false);
			panel.add(iNumField);
			panel.add(new BlankPanel(10, true));
		}

		panel.add(paramBtn);
		panel.add(new BlankPanel(10, true));
		panel.add(okBtn);
		panel.add(cancelBtn);

		return panel;
	}

	/**
	 * @return
	 * @uml.property name="formula"
	 */
	public String[] getFormula() {
		return formula;
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
	 * @uml.property name="ok"
	 */
	public boolean isOk() {
		return ok;
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
		tools.setParams(params);
	}

	public void showDialog() {
		ok = false;
		titleField.setText(title);
		tools.setFormula(formula);
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
