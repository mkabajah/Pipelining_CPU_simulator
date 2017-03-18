// Microprocessor Simulator


package sim;

public class Instruction
{
	public enum ArgType {REG, IMM, UADD, SADD};

	public enum InstructionType {
		// the r-type instruction
		ADD	    ("add"  ,1,	new ArgType[] {ArgType.REG, ArgType.REG,ArgType.REG},		new int[] {9,6,3},	new int[] {3,3,3}),
		SUB	    ("sub"  ,1, new ArgType[] {ArgType.REG, ArgType.REG,ArgType.REG},		new int[] {9,6,3},	new int[] {3,3,3}),
		COMP	("comp" ,1,	new ArgType[] {ArgType.REG, ArgType.REG,ArgType.REG},		new int[] {9,6,3},	new int[] {3,3,3}),
		SLT		("slt"  ,1,	new ArgType[] {ArgType.REG, ArgType.REG,ArgType.REG},		new int[] {9,6,3},	new int[] {3,3,3}),
		AND 	("and"  ,1,	new ArgType[] {ArgType.REG, ArgType.REG,ArgType.REG},		new int[] {9,6,3},	new int[] {3,3,3}),
		CAS		("cas"  ,0,	new ArgType[] {ArgType.REG, ArgType.REG,ArgType.REG},		new int[] {9,6,3},	new int[] {3,3,3}),
		CASL	("casl" ,1,	new ArgType[] {ArgType.REG, ArgType.REG,ArgType.REG},		new int[] {9,6,3},	new int[] {3,3,3}),  
		JR		("jr"   ,0, new ArgType[] {ArgType.REG, ArgType.REG,ArgType.REG},		new int[] {9,6,3},	new int[] {3,3,3}),

		
		
		// the i-type instruction 
		ADDi	("addi",0,	new ArgType[] {ArgType.REG,ArgType.REG, ArgType.IMM },		new int[] {9,6,0},	new int[] {3,3,6} ),
		SUBi	("sub " ,0,	new ArgType[] {ArgType.REG,ArgType.REG, ArgType.IMM },		new int[] {9,6,0},	new int[] {3,3,6} ),
		COMPi	("comp",0,	new ArgType[] {ArgType.REG,ArgType.REG, ArgType.IMM },		new int[] {9,6,0},	new int[] {3,3,6} ),		
		SLTi	("slti",0,  new ArgType[] {ArgType.REG,ArgType.REG, ArgType.IMM },		new int[] {9,6,0},	new int[] {3,3,6} ),
		LWi	    ("lw",0,  new ArgType[] {ArgType.REG,ArgType.REG, ArgType.IMM },		new int[] {9,6,0},	new int[] {3,3,6} ),
		LLBi	("llb",0,  new ArgType[] {ArgType.REG,ArgType.REG, ArgType.IMM },		new int[] {9,6,0},	new int[] {3,3,6} ),
		SWi 	("sw",0,  new ArgType[] {ArgType.REG,ArgType.REG, ArgType.IMM },		new int[] {9,6,0},	new int[] {3,3,6} ),
		ANDi	("andi",0,	new ArgType[] {ArgType.REG,ArgType.REG, ArgType.IMM },		new int[] {9,6,0},	new int[] {3,3,6} ),
		BEQ		("beq" ,2,	new ArgType[] {ArgType.REG,ArgType.REG, ArgType.IMM },		new int[] {9,6,0},	new int[] {3,3,6} ),
		BNE		("bne" ,2, new ArgType[] {ArgType.REG,ArgType.REG, ArgType.IMM },		new int[] {9,6,0},	new int[] {3,3,6} ),


		// the j-type instruction 
		
		J	("j",	2,	new ArgType[] {ArgType.UADD},						new int[] {0},		new int[] {12}),
		JAL	("jal",	2,	new ArgType[] {ArgType.UADD},						new int[] {0},		new int[] {12}),
		LUI	("lui",	2,	new ArgType[] {ArgType.UADD},						new int[] {0},		new int[] {12}),


//		NEGr	("neg",	1,	new ArgType[] {ArgType.REG},						new int[] {0},		new int[] {4}),

