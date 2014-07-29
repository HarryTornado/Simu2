package component.param;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ParameterEditor extends JDialog implements ActionListener {
	private JButton cancelBtn = new JButton("cancel");
	private JTextField formulaField = new JTextField(30);

	private JTextField nameField = new JTextField(30);

	private boolean ok;

	private JButton okBtn = new JButton("ok");

	public ParameterEditor() {
		this(null);
	}

	public ParameterEditor(Parameter p) {
		this.setTitle("Parameter Editor");
		this.setModal(true);

		if (p != null) {
			nameField.setText(p.getName());
			nameField.setEditable(false);
			formulaField.setText(p.getFormula());
		}
		Container window = this.getContentPane();
		window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));

		window.add(makeNamePanel());
		window.add(makeFormulaPanel());
		window.add(makeCtlPanel());

		pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okBtn) {
			ok = true;
		} else {
			ok = false;
		}
		setVisible(false);
	}

	public String getName() {
		return nameField.getText();
	}

	public String getFormula() {
		return formulaField.getText();
	}

	private JPanel makeCtlPanel() {
		JPanel panel = new JPanel();

		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);

		panel.add(okBtn);
		panel.add(cancelBtn);

		return panel;
	}

	private JPanel makeFormulaPanel() {
		JPanel fp = new JPanel();
		fp.setLayout(new FlowLayout(FlowLayout.RIGHT));
		fp.add(new JLabel("formula: "));
		fp.add(formulaField);
		return fp;
	}

	private JPanel makeNamePanel() {
		JPanel np = new JPanel();
		np.setLayout(new FlowLayout(FlowLayout.RIGHT));
		np.add(new JLabel("name: "));
		np.add(nameField);
		return np;
	}

	public boolean isOk() {
		return ok;
	}
}
