// Microprocessor Simulator
//mohammad kabajah 
//1081096	
//Dr.Aziz Qaroush 
package sim;

public class Processor
{
	public enum AluOp {ADD, ADDC , SUB , SUBC , AND , XOR , OR, OPA, OPB, ROL, SHL, SHR, max, min, comp, pc, llb, ADDjal} //operation in alu

	// Options
	public boolean enableRegForwarding = true;
	public boolean enableJumpFlush = true; 
	public boolean enableLoadStall = true;

	// Register File and Data Memory
	public int[] registerFile;
	public int[] dataMemory;

	public long clockCount; // clock in the system 

	public int feInsnAdd; // in fetch 
	public int deInsnAdd; // in decoder 
	public int exInsnAdd; // in execution
	public int maInsnAdd; // memory 
	public int wbInsnAdd; // write back

	public class Signals implements Cloneable
	{
		// FETCH
		// ------

		// reg fetch stage 
		public int fe_pc;  // the current pc

		// comb
		public int fe_pcPlus;  // the pc+1

		// DECODE
		// ------

		// reg decoder stage 
		public int de_pc;  // the pc in decoder stage 
		public int de_feFlush = 1; // fetch flush 
		public int de_opCode;  // decoder opcode
		public int de_rAddA;  //decoder add 1
		public int de_rAddB; // decoder add b
		public int de_rAddc; // decoder address destenation register 
		public int de_imm; // decoder immediate
		public int de_insn; // decoder instruction
		
		// EXECUTE
		// -------

		// reg in execution stage 
		public int ex_pc;  // pc in the execution stage
		public int ex_deFlush = 1; // decoder flush
		public int ex_feFlush = 1; // fetch flush 
		public int ex_opCode; // execution opcode 
		public int ex_rAddA;  //execution address a
		public int ex_rAddB;  // execution address b
		public int ex_rAddc; // execution address destenation register 

		public int ex_rDatA;  //execution data a
		public int ex_rDatB;  // execution data b
		public int ex_imm;    // execution immediate

		// comb
		public int ex_opASE;  // operand a extend
		public int ex_pcRel;   //  
		public int ex_pcJmp;   // execution pc for jump 
		public int ex_nPC;	//  execution now pc 
		public int ex_opA;   // execution operand a
		public int ex_opB;   // execution operand b
		public int ex_aluOut; // execution result from alu 
		public int ex_mAdd; //excution memory address 

		public int ex_flagZ;  // execution flag zero 
		public int ex_flagC;  // execution flag carry 

		// MEMORY ACCESS
		// -------------

		// reg
		public int ma_wrEn;			// Register File Write Enable
		public int ma_wrAdd;         // write address 
		public int ma_muxDS;        // data select (from alu or memory)
		public int ma_aluOut;       // result from alu 
		public int ma_mAdd;       //memory addres 
		public int ma_mEn; 		// memory enable 

		public int ma_flagZ;  // zero flag
		public int ma_flagC;  // carry flag 

		// WRITEBACK
		// ---------

		// write in write back stage
		public int wb_wrEn;			// Register File Write Enable
		public int wb_wrAdd;        // write adress 
		public int wb_muxDS;		//data select mux
		public int wb_aluOut;		// alu out data
		public int wb_mD;		// memory data 

		// comb
		public int wb_data;      // writr back data 

		// MEMORIES
		// --------
		public int im_dOut;	
		public int rf_dOutA;
		public int rf_dOutB;
		public int dm_dOut;

		public Object clone()
		{
			try
			{
				return super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				return null;
			}
		}
	}

	public class Controls
	{
		public int pcEn;     //pc enable 
		public int feFlush;  // fetch flush 
		public int deFlush;  // fetch decoder 
		public int fedeEn;  // fetch-decoder enable
		public int deexEn;	// decoder-execution enable 
		public int wrEn;   // write enable (register)
		public int memEn;  // write enable memory 
		public AluOp aluOp;  // alu operation 
		public int muxFA;  //forward a
		public int muxFB;	// forward b
		public int muxAS;  // address select 
		public int muxJS;  // jump select 
		public int muxDS;  // data select 
		public int muxCS;  // counter select 
	}

	public Instruction[] program;
	public Signals signals;
	public Controls controls;


	public Processor(Instruction[] program)
	{
		this.program = program;
		reset();
	}

