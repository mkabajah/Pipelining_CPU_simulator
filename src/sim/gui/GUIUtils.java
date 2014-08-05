// Microprocessor Simulator
// A.Greensted - University of York
// ajg112@ohm.york.ac.uk
// November 2008

package sim.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public abstract class GUIUtils
{
	public final static Font LABEL_FONT = new Font("default", Font.BOLD, 12);
	public final static Font VAR_FONT = new Font("default", Font.PLAIN, 12);

	public final static Color LABEL_COLOUR = Color.BLUE;
	public final static Color VAR_COLOUR = Color.BLACK;
	public final static Color HIGHLIGHT_COLOUR = new Color(188,210,255);

	public static JLabel createLabel(String text, Font font, Color colour)
	{
		JLabel label = new JLabel(text);
		label.setFont(font);
		label.setForeground(colour);
		return label;
	}

	public static JLabel createStatusLabel(String str)
	{
		JLabel label = null;
		if (str != null) label = new JLabel(str);
		else label = new JLabel();
		label.setFont(LABEL_FONT);
		label.setForeground(LABEL_COLOUR);
		label.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		return label;
	}

}
