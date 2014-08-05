// Microprocessor Simulator
// A.Greensted - University of York
// ajg112@ohm.york.ac.uk
// November 2008

package sim.gui;

import sim.*;

import java.util.*;
import java.lang.reflect.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class VariablesPanel extends JPanel
{
	private JTable signalTable;
	private DataModel dataModel;

	private Field fieldArray[];
	private int numFields;

	private Object source;

	public VariablesPanel(String className, String title)
	{
		setLayout(new BorderLayout());

		setBorder(new TitledBorder(title));

		try
		{
			Class signalsClass = Class.forName(className);
			fieldArray = signalsClass.getFields();
		}
		catch (ClassNotFoundException cnfE) {}

		ArrayList<Field> fieldList = new ArrayList<Field>();

		// Extract int Fields
		for (Field field : fieldArray) if (field.getType().equals(int.class)) fieldList.add(field);

		// Create Array from list of int fields
		fieldArray = new Field[fieldList.size()];
		fieldArray = fieldList.toArray(fieldArray);
		numFields = fieldArray.length;

		dataModel = new DataModel();
		signalTable = new JTable(dataModel);
		JScrollPane scrollPane = new JScrollPane(signalTable);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);

		int height = calcHeight();

		signalTable.setDefaultRenderer(signalTable.getColumnClass(0), new DataRenderer());
		signalTable.setPreferredScrollableViewportSize(new Dimension(250, height));
	}

	private int calcHeight()
	{
		Component comp = signalTable.getDefaultRenderer(signalTable.getColumnClass(0)).getTableCellRendererComponent(signalTable, "STRING", false, false, 0, 0);
		int cellHeight = comp.getPreferredSize().height - 1; // Subtract one to allow for overlap
		return cellHeight * fieldArray.length;
	}
	class DataRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == 0) comp.setFont(GUIUtils.LABEL_FONT);
			else comp.setFont(GUIUtils.VAR_FONT);
			return comp;
		}
	}

	class DataModel extends AbstractTableModel
	{
		public int getColumnCount()
		{
			return 3;
		}
		public int getRowCount()
		{
			return numFields;
		}
		public String getColumnName(int col)
		{
			switch (col)
			{
				case 0: return "Name";
				case 1: return "Value (Dec)";
				case 2: return "Value (Hex)";
			}

			return "??";
		}
		public Object getValueAt(int row, int col)
		{	
			if (col == 0)
			{
				return fieldArray[row].getName();
			}
			else
			{
				if (source == null) return "?";
				int val = 0;

				try
				{
					val = fieldArray[row].getInt(source);
				}
				catch (IllegalAccessException iaE)
				{
					return "-";
				}

				if (col == 1) return Integer.toString(val);
				if (col == 2) return String.format("x%02X", val);
				return "??";
			}
		}
	};

	public void update(Object source)
	{
		this.source = source;
		dataModel.fireTableDataChanged();
	}
}