	public void reset()
	{
		signals = new Signals();
		controls = new Controls();

		registerFile = new int[8];
		dataMemory = new int[256];

		wbInsnAdd = 0;
		maInsnAdd = 0;
		exInsnAdd = 0;
		deInsnAdd = 0;
		feInsnAdd = 0;

		updateControls();
		updateCombinatorial();

		clockCount = 0;
	}
	public void clock()
	{
		clockReg();

		wbInsnAdd = maInsnAdd;
		maInsnAdd = exInsnAdd;
		if (controls.deexEn == 1) exInsnAdd = deInsnAdd;
		if (controls.fedeEn == 1) deInsnAdd = feInsnAdd;
		feInsnAdd = signals.fe_pc;
		
		updateControls();
		updateCombinatorial();

		clockCount ++;
	}

	private void clockReg()
	{
		Signals oldSignals = signals;
		signals = (Signals) oldSignals.clone();		

		// Fetch
		if (controls.pcEn == 1)
		{
			signals.fe_pc			= oldSignals.ex_nPC;
		}

		// Decode
		if (controls.fedeEn == 1)
		{
			signals.de_pc			= oldSignals.fe_pc;
			signals.de_feFlush	= controls.feFlush;

			// Instruction Memory Latch
			signals.de_insn = oldSignals.im_dOut;
		}

		// Execute
		if (controls.deexEn == 1)
		{
			signals.ex_pc			= oldSignals.de_pc;
			signals.ex_deFlush	= controls.deFlush;
			signals.ex_feFlush	= oldSignals.de_feFlush;
			signals.ex_opCode		= oldSignals.de_opCode;
			signals.ex_rAddA		= oldSignals.de_rAddA;
			signals.ex_rAddB		= oldSignals.de_rAddB;
			signals.ex_rAddc		= oldSignals.de_rAddc;
			
			signals.ex_imm			= oldSignals.de_imm;

			// Register File Latch
			signals.ex_rDatA = oldSignals.rf_dOutA;
			signals.ex_rDatB = oldSignals.rf_dOutB;
		}

		// Memory Access
		signals.ma_wrEn		= controls.wrEn;
		signals.ma_wrAdd		= oldSignals.ex_rAddc;
		signals.ma_muxDS		= controls.muxDS;
		signals.ma_aluOut		= oldSignals.ex_aluOut;
		signals.ma_mAdd		= oldSignals.ex_mAdd;
		signals.ma_mEn			= controls.memEn;

		signals.ma_flagZ		= oldSignals.ex_flagZ;
		signals.ma_flagC		= oldSignals.ex_flagC;

		// Writeback
		signals.wb_wrEn		= oldSignals.ma_wrEn;
		signals.wb_wrAdd		= oldSignals.ma_wrAdd;
		signals.wb_muxDS		= oldSignals.ma_muxDS;
		signals.wb_aluOut		= oldSignals.ma_aluOut;
		signals.wb_mD			= oldSignals.dm_dOut;

		// Data Memory Write
		if (oldSignals.ma_mEn == 1) dataMemory[oldSignals.ma_mAdd] = oldSignals.ma_aluOut;

		// Register File Write
		if (oldSignals.wb_wrEn == 1) registerFile[oldSignals.wb_wrAdd] = oldSignals.wb_data;
	}

