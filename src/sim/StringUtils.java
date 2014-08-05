// Microprocessor Simulator
// A.Greensted - University of York
// ajg112@ohm.york.ac.uk
// November 2008

package sim;

public class StringUtils
{
	public static String intToBinaryString(int val, int numBits, boolean pad)
	{
		int numChars = (pad) ? (numBits + ((numBits+1) / 4)) : numBits;
		char[] buffer = new char[numChars];
		int c=numChars-1;

		for (int b=0 ; b<numBits ; b++)
		{
			buffer[c--] = ((val & 0x1) == 1) ? '1' : '0';
			val >>= 1;

			if (pad && (b%4)==3) buffer[c--] = '_';
		}

		return new String(buffer);
	}
}
