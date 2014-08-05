// Microprocessor Simulator
// A.Greensted - University of York
// ajg112@ohm.york.ac.uk
// November 2008

package sim.gui;

import sim.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class InstructionDisplay extends JComponent
{
	private final static int IMG_MARGIN_TOP = 5;
	private final static int IMG_MARGIN_BOTTOM = 5;
	private final static int IMG_MARGIN_LEFT = 22;
	private final static int IMG_MARGIN_RIGHT = 5;

	private int width;
	private int height;

	private Image imageTop;
	private Image imageBlank;
	private Image[] formatImageArray;
	private String[] formatStringArray = new String[] {"FE", "DE", "EX", "MA", "WB"};

	private Instruction[] program;
	private Processor.Signals signals;

	private int offsetX;
	private int offsetY;

	public InstructionDisplay(Instruction[] program)
	{
		this.program = program;

		ImageIcon imageIcon;

		height = IMG_MARGIN_TOP + IMG_MARGIN_BOTTOM;

		imageIcon = new ImageIcon("images/insnTop.png");
		imageTop = imageIcon.getImage();

		width = IMG_MARGIN_LEFT + imageIcon.getIconWidth() + IMG_MARGIN_RIGHT;
		height += imageIcon.getIconHeight();

		imageIcon = new ImageIcon("images/insnBlank.png");
		imageBlank = imageIcon.getImage();

		formatImageArray = new Image[4];

		for (int i=0 ; i<4 ; i++)
		{	
			imageIcon = new ImageIcon("images/insn" + i + ".png");
			formatImageArray[i] = imageIcon.getImage();
		}

		// Add height for stages
		height += (imageIcon.getIconHeight() * 5);
	}

	public Dimension getSize()
	{
		Insets insets = getInsets();
		return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
	}

	public Dimension getPreferredSize()
	{
		Insets insets = getInsets();
		return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
	}

	public void paintComponent(Graphics g)
	{
		Insets insets = getInsets();
		Rectangle rect = getBounds();

		offsetX = (rect.width - width - insets.left - insets.right) / 2;
		offsetY = (rect.height - height - insets.top - insets.bottom) / 2;

		// Translate Cooridates to deal with centring offset
		g.translate(offsetX, offsetY);

		g.setColor(Color.WHITE);
		g.fillRect(insets.left, insets.top, width, height);

		// Draw Background Image
		g.drawImage(imageTop, IMG_MARGIN_LEFT, IMG_MARGIN_TOP, null);

		g.setColor(Color.GRAY);
		g.drawRect(insets.left, insets.top, width-1, height-1);

		// If signals is null, finish drawing here
		if (signals == null) return;

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Instruction[] insnArray = new Instruction[5];
/*
		insnArray[0] = (signals.fe_pc<0 || signals.fe_pc>=program.length || signals.fe_en == 0) ? null : program[signals.fe_pc];
		insnArray[1] = (signals.de_pc<0 || signals.de_pc>=program.length || signals.de_en == 0) ? null : program[signals.de_pc];
		insnArray[2] = (signals.ex_pc<0 || signals.ex_pc>=program.length || signals.ex_en == 0) ? null : program[signals.ex_pc];
		insnArray[3] = (signals.ma_pc<0 || signals.ma_pc>=program.length || signals.ma_en == 0) ? null : program[signals.ma_pc];
		insnArray[4] = (signals.wb_pc<0 || signals.wb_pc>=program.length || signals.wb_en == 0) ? null : program[signals.wb_pc];

		int yPos = 18;

		for (int s=0 ; s<5 ; s++)
		{
			if (insnArray[s] != null)
			{
				int encoding = insnArray[s].encoding;
				int format = insnArray[s].insnType.format;
				float xPos = 27;

				g.drawImage(formatImageArray[format], IMG_MARGIN_LEFT, IMG_MARGIN_TOP + yPos, null);
				g.setColor(Color.BLACK);
				g.drawString(formatStringArray[s], 2, yPos+33);

				g.setColor(Color.BLUE);

				for (int b=0 ; b<18 ; b++)
				{
					int bit = encoding & 0x1;
					encoding >>= 1;
					String bitStr = (bit == 0) ? "0" : "1";
					g.drawString(bitStr, (int)xPos,	yPos+33);
					xPos += 22.2;
				}
			}
			else
			{
				g.drawImage(imageBlank, IMG_MARGIN_LEFT, IMG_MARGIN_TOP + yPos, null);
			}

			yPos += 33;
		}*/
	}

	public void update(Processor processor)
	{
		signals = processor.signals;
		repaint();
	}
}