	private void updateControls()
	{
		// Defaults
		controls.aluOp = AluOp.OPA;

		controls.pcEn = 1;
		controls.fedeEn = 1;
		controls.deexEn = 1;
		controls.wrEn = 0;
		controls.memEn = 0;

		controls.muxCS = 0;
		controls.muxJS = 0;
		controls.muxFA = 0;
		controls.muxFB = 0;
		controls.muxAS = 0;
		controls.muxDS = 0;

		controls.feFlush = 0;
		controls.deFlush = 0;

		// If instruction has been flushed, use defaults
		if (signals.ex_deFlush == 1 || signals.ex_feFlush == 1) return;

		switch (Instruction.getType(signals.ex_opCode))
		{
		
		
		///////////////////////////////the R_type instruction ///////////////////////////////////////////
		///////////////////////////////the R_type instruction ///////////////////////////////////////////
		///////////////////////////////the R_type instruction ///////////////////////////////////////////
		///////////////////////////////the R_type instruction ///////////////////////////////////////////

			case ADD :
			controls.aluOp = AluOp.ADD;
			controls.wrEn = 1;
				break;
			
			case SUB:
				controls.aluOp = AluOp.SUB;
				controls.wrEn = 1;
				break;
			
				
			
			case SLT :
				controls.aluOp = AluOp.comp;
				controls.wrEn = 1;
				
				break;
			
			
			case AND:
				controls.aluOp = AluOp.AND;
				controls.wrEn = 1;
				break;	
				
			case CAS:
				controls.aluOp = AluOp.max;
				controls.wrEn = 1;
				break;
				
			case CASL:
				controls.aluOp = AluOp.max;
				controls.muxAS = 1; //address select
				controls.wrEn = 1;	//write enable on register 
				controls.muxDS = 1; // Data select
				break;
				
			case JR:

				controls.aluOp = AluOp.OPA;
				controls.wrEn = 1;	//write enable on register 
				controls.feFlush = 1; //????
				controls.deFlush = 1;//????
	     		break;

	     		
	     		
///////////////////////////////the I_type instruction ///////////////////////////////////////////	
///////////////////////////////the I_type instruction ///////////////////////////////////////////	     		
///////////////////////////////the I_type instruction ///////////////////////////////////////////	     		

	     		
			case ADDi :
				

				controls.aluOp = AluOp.ADD;
				controls.wrEn = 1;
				controls.muxFB = 1;
				break;
			
				
			case SLTi :
				System.out.println("kbooj");

				controls.aluOp = AluOp.comp;
				controls.wrEn = 1;
				controls.muxFB = 1;

				break;
			
			case LWi :
				controls.aluOp = AluOp.ADD;
				controls.muxAS = 1;
				controls.muxFB = 1;
				controls.wrEn = 1;
				controls.muxDS = 1;
				
				break;
				
			case LLBi:
				controls.aluOp = AluOp.llb;
				controls.muxAS = 1;
				controls.muxFB = 1;
				controls.wrEn = 1;
				controls.muxDS = 1;
				
				break;
				
			case SWi:
				controls.aluOp = AluOp.ADD;
				controls.muxFB = 1;
				controls.memEn = 1;
				break;
				
				
			case ANDi :
				controls.aluOp = AluOp.AND;
				controls.wrEn = 1;
				controls.muxFB = 1;
				break;
				
			
			case BEQ :
				if (signals.ma_flagZ == 1)
				{
					controls.muxJS = 1;
					controls.muxCS = 2;
					controls.feFlush = 1;
					controls.deFlush = 1;
				}
				break;

			case BNE :
				if (signals.ma_flagZ == 0)
				{
					controls.muxJS = 1;
					controls.muxCS = 2;
					controls.feFlush = 1;
					controls.deFlush = 1;
				}
				break;
				
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



///////////////////////////////the j_type instruction ///////////////////////////////////////////	
///////////////////////////////the j_type instruction ///////////////////////////////////////////	     		
///////////////////////////////the j_type instruction ///////////////////////////////////////////	     		
				
				
				
			case J :
				
				controls.muxJS = 1;
				
				controls.muxCS = 2;
				controls.feFlush = 1;
				controls.deFlush = 1;

				break;

				
			case JAL :
				System.out.println("fff1");

				signals.de_rAddc=7;
				controls.muxJS = 1;
				controls.muxCS = 2;
				controls.aluOp = AluOp.ADDjal;
				controls.wrEn = 1;


				break;
				
			case LUI:
				controls.aluOp = AluOp.SHL;
				signals.de_rAddc=1;
				controls.wrEn = 1;

				break;
/*
			case ORi :
				controls.aluOp = AluOp.OR;
				controls.wrEn = 1;
				controls.muxFB = 1;
				break;
			case ORr :
				controls.aluOp = AluOp.OR;
				controls.wrEn = 1;
				break;

			case XORi :
				controls.aluOp = AluOp.XOR;
				controls.wrEn = 1;
				controls.muxFB = 1;
				break;
			case XORr :
				controls.aluOp = AluOp.XOR;
				controls.wrEn = 1;
				break;

//			case ROTi :
//				controls.wrEn = 1;
//				controls.muxFB = 1;
//				break;
//			case ROTr :
//				controls.wrEn = 1;
//				break;

			case SHLi :
				controls.aluOp = AluOp.SHL;
				controls.wrEn = 1;
				controls.muxFB = 1;
				break;
			case SHLr :
				controls.aluOp = AluOp.SHL;
				controls.wrEn = 1;
				break;

			case SHRi :
				controls.aluOp = AluOp.SHR;
				controls.wrEn = 1;
				controls.muxFB = 1;
				break;
			case SHRr :
				controls.aluOp = AluOp.SHR;
				controls.wrEn = 1;
				break;

			case MOVi :
				controls.aluOp = AluOp.OPA;
				controls.wrEn = 1;
				controls.muxFA = 1;
				break;
			case MOVr :
				controls.aluOp = AluOp.OPB;
				controls.wrEn = 1;
				break;

			// LOAD / STORE
			// ------------
			case LDi :
				controls.aluOp = AluOp.OPB;
				controls.muxAS = 1;
				controls.muxFB = 1;
				controls.wrEn = 1;
				controls.muxDS = 1;
				break;

			case LDr :
				controls.aluOp = AluOp.OPB;
				controls.muxAS = 1;
				controls.wrEn = 1;
				controls.muxDS = 1;
				break;


			case STi :
				controls.aluOp = AluOp.OPB;
				controls.muxFB = 1;
				controls.memEn = 1;
				break;

			case STa :
				controls.aluOp = AluOp.OPA;
				controls.muxFB = 1;
				controls.muxAS = 1;
				controls.memEn = 1;
				break;

			case STr :
				controls.aluOp = AluOp.OPB;
				controls.memEn = 1;
				break;

			// JUMPS / BRANCHES
			// ----------------
			
//			case JMPRi :
//				break;

//			case JMPRr :
//				break;

*/
			
		default : System.err.println("Processor.updateCombinatorial: Invalid controls.ex_AluOp value (" + signals.ex_opCode + ")");

		}

		
		
		
		
		
		// If Jump Flush is disabled, clear flush controls
		if (enableJumpFlush == false)
		{
			controls.feFlush = 0;
			controls.deFlush = 0;
		}

		
		
		
		// Forwarding
		if (enableRegForwarding && controls.muxFA == 0) // if muxFa 
		{
			if (signals.ex_rAddA == signals.ma_wrAdd && signals.ma_wrEn == 1) controls.muxFA = 3;// if the data needed in execution foundd in the mem stage make forward 
			else if (signals.ex_rAddA == signals.wb_wrAdd && signals.wb_wrEn == 1) controls.muxFA = 2;// if the data needed in execution foundd in the WB stage make forward 

		}

		if (enableRegForwarding && controls.muxFB == 0)
		{
			if (signals.ex_rAddB == signals.ma_wrAdd && signals.ma_wrEn == 1) controls.muxFB = 3;// if the data needed in execution foundd in the mem stage make forward 

			else if (signals.ex_rAddB == signals.wb_wrAdd && signals.wb_wrEn == 1) controls.muxFB = 2;// if the data needed in execution foundd in the WB stage make forward 

		}

		// Load Stall
		if (enableLoadStall && signals.ma_muxDS == 1 && signals.ma_wrEn == 1)
		{
			controls.pcEn = 0;
			controls.fedeEn = 0;
			controls.deexEn = 0;
			controls.wrEn = 0;
		}
	}

