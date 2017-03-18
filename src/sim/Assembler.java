// Microprocessor Simulator
package sim;

import java.io.*;
import java.util.*;

public class Assembler
{
	static String [] instructionMEm;
	public static Instruction[] assemble(File asmFile, PrintStream log, boolean verbose)
	{
		BufferedReader reader = null;
		Hashtable<String, Integer> labelTable = new Hashtable<String, Integer>();

		// PASS 1
		// ---------------------------------------------------
		// Find labels and add label and address to labelTable
		// ---------------------------------------------------
		try
		{
			reader = new BufferedReader(new FileReader(asmFile));
		}
		catch (FileNotFoundException fnfE)// error in file 
		{
			log.println("ERR: Assembler.assemble: Could not open file");
			return null;
		}

		boolean errorFound = false; 
		int address = 0;
		int lineNo = 0;

	

		try
		{
			reader.close();
		}
		catch(IOException ioE)
		{
			log.println("ERR: Assembler.assemble: (Pass 1) IOException when closing reader");
			return null;
		}

		if (errorFound)
		{
			log.println("ERR: Assembler.assemble: (Pass 1) Syntax errors found");
			return null;
		}

		// Display Contents of Label Table
		if (verbose)
		{
			if (labelTable.size() == 0)
			{
				log.println("Assembler.assemble: (Pass 1) No labels found");
			}
			else
			{
				log.println("Assembler.assemble: (Pass 1) Label Table:");
				Enumeration<String> e = labelTable.keys();
				while (e.hasMoreElements())
				{
					String label = e.nextElement();
					int add = labelTable.get(label);
					log.println("  " + label + ": " + add);
				}
			}
		}


		// PASS 2
		// ------
		try
		{
			reader = new BufferedReader(new FileReader(asmFile));
		}
		catch (FileNotFoundException fnfE)
		{
			log.println("ERR: Assembler.assemble: Could not open file");
			return null;
		}

		ArrayList<Instruction> insnList = new ArrayList<Instruction>();
		errorFound = false;
		address = 0;
		lineNo = 0;

		while (true)
		{
			lineNo ++;
			String line = null;

			try
			{
				line = reader.readLine();
			}
			catch(IOException ioE)
			{
				errorFound = true;
				log.println("ERR: Assembler.assemble: (Pass 2) IOException reading assembler file (Line no: " + lineNo +")");
				break;
			}

			if (line == null) break;

			int commentPos = line.indexOf(";");
			if (commentPos != -1) line = line.substring(0, commentPos);
			line = line.trim();

			if (line.length() == 0) continue;

			// Split line at whitespace
			String[] symbols = line.split("\\s+");

			// Ignore label lines
			if (symbols[0].endsWith(":")) continue;// check for label here 

			String opString = symbols[0]; // get the operand here 

			String argStrings[] = new String[symbols.length-1];
			for (int s=0 ; s<symbols.length-1 ; s++) {
				
			argStrings[s] = symbols[s+1];
			log.println(argStrings[s]);//argumaent string 


			}
		
			////
			////
			////
			////
			////
			////
			
			
			Instruction insn = parseInsn(opString, argStrings, labelTable, lineNo, log);
			
			if (insn == null) errorFound = true;
			else insnList.add(insn);
			
			
			////
			////
			////
			////
			////
		
			
		
		}

		try
		{
			reader.close();
		}
		catch(IOException ioE)
		{
			log.println("ERR: Assembler.assemble: (Pass 1) IOException when closing reader");
			return null;
		}

		if (errorFound)
		{
			log.println("ERR: Assembler.assemble: (Pass 2) Errors occured");
			return null;
		}

		Instruction insnArray[] = new Instruction[insnList.size()];
		insnArray = insnList.toArray(insnArray);
		if (verbose) log.println("Assembler.assemble: Assembled " + insnArray.length + " instructions");
for(int i=0;i <insnList.size();i++ ){
	log.println(StringUtils.intToBinaryString(insnList.get(i).encoding, 16, true));
	
}
		return insnArray;
	}

	private static Instruction parseInsn(String opStr, String[] argStrings, Hashtable<String, Integer> labelTable, int lineNo, PrintStream log)
	{
		boolean argError = false;

		// Parse Argument
		Integer[] argValArray = new Integer[argStrings.length]; // argument value array 
		Instruction.ArgType[] argTypeArray = new Instruction.ArgType[argStrings.length];

//		String srcString = "";

		for (int a=0 ; a<argStrings.length ; a++)
		{
			if (argStrings[a] != null)
			{
//				srcString += " " + argStrings[a];

				argValArray[a] = Parser.parseRegister(argStrings[a]);
				if (argValArray[a] != null) argTypeArray[a] = Instruction.ArgType.REG;
				else
				{
					argValArray[a] = Parser.parseImmediate(argStrings[a]);
					if (argValArray[a] != null) argTypeArray[a] = Instruction.ArgType.IMM;
					else
					{
						argValArray[a] = labelTable.get(argStrings[a]);
						if (argValArray[a] != null) argTypeArray[a] = Instruction.ArgType.IMM;
						else
						{
							log.println("Assembler.parseInsn: Bad argument, '" + argStrings[a] + "' (Line no: " + lineNo + ")");
							argError = true;
						}
					}
				}
			}
		}

		// Return if there was an error in the arguments
		if (argError) return null;

		InsnLoop: for (Instruction.InstructionType insnType : Instruction.InstructionType.values())
		{
			// Check for a opCode mnemonic match
			if (!opStr.equals(insnType.opCode)) continue;

			// Check for correct number of arguments
			if (argStrings.length != insnType.argTypeArray.length) continue;

			int encoding = (insnType.ordinal() << 12);

			String srcString = insnType.opCode;

			ArgLoop: for (int a=0 ; a<insnType.argTypeArray.length ; a++)
			{
				switch (insnType.argTypeArray[a])
				{
					case REG:
						if (argTypeArray[a] != Instruction.ArgType.REG) continue InsnLoop;
						if (argValArray[a] < 0 || argValArray[a] > 7) continue InsnLoop;
						srcString += " r" + argValArray[a];
						break;

					case IMM:
						if (argTypeArray[a] != Instruction.ArgType.IMM) continue InsnLoop;
						if (argValArray[a] < 0 || argValArray[a] > 64) continue InsnLoop;
						srcString += " " + String.format("x%02X", argValArray[a]);
						break;

					case UADD:
						if (argTypeArray[a] != Instruction.ArgType.IMM) continue InsnLoop;
						if (argValArray[a] < 0 || argValArray[a] > 4095) continue InsnLoop;
						srcString += " " + String.format("x%03X", argValArray[a]);
						break;

				
				}
				
			int arg = argValArray[a] & ((1 << insnType.argLenArray[a])-1);	// Mask
			encoding |= (arg << insnType.argPosArray[a]);						// Shift into position and set
			}
			log.println(srcString);
			//encoding=Encoding.parse(srcString);
			
			//instructionMEm[a]=StringUtils.intToBinaryString(encoding, 16, false);

			int args[] = new int[argValArray.length];
			for (int a=0 ; a<argValArray.length ; a++) args[a] = argValArray[a];
		
	//		srcString = srcString += insnType.opCode;

			return new Instruction(insnType, encoding, args, lineNo, srcString);
		}

		log.println("Assembler.parseInsn: Bad instruction (Line no: " + lineNo + ")");
		return null;
	}
}
