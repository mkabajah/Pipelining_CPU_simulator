package sim;


public class Encoding {
	public enum opcode {
		add, sub, mul, div, and, or, xor, shl, slr, sar, ceq, cgr, clt, addc, subc, 
		mulc, divc, andc, orc, xorc, shlc, slrc, sarc, ceqc, cgrc, cltc, ld, st, beq, bne, 
		slt, cas, casl, jr, andi, addi, slti, lw, llb, sw, j, jal, lui, in, out;
	}

	public static int func;

	public static int parse(String s) {
		String[] tokenArray = s.split(" ");
		for (int i = 0; i < tokenArray.length; i++) {

		}
		int n = tokenArray.length;
		int[] intTokens = new int[n];

		for (int i = 0; i < n; i++) {
			intTokens[i] = toInt(tokenArray[i],n);

			System.out.println(intTokens[i]);

		}

		// now take integer tokens and form instruction in machine format

		return buildIns(intTokens);

	}

	/*
	 * Helper method to convert string tokens to integer representations
	 * Example: Register value r3 should have integer value 3, regardless if it
	 * is in argument position C, A, or B.
	 */
	private static int toInt(String token ,int n) {
		
		if (token.startsWith("r")) { /* Pattern match register */

			int register = Integer.parseInt(token.substring(1));

			if (register > 0 & register < 8) {
				return register;
			}

		} else if (token.startsWith("d") | token.startsWith("x")) { /*
																	 * Pattern
																	 * match
																	 * literal
																	 */

			int literal = Integer.parseInt(token.substring(1));
			return literal;

		} else {/* Pattern match opcode */

			opcode op = opcode.valueOf(token);
			if (true) {
			}
			switch (op) {

			case and:
				func = 0;
				return 0;
			case add:
				func = 1;
				return 0;
			case sub:
				func = 2;
				return 0;
			case slt:
				func = 3;
				return 0;
			case cas:
				func = 4;
				return 0;
			case casl:
				func = 5;
				return 0;
			case jr:
				func = 7;
				return 0;

			case andi:
				return 8;
			case addi:
				return 4;
			case slti:
				return 5;
			case lw:
				return 6;
			case llb:
				return 14;
			case sw:
				return 7;
			case beq:
				return 10;
			case bne:
				return 11;

			case j:
				return 12;
			case jal:
				return 13;
			case lui:
				return 15;

				/*
				 * case mul : return 3; case div : return 4; case or : return 6;
				 * case xor: return 7; case shl: return 8; case slr: return 9;
				 * case sar: return 10; case ceq: return 11; case cgr: return
				 * 12; case clt: return 13; case subc : return 18; case mulc :
				 * return 19; case divc : return 20; case orc : return 22; case
				 * xorc : return 23; case shlc : return 24; case slrc : return
				 * 25; case sarc : return 26; case ceqc : return 27; case cgrc :
				 * return 28; case cltc : return 29; case ld : return 16; case
				 * st : return 31;
				 */
			case in:
				return 49;
			case out:
				return 50;
			default:
				return 0; // probably should throw exception
			} // end switch
} // end else
		return -1;	
	}// end method toInt

	/*
	 * Helper method to build instruction in machine readable format from the
	 * integer array constructed using the toInt helper method.
	 */
	private static int buildIns(int[] intTokens) {

		int instruction = 0;
		int token = 0;
		
int opcode;
		// intTokens[0] is always opcode
		opcode = intTokens[0];
		System.out.println(instruction);

		instruction = instruction | (opcode << 12); // opcode 15 thru 11, move
													// bits 3-0. 15-4 = 11

		
		// starting with r-type instruction 
			if (opcode==0){
				if(intTokens.length-1 < 4){
				token = intTokens[1];
				instruction = instruction | (token << 3);
				token = intTokens[2];
				instruction = instruction | (token << 9);
				token =intTokens[3];
				instruction = instruction | (token << 6);
				token = func;
				instruction = instruction | (token << 0);
				
				System.out.println(StringUtils.intToBinaryString(instruction, 16, true));
				}
				
				else {
				System.out.println("bad format in the R-type instruction ");
				}
			}
			
			
			// the opcode for the I-type instruction 
			if(opcode < 12 & opcode ==14) {
				if(intTokens.length-1 == 4){
					
					token = intTokens[1];
					instruction = instruction | (token << 9);
					token = intTokens[2];
					instruction = instruction | (token << 6);
					if(intTokens[3]<64){
					token =intTokens[3];
					instruction = instruction | (token << 0);
					
					//System.out.println(StringUtils.intToBinaryString(instruction, 16, true));

					}
					else{
						
				System.out.println("bad format in the I-type instruction ");

					}
					
				}
				
			}
			
			
			// the opcode of the j-type instruction 
			
			if (opcode >= 12 & opcode < 15){
				
				
				if(intTokens[1]>=0 & intTokens[1]< 4096){
					token=intTokens[1];
					instruction = instruction | (( token << 12 ) >>> 12);
					
					System.out.println(" the J-type instruction ");

					System.out.println(StringUtils.intToBinaryString(instruction, 16, true));

				}
				
				else {
					
					System.out.println("bad format in the J-type instruction ");

				}
			}
			
			
			
			// check to see if operation is a break, in, our out, which only has two
			// elements
			if (opcode >= 48 && opcode <= 50) { // instruction is of
															// class Misc.

				return instruction;

			}

			
			

		return instruction;

	} // end method buildIns

	// end class Parser

	public static void main(String[] args) {

		int x = 0;

		x = Encoding.parse("andi r1 r2 d45");

		System.out.println(x);
	}}