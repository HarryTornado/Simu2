package model;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import Jama.ColumnMatrix;
import Jama.Matrix;

/**
 * @author    jyk
 */
@SuppressWarnings("serial")
public class MatrixDialog extends JDialog implements ActionListener {
	private Matrix x;
	private JTextField[] flds;

	public MatrixDialog(Matrix x, String name, String prefix) {
		this.setTitle(name);
		this.setModal(true);
		this.x = x;

		Container window = this.getContentPane();
		BoxLayout bl = new BoxLayout(window, BoxLayout.Y_AXIS);
		window.setLayout(bl);

		flds = new JTextField[x.getRowDimension()];

		DecimalFormat df = new DecimalFormat("###.########");

		for (int i = 0; i < flds.length; i++) {
			JPanel p = new JPanel();

			flds[i] = new JTextField(10);
			flds[i].setFont(new Font(Font.DIALOG, Font.BOLD, 16));
			flds[i].setText(df.format(x.get(i, 0)));

			p.add(new JLabel(prefix + (i + 1) + ": "));
			p.add(flds[i]);

			window.add(p);
		}

		window.add(getCtlPanel());

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * @uml.property  name="ok"
	 */
	private boolean ok = false;

	/**
	 * @return
	 * @uml.property  name="ok"
	 */
	public boolean isOk() {
		return ok;
	}

	public Matrix getDataMatrix() {
		return x;
	}

	private JButton okBtn;
	private JButton cancelBtn;

	private JPanel getCtlPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.RAISED));

		okBtn = new JButton("ok");
		cancelBtn = new JButton("cancel");

		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);

		panel.add(okBtn);
		panel.add(cancelBtn);
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okBtn) {
			try {
				double[] v = new double[flds.length];
				for (int i = 0; i < flds.length; i++) {
					v[i] = Double.parseDouble(flds[i].getText());
				}
				x = new ColumnMatrix(v);
				ok = true;
				setVisible(false);
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(null, e1.getLocalizedMessage());
			}
		} else {
			ok = false;
			setVisible(false);
		}
	}
}
