package component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.FormulaDialog;
import net.effortech.harry.swing.IntSpinner;

import component.param.Parameter;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class FormulaTools extends JPanel implements ChangeListener,
		ActionListener {
	private JTextArea area = new JTextArea(10, 30);
	/**
	 * @uml.property name="formula"
	 */
	private String[] formula;
	private String prefix;
	private IntSpinner numSpinner;
	private JTextField numField;
	private String name;
	private JButton editBtn = new JButton("edit");
	private List<Parameter> params;
	private List<Parameter> sysParams;

	public FormulaTools(String name, String prefix, String[] formula,
			boolean numChangeable, List<Parameter> params,
			List<Parameter> sysParams) {
		setLayout(new BorderLayout());
		setBorder(new TitledBorder(name));
		this.params = params;
		this.sysParams = sysParams;
		if (formula == null) {
			formula = new String[] { "0" };
		}
		if (numChangeable) {
			numSpinner = new IntSpinner(this, formula.length, 1, 10, 1);
		} else {
			numField = new JTextField("" + formula.length);
			numField.setEditable(false);
		}
		area.setEditable(false);
		area.addMouseListener(ml);
		editBtn.addActionListener(this);

		JPanel cp = new JPanel();
		cp.add(new JLabel("Number of " + name + ": "));
		if (numChangeable)
			cp.add(numSpinner);
		else
			cp.add(numField);
		cp.add(editBtn);

		add(new JScrollPane(area));
		add(cp, BorderLayout.NORTH);

		this.formula = formula;
		this.prefix = prefix;
		this.name = name;
		display();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(300, 300);
	}

	private MouseListener ml = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			edit();
			display();
		}
	};

	private void display() {
		StringBuffer sb = new StringBuffer();
		int i = 1;
		if (formula != null)
			for (String s : formula) {
				sb.append(prefix + i + " = " + s + "\n\n");
				i++;
			}
		area.setText(sb.toString());
	}

	private void edit() {
		FormulaDialog fd = new FormulaDialog(name + " formula", prefix,
				formula, params, sysParams);
		if (fd.isOk()) {
			formula = fd.getFormula();
		}
	}

	public void update() {
		display();
	}

	/**
	 * @return
	 * @uml.property name="formula"
	 */
	public String[] getFormula() {
		return formula;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		IntSpinner sp = (IntSpinner) e.getSource();

		int dim = sp.getInt();
		if (formula == null || dim != formula.length) {
			String[] newFormula = new String[dim];
			int md = Math.min(formula == null ? 1 : formula.length,
					newFormula.length);
			for (int i = 0; i < md; i++)
				newFormula[i] = formula[i];
			for (int i = md; i < newFormula.length; i++)
				newFormula[i] = "0";
			formula = newFormula;
		}
		display();
	}

	/**
	 * @param formula
	 * @uml.property name="formula"
	 */
	public void setFormula(String[] formula) {
		int dim = 1;
		if (formula != null)
			dim = formula.length;
		else
			formula = new String[] { "0" };
		if (numSpinner != null)
			numSpinner.setCrt(dim);
		if (numField != null)
			numField.setText("" + dim);

		this.formula = formula;

		display();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		edit();
		display();
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
	}
}
