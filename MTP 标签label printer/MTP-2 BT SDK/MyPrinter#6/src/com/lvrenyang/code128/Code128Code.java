package com.lvrenyang.code128;

public class Code128Code {

	private static final int cSHIFT = 98;
	private static final int cCODEA = 101;
	private static final int cCODEB = 100;

	private static final int cSTARTA = 103;
	private static final int cSTARTB = 104;
	private static final int cSTOP = 106;

	// / <summary>
	// / Get the Code128 code value(s) to represent an ASCII character, with
	// / optional look-ahead for length optimization
	// / </summary>
	// / <param name="CharAscii">The ASCII value of the character to
	// translate</param>
	// / <param name="LookAheadAscii">The next character in sequence (or -1 if
	// none)</param>
	// / <param name="CurrCodeSet">The current codeset, that the returned codes
	// need to follow;
	// / if the returned codes change that, then this value will be changed to
	// reflect it</param>
	// / <returns>An array of integers representing the codes that need to be
	// output to produce the
	// / given character</returns>
	public static int[] CodesForChar(int CharAscii, int LookAheadAscii,
			CodeSet CurrCodeSet) {
		int[] result;
		int shifter = -1;

		if (!CharCompatibleWithCodeset(CharAscii, CurrCodeSet)) {
			// if we have a lookahead character AND if the next character is
			// ALSO not compatible
			if ((LookAheadAscii != -1)
					&& !CharCompatibleWithCodeset(LookAheadAscii, CurrCodeSet)) {
				// we need to switch code sets
				switch (CurrCodeSet) {
				case CodeA:
					shifter = cCODEB;
					CurrCodeSet = CodeSet.CodeB;
					break;
				case CodeB:
					shifter = cCODEA;
					CurrCodeSet = CodeSet.CodeA;
					break;
				}
			} else {
				// no need to switch code sets, a temporary SHIFT will suffice
				shifter = cSHIFT;
			}
		}

		if (shifter != -1) {
			result = new int[2];
			result[0] = shifter;
			result[1] = CodeValueForChar(CharAscii);
		} else {
			result = new int[1];
			result[0] = CodeValueForChar(CharAscii);
		}

		return result;
	}

	// / <summary>
	// / Tells us which codesets a given character value is allowed in
	// / </summary>
	// / <param name="CharAscii">ASCII value of character to look at</param>
	// / <returns>Which codeset(s) can be used to represent this
	// character</returns>
	public static CodeSetAllowed CodesetAllowedForChar(int CharAscii) {
		if (CharAscii >= 32 && CharAscii <= 95) {
			return CodeSetAllowed.CodeAorB;
		} else {
			return (CharAscii < 32) ? CodeSetAllowed.CodeA
					: CodeSetAllowed.CodeB;
		}
	}

	// / <summary>
	// / Determine if a character can be represented in a given codeset
	// / </summary>
	// / <param name="CharAscii">character to check for</param>
	// / <param name="currcs">codeset context to test</param>
	// / <returns>true if the codeset contains a representation for the ASCII
	// character</returns>
	public static boolean CharCompatibleWithCodeset(int CharAscii,
			CodeSet currcs) {
		CodeSetAllowed csa = CodesetAllowedForChar(CharAscii);
		return csa == CodeSetAllowed.CodeAorB
				|| (csa == CodeSetAllowed.CodeA && currcs == CodeSet.CodeA)
				|| (csa == CodeSetAllowed.CodeB && currcs == CodeSet.CodeB);
	}

	// / <summary>
	// / Gets the integer code128 code value for a character (assuming the
	// appropriate code set)
	// / </summary>
	// / <param name="CharAscii">character to convert</param>
	// / <returns>code128 symbol value for the character</returns>
	public static int CodeValueForChar(int CharAscii) {
		return (CharAscii >= 32) ? CharAscii - 32 : CharAscii + 64;
	}

	// / <summary>
	// / Return the appropriate START code depending on the codeset we want to
	// be in
	// / </summary>
	// / <param name="cs">The codeset you want to start in</param>
	// / <returns>The code128 code to start a barcode in that codeset</returns>
	public static int StartCodeForCodeSet(CodeSet cs) {
		return cs == CodeSet.CodeA ? cSTARTA : cSTARTB;
	}

	// / <summary>
	// / Return the Code128 stop code
	// / </summary>
	// / <returns>the stop code</returns>
	public static int StopCode() {
		return cSTOP;
	}

	// / <summary>
	// / Indicates which code sets can represent a character -- CodeA, CodeB, or
	// either
	// / </summary>
	public enum CodeSetAllowed {
		CodeA, CodeB, CodeAorB
	}

}
