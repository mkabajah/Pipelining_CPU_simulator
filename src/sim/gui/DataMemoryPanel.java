// Microprocessor Simulator
// A.Greensted - University of York
// ajg112@ohm.york.ac.uk
// November 2008

package sim.gui;

import sim.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class DataMemoryPanel extends JComponent
{
	private final static int CELL_SIZE = 9;
	private final static int CELL_SPACE = 1;
	private final static int DIM = (CELL_SIZE * 16) + (CELL_SPACE * 17) + 2;

	private int[] mem;
	private Color[] colourArray;

	public DataMemoryPanel()
	{
		setBorder(new TitledBorder("Data Memory"));

		colourArray = new Color[] {new Color(0,	0,		0),		// Black
											new Color(128,	128,	128),		// Dark Gray
											new Color(128,	0,		0),		// Dark Red
											new Color(255,	0,		0),		// Red
											new Color(0,	128,	0),		// Dark Green
											new Color(0,	255,	0),		// Green
											new Color(128,	128,	0),		// Dark Yellow
											new Color(255,	255,	0),		// yellow
											new Color(0,	0,		128),		// Dark Blue
											new Color(0,	0,		255),		// Blue
											new Color(128,	0,		128),		// Dark Magenta
											new Color(255,	0,		255),		// Magenta
											new Color(0,	128,	128),		// Dark Cyan
											new Color(0,	255,	255),		// Cyan
											new Color(192,	192,	192),		// Light Gray
											new Color(255,	255,	255)};	// White
	}

	public void update(Processor processor)
	{
		mem = (int[]) processor.dataMemory;
		repaint();
		
	}

	public Dimension getSize()
	{
		Insets insets = getInsets();
		return new Dimension(DIM + insets.left + insets.right, DIM + insets.top + insets.bottom);
	}

	public Dimension getPreferredSize()
	{
		Insets insets = getInsets();
		return new Dimension(DIM + insets.left + insets.right, DIM + insets.top + insets.bottom);
	}

	public void paintComponent(Graphics g)
	{
		Insets insets = getInsets();
		Rectangle rect = getBounds();

		int offsetX = (rect.width - DIM - insets.left - insets.right) / 2;
		int offsetY = (rect.height - DIM - insets.top - insets.bottom) / 2;

		g.setColor(Color.WHITE);
		g.fillRect(offsetX + insets.left, offsetY + insets.top, DIM, DIM);
		g.setColor(Color.GRAY);
		g.drawRect(offsetX + insets.left, offsetY + insets.top, DIM-1, DIM-1);

		for (int x=0 ; x<16 ; x++)
		{
			for (int y=0 ; y<16 ; y++)
			{
				int xPos = offsetX + insets.left + 1 + CELL_SPACE + (x * (CELL_SIZE + CELL_SPACE));
				int yPos = offsetY + insets.top + 1 + CELL_SPACE + (y * (CELL_SIZE + CELL_SPACE));
			
				if (mem != null)
				{
					int i = x + (y*16);
					g.setColor(colourArray[mem[i]&0xF]);
					g.fillRect(xPos, yPos, CELL_SIZE, CELL_SIZE);
				}
				else
				{
					g.setColor(Color.GRAY);
					g.drawRect(xPos, yPos, CELL_SIZE-1, CELL_SIZE-1);
				}
			}
		}
	}
}
