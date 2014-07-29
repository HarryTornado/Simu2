package component.param;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import net.effortech.harry.swing.BlankPanel;

@SuppressWarnings("serial")
public class ParameterManager extends JDialog implements ActionListener {
	private JButton addBtn = new JButton("add");
	private JButton delBtn = new JButton("delete");
	private JButton editBtn = new JButton("edit");
	private JButton closeBtn = new JButton("close");

	private TBModel model;

	private JTable table;

	public ParameterManager() {
		this(new ArrayList<Parameter>());
	}

	public ParameterManager(List<Parameter> params) {
		this.setTitle("Parameter Editor");
		this.setModal(true);

		model = new TBModel();
		model.setParams(params);
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					editParamCell();
				}
			}
		});
		add(new JScrollPane(table));
		add(getCtlPanel(), BorderLayout.SOUTH);

		setSize(500, 400);
		this.setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeBtn) {
			this.setVisible(false);
		} else if (e.getSource() == addBtn) {
			ParameterEditor pe = new ParameterEditor();
			if (pe.isOk()) {
				try {
					model.add(new Parameter(pe.getName(), pe.getFormula()));
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
		} else if (e.getSource() == editBtn) {
			editParamCell();
		} else if (e.getSource() == delBtn) {
			int row = table.getSelectedRow();
			model.delete(row);
		}
	}

	private void editParamCell() {
		int row = table.getSelectedRow();
		if (row >= 0) {
			ParameterEditor pe = new ParameterEditor(model.getParameter(row));
			if (pe.isOk()) {
				try {
					model.update(row, pe.getFormula());
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
		}
	}

	public void add(Parameter p) throws Exception {
		model.add(p);
	}

	private JPanel getCtlPanel() {
		JPanel panel = new JPanel();

		addBtn.addActionListener(this);
		delBtn.addActionListener(this);
		editBtn.addActionListener(this);
		closeBtn.addActionListener(this);

		panel.add(addBtn);
		panel.add(delBtn);
		panel.add(editBtn);
		panel.add(new BlankPanel(10, true));
		panel.add(closeBtn);

		return panel;
	}

	public List<Parameter> getParams() {
		return model.getParams();
	}
}

@SuppressWarnings("serial")
class TBModel extends AbstractTableModel {
	private List<Parameter> params = new ArrayList<Parameter>();

	public void add(Parameter p) throws Exception {
		for (Parameter pr : params)
			if (p.getName().equals(pr.getName()))
				throw new Exception(p.getName() + " already defined!");
		params.add(p);
		update();

		this.fireTableRowsInserted(params.size() - 1, params.size());
	}

	public void delete(int row) {
		params.remove(row);
		update();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0)
			return "name";
		if (columnIndex == 1)
			return "formula";
		return "value";
	}

	public Parameter getParameter(int i) {
		return params.get(i);
	}

	public List<Parameter> getParams() {
		return params;
	}

	@Override
	public int getRowCount() {
		if (params == null)
			return 0;
		return params.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (params == null)
			return null;
		Parameter p = params.get(rowIndex);
		if (columnIndex == 0)
			return p.getName();
		else if (columnIndex == 1)
			return p.getFormula();
		else
			return p.getValue();
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
		update();
	}

	private void update() {
		for (Parameter prm : params)
			prm.evaluate(params);
		this.fireTableChanged(null);
	}

	public void update(int row, String formula) throws Exception {
		Parameter pe = params.get(row);
		pe.setFormula(formula);
		update();
	}
}
