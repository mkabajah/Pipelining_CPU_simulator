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

public class PipelineDisplay extends JComponent
{
	private final static int IMG_MARGIN_TOP = 0;
	private final static int IMG_MARGIN_BOTTOM = 0;
	private final static int IMG_MARGIN_LEFT = 0;
	private final static int IMG_MARGIN_RIGHT = 0;

	private int width;
	private int height;

	private Image image;

	private Instruction[] program;
	private Processor processor;
	private Processor.Signals signals;
	private Processor.Controls controls;

	private int offsetX;
	private int offsetY;

	// Paths
	private final static Color PATH_COLOUR = new Color(24,221,25);
	private BasicStroke highlightStroke = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	private Font textFont;
	private FontMetrics fontMetrics;

	public PipelineDisplay(Instruction[] program)
	{
		this.program = program;

		ImageIcon imageIcon = new ImageIcon("/Users/mohammed/Desktop/arch.png");
		image = imageIcon.getImage();

		width = IMG_MARGIN_LEFT + imageIcon.getIconWidth() + IMG_MARGIN_RIGHT;
		height = IMG_MARGIN_TOP + imageIcon.getIconHeight() + IMG_MARGIN_BOTTOM;

/*		addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseDragged(MouseEvent mE) {}
			public void mouseMoved(MouseEvent mE)
			{
				int x = mE.getX() - offsetX;
				int y = mE.getY() - offsetY;
				System.out.printf("(x:%d,y:%d)\n", x, y);
			}
		});*/

		textFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
		fontMetrics = getFontMetrics(textFont);
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
		g.drawImage(image, IMG_MARGIN_LEFT, IMG_MARGIN_TOP, null);

		g.setColor(Color.GRAY);
		g.drawRect(insets.left, insets.top, width-1, height-1);

		// If signals is null, finish drawing here
		if (signals == null) return;

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(textFont);

		int lineWidth;
		String insnLine;
		String insnMn;
		boolean strike;

		synchronized(processor)
		{
			// Fetch // draw the question mark if there is no instruction 
			g.setColor(Color.GRAY);
			insnLine = String.format("[%03X] ", processor.feInsnAdd);
			if (processor.feInsnAdd<0 || processor.feInsnAdd>=program.length) {
				insnLine += "no Instruction";
				insnMn = "no instruction";
			}
			else {
				insnLine += program[processor.feInsnAdd].srcString;
				insnMn = "(" + program[processor.feInsnAdd].insnType + ")";
			}
			drawCentreString(g, insnLine, 78, height-20, false);
			drawCentreString(g, insnMn, 78, height-7, false);

			// Decode
			insnLine = String.format("[%03X] ",processor.deInsnAdd);
			if (processor.deInsnAdd<0 || processor.deInsnAdd>=program.length) {
				insnLine += "?";
				insnMn = "?";
			}
			else {
				insnLine += program[processor.deInsnAdd].srcString;
				insnMn = "(" + program[processor.deInsnAdd].insnType + ")";
			}
			strike = (signals.de_feFlush == 1) ? true : false;
			drawCentreString(g, insnLine, 280, height-20, strike);
			drawCentreString(g, insnMn, 280, height-7, strike);

			// Execute
			insnLine = String.format("[%03X] ",processor.exInsnAdd);
			if (processor.exInsnAdd<0 || processor.exInsnAdd>=program.length) {
				insnLine += "?";
				insnMn = "?";
			}
			else {
				insnLine += program[processor.exInsnAdd].srcString;
				insnMn = "(" + program[processor.exInsnAdd].insnType + ")";
			}
			strike = (signals.ex_feFlush == 1 || signals.ex_deFlush == 1) ? true : false;
			drawCentreString(g, insnLine, 605, height-20, strike);
			drawCentreString(g, insnMn, 605, height-7, strike);

			// Memory Access
			insnLine = String.format("[%03X] ",processor.maInsnAdd);
			if (processor.maInsnAdd<0 || processor.maInsnAdd>=program.length) {
				insnLine += "?";
				insnMn = "?";
			}
			else {
				insnLine += program[processor.maInsnAdd].srcString;
				insnMn = "(" + program[processor.maInsnAdd].insnType + ")";
			}
			strike = (signals.ma_wrEn == 0 && signals.ma_mEn == 0) ? true : false;
			drawCentreString(g, insnLine, 865, height-20, strike);
			drawCentreString(g, insnMn, 865, height-7, strike);

			// Writeback
			insnLine = String.format("[%03X] ",processor.wbInsnAdd);
			if (processor.wbInsnAdd<0 || processor.wbInsnAdd>=program.length) {
				insnLine += "?";
				insnMn = "?";
			}
			else {
				insnLine += program[processor.wbInsnAdd].srcString;
				insnMn = "(" + program[processor.wbInsnAdd].insnType + ")";
			}
			strike = (signals.wb_wrEn == 0) ? true : false;
			drawCentreString(g, insnLine, 1025, height-20, strike);
			drawCentreString(g, insnMn, 1025, height-7, strike);
		}

		g.setColor(Color.GRAY);

		// Controls
		g.drawString(String.format("%X",		controls.wrEn),		747,	130);
		g.drawString(String.format("%X",		controls.feFlush),	124,	120);
		g.drawString(String.format("%X",		controls.deFlush),	380,	87);
		g.drawString(String.format("%X",		controls.memEn),		747,	390);

		g.drawString(String.format("%X",		controls.pcEn),		87,	88);
		g.drawString(String.format("%X",		controls.fedeEn),		52,	389);
		g.drawString(String.format("%X",		controls.fedeEn),		178,	140);
		g.drawString(String.format("%X",		controls.deexEn),		435,	46);
		g.drawString(String.format("%X",		controls.deexEn),		235,	278);

		g.drawString(String.format("%X",		controls.muxDS),		747,	184);
		g.drawString(String.format("%s",		controls.aluOp),		686,	187);

		g.setColor(Color.BLACK);

		// Memories
		g.drawString(String.format("%05X",	signals.im_dOut),		110,	323);
		g.drawString(String.format("%02X",	signals.rf_dOutA),	371,	222);
		g.drawString(String.format("%02X",	signals.rf_dOutB),	371,	252);
		g.drawString(String.format("%02X",	signals.dm_dOut),		908,	364);

		// Muxes
		g.drawString(String.format("%X",		controls.muxCS),		50,	70);
		g.drawString(String.format("%X",		controls.muxJS),		697,	94);
		g.drawString(String.format("%X",		controls.muxFA),		560,	241);
		g.drawString(String.format("%X",		controls.muxFB),		560,	324);
		g.drawString(String.format("%X",		controls.muxAS),		649,	373);
		g.drawString(String.format("%X",		signals.wb_muxDS),	1027,	289);


		g.setColor(Color.BLUE);

		// Flags
		g.drawString(Integer.toString(signals.ex_flagZ), 			737,	212);
		g.drawString(Integer.toString(signals.ex_flagC), 			761,	212);
		g.drawString(Integer.toString(signals.ma_flagZ), 			829,	212);
		g.drawString(Integer.toString(signals.ma_flagC), 			853,	212);


		// Fetch
		g.drawString(String.format("%03X",	signals.fe_pc),		32,	288);
		g.drawString(String.format("%03X",	signals.fe_pcPlus),	115,	27);

		// Decode
		g.drawString(String.format("%05X",	signals.de_insn),		162,	229);

		g.drawString(String.format("r%X",	signals.de_rAddA),	242,	204);
		g.drawString(String.format("r%X",	signals.de_rAddB),	242,	234);

		g.drawString(String.format("%03X",	signals.de_pc),		305,	70);
		g.drawString(String.format("%X",		signals.de_feFlush),	305,	105);
		g.drawString(String.format("%02X",	signals.de_opCode),	305,	123);
		g.drawString(String.format("r%X",	signals.de_rAddA),	305,	140);
		g.drawString(String.format("r%X",	signals.de_rAddB),	305,	158);
		g.drawString(String.format("%03X",	signals.de_imm),		305,	176);

		// Execute
		g.drawString(String.format("%03X",	signals.ex_pc),		480,	69);
		g.drawString(String.format("%X",		signals.ex_deFlush),	480,	87);
		g.drawString(String.format("%X",		signals.ex_feFlush),	480,	105);
		g.drawString(String.format("%02X",	signals.ex_opCode),	480,	123);
		g.drawString(String.format("r%01X",	signals.ex_rAddc),	480,	140);
		g.drawString(String.format("r%01X",	signals.ex_rAddB),	480,	158);
		g.drawString(String.format("%03X",	signals.ex_imm),		480,	176);

		g.drawString(String.format("%02X",	signals.ex_rDatA),	430,	219);
		g.drawString(String.format("%02X",	signals.ex_rDatB),	430,	249);

		g.drawString(String.format("%02X",	(signals.ex_imm & 0xFF)),	525,	197);

		g.drawString(String.format("%02X",	signals.ex_opA),		645,	237);
		g.drawString(String.format("%02X",	signals.ex_opB),		645,	319);
		g.drawString(String.format("%02X",	signals.ex_aluOut),	738,	278);
		g.drawString(String.format("%02X",	signals.ex_mAdd),		696,	367);

		g.drawString(String.format("%03X",	signals.ex_opASE),	562,	81);
		g.drawString(String.format("%03X",	signals.ex_pcRel),	643,	71);
		g.drawString(String.format("%03X",	signals.ex_imm),		643,	107);
		g.drawString(String.format("%03X",	signals.ex_pcJmp),	365,	25);
		g.drawString(String.format("%03X",	signals.ex_nPC),		24,	95);

		// Memory Access
		g.drawString(String.format("%X",		signals.ma_wrEn),		857,	116);
		g.drawString(String.format("r%X",	signals.ma_wrAdd),	853,	140);
		g.drawString(String.format("%X",		signals.ma_muxDS),	857,	169);
		g.drawString(String.format("%02X",	signals.ma_aluOut),	829,	266);
		g.drawString(String.format("%02X",	signals.ma_mAdd),		832,	366);
		g.drawString(String.format("%X",		signals.ma_mEn),		835,	390);

		g.drawString(String.format("%02X",	signals.ma_aluOut),	715,	430);

		// Write Back
		g.drawString(String.format("%X",		signals.wb_wrEn),		1009,	116);
		g.drawString(String.format("r%X",	signals.wb_wrAdd),	1005,	140);
		g.drawString(String.format("%X",		signals.wb_muxDS),	1009,	169);
		g.drawString(String.format("%02X",	signals.wb_aluOut),	968,	266);
		g.drawString(String.format("%02X",	signals.wb_data),		1054,	284);
		g.drawString(String.format("%02X",	signals.wb_mD),		968,	302);

		g.drawString(String.format("%02X",	signals.wb_data),		715,	448);
		g.drawString(String.format("r%X",	signals.wb_wrAdd),	715,	464);
		g.drawString(String.format("%X",		signals.ma_wrEn),		719,	483);

		g.drawString(String.format("%02X",	signals.wb_data),		242,	318);
		g.drawString(String.format("r%X",	signals.wb_wrAdd),	242,	342);
		g.drawString(String.format("%X",		signals.ma_wrEn),		246,	366);

		g.setColor(PATH_COLOUR);
		g2D.setStroke(highlightStroke);

		// Forward MA
		if (controls.muxFA == 3 || controls.muxFB == 3) g2D.drawPolyline(new int[] {800, 809, 809, 502, 502}, new int[] {267, 267, 432, 432, 327}, 5);

	 	// Forward WB
		if (controls.muxFA == 2 || controls.muxFB == 2) g2D.drawPolyline(new int[] {1041, 1075, 1075, 484, 484}, new int[] {273, 273, 450, 450, 314}, 5);

		if (controls.muxFA == 2) g2D.drawPolyline(new int[] {484, 484, 535}, new int[] {314, 231, 231}, 3);
		if (controls.muxFA == 3) g2D.drawPolyline(new int[] {502, 502, 535}, new int[] {327, 243, 243}, 3);

		if (controls.muxFB == 2) g2D.drawLine(484,314,535,314);
		if (controls.muxFB == 3) g2D.drawLine(502,326,535,326);

	}

	private void drawCentreString(Graphics g, String str, int x, int y, boolean strike)
	{
		g.setColor(Color.GRAY);
		int lineWidth = fontMetrics.stringWidth(str);
		g.drawString(str, x - (lineWidth/2), y);

		if (strike)
		{
			g.setColor(Color.RED);
			g.drawLine(x-(lineWidth/2), y-4, x+(lineWidth/2), y-4);
		}
	}

	public void update(Processor processor)
	{
		this.processor = processor;
		signals = processor.signals;
		controls = processor.controls;
		repaint();
	}
}