	private void updateCombinatorial()
	{
		signals.fe_pcPlus = (signals.fe_pc + 1) & 0xFFF;

		signals.de_opCode		= (signals.de_insn >> 12) & 0x0F;
		signals.de_rAddA		= (signals.de_insn >> 6) & 0x7;
		signals.de_rAddB		= (signals.de_insn >> 3)& 0x7;
		signals.de_rAddc		= (signals.de_insn >> 9)& 0x7; // Destination register 

		signals.de_imm			= signals.de_insn & 0x0FFF;   //full immediATE

		// Mux DS
		if (signals.wb_muxDS ==0 )	{signals.wb_data = signals.wb_aluOut;}
		else {	signals.wb_data = signals.wb_mD;}
		

		// Mux FA
		if (controls.muxFA==0 ){signals.ex_opA = signals.ex_rDatA;}
		else if(controls.muxFA == 1 ){	signals.ex_opB = (signals.ex_imm & 0x3F);}
		else if(controls.muxFA == 2) {signals.ex_opB = signals.wb_data;	}
		else if (controls.muxFA == 3) {signals.ex_opB = signals.ma_aluOut;		}
		

		// Mux FB
		if (controls.muxFB==0 ){	signals.ex_opB = signals.ex_rDatB;	}
		else if(controls.muxFB == 1 ){ signals.ex_opA = (signals.ex_imm & 0x3F);}
		else if(controls.muxFB == 2) {signals.ex_opA = signals.wb_data;	 }
		else if(controls.muxFB == 3){signals.ex_opA = signals.ma_aluOut;	}
		
		
		// Mux AS
		switch (controls.muxAS)
		{
			case 0 :	signals.ex_mAdd = signals.ex_opA; break;
			case 1 :	signals.ex_mAdd = signals.ex_opB; break;
			default : System.err.println("Processor.updateCombinatorial: Invalid controls.muxAS value (" + controls.muxAS + ")");
		}

		// Sign Extend
		signals.ex_opASE = signals.ex_opA;
		if ((signals.ex_opA & 0x40) == 0x40) signals.ex_opASE |= 0xF00;

		// PC Add
		signals.ex_pcRel = (signals.ex_opASE + signals.ex_pc) & 0xFFFF;

		// Mux JS
		if (controls.muxJS==0 ){signals.ex_pcJmp = signals.ex_pcRel ;}
		else if(controls.muxJS==1 ){ signals.ex_pcJmp  = signals.ex_imm; }
		

		// ALU
		boolean setFlags = false;
		switch (controls.aluOp)
		{
			case ADD :	signals.ex_aluOut = signals.ex_opA + signals.ex_opB;
							setFlags = true; break;

							
			case SUB :	signals.ex_aluOut = signals.ex_opA - signals.ex_opB;
							setFlags = true; break;

							
			case AND :	signals.ex_aluOut = signals.ex_opA & signals.ex_opB;
							setFlags = true; break;
							
			case XOR :	signals.ex_aluOut = signals.ex_opA ^ signals.ex_opB;
							setFlags = true; break;
							
			case OR :	signals.ex_aluOut = signals.ex_opA | signals.ex_opB;
							setFlags = true; break;
							
			case max: signals.ex_aluOut = (signals.ex_opA > signals.ex_opB) ? signals.ex_opA :signals.ex_opB;
							setFlags = true; break;
							
			case min : signals.ex_aluOut = (signals.ex_opA < signals.ex_opB) ? signals.ex_opA :signals.ex_opB;
							setFlags = true; break;
							
			
			case OPA :	signals.ex_aluOut = signals.ex_opA;
							setFlags = true; break;
							
			case OPB :	signals.ex_aluOut = signals.ex_opB;
							setFlags = true; break;
			
			case pc :  signals.fe_pc=signals.ex_opA;
							setFlags = true; break;

			case llb:  signals.ex_aluOut = (signals.ex_opA + signals.ex_opB) & 0x00FF;
							setFlags = true; break;
					
			case ADDjal:signals.ex_aluOut = signals.fe_pc+2;
							setFlags = true; break;

			case comp: signals.ex_aluOut =(signals.ex_opA < signals.ex_opB) ? 1 : 0;
							setFlags = true; break;

			case SHL :	signals.ex_aluOut =signals.de_imm<< 4;
							setFlags = true; break;
			case SHR :	signals.ex_aluOut = signals.ex_opA >> signals.ex_opB;
							setFlags = true; break;

			default : System.err.println("Processor.updateCombinatorial: Invalid controls.ex_AluOp value (" + controls.aluOp + ")");
		}

		// Flags
		if (setFlags)
		{
			signals.ex_flagZ = ((signals.ex_aluOut & 0xFF) == 0) ? 1 : 0;
			signals.ex_flagC = ((signals.ex_aluOut & 0x100) == 0x100) ? 1 : 0;
		}

		// Mask ALU Output
		signals.ex_aluOut &= 0xFFFF;

		// Mux CS
		switch (controls.muxCS)
		{
			case 0 :	signals.ex_nPC = signals.fe_pcPlus;	break;
//			case 1 :	signals.ex_nPC = ;		break;
			case 2 :	signals.ex_nPC = signals.ex_pcJmp;	break;
			default : System.err.println("Processor.updateCombinatorial: Invalid controls.muxCS value (" + controls.muxCS + ")");
		}

		// Instruction Memory (Internal Read)
		signals.im_dOut = (signals.fe_pc >= program.length) ? 0 : program[signals.fe_pc].encoding;

		// Data Memory (Internal Read)
		signals.dm_dOut = dataMemory[signals.ma_aluOut];

		// Register File
		if (signals.wb_wrAdd == signals.de_rAddA && signals.wb_wrEn == 1) signals.rf_dOutA = signals.wb_data;
		else signals.rf_dOutA = registerFile[signals.de_rAddA];

		if (signals.wb_wrAdd == signals.de_rAddB && signals.wb_wrEn == 1) signals.rf_dOutB = signals.wb_data;
		else signals.rf_dOutB = registerFile[signals.de_rAddB];
	}
}
