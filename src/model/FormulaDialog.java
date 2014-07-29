package model;

import help.Help;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.effortech.harry.swing.BlankPanel;

import component.param.Parameter;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class FormulaDialog extends JDialog implements ActionListener {
	private JTextField[] flds;
	/**
	 * @uml.property name="formula"
	 */
	private String[] formula;
	/**
	 * @uml.property name="ok"
	 */
	private boolean ok = false;
	private JButton okBtn;
	private JButton cancelBtn;

	private JPanel getParamPanel(List<Parameter> params,
			List<Parameter> sysParams) {
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Available parameters"));
		panel.setLayout(new BorderLayout());
		JTextArea area = new JTextArea();
		area.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEditable(false);
		area.setFocusable(false);
		StringBuffer sb = new StringBuffer();
		sb.append("System wide parameter:");
		for (Parameter p : sysParams) {
			sb.append("\n\t" + p);
		}
		sb.append("\nlocal parameters:");
		for (Parameter p : params) {
			sb.append("\n\t" + p);
		}
		area.setText(sb.toString());
		JScrollPane jsp = new JScrollPane(area);
		panel.add(jsp);
		panel.add(new BlankPanel(20, true), BorderLayout.WEST);
		panel.setPreferredSize(new Dimension(300, 120));
		panel.setMinimumSize(getPreferredSize());
		return panel;
	}

	public FormulaDialog(String title, String prefix, String[] formula,
			List<Parameter> params, List<Parameter> sysParams) {
		this.setTitle(title + " dialog");
		this.setModal(true);
		this.setModal(true);

		addHelpMenu();

		Container window = this.getContentPane();
		window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));

		window.add(getParamPanel(params, sysParams));

		flds = new JTextField[formula.length];

		for (int i = 0; i < flds.length; i++) {
			JPanel p = new JPanel();
			flds[i] = new JTextField(40);
			flds[i].setFont(new Font(Font.DIALOG, Font.BOLD, 16));
			if (formula != null)
				flds[i].setText(formula[i]);
			p.add(new JLabel(prefix + (i + 1)));
			p.add(new JLabel("="));
			p.add(flds[i]);

			window.add(p);
		}

		window.add(getCtlPanel());

		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		pack();
		this.setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okBtn) {
			ok = true;
			formula = new String[flds.length];
			for (int i = 0; i < flds.length; i++)
				formula[i] = flds[i].getText();
		}
		setVisible(false);
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

		okBtn = new JButton("ok");
		cancelBtn = new JButton("cancel");

		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);

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
	 * @uml.property name="ok"
	 */
	public boolean isOk() {
		return ok;
	}
}
