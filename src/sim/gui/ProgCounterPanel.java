// Microprocessor Simulator
// A.Greensted - University of York
// ajg112@ohm.york.ac.uk
// November 2008

package sim.gui;

import sim.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class ProgCounterPanel extends JComponent
{
	private final static int WIDTH = 120;
	private final static int HEIGHT = 30;

	private int pc;
	private long clks;

	public ProgCounterPanel()
	{
		setBorder(new TitledBorder("Program Counter"));
	}

	public void update(Processor processor)
	{
		pc = processor.signals.fe_pc;
		clks = processor.clockCount;
		repaint();
	}

	public Dimension getSize()
	{
		Insets insets = getInsets();
		return new Dimension(WIDTH + insets.left + insets.right, HEIGHT + insets.top + insets.bottom);
	}

	public Dimension getPreferredSize()
	{
		Insets insets = getInsets();
		return new Dimension(WIDTH + insets.left + insets.right, HEIGHT + insets.top + insets.bottom);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Insets insets = getInsets();

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setFont(GUIUtils.LABEL_FONT);
		g.setColor(GUIUtils.LABEL_COLOUR);
		g.drawString("PC:", insets.left, insets.top + 9);
		g.drawString("Clks:", insets.left, insets.top + 26);
			
		g.setFont(GUIUtils.VAR_FONT);
		g.setColor(GUIUtils.VAR_COLOUR);
		g.drawString(String.format("x%03X  %04d", pc, pc), insets.left + 40, insets.top + 9);
		g.drawString(String.format("%06d", clks), insets.left + 40, insets.top + 26);
	}
}
