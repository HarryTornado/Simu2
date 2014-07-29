package component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import connector.Connector;
import connector.InputConnector;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class EPConnectablesDialog extends JDialog implements ActionListener {
	private LinkedList<InputConnector> connectables = new LinkedList<InputConnector>();
	private JList cb;
	private JButton okBtn;
	private JButton cancelBtn;

	public EPConnectablesDialog(ElementPanel panel, Component ele) {
		this.setTitle("Input Connectors");
		this.setModal(true);

		for (Component e : panel.getElements()) {
			if (e == ele)
				continue;
			if (e instanceof InputStub)
				continue;
			for (Connector c : e.getConnectors()) {
				if (c instanceof InputConnector)
					if (!((InputConnector) c).isConnected())
						connectables.add((InputConnector) c);
			}
		}

		add(getCtlPanel(), BorderLayout.SOUTH);

		if (connectables.size() > 0) {
			cb = new JList(connectables.toArray(new Connector[connectables
					.size()]));
			cb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			cb.setSelectedIndex(0);
			add(new JScrollPane(cb));
		} else {
			okBtn.setEnabled(false);
			JPanel ip = new JPanel();
			ip.setLayout(new BorderLayout());
			ip.setPreferredSize(new Dimension(300, 200));
			ip.add(new JLabel("No avaliable connectors", JLabel.CENTER));
			add(ip);
		}

		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		pack();
		this.setLocationRelativeTo(null);
	}

	private JPanel getCtlPanel() {
		JPanel p = new JPanel();

		okBtn = new JButton("ok");
		okBtn.addActionListener(this);

		cancelBtn = new JButton("cancel");
		cancelBtn.addActionListener(this);

		p.add(okBtn);
		p.add(cancelBtn);
		return p;
	}

	private InputConnector selected;
	/**
	 * @uml.property name="ok"
	 */
	private boolean ok;

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == okBtn) {
			ok = true;
			selected = (InputConnector) cb.getSelectedValue();
			setVisible(false);
		} else if (src == cancelBtn) {
			ok = false;
			setVisible(false);
		}
	}

	/**
	 * @return
	 * @uml.property name="selected"
	 */
	public InputConnector getSelected() {
		return selected;
	}

	/**
	 * @return
	 * @uml.property name="ok"
	 */
	public boolean isOk() {
		return ok;
	}
}
