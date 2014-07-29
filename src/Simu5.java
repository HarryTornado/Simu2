import help.Help;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.effortech.harry.swing.Laf;

import component.ElementLists;
import component.ElementPanel;

/**
 * @author JiHsianLee
 */
@SuppressWarnings("serial")
public class Simu5 extends JFrame implements ActionListener {
	private JToolBar componentTB;

	private JTextField dtField = new JTextField("0.001", 5);

	private JButton exitBtn = new JButton("exit");

	private File file;
	private JTextField maxtField = new JTextField("10", 3);
	private JTextField mintField = new JTextField("0", 3);
	private ElementPanel panel;
	private JScrollPane panelJSP;

	private JButton simuBtn = new JButton("simu");

	public Simu5() {
		super("Nonlinear System Simulation");

		panel = new ElementPanel();
		panel.setAdjustListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelJSP.invalidate();
				Simu5.this.invalidate();
			}
		});
		panel.setListener(this);

		addFileMenu();
		addComponentMenu();
		addParamMenu();
		addHelpMenu();

		this.makeComponentTB();

		panelJSP = new JScrollPane(panel);
		panelJSP
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelJSP
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		add(panelJSP);

		add(getCtlPanel(), BorderLayout.PAGE_END);
		add(componentTB, BorderLayout.WEST);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				queryToSave();
			}
		});
		Dimension dm = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(dm.width, dm.height - 35);
		this.setLocation(0, 0);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == simuBtn) {
			simulate();
		} else if (s == exitBtn || s == exitItem) {
			queryToSave();
			System.exit(0);
		} else if (s == panel) {
			if (file != null) {
				saveItem.setEnabled(true);
			}
			saveAsItem.setEnabled(true);
		}
	}

	private void queryToSave() {
		if (saveItem.isEnabled() && file != null || file == null
				&& saveAsItem.isEnabled()) {
			if (JOptionPane.showConfirmDialog(null,
					"Data Changed. Want to save?", "Save?",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				if (file != null)
					saveDesign(file);
				else
					saveDesignToNewFile();
			}
		}
	}

	private void addComponentMenu() {
		JMenuBar jb = getJMenuBar();
		if (jb == null)
			jb = new JMenuBar();

		JMenu menu = new JMenu("component");

		for (JComponent cp : ElementLists.getMenuItemsForSimu(panel))
			menu.add(cp);

		jb.add(menu);

		setJMenuBar(jb);
	}

	private JMenuItem saveItem, saveAsItem, exitItem;

	private void addFileMenu() {
		JMenuBar jb = this.getJMenuBar();
		if (jb == null)
			jb = new JMenuBar();

		JMenu menu = new JMenu("file");

		JMenuItem newItem = new JMenuItem("new simulation ...");
		saveItem = new JMenuItem("save");
		saveAsItem = new JMenuItem("save as");
		JMenuItem openItem = new JMenuItem("open ...");
		JCheckBox toolBox = new JCheckBox("component tools", true);
		exitItem = new JMenuItem("exit");
		exitItem.addActionListener(this);

		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDesign(file);
				saveItem.setEnabled(false);
			}
		});
		saveItem.setEnabled(false);

		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setFile(null);
				panel.clear();
				saveItem.setEnabled(false);
			}
		});
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDesignToNewFile();
				saveItem.setEnabled(false);
			}
		});
		saveAsItem.setEnabled(false);

		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (saveItem.isEnabled() || saveAsItem.isEnabled()) {
					queryToSave();
				}
				JFileChooser fc = new JFileChooser();

				if (file != null)
					fc.setSelectedFile(file);

				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Simu Files", "smu");

				fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File fileTmp = fc.getSelectedFile();
					try {
						panel.clear();
						panel.readFrom(fileTmp);
						setFile(fileTmp);
						saveItem.setEnabled(false);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null,
								"Error in reading in file\n    "
										+ fileTmp.getName()
										+ "\nError Message:\n    "
										+ e1.getMessage());
					}
				}
			}
		});

		toolBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				componentTB.setVisible(cb.isSelected());
			}
		});

		menu.add(newItem);
		menu.add(openItem);
		menu.add(new JSeparator());
		menu.add(saveItem);
		menu.add(saveAsItem);
		menu.add(new JSeparator());
		menu.add(toolBox);
		menu.add(new JSeparator());
		menu.add(exitItem);
		jb.add(menu);

		this.setJMenuBar(jb);
	}

	private void setFile(File ftmp) {
		file = ftmp;
		if (file == null)
			setTitle("Empty - simu");
		else {
			setTitle(file.getName() + " - simu");
		}
	}

	private void addHelpMenu() {
		JMenuBar jb = this.getJMenuBar();
		if (jb == null)
			jb = new JMenuBar();

		jb.add(Help.makeHelpMenu());

		this.setJMenuBar(jb);
	}

	private void addParamMenu() {
		JMenuBar jb = this.getJMenuBar();
		if (jb == null)
			jb = new JMenuBar();

		JMenu menu = new JMenu("Parameters");
		JMenuItem item = new JMenuItem("set parameter");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.editParams();
			}
		});

		menu.add(item);

		jb.add(menu);
		this.setJMenuBar(jb);
	}

	private JPanel getCtlPanel() {
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.TRAILING));

		// p.add(new JToolBar.Separator());
		// p.add(panel.getCtlPanel());
		// p.add(new JToolBar.Separator());

		p.add(new JLabel("from"));
		p.add(mintField);
		p.add(new JLabel("to"));
		p.add(maxtField);
		p.add(new JLabel("with dt"));
		p.add(dtField);
		simuBtn.addActionListener(this);

		p.add(new JToolBar.Separator());
		p.add(simuBtn);
		p.add(new JToolBar.Separator());

		exitBtn.addActionListener(this);
		p.add(exitBtn);
		p.add(new JToolBar.Separator());

		return p;
	}

	private void makeComponentTB() {
		componentTB = new JToolBar(JToolBar.VERTICAL);

		for (JComponent cp : ElementLists.getBtnsForSimu(panel))
			componentTB.add(cp);
	}

	private void saveDesign(File fileTmp) {
		try {
			if (fileTmp != null) {
				panel.saveTo(fileTmp);
				file = fileTmp;
			}
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Exception: " + e1
					+ "\nmessage:\n" + e1.getMessage());
			e1.printStackTrace();
		}
	}

	private void simulate() {
		double mint = Double.parseDouble(mintField.getText());
		double maxt = Double.parseDouble(maxtField.getText());
		double dt = Double.parseDouble(dtField.getText());

		try {
			panel.setTime(mint, maxt, dt);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return;
		}

		new Thread() {
			{
				setDaemon(true);
				start();
			}

			public void run() {
				simuBtn.setEnabled(false);
				panel.simulate();
				simuBtn.setEnabled(true);
			}
		};
	}

	private void saveDesignToNewFile() {
		JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Simu Files", "smu");

		fc.setFileFilter(filter);

		fc.setSelectedFile(file);

		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File fileTmp = fc.getSelectedFile();
			String fileName = fileTmp.getAbsolutePath();
			if (!fileName.toLowerCase().endsWith(".smu"))
				fileTmp = new File(fileName + ".smu");

			if (fileTmp.exists()) {
				if (JOptionPane.showConfirmDialog(null, "File\n  "
						+ fileTmp.getAbsolutePath()
						+ "\nAlready exists, Override it?", "Save?",
						JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION)
					return;
			}
			saveDesign(fileTmp);
			setFile(fileTmp);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException {
		//Laf.setLaf(Laf.CROSS);
		new Simu5();
	}
}
