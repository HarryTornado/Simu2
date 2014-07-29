package component;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import net.effortech.harry.swing.BlankPanel;

import component.param.Parameter;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class TfDialog extends JDialog {
	private int dim;
	private int oNum;
	private JTextField[][] numFlds;
	private JTextField[] denFlds;
	/**
	 * @uml.property name="ok"
	 */
	private boolean ok;

	/**
	 * @return
	 * @uml.property name="ok"
	 */
	public boolean isOk() {
		return ok;
	}

	private JPanel getParamPanel(List<Parameter> params, List<Parameter> sysParams) {
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
		panel.add(new JScrollPane(area));
		panel.add(new BlankPanel(20, true), BorderLayout.WEST);
		panel.setPreferredSize(new Dimension(300, 120));
		return panel;
	}

	public TfDialog(String title, int dim, int oNum, List<Parameter> params, List<Parameter> sysParams) {
		this.setTitle("Transfer Function for " + title);
		this.setModal(true);

		this.dim = dim;
		this.oNum = oNum;

		numFlds = new JTextField[oNum][dim + 1];
		for (int k = 0; k < numFlds.length; k++)
			for (int i = 0; i < numFlds[k].length; i++)
				numFlds[k][i] = new JTextField("0", 5);

		denFlds = new JTextField[dim];
		for (int i = 0; i < denFlds.length; i++)
			denFlds[i] = new JTextField("0", 5);

		Container window = this.getContentPane();
		window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));

		window.add(getParamPanel(params, sysParams));

		JPanel[] nps = getNumPanel();
		JPanel np = new JPanel();
		np.setLayout(new BoxLayout(np, BoxLayout.Y_AXIS));
		np.setBorder(new BevelBorder(BevelBorder.LOWERED));
		for (int k = 0; k < nps.length; k++)
			np.add(nps[k]);

		window.add(np);
		window.add(getDenPanel());
		window.add(getCtlPanel());

		pack();
		this.setLocationRelativeTo(null);
		setVisible(true);
	}

	private JPanel[] getNumPanel() {
		JPanel[] ps = new JPanel[oNum];
		for (int k = 0; k < ps.length; k++) {
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			panel.add(new JLabel((k + 1) + "-th num: "));
			for (int i = numFlds[k].length - 1; i >= 0; i--) {
				panel.add(numFlds[k][i]);
				if (i != 0)
					panel.add(new JLabel("s^" + i + "+"));
			}
			ps[k] = panel;
		}
		return ps;
	}

	private JPanel getDenPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		panel.add(new JLabel("s^" + dim + "+"));
		for (int i = denFlds.length - 1; i >= 0; i--) {
			panel.add(denFlds[i]);
			if (i != 0)
				panel.add(new JLabel("s^" + i + "+"));
		}
		return panel;
	}

	private JPanel getCtlPanel() {
		JPanel panel = new JPanel();

		JButton okBtn = new JButton("ok");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = true;
				setVisible(false);
			}
		});

		JButton cancelBtn = new JButton("cancel");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = false;
				setVisible(false);
			}
		});

		panel.add(okBtn);
		panel.add(cancelBtn);

		return panel;
	}

	public String getA(int i) {
		return denFlds[i].getText();
	}

	public String getB(int k, int i) {
		return numFlds[k][i].getText();
	}
}
