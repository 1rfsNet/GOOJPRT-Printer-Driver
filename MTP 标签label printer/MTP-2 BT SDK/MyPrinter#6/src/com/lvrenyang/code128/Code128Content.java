package com.lvrenyang.code128;

import java.util.ArrayList;

/// <summary>
/// Represent the set of code values to be output into barcode form
/// </summary>
public class Code128Content {
	private int[] mCodeList;

	// / <summary>
	// / Create content based on a string of ASCII data
	// / </summary>
	// / <param name="AsciiData">the string that should be represented</param>
	public Code128Content(String AsciiData) {
		mCodeList = StringToCode128(AsciiData);
	}

	// / <summary>
	// / Provides the Code128 code values representing the object's string
	// / </summary>
	public int[] Codes() {
		return mCodeList;
	}

	// / <summary>
	// / Transform the string into integers representing the Code128 codes
	// / necessary to represent it
	// / </summary>
	// / <param name="AsciiData">String to be encoded</param>
	// / <returns>Code128 representation</returns>
	private int[] StringToCode128(String AsciiData) {
		// turn the string into ascii byte data
		byte[] asciiBytes = AsciiData.getBytes();

		// decide which codeset to start with
		Code128Code.CodeSetAllowed csa1 = asciiBytes.length > 0 ? Code128Code
				.CodesetAllowedForChar(asciiBytes[0])
				: Code128Code.CodeSetAllowed.CodeAorB;
		Code128Code.CodeSetAllowed csa2 = asciiBytes.length > 0 ? Code128Code
				.CodesetAllowedForChar(asciiBytes[1])
				: Code128Code.CodeSetAllowed.CodeAorB;
		CodeSet currcs = GetBestStartSet(csa1, csa2);

		// set up the beginning of the barcode
		ArrayList<Integer> codes = new ArrayList<Integer>(asciiBytes.length + 3); // assume
																					// no
																					// codeset
																					// changes,
																					// account
																					// for
																					// start,
																					// checksum,
																					// and
																					// stop
		codes.add(Code128Code.StartCodeForCodeSet(currcs));

		// add the codes for each character in the string
		for (int i = 0; i < asciiBytes.length; i++) {
			int thischar = asciiBytes[i];
			int nextchar = asciiBytes.length > (i + 1) ? asciiBytes[i + 1] : -1;

			int[] codesForChar = Code128Code.CodesForChar(thischar, nextchar,
					currcs);
			for (int code : codesForChar)
				codes.add(code);
		}

		// calculate the check digit
		int checksum = (int) codes.get(0);
		for (int i = 1; i < codes.size(); i++) {
			checksum += i * (int) (codes.get(i));
		}
		codes.add(checksum % 103);

		codes.add(Code128Code.StopCode());

		int[] result = new int[codes.size()];
		for (int i = 0; i < codes.size(); ++i)
			result[i] = codes.get(i);
		return result;
	}

	// / <summary>
	// / Determines the best starting code set based on the the first two
	// / characters of the string to be encoded
	// / </summary>
	// / <param name="csa1">First character of input string</param>
	// / <param name="csa2">Second character of input string</param>
	// / <returns>The codeset determined to be best to start with</returns>
	private CodeSet GetBestStartSet(Code128Code.CodeSetAllowed csa1,
			Code128Code.CodeSetAllowed csa2) {
		int vote = 0;

		vote += (csa1 == Code128Code.CodeSetAllowed.CodeA) ? 1 : 0;
		vote += (csa1 == Code128Code.CodeSetAllowed.CodeB) ? -1 : 0;
		vote += (csa2 == Code128Code.CodeSetAllowed.CodeA) ? 1 : 0;
		vote += (csa2 == Code128Code.CodeSetAllowed.CodeB) ? -1 : 0;

		return (vote > 0) ? CodeSet.CodeA : CodeSet.CodeB; // ties go to codeB
															// due to my own
															// prejudices
	}
}
