// Microprocessor Simulator
// A.Greensted - University of York
// ajg112@ohm.york.ac.uk
// November 2008

package sim;

import sim.gui.*;

import java.io.*;
import java.awt.*;
import javax.swing.*;

public class Sim implements GUIListener, Runnable
{
	private PrintStream log = System.out;

	private Instruction[] program;
	private Processor processor;

	private SimFrame simFrame;

	private Thread thread;
	private boolean running = false;
	private int steps;
	private boolean nonStop;
	private boolean update;

	public Sim(String asmFilename)
	{
		File asmFile = new File(asmFilename);

		program = Assembler.assemble(asmFile, log, true);
		for(int i=0;i<program.length;i++){log.println(program[i]);
}
		if (program == null)// ////////////
		{//////////
			log.println("ERR: Sim: Could not create program");////////
			System.exit(1);//////////////
			
		}

		processor = new Processor(program);
		processor.reset();

		simFrame = new SimFrame(program, processor, this, asmFile);
		simFrame.update(processor, true);
	}

	public void stepPressed()
	{
		if (running) return;
		step();
	}

	public void runPressed(int steps, boolean nonStop, boolean update)
	{
		if (running)
		{
			running = false;
		}
		else
		{
			simFrame.setState(SimFrame.State.RUNNING);
			running = true;
			this.steps = steps;
			this.nonStop = nonStop;
			this.update = update;
			thread = new Thread(this);
			thread.start();
		}
	}

	public void resetPressed()
	{
		if (running) return;
		reset();
	}

	public void step()
	{
		processor.clock();

		// Update GUI elements with latest processor states
		simFrame.update(processor, true);
	}

	public void reset()
	{
		processor.reset();
		simFrame.update(processor, true);
	}

	public void run()
	{
		while(running)
		{
			synchronized(processor)
			{
				processor.clock();

//				try
//				{
//					thread.sleep(1);
//				}
//				catch (InterruptedException iE) {}
			}
			if (update) simFrame.update(processor, false);

			if (!nonStop)
			{
				steps --;
				if (steps == 0) running = false;
			}
		}

		simFrame.update(processor, true);
		simFrame.setState(SimFrame.State.NOT_RUNNING);
	}


	public static void main(String[] args)
	{
		
		 new Sim("/Users/mohammed/Desktop/memTest2.asm");

	}
}
