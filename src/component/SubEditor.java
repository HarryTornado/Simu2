package component;

import help.Help;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

/**
 * @author JiHsianLee
 */
@SuppressWarnings("serial")
public class SubEditor extends JDialog implements ActionListener {
	private ElementPanel panel;
	private JScrollPane panelJSP;
	private JTextField nameField = new JTextField(30);

	public SubEditor(SubSystem sub) {
		this.setTitle("SubEditor - " + sub.getName());
		nameField.setText(sub.getName());
		this.setModal(true);

		JPanel np = new JPanel();
		np.setLayout(new FlowLayout(FlowLayout.LEADING));
		np.add(new JLabel("name:"));
		np.add(nameField);

		add(np, BorderLayout.NORTH);

		nameField.addActionListener(this);

		panel = sub.getSubPanel();
		// panel = new ElementPanel();
		// panel.setElements(sub.getElements());

		// addFileMenu();
		// addComponentMenu();
		addParamMenu();
		addHelpMenu();

		this.makeComponentTB();

		panelJSP = new JScrollPane(panel);
		panelJSP
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelJSP
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		add(panelJSP);

		add(componentTB, BorderLayout.WEST);

		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
	}

	public List<Component> getElements() {
		return panel.getElements();
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

	private void addHelpMenu() {
		JMenuBar jb = this.getJMenuBar();
		if (jb == null)
			jb = new JMenuBar();

		jb.add(Help.makeHelpMenu());

		this.setJMenuBar(jb);
	}

	// private File file;

	// private void addFileMenu() {
	// JMenuBar jb = this.getJMenuBar();
	// if (jb == null)
	// jb = new JMenuBar();
	//
	// JMenu menu = new JMenu("file");
	//
	// JMenuItem newItem = new JMenuItem("new simulation ...");
	// final JMenuItem saveItem = new JMenuItem("save");
	// JMenuItem saveAsItem = new JMenuItem("save as");
	// JMenuItem openItem = new JMenuItem("open ...");
	// JCheckBox toolBox = new JCheckBox("component tools", true);
	// JMenuItem exitItem = new JMenuItem("exit");
	//
	// saveItem.addActionListener(new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// saveDesign(file);
	// }
	// });
	// saveItem.setEnabled(false);
	//
	// newItem.addActionListener(new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// file = null;
	// panel.clear();
	// saveItem.setEnabled(false);
	// }
	// });
	// saveAsItem.addActionListener(new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// JFileChooser fc = new JFileChooser();
	// FileNameExtensionFilter filter = new FileNameExtensionFilter(
	// "Simu Files", "smu");
	//
	// fc.setFileFilter(filter);
	// int returnVal = fc.showSaveDialog(null);
	// if (returnVal == JFileChooser.APPROVE_OPTION) {
	// File fileTmp = fc.getSelectedFile();
	// String fileName = fileTmp.getAbsolutePath();
	// if (!fileName.toLowerCase().endsWith(".smu"))
	// fileTmp = new File(fileName + ".smu");
	// saveDesign(fileTmp);
	// }
	// }
	// });
	//
	// openItem.addActionListener(new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// JFileChooser fc = new JFileChooser();
	// FileNameExtensionFilter filter = new FileNameExtensionFilter(
	// "Simu Files", "smu");
	//
	// fc.setFileFilter(filter);
	// int returnVal = fc.showOpenDialog(null);
	// if (returnVal == JFileChooser.APPROVE_OPTION) {
	// File fileTmp = fc.getSelectedFile();
	// try {
	// panel.clear();
	// panel.readFrom(fileTmp);
	// file = fileTmp;
	// saveItem.setEnabled(true);
	// } catch (Exception e1) {
	// JOptionPane.showMessageDialog(null,
	// "Error in reading in file\n "
	// + fileTmp.getName());
	// }
	// }
	// }
	// });
	//
	// toolBox.addActionListener(new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// JCheckBox cb = (JCheckBox) e.getSource();
	// componentTB.setVisible(cb.isSelected());
	// }
	// });
	// exitItem.addActionListener(new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// System.exit(0);
	// }
	// });
	//
	// menu.add(newItem);
	// menu.add(openItem);
	// menu.add(new JSeparator());
	// menu.add(saveItem);
	// menu.add(saveAsItem);
	// menu.add(new JSeparator());
	// menu.add(toolBox);
	// menu.add(new JSeparator());
	// menu.add(exitItem);
	// jb.add(menu);
	//
	// this.setJMenuBar(jb);
	// }

	private JToolBar componentTB;

	// private void addComponentMenu() {
	// JMenuBar jb = getJMenuBar();
	// if (jb == null)
	// jb = new JMenuBar();
	//
	// JMenu menu = new JMenu("component");
	//
	// for (JComponent cp : ElementLists.getMenuItemsForModule(panel))
	// menu.add(cp);
	//
	// jb.add(menu);
	//
	// setJMenuBar(jb);
	// }

	private void makeComponentTB() {
		componentTB = new JToolBar(JToolBar.VERTICAL);

		for (JComponent cp : ElementLists.getBtnsForModule(panel))
			componentTB.add(cp);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		setTitle("SubEditor - " + nameField.getText());
	}

	public String getModName() {
		return nameField.getText();
	}

	// private void saveDesign(File fileTmp) {
	// try {
	// if (fileTmp != null) {
	// panel.saveTo(fileTmp);
	// file = fileTmp;
	// }
	// } catch (IOException e1) {
	// JOptionPane.showMessageDialog(null, "Exception: " + e1
	// + "\nmessage:\n" + e1.getMessage());
	// e1.printStackTrace();
	// }
	// }

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
