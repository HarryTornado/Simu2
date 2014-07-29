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
public class FuncOneEditor extends JDialog implements ActionListener {
	/**
	 * @uml.property name="ok"
	 */
	private boolean ok = false;
	private List<Parameter> params = new ArrayList<Parameter>();
	private JButton paramBtn = new JButton("params");
	private JButton okBtn = new JButton("ok");
	private JButton cancelBtn = new JButton("cancel");
	/**
	 * @uml.property name="title"
	 */
	private String title;
	/**
	 * @uml.property name="formula"
	 */
	private String[] formula;

	private JTextField titleField = new JTextField(6);

	private FormulaTools tools;

	public FuncOneEditor(String title, String prefix, String[] formula,
			FuncOne fOne) {
		this.setTitle(title);
		this.title = title;
		this.setModal(true);

		addHelpMenu();

		this.formula = formula;
		tools = new FormulaTools(title + " equations", prefix, formula, false,
				params, fOne.getPanel().getParams());

		add(tools);
		add(getCtlPanel(), BorderLayout.NORTH);

		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		this.setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelBtn) {
			tools.setFormula(formula);
			ok = false;
			setVisible(false);
		} else if (e.getSource() == paramBtn) {
			ParameterManager pm = new ParameterManager(params);
			pm.setVisible(true);
			params = pm.getParams();
		} else if (e.getSource() == okBtn) {
			title = titleField.getText();
			FuncOneEditor.this.setTitle(title);
			formula = tools.getFormula();
			ok = true;
			setVisible(false);
		} else {
			title = titleField.getText();
			FuncOneEditor.this.setTitle(title);
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
		JPanel panel = new JPanel();

		panel.add(new JLabel("Title: "));
		panel.add(titleField);
		titleField.addActionListener(this);

		panel.add(new BlankPanel(10, true));
		panel.add(paramBtn);
		panel.add(new BlankPanel(10, true));

		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		paramBtn.addActionListener(this);

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

	public void showDialog() {
		ok = false;
		titleField.setText(title);
		super.setVisible(true);
	}

	public List<Parameter> getParams() {
		return params;
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
		tools.setParams(params);
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
