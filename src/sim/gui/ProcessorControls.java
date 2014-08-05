

package sim.gui;

import sim.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ProcessorControls extends JPanel implements ActionListener
{
	private JCheckBox forwardSelect;
	private JCheckBox flushSelect;
	private JCheckBox loadStallSelect;

	private Processor processor;

	public ProcessorControls(Processor processor)
	{
		this.processor = processor;

		setBorder(new TitledBorder("Processor Controls"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		forwardSelect = new JCheckBox("Reg Forwarding");
		forwardSelect.addActionListener(this);
		forwardSelect.setSelected(processor.enableRegForwarding);
		add(forwardSelect);

		flushSelect = new JCheckBox("Jump Flush");
		flushSelect.addActionListener(this);
		flushSelect.setSelected(processor.enableJumpFlush);
		add(flushSelect);

		loadStallSelect = new JCheckBox("Load Stall");
		loadStallSelect.addActionListener(this);
		loadStallSelect.setSelected(processor.enableLoadStall);
		add(loadStallSelect);
	}

	public void actionPerformed(ActionEvent aE)
	{
		Object o = aE.getSource();

		if (o == forwardSelect) processor.enableRegForwarding = forwardSelect.isSelected();
		else if (o == flushSelect) processor.enableJumpFlush = flushSelect.isSelected();
		else if (o == loadStallSelect) processor.enableLoadStall = loadStallSelect.isSelected();
	}
}
