// Microprocessor Simulator
// A.Greensted - University of York
// ajg112@ohm.york.ac.uk
// November 2008

package sim.gui;

import sim.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class RegisterFilePanel extends JComponent
{
	private final static int WIDTH = 250;
	private final static int HEIGHT = 160;

	private int[] regFile;

	public RegisterFilePanel()
	{
		setBorder(new TitledBorder("Register File"));
	}

	public void update(Processor processor)
	{
		regFile = processor.registerFile;
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
//		Insets insets = getInsets();
//		Rectangle rect = getBounds();

//		int offsetX = (rect.width - WIDTH - insets.left - insets.right) / 2;
//		int offsetY = (rect.height - HEIGHT - insets.top - insets.bottom) / 2;

		// Translate Cooridates to deal with centring offset
//		g.translate(offsetX, offsetY);

		// If regFile is null, finish drawing here
		if (regFile == null) return;

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int yPos = 30;

		for (int i=0 ; i<8 ; i++)
		{
			g.setFont(GUIUtils.LABEL_FONT);
			g.setColor(GUIUtils.LABEL_COLOUR);
			g.drawString(String.format("%d:", i), 10, yPos);
		//	g.drawString(String.format("%d:", i+8), 130, yPos);
			

			g.setFont(GUIUtils.VAR_FONT);
			g.setColor(GUIUtils.VAR_COLOUR);
			g.drawString(String.format("x%02X  %03d  %c", regFile[i], regFile[i], regFile[i]), 35, yPos);
		//	g.drawString(String.format("x%02X  %03d  %c", regFile[i+8], regFile[i+8], regFile[i+8]), 165, yPos);

			yPos += 20;
		}
	}
}