		// Logic

		ORi	("or",	0,	new ArgType[] {ArgType.REG, ArgType.IMM},		new int[] {8, 0},	new int[] {4, 8}),
		ORr	("or",	1,	new ArgType[] {ArgType.REG, ArgType.REG},		new int[] {8, 0},	new int[] {4, 4}),

		XORi	("xor",	0,	new ArgType[] {ArgType.REG, ArgType.IMM},		new int[] {8, 0},	new int[] {4, 8}),
		XORr	("xor",	1,	new ArgType[] {ArgType.REG, ArgType.REG},		new int[] {8, 0},	new int[] {4, 4}),

//		ROTi	("rot",	0,	new ArgType[] {ArgType.REG, ArgType.IMM},		new int[] {8, 0},	new int[] {4, 8}),
//		ROTr	("rot",	1,	new ArgType[] {ArgType.REG, ArgType.REG},		new int[] {8, 0},	new int[] {4, 4}),

		SHLi	("shl",	0,	new ArgType[] {ArgType.REG, ArgType.IMM},		new int[] {8, 0},	new int[] {4, 8}),
		SHLr	("shl",	1,	new ArgType[] {ArgType.REG, ArgType.REG},		new int[] {8, 0},	new int[] {4, 4}),

		SHRi	("shr",	0,	new ArgType[] {ArgType.REG, ArgType.IMM},		new int[] {8, 0},	new int[] {4, 8}),
		SHRr	("shr",	1,	new ArgType[] {ArgType.REG, ArgType.REG},		new int[] {8, 0},	new int[] {4, 4}),

		// Moves
		MOVi	("mov",	0,	new ArgType[] {ArgType.REG, ArgType.IMM},		new int[] {8, 0},	new int[] {4, 8}),
		MOVr	("mov",	1,	new ArgType[] {ArgType.REG, ArgType.REG},		new int[] {8, 0},	new int[] {4, 4}),

		// Data Memory
		LDi	("ld",	0,	new ArgType[] {ArgType.REG, ArgType.IMM},		new int[] {8, 0},	new int[] {4, 8}),
		LDr	("ld",	1,	new ArgType[] {ArgType.REG, ArgType.REG},		new int[] {8, 0},	new int[] {4, 4}),

		STi	("st",	0,	new ArgType[] {ArgType.REG, ArgType.IMM},		new int[] {8, 0},	new int[] {4, 8}),
		STa	("st",	0,	new ArgType[] {ArgType.IMM, ArgType.REG},		new int[] {0, 8},	new int[] {8, 4}),
		STr	("st",	1,	new ArgType[] {ArgType.REG, ArgType.REG},		new int[] {8, 0},	new int[] {4, 4});



		// Jumps/Branches
		

		public String opCode;
		public int format;
		public ArgType[] argTypeArray;
		public int[] argPosArray;
		public int[] argLenArray;

		InstructionType(String opCode, int format, ArgType[] argTypeArray, int[] argPosArray, int[] argLenArray)
		{
			this.opCode = opCode;
			this.format = format;
			this.argTypeArray = argTypeArray;
			this.argPosArray = argPosArray;
			this.argLenArray = argLenArray;
		}
	}

	public InstructionType insnType;
	public int encoding;
	public int[] argValArray;
	public int srcLine;
	public String srcString;

	public Instruction(InstructionType insnType, int encoding, int[] argValArray, int srcLine, String srcString)
	{
		this.insnType = insnType;
		this.encoding = encoding;
		this.argValArray = argValArray;
		this.srcLine = srcLine;
		this.srcString = srcString;
	}

	public String toString()
	{
		return srcString + " (" + StringUtils.intToBinaryString(encoding, 16, true) + ") (" + Integer.toString(srcLine) + ")";
	}

	public static InstructionType getType(int opCode)
	{
		return InstructionType.values()[opCode];
	}
}
