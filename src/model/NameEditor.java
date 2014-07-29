package model;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.effortech.harry.swing.BlankPanel;

/**
 * @author   jyk
 */
@SuppressWarnings("serial")
public class NameEditor extends JDialog implements ActionListener {
	private JButton cancelBtn = new JButton("cancel");
	/**
	 * @uml.property  name="name"
	 */
	private String name;

	private JTextField nameField = new JTextField(30);
	private JTextField infoField = new JTextField(20);

	/**
	 * @uml.property  name="ok"
	 */
	private boolean ok;
	private JButton okBtn = new JButton("ok");

	public NameEditor(String title, String message, String name) {
		this.setTitle(title);
		this.setModal(true);

		JPanel window = new JPanel();
		window.setLayout(new BorderLayout());

		window.add(new BlankPanel(5, true), BorderLayout.NORTH);
		window.add(new BlankPanel(2, true), BorderLayout.EAST);
		window.add(new BlankPanel(5, true), BorderLayout.SOUTH);
		window.add(new BlankPanel(2, true), BorderLayout.WEST);

		JPanel p = new JPanel();
		p.add(new JLabel(message + ":"));
		p.add(nameField);
		
		this.name = name;

		window.add(p);

		JPanel ip = new JPanel();
		ip.add(new JLabel("information:"));
		ip.add(infoField);
		infoField.setEditable(false);

		add(ip, BorderLayout.NORTH);

		add(window);

		add(getCtlPanel(), BorderLayout.SOUTH);

		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		this.setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okBtn) {
			name = nameField.getText();
			ok = true;
		} else {
			ok = false;
		}
		this.setVisible(false);
	}

	private JPanel getCtlPanel() {
		JPanel p = new JPanel();

		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);

		p.add(okBtn);
		p.add(new BlankPanel(40, true));
		p.add(cancelBtn);

		return p;
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 * @uml.property  name="ok"
	 */
	public boolean isOk() {
		return ok;
	}

	public void showDialog(String info) {
		ok = false;
		nameField.setText(name);
		infoField.setText(info);
		setVisible(true);
	}
}
