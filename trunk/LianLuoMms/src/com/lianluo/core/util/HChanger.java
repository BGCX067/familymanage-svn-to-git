package com.lianluo.core.util;

public class HChanger {

	/**
	 * 对字符串加密
	 * 
	 * @param sSrouce
	 *            加密的字符串
	 */
	public static String ConvertJiaMi(String sSrouce) {
		char uChar = 0;
		short uHighChar = 0;
		short uLowChar = 0;

		char[] bufUnit = new char[4];
		char[] bufHigh = new char[2];
		char[] bufLow = new char[2];
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < sSrouce.length(); i++) {
			uChar = sSrouce.charAt(i);
			uHighChar = (short) (uChar / 256);
			uLowChar = (short) (uChar % 256);

			uHighChar++;
			uLowChar++;
			bufHigh = GetHexString(uHighChar, bufHigh);
			bufLow = GetHexString(uLowChar, bufLow);
			// bufUnit.Format(_L8("%S%S"),&bufHigh,&bufLow);//将加密的字符串还原成一个正确的字符串
			for (int j = 1; j >= 0; j--) {
				bufUnit[j + 2] = bufLow[j];
				bufUnit[j] = bufHigh[j];
			}
			// String str=String.valueOf(bufUnit);//
			sb.append(bufUnit);
		}
		return sb.toString();
	}

	/**
	 * 字符串解密 sSource 要解密的字符串 sDest 解密后添加在这个的后面
	 */
	public static String convertJiemi(String sSource, String sDest) {
		if(sSource == null || "".equals(sSource)) {
			return sSource;
		}
		
		char uChar = 0;
		short uHighChar = 0;
		short uLowChar = 0;

		// TBuf<4> bufUnit = _L("");
		// char[] bufUnit = new char[4];

		if ((sSource.length() % 4) != 0) {
			return "";
		}

		char c1, c2, c3, c4;

		for (int i = 0; i < sSource.length(); i += 4) {
			c1 = sSource.toCharArray()[i];
			c2 = sSource.toCharArray()[i + 1];
			c3 = sSource.toCharArray()[i + 2];
			c4 = sSource.toCharArray()[i + 3];

			uHighChar = GetHexHex(c1, c2);
			uLowChar = GetHexHex(c3, c4);
			uHighChar--;
			uLowChar--;

			uChar = (char) (uHighChar * 256 + uLowChar);
			sDest += uChar;
			// bufUnit = _L("");
		}
		return sDest;
	}

	private static short GetHexHex(char chHigh, char chLow) {
		short nHigh, nLow;
		nHigh = GetCharHex(chHigh);
		nLow = GetCharHex(chLow);

		return (short) (nHigh * 16 + nLow);
	}

	private static short GetCharHex(char chChar) {
		switch (chChar) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'A':
		case 'a':
			return 10;
		case 'B':
		case 'b':
			return 11;
		case 'C':
		case 'c':
			return 12;
		case 'D':
		case 'd':
			return 13;
		case 'E':
		case 'e':
			return 14;
		case 'F':
		case 'f':
			return 15;
		}

		return 0;
	}

	private static char GetHexChar(short nChar) {
		switch (nChar) {
		case 0:
			return '0';
		case 1:
			return '1';
		case 2:
			return '2';
		case 3:
			return '3';
		case 4:
			return '4';
		case 5:
			return '5';
		case 6:
			return '6';
		case 7:
			return '7';
		case 8:
			return '8';
		case 9:
			return '9';
		case 10:
			return 'A';
		case 11:
			return 'B';
		case 12:
			return 'C';
		case 13:
			return 'D';
		case 14:
			return 'E';
		case 15:
			return 'F';
		}

		return '0';
	}

	private static char[] GetHexString(short uHex, char[] sString) {
		short nHigh, nLow;
		nHigh = (short) (uHex / 16);
		nLow = (short) (uHex % 16);
		sString[0] = GetHexChar(nHigh);
		sString[1] = GetHexChar(nLow);
		return sString;
	}

	/**
	 * 对字符串加密
	 * 
	 * @param sSrouce
	 * @param sDest8
	 */
	// void ConvertTBuf2TBuf8Test(String sSrouce, String sDest8)
	// {
	// short uChar = 0;
	// short uHighChar = 0;
	// short uLowChar = 0;
	//
	// short[] bufUnit = new short[6];
	// short[] bufHigh = new short[3];
	// short[] bufLow = new short[3];
	//
	// for(int i=0;i<sSrouce.length();i++)
	// {
	// uChar = (short)sSrouce.toCharArray()[i];
	// uHighChar = (short)(uChar/256);
	// uLowChar = (short)(uChar%256);
	//
	// uHighChar++;
	// uLowChar++;
	//
	// GetHexString(uHighChar,bufHigh);
	// GetHexString(uLowChar,bufLow);
	//
	// if(uChar != ' '){
	// bufUnit.Format(_L8("%S%S"),&bufHigh,&bufLow);
	// }else{
	// bufUnit= _L8(" ");
	// }
	// sDest8 += bufUnit;
	// }
	// }

}
