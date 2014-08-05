// Microprocessor Simulator
// A.Greensted - University of York
// ajg112@ohm.york.ac.uk
// November 2008

package sim.gui;

import sim.*;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

public class SourceViewer extends JPanel
{
	private Instruction[] program;
	private JTextArea textArea;
	private LineExtents[] lineExtentsArray;

	private Highlighter highlighter;
//	private Highlighter.HighlightPainter feHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(188,210,255));
//	private Highlighter.HighlightPainter deHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(188,255,210));
//	private Highlighter.HighlightPainter exHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(255,210,188));
//	private Highlighter.HighlightPainter maHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(255,188,210));
//	private Highlighter.HighlightPainter wbHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(210,188,255));

	class LineExtents
	{
		int start;
		int end;

		public LineExtents(int start, int end)
		{
			this.start = start;
			this.end = end;
		}
	}

	public SourceViewer(File asmFile, Instruction[] program)
	{
		this.program = program;
		
		// Needs to be a BorderLayout, so ScrollPane resizing works
		setLayout(new BorderLayout());
		setBorder(new TitledBorder("Source Viewer"));

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setTabSize(3);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		String line = null;

		ArrayList<LineExtents> lineExtentsList = new ArrayList<LineExtents>();

		try
		{
			int pos = 0;
			int srcLineNum = 1;
			int insnNum = 0;

			BufferedReader in = new BufferedReader(new FileReader(asmFile));

			while ((line = in.readLine()) != null)
			{
				if (pos != 0) textArea.append("\n");
				int length = line.length() + 1;
				lineExtentsList.add(new LineExtents(pos, pos + length));

				if (insnNum < program.length && program[insnNum].srcLine == srcLineNum)
				{
					line = String.format("[%03X] ", insnNum) + line;
					insnNum ++;
				}
				else
				{
					line = "      " + line;
				}

				textArea.append(line);
				pos += length;

				srcLineNum ++;
			}
		}
		catch (FileNotFoundException fnfE) {}
		catch (IOException ioE) {}

		// Convert the ArrayList to an array
		lineExtentsArray = new LineExtents[lineExtentsList.size()];
		lineExtentsArray = lineExtentsList.toArray(lineExtentsArray);

		highlighter = textArea.getHighlighter();

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		add(scrollPane, BorderLayout.CENTER);
	}

	public void update(Processor processor)
	{
		// highlighter.removeAllHighlights();
		// if (processor.signals.ex_en == 1) highlightLine(processor.signals.ex_pc, exHighlightPainter);
	}

	private void highlightLine(int pc, Highlighter.HighlightPainter painter)
	{
		if (pc<0 || pc>=program.length) return;

		Instruction insn = program[pc];
		int srcLine = insn.srcLine - 1;	// Subtract 1, as line numbers start at one, but extents Array starts at 0
		LineExtents extents = lineExtentsArray[srcLine];

		try
		{
			highlighter.addHighlight(extents.start, extents.end, painter);
		}
		catch (BadLocationException blE){}
	}
}
