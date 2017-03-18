// Microprocessor Simulator

package sim;

import java.io.*;

public class Parser
{
	public static Integer parseRegister(String str)
	{
		if (str.length() != 2) return null;

		char[] chars = str.toCharArray();
		if (chars[0] != 'r') return null;

		int digit = chars[1];

		digit -= 48;
		if (digit < 0 || digit > 9)
		{
			digit -= 7;
			if (digit < 10 || digit > 15)
			{
				digit -= 32;
				if (digit < 10 || digit > 15)
				{
					return null;
				}
			}
		}

		return digit;
	}

	public static Integer parseImmediate(String str)
	{
		char[] chars = str.toCharArray();

		// Get string length
		int length = chars.length;

		// Check length
		if (length < 2)
		{
			return null;
		}

		int number = 0;

		boolean negative = false;
		int offset = 0;

		if (chars[0] == '-') {
			offset ++;
			negative = true;
		}

		switch (chars[offset])
		{
			// Binary
			case 'b':	
				for (int d=(offset+1) ; d<length ; d++)
				{
					if (chars[d] == '_') continue;
					int digit = chars[d] - 48;

					if (digit < 0 || digit > 1)
					{
						return null;
					}

					number <<= 1;
					number += digit;
				}
				break;

			// Octal
			case 'o':
				for (int d=(offset+1) ; d<length ; d++)
				{
					if (chars[d] == '_') continue;
					int digit = chars[d] - 48;

					if (digit < 0 || digit > 7)
					{
						return null;
					}

					number <<= 3;
					number += digit;
				}

				break;

			// Decimal
			case 'd':
				for (int d=(offset+1) ; d<length ; d++)
				{
					if (chars[d] == '_') continue;
					int digit = chars[d] - 48;

					if (digit < 0 || digit > 9)
					{
						return null;
					}

					number *= 10;
					number += digit;
				}
				break;

			// Hexadecimal
			case 'h':
			case 'x':
				for (int d=(offset+1) ; d<length ; d++)
				{
					if (chars[d] == '_') continue;
					int digit = chars[d];

					digit -= 48;
					if (digit < 0 || digit > 9)
					{
						digit -= 7;
						if (digit < 10 || digit > 15)
						{
							digit -= 32;
							if (digit < 10 || digit > 15)
							{
								return null;
							}
						}
					}

					number <<= 4;
					number += digit;
				}
				break;

			// Invalid Option
			default:
				return null;
		}

		if (negative) number *= -1;

//		if (number > MAX_NUMBER)
//		{
//			error.explanation = "Literal to large";
//			return null;
//		}

		return new Integer(number);
	}
}
