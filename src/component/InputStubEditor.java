package component;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.effortech.harry.swing.IntSpinner;

@SuppressWarnings("serial")
public class InputStubEditor extends JDialog implements ActionListener {
	private JTextField nameField = new JTextField(10);
	private IntSpinner is = new IntSpinner(1, 1, 100, 1);
	private boolean ok;
	private JButton okBtn = new JButton("ok"),
			cancelBtn = new JButton("cancel");

	private void changeTitle(String t) {
		setTitle("InputStub Editor: " + t);
	}

	public InputStubEditor(InputStub inputStub) {
		this.setModal(true);

		changeTitle(inputStub.getName());
		nameField.setText(inputStub.getName());
		is.setCrt(inputStub.getDim());

		Container window = this.getContentPane();
		window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));

		window.add(makeNamePanel());

		window.add(makeDimPanel());

		window.add(makeCtlPanel());

		pack();
	}

	private JPanel makeCtlPanel() {
		JPanel cp = new JPanel();
		cp.add(okBtn);
		cp.add(cancelBtn);

		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);

		return cp;
	}

	private JPanel makeDimPanel() {
		JPanel dp = new JPanel();
		dp.add(new JLabel("Dimension: "));
		dp.add(is);
		return dp;
	}

	private JPanel makeNamePanel() {
		JPanel np = new JPanel();
		np.add(new JLabel("name: "));
		np.add(nameField);

		nameField.addActionListener(this);
		return np;
	}

	public void showDialog() {
		ok = false;
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == nameField)
			changeTitle(nameField.getText());
		else if (e.getSource() == okBtn) {
			ok = true;
			setVisible(false);
		} else if (e.getSource() == cancelBtn) {
			ok = false;
			setVisible(false);
		}
	}

	public boolean isOk() {
		return ok;
	}

	public String getName() {
		return nameField.getText();
	}

	public int getDim() {
		return is.getCrt();
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
