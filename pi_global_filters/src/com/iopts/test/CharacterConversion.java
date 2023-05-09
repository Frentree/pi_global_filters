package com.iopts.test;

import java.util.HashMap;

public class CharacterConversion {

	public static void main(String[] args) {

		String arg = "アｱイｲウｳエｴオｵカｶキｷクｸケｹコｺサｻシｼスｽセｾソｿタﾀチﾁツﾂテﾃトﾄマﾏミﾐムﾑメﾒモﾓヤﾔユﾕヨﾖラﾗリﾘルﾙレﾚロﾛアｱイウ（）()ンﾝ果物";
		try {
			String x = toSBCS(arg);
			System.out.println("＄1-> " + x);

			String y = toDBCS(arg);
			System.out.println("★1-> " + y);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String toFullChar(String inputStr) {
		String rtnStr = null;
		try {
			if (inputStr == null)
				return null;
			StringBuffer strBuf = new StringBuffer();
			char c = 0;
			int nSrcLength = inputStr.length();
			for (int i = 0; i < nSrcLength; i++) {
				c = inputStr.charAt(i);
				if (c >= 0x21 && c <= 0x7e) {
					c += 0xfee0;
				} else if (c == 0x20) {
					c = 0x3000;
				}
				strBuf.append(c);
			}

			rtnStr = toDBCS(strBuf.toString());

		} catch (Exception e) {
			System.out.println("Full Character convert failed!!");
		}
		return rtnStr;
	}

	/**
	 * param文字列が半角文字列に変換する
	 *
	 * @param param
	 *            文字列
	 * @return string 半角文字列
	 */
	public static String toHalfChar(String inputStr) {
		String rtnStr = null;
		try {
			StringBuffer strBuf = new StringBuffer();
			char c = 0;
			int nSrcLength = inputStr.length();
			for (int i = 0; i < nSrcLength; i++) {
				c = inputStr.charAt(i);
				if (c >= '！' && c <= '～') {
					c -= 0xfee0;
				} else if (c == '　') {
					c = 0x20;
				}
				strBuf.append(c);
			}
			rtnStr = toSBCS(strBuf.toString());

		} catch (Exception e) {
			System.out.println("Half Character convert failed!!");
		}
		return rtnStr;
	}

	private static String changeDisposal(String psValue, HashMap phHash) {
		char charVal[] = psValue.toCharArray();
		int sizeChar = charVal.length;
		String strTemp = "";
		String strAdd = "";
		String strRet = "";
		for (int i = 0; i < sizeChar; i++) {
			strTemp = String.valueOf(charVal[i]);
			if ((strAdd = (String) phHash.get(strTemp)) != null)
				strRet = strRet + strAdd;
			else
				strRet = strRet + strTemp;
		}

		return strRet;
	}

	/**
	 * 全角濁音に濁音を外す。
	 */
	private static void createBilabialHash() {
		hashBilabial.put("\u30AC", "\u30AB");
		hashBilabial.put("\u30AE", "\u30AD");
		hashBilabial.put("\u30B0", "\u30AF");
		hashBilabial.put("\u30B2", "\u30B1");
		hashBilabial.put("\u30B4", "\u30B3");
		hashBilabial.put("\u30B6", "\u30B5");
		hashBilabial.put("\u30B8", "\u30B7");
		hashBilabial.put("\u30BA", "\u30B9");
		hashBilabial.put("\u30BC", "\u30BB");
		hashBilabial.put("\u30BE", "\u30BD");
		hashBilabial.put("\u30C0", "\u30BF");
		hashBilabial.put("\u30C2", "\u30C1");
		hashBilabial.put("\u30C5", "\u30C4");
		hashBilabial.put("\u30C7", "\u30C6");
		hashBilabial.put("\u30C9", "\u30C8");
		hashBilabial.put("\u30D0", "\u30CF");
		hashBilabial.put("\u30D3", "\u30D2");
		hashBilabial.put("\u30D6", "\u30D5");
		hashBilabial.put("\u30D9", "\u30D8");
		hashBilabial.put("\u30DC", "\u30DB");
		hashBilabial.put("\u30D1", "\u30CF");
		hashBilabial.put("\u30D4", "\u30D2");
		hashBilabial.put("\u30D7", "\u30D5");
		hashBilabial.put("\u30DA", "\u30D8");
		hashBilabial.put("\u30DD", "\u30DB");
		hashBilabial.put("\u30F4", "\u30A6");
	}

	private static void createKanaHash() {
		hashKana.put("\u30C2", "\u30B8");
		hashKana.put("\u30C5", "\u30BA");
		hashKana.put("\u30F2", "\u30AA");
	}

	/**
	 * 全角小文字Alphabetを全角大文字Alphabetに変換
	 * 
	 */
	private static void createCapitalHash() {
		hashCapital.put("\uFF41", "\uFF21");
		hashCapital.put("\uFF42", "\uFF22");
		hashCapital.put("\uFF43", "\uFF23");
		hashCapital.put("\uFF44", "\uFF24");
		hashCapital.put("\uFF45", "\uFF25");
		hashCapital.put("\uFF46", "\uFF26");
		hashCapital.put("\uFF47", "\uFF27");
		hashCapital.put("\uFF48", "\uFF28");
		hashCapital.put("\uFF49", "\uFF29");
		hashCapital.put("\uFF4A", "\uFF2A");
		hashCapital.put("\uFF4B", "\uFF2B");
		hashCapital.put("\uFF4C", "\uFF2C");
		hashCapital.put("\uFF4D", "\uFF2D");
		hashCapital.put("\uFF4E", "\uFF2E");
		hashCapital.put("\uFF4F", "\uFF2F");
		hashCapital.put("\uFF50", "\uFF30");
		hashCapital.put("\uFF51", "\uFF31");
		hashCapital.put("\uFF52", "\uFF32");
		hashCapital.put("\uFF53", "\uFF33");
		hashCapital.put("\uFF54", "\uFF34");
		hashCapital.put("\uFF55", "\uFF35");
		hashCapital.put("\uFF56", "\uFF36");
		hashCapital.put("\uFF57", "\uFF37");
		hashCapital.put("\uFF58", "\uFF38");
		hashCapital.put("\uFF59", "\uFF39");
		hashCapital.put("\uFF5A", "\uFF3A");
	}

	/**
	 * 全角大文字Alphabetを全角小文字Alphabetに変換
	 * 
	 */
	private static void createLower() {
		hashLower.put("\uFF21", "\uFF41");
		hashLower.put("\uFF22", "\uFF42");
		hashLower.put("\uFF23", "\uFF43");
		hashLower.put("\uFF24", "\uFF44");
		hashLower.put("\uFF25", "\uFF45");
		hashLower.put("\uFF26", "\uFF46");
		hashLower.put("\uFF27", "\uFF47");
		hashLower.put("\uFF28", "\uFF48");
		hashLower.put("\uFF29", "\uFF49");
		hashLower.put("\uFF2A", "\uFF4A");
		hashLower.put("\uFF2B", "\uFF4B");
		hashLower.put("\uFF2C", "\uFF4C");
		hashLower.put("\uFF2D", "\uFF4D");
		hashLower.put("\uFF2E", "\uFF4E");
		hashLower.put("\uFF2F", "\uFF4F");
		hashLower.put("\uFF30", "\uFF50");
		hashLower.put("\uFF31", "\uFF51");
		hashLower.put("\uFF32", "\uFF52");
		hashLower.put("\uFF33", "\uFF53");
		hashLower.put("\uFF34", "\uFF54");
		hashLower.put("\uFF35", "\uFF55");
		hashLower.put("\uFF36", "\uFF56");
		hashLower.put("\uFF37", "\uFF57");
		hashLower.put("\uFF38", "\uFF58");
		hashLower.put("\uFF39", "\uFF59");
		hashLower.put("\uFF3A", "\uFF5A");
	}

	/**
	 * ひらがなを全角カタカナに変換
	 * 
	 */
	private static void createKatakana() {
		hashKatakana.put("\u3042", "\u30A2");
		hashKatakana.put("\u3044", "\u30A4");
		hashKatakana.put("\u3046", "\u30A6");
		hashKatakana.put("\u3048", "\u30A8");
		hashKatakana.put("\u304A", "\u30AA");
		hashKatakana.put("\u304B", "\u30AB");
		hashKatakana.put("\u304D", "\u30AD");
		hashKatakana.put("\u304F", "\u30AF");
		hashKatakana.put("\u3051", "\u30B1");
		hashKatakana.put("\u3053", "\u30B3");
		hashKatakana.put("\u3055", "\u30B5");
		hashKatakana.put("\u3057", "\u30B7");
		hashKatakana.put("\u3059", "\u30B9");
		hashKatakana.put("\u305B", "\u30BB");
		hashKatakana.put("\u305D", "\u30BD");
		hashKatakana.put("\u305F", "\u30BF");
		hashKatakana.put("\u3061", "\u30C1");
		hashKatakana.put("\u3064", "\u30C4");
		hashKatakana.put("\u3066", "\u30C6");
		hashKatakana.put("\u3068", "\u30C8");
		hashKatakana.put("\u306A", "\u30CA");
		hashKatakana.put("\u306B", "\u30CB");
		hashKatakana.put("\u306C", "\u30CC");
		hashKatakana.put("\u306D", "\u30CD");
		hashKatakana.put("\u306E", "\u30CE");
		hashKatakana.put("\u306F", "\u30CF");
		hashKatakana.put("\u3072", "\u30D2");
		hashKatakana.put("\u3075", "\u30D5");
		hashKatakana.put("\u3078", "\u30D8");
		hashKatakana.put("\u307B", "\u30DB");
		hashKatakana.put("\u307E", "\u30DE");
		hashKatakana.put("\u307F", "\u30DF");
		hashKatakana.put("\u3080", "\u30E0");
		hashKatakana.put("\u3081", "\u30E1");
		hashKatakana.put("\u3082", "\u30E2");
		hashKatakana.put("\u3084", "\u30E4");
		hashKatakana.put("\u3086", "\u30E6");
		hashKatakana.put("\u3088", "\u30E8");
		hashKatakana.put("\u3089", "\u30E9");
		hashKatakana.put("\u308A", "\u30EA");
		hashKatakana.put("\u308B", "\u30EB");
		hashKatakana.put("\u308C", "\u30EC");
		hashKatakana.put("\u308D", "\u30ED");
		hashKatakana.put("\u308F", "\u30EF");
		hashKatakana.put("\u3092", "\u30F2");
		hashKatakana.put("\u3093", "\u30F3");
		hashKatakana.put("\u304C", "\u30AC");
		hashKatakana.put("\u304E", "\u30AE");
		hashKatakana.put("\u3050", "\u30B0");
		hashKatakana.put("\u3052", "\u30B2");
		hashKatakana.put("\u3054", "\u30B4");
		hashKatakana.put("\u3056", "\u30B6");
		hashKatakana.put("\u3058", "\u30B8");
		hashKatakana.put("\u305A", "\u30BA");
		hashKatakana.put("\u305C", "\u30BC");
		hashKatakana.put("\u305E", "\u30BE");
		hashKatakana.put("\u3060", "\u30C0");
		hashKatakana.put("\u3062", "\u30C2");
		hashKatakana.put("\u3065", "\u30C5");
		hashKatakana.put("\u3067", "\u30C7");
		hashKatakana.put("\u3069", "\u30C9");
		hashKatakana.put("\u3070", "\u30D0");
		hashKatakana.put("\u3073", "\u30D3");
		hashKatakana.put("\u3076", "\u30D6");
		hashKatakana.put("\u3079", "\u30D9");
		hashKatakana.put("\u307C", "\u30DC");
		hashKatakana.put("\u3071", "\u30D1");
		hashKatakana.put("\u3074", "\u30D4");
		hashKatakana.put("\u3077", "\u30D7");
		hashKatakana.put("\u307A", "\u30DA");
		hashKatakana.put("\u307D", "\u30DD");
		hashKatakana.put("\u3041", "\u30A1");
		hashKatakana.put("\u3043", "\u30A3");
		hashKatakana.put("\u3045", "\u30A5");
		hashKatakana.put("\u3047", "\u30A7");
		hashKatakana.put("\u3049", "\u30A9");
		hashKatakana.put("\u3063", "\u30C3");
		hashKatakana.put("\u3083", "\u30E3");
		hashKatakana.put("\u3085", "\u30E5");
		hashKatakana.put("\u3087", "\u30E7");
		hashKatakana.put("\u3090", "\u30F0");
		hashKatakana.put("\u3091", "\u30F1");
	}

	/**
	 * 全角カタカナをひらがなに変換
	 * 
	 */
	private static void createHiragana() {
		hashHiragana.put("\u30A2", "\u3042");
		hashHiragana.put("\u30A4", "\u3044");
		hashHiragana.put("\u30A6", "\u3046");
		hashHiragana.put("\u30A8", "\u3048");
		hashHiragana.put("\u30AA", "\u304A");
		hashHiragana.put("\u30AB", "\u304B");
		hashHiragana.put("\u30AD", "\u304D");
		hashHiragana.put("\u30AF", "\u304F");
		hashHiragana.put("\u30B1", "\u3051");
		hashHiragana.put("\u30B3", "\u3053");
		hashHiragana.put("\u30B5", "\u3055");
		hashHiragana.put("\u30B7", "\u3057");
		hashHiragana.put("\u30B9", "\u3059");
		hashHiragana.put("\u30BB", "\u305B");
		hashHiragana.put("\u30BD", "\u305D");//
		hashHiragana.put("\u30BF", "\u305F");
		hashHiragana.put("\u30C1", "\u3061");
		hashHiragana.put("\u30C4", "\u3064");
		hashHiragana.put("\u30C6", "\u3066");
		hashHiragana.put("\u30C8", "\u3068");
		hashHiragana.put("\u30CA", "\u306A");
		hashHiragana.put("\u30CB", "\u306B");
		hashHiragana.put("\u30CC", "\u306C");
		hashHiragana.put("\u30CD", "\u306D");
		hashHiragana.put("\u30CE", "\u306E");
		hashHiragana.put("\u30CF", "\u306F");
		hashHiragana.put("\u30D2", "\u3072");
		hashHiragana.put("\u30D5", "\u3075");
		hashHiragana.put("\u30D8", "\u3078");
		hashHiragana.put("\u30DB", "\u307B");
		hashHiragana.put("\u30DE", "\u307E");
		hashHiragana.put("\u30DF", "\u307F");
		hashHiragana.put("\u30E0", "\u3080");
		hashHiragana.put("\u30E1", "\u3081");
		hashHiragana.put("\u30E2", "\u3082");
		hashHiragana.put("\u30E4", "\u3084");
		hashHiragana.put("\u30E6", "\u3086");
		hashHiragana.put("\u30E8", "\u3088");
		hashHiragana.put("\u30E9", "\u3089");
		hashHiragana.put("\u30EA", "\u308A");
		hashHiragana.put("\u30EB", "\u308B");
		hashHiragana.put("\u30EC", "\u308C");
		hashHiragana.put("\u30ED", "\u308D");
		hashHiragana.put("\u30EF", "\u308F");
		hashHiragana.put("\u30F2", "\u3092");
		hashHiragana.put("\u30F3", "\u3093");
		hashHiragana.put("\u30AC", "\u304C");
		hashHiragana.put("\u30AE", "\u304E");
		hashHiragana.put("\u30B0", "\u3050");
		hashHiragana.put("\u30B2", "\u3052");
		hashHiragana.put("\u30B4", "\u3054");
		hashHiragana.put("\u30B6", "\u3056");
		hashHiragana.put("\u30B8", "\u3058");
		hashHiragana.put("\u30BA", "\u305A");
		hashHiragana.put("\u30BC", "\u305C");
		hashHiragana.put("\u30BE", "\u305E");
		hashHiragana.put("\u30C0", "\u3060");
		hashHiragana.put("\u30C2", "\u3062");
		hashHiragana.put("\u30C5", "\u3065");
		hashHiragana.put("\u30C7", "\u3067");
		hashHiragana.put("\u30C9", "\u3069");
		hashHiragana.put("\u30D0", "\u3070");
		hashHiragana.put("\u30D3", "\u3073");
		hashHiragana.put("\u30D6", "\u3076");
		hashHiragana.put("\u30D9", "\u3079");
		hashHiragana.put("\u30DC", "\u307C");
		hashHiragana.put("\u30D1", "\u3071");
		hashHiragana.put("\u30D4", "\u3074");
		hashHiragana.put("\u30D7", "\u3077");
		hashHiragana.put("\u30DA", "\u307A");
		hashHiragana.put("\u30DD", "\u307D");
		hashHiragana.put("\u30A1", "\u3041");
		hashHiragana.put("\u30A3", "\u3043");
		hashHiragana.put("\u30A5", "\u3045");
		hashHiragana.put("\u30A7", "\u3047");
		hashHiragana.put("\u30A9", "\u3049");
		hashHiragana.put("\u30C3", "\u3063");
		hashHiragana.put("\u30E3", "\u3083");
		hashHiragana.put("\u30E5", "\u3085");
		hashHiragana.put("\u30E7", "\u3087");
		hashHiragana.put("\u30F0", "\u3090");
		hashHiragana.put("\u30F1", "\u3091");
	}

	private static StringBuffer editH(StringBuffer pbRsb, int piIndex, char c) {
		for (int j = 0; j < table.length; j++)
			if (c == table[j])// table[j] - 全角カタカナ
			{
				pbRsb.setCharAt(piIndex, table2[j]); // table2[j] - 半角カタカナ
				return pbRsb;
			}

		String str = String.valueOf(c);
		for (int j = 0; j < dtable.length; j++) {

			if (str.equals(dtable[j]))// dtable[j] - 全角・半角カタカナ(濁音・半濁
			{
				// System.out.println("★★★★str.equals(dtable[j])");
				// System.out.println("★★★★ dtable[j] = "+dtable[j]);

				StringBuffer sb = new StringBuffer();
				// sb.append(str);
				sb.append(pbRsb.toString().substring(0, piIndex));
				// System.out.println("pbRsb.toString().substring(0, piIndex)
				// ="+pbRsb.toString().substring(0, piIndex));
				sb.append(dtable[j + 1]);
				if (piIndex + 1 != pbRsb.length())
					sb.append(pbRsb.toString().substring(piIndex + 1, pbRsb.toString().length()));
				pbRsb = sb;
				// System.out.println("### editH() sb="+ sb);
				return pbRsb;
			}
		}
		for (int j = 0; j < htable.length; j++)
			if (str.equals(htable[j])) {
				StringBuffer sb = new StringBuffer();
				sb.append(pbRsb.toString().substring(0, piIndex));
				sb.append(htable[j + 1]);
				if (piIndex + 1 != pbRsb.length())
					sb.append(pbRsb.toString().substring(piIndex + 1, pbRsb.toString().length()));
				pbRsb = sb;
				return pbRsb;
			}

		if (c == '\uFF67' || c == '\uFF68' || c == '\uFF69' || c == '\uFF6A' || c == '\uFF6B') {
			c += '\n';
			pbRsb.setCharAt(piIndex, c);
		} else if (c == '\uFF6F') {
			c = '\uFF82';
			pbRsb.setCharAt(piIndex, c);
		} else if (c == '\uFF6C' || c == '\uFF6D' || c == '\uFF6E') {
			c += '(';
			pbRsb.setCharAt(piIndex, c);
		}
		if (c == '\u30FC' || c == '\u2015' || c == '\u2010' || c == '\uFF0D' || c == '\u2014' || c == '\u2212')
			pbRsb.setCharAt(piIndex, '-');
		return pbRsb;
	}

	private static StringBuffer editZ(StringBuffer pbBuf, int piIndex, char pcChar) {
		for (int j = 0; j < table2.length; j++) {
			if (pcChar != table2[j])
				continue;
			if (pbBuf.length() > piIndex + 1) {
				if (pbBuf.charAt(piIndex + 1) == '\uFF9E') // " 濁点
					pbBuf = dakuten(pbBuf, piIndex);
				else if (pbBuf.charAt(piIndex + 1) == '\uFF9F') // 半濁
				{
					pbBuf = handaku(pbBuf, piIndex);
				} else {
					pbBuf.setCharAt(piIndex, table[j]);
					return pbBuf;
				}
			} else {
				pbBuf.setCharAt(piIndex, table[j]);
				return pbBuf;
			}
			break;
		}

		return pbBuf;
	}

	private static StringBuffer dakuten(StringBuffer pbBuf, int piIndex) {
		char a[] = pbBuf.toString().toCharArray();
		String str = new String(a, piIndex, 2);
		for (int k = 0; k < dtable.length; k++)
			if (str.equals(dtable[k])) {
				StringBuffer sb = new StringBuffer();
				sb.append(pbBuf.toString().substring(0, piIndex));
				sb.append(dtable[k - 1]);
				if (piIndex + 2 != pbBuf.length())
					sb.append(pbBuf.toString().substring(piIndex + 2, pbBuf.toString().length()));
				pbBuf = sb;
			}

		return pbBuf;
	}

	private static StringBuffer handaku(StringBuffer rsb, int index) {
		return dakuten(rsb, index);
		// char a[] = rsb.toString().toCharArray();
		// String str = new String(a, index, 2);
		// for(int k = 0; k < htable.length; k++)
		// if(str.equals(htable[k]))
		// {
		// StringBuffer sb = new StringBuffer();
		// sb.append(rsb.toString().substring(0, index));
		// sb.append(htable[k - 1]);
		// if(index + 2 != rsb.length())
		// sb.append(rsb.toString().substring(index + 2, rsb.toString().length()));
		// rsb = sb;
		// }
		//
		// return rsb;
	}

	static boolean isNull(String psValue) {
		return psValue == null;
	}

	public static String toKanaConsonant(String psValue) throws Exception {
		if (isNull(psValue))
			throw new Exception("errors.EDIT0001");
		if (first3)
			synchronized (lock3) {
				if (first3) {
					createKanaHash();
					first3 = false;
				}
			}
		return psValue.equals("") ? "" : changeDisposal(psValue, hashKana);
	}

	public static String toBilabialRemove(String psValue) throws Exception {
		if (isNull(psValue))
			throw new Exception("errors.EDIT0001");
		if (first1)
			synchronized (lock1) {
				if (first1) {
					createBilabialHash();
					first1 = false;
				}
			}
		return psValue.equals("") ? "" : changeDisposal(psValue, hashBilabial);
	}

	public static String toLowerAlphabet(String psValue) throws Exception {
		if (isNull(psValue))
			throw new Exception("errors.EDIT0001");
		if (first4)
			synchronized (lock4) {
				if (first4) {
					createLower();
					first4 = false;
				}
			}
		return psValue.equals("") ? "" : changeDisposal(psValue, hashLower);
	}

	public static String toCapitalAlphabet(String psValue) throws Exception {
		if (isNull(psValue))
			throw new Exception("errors.EDIT0001");
		if (first2)
			synchronized (lock2) {
				if (first2) {
					createCapitalHash();
					first2 = false;
				}
			}
		return psValue.equals("") ? "" : changeDisposal(psValue, hashCapital);
	}

	/**
	 * DBCS(double byte character set 全角)
	 * 
	 * @param psStr
	 * @return
	 * @throws Exception
	 */
	public static String toDBCS(String psStr) throws Exception {
		if (isNull(psStr))
			throw new Exception("errors.EDIT0001");
		String strRet = "";

		if (psStr.length() == 0) {
			strRet = "";
		} else {
			StringBuffer rsb = new StringBuffer(psStr);
			for (int index = 0; index < rsb.length(); index++) {
				char c3 = rsb.charAt(index);
				rsb = editZ(rsb, index, c3);
			}

			strRet = rsb.toString();
		}
		return strRet;
	}

	public static String toHyphen(String psStr) throws Exception {
		if (isNull(psStr))
			throw new Exception("errors.EDIT0001");
		String strRet = "";
		char inchar[] = psStr.toCharArray();
		for (int i = 0; i < inchar.length; i++)
			if (inchar[i] == '\u30FC' || inchar[i] == '\u2014' || inchar[i] == '\u2015' || inchar[i] == '\u2010')
				inchar[i] = '\u2212';

		String outstr = String.valueOf(inchar);
		strRet = outstr;
		return strRet;
	}

	public static String toLong(String psStr) throws Exception {
		if (isNull(psStr))
			throw new Exception("errors.EDIT0001");
		char inchar[] = psStr.toCharArray();
		for (int i = 0; i < inchar.length; i++)
			if (inchar[i] == '\uFF0D' || inchar[i] == '\u2212' || inchar[i] == '\u2014' || inchar[i] == '\u2015' || inchar[i] == '\u2010')
				inchar[i] = '\u30FC';

		String outstr = String.valueOf(inchar);
		return outstr;
	}

	public static String toSBCS(String psStr) throws Exception {
		if (isNull(psStr))
			throw new Exception("errors.EDIT0001");
		String strRet = "";

		if (psStr.length() == 0) {
			strRet = "";
		} else {
			StringBuffer rsb = new StringBuffer(psStr);
			for (int index = 0; index < rsb.length(); index++) {
				char c = rsb.charAt(index);
				// System.out.println("$toSBCS$ c="+ c);
				if (c != '\uFF9E' && c != '\uFF9F') {
					// System.out.println("$toSBCS$ index="+ index);
					rsb = editH(rsb, index, c);
				}
			}

			strRet = rsb.toString();
		}
		return strRet;
	}

	public static String toKatakana(String psStr) throws Exception {
		if (isNull(psStr))
			throw new Exception("errors.EDIT0001");
		if (first7)
			synchronized (lock7) {
				if (first7) {
					createKatakana();
					first7 = false;
				}
			}
		return psStr.equals("") ? "" : changeDisposal(psStr, hashKatakana);
	}

	public static String toHiragana(String psStr) throws Exception {
		if (isNull(psStr))
			throw new Exception("errors.EDIT0001");
		if (first8)
			synchronized (lock8) {
				if (first8) {
					createHiragana();
					first8 = false;
				}
			}
		return psStr.equals("") ? "" : changeDisposal(psStr, hashHiragana);
	}

	static HashMap hashBilabial = new HashMap();

	static HashMap hashCapital = new HashMap();

	static HashMap hashKana = new HashMap();

	static HashMap hashLower = new HashMap();

	static HashMap hashHiragana = new HashMap();

	static HashMap hashKatakana = new HashMap();

	static char table[];

	static char table2[];

	static String dtable[];

	static String htable[];

	private static boolean first1 = true;

	private static boolean first2 = true;

	private static boolean first3 = true;

	private static boolean first4 = true;

	private static boolean first5 = true;

	private static boolean first6 = true;

	private static boolean first7 = true;

	private static boolean first8 = true;

	private static Object lock1 = new Object();

	private static Object lock2 = new Object();

	private static Object lock3 = new Object();

	private static Object lock4 = new Object();

	private static Object lock5 = new Object();

	private static Object lock6 = new Object();

	private static Object lock7 = new Object();

	private static Object lock8 = new Object();

	private static String hanNum = "0123456789";

	private static String zenNum = "０１２３４５６７８９";

	private static String hanAlp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private static String zenAlp = "ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ";

	private static String hankigou = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~｡｢｣､･";

	private static String zenkigou = "！”＃＄％＆’（）＊＋，－．／：；＜＝＞？＠［￥］＾＿‘｛｜｝～。「」、・";

	private static String hanhira = "ｦｧｨｩｪｫｬｭｮｯｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝ";

	private static String zenhira = "をぁぃぅぇぉゃゅょっあいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわん";

	private static String hanhira2 = "ｶﾞｷﾞｸﾞｹﾞｺﾞｻﾞｼﾞｽﾞｾﾞｿﾞﾀﾞﾁﾞﾂﾞﾃﾞﾄﾞﾊﾞﾋﾞﾌﾞﾍﾞﾎﾞﾊﾟﾋﾟﾌﾟﾍﾟﾎﾟ";

	private static String zenhira2 = "がぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽ";

	private static String hankana = "ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝﾞﾟ";

	private static String zenkana = "ヲァィゥェォャュョッーアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン゛゜";

	private static String hankana2 = "ｳﾞｶﾞｷﾞｸﾞｹﾞｺﾞｻﾞｼﾞｽﾞｾﾞｿﾞﾀﾞﾁﾞﾂﾞﾃﾞﾄﾞﾊﾞﾋﾞﾌﾞﾍﾞﾎﾞﾊﾟﾋﾟﾌﾟﾍﾟﾎﾟ";

	private static String zenkana2 = "ヴガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ";

	private static String hanSpace = " ";

	private static String zenSpace = "　";

	static {
		createBilabialHash();
		createCapitalHash();
		createHiragana();
		createKanaHash();
		createKatakana();
		createLower();

		char[] zenkana2char = zenkana2.toCharArray();
		String dtable1[] = new String[zenkana2char.length];

		for (int i = 0; i < zenkana2char.length; i++) {
			dtable1[i] = String.valueOf(zenkana2char[i]);
		}

		// 半角濁音・半濁
		char[] hankana2char = hankana2.toCharArray();
		String dtable2[] = new String[hankana2char.length / 2];
		int indx = 0;
		for (int i = 0; i < hankana2char.length; i++) {

			if ((i + 1 != hankana2char.length) && (hankana2char[i + 1] == '\uFF9E' || hankana2char[i + 1] == '\uFF9F')) {
				dtable2[indx] = String.valueOf(hankana2char[i]) + String.valueOf(hankana2char[i + 1]);
				indx++;
			} else if (hankana2char[i] == '\uFF9E' || hankana2char[i] == '\uFF9F') {
				continue;
			} else {
				dtable2[indx] = String.valueOf(hankana2char[i]);
				indx++;
			}

		}

		// System.out.println("dtable1.size = "+dtable1.length);
		// System.out.println("dtable2.size = "+dtable2.length);

		// 全角・半角 濁音・半濁カタカナ集まり
		dtable = new String[dtable1.length + dtable2.length];
		// System.arraycopy(dtable1, 0, dtable, 0, dtable1.length);
		// System.arraycopy(dtable2, 0, dtable, dtable1.length, dtable2.length);

		int k1 = 0, k2 = 0;
		for (int i = 0; i < dtable.length; i++) {
			if (i % 2 == 0) {
				dtable[i] = dtable1[k1];
				k1++;
			} else {
				dtable[i] = dtable2[k2];
				k2++;
			}
			// System.out.println("dtable["+i+"] = "+dtable[i]);
		}

		// TODO
		htable = new String[dtable1.length + dtable2.length];

		int s1 = 0, s2 = 0;
		for (int i = 0; i < htable.length; i++) {
			if (i % 2 != 0) {
				htable[i] = dtable1[s1];
				s1++;
			} else {
				htable[i] = dtable2[s2];
				s2++;
			}
			// System.out.println("htable["+i+"] = "+htable[i]);
		}

		// table[j] - 全角カタカナ(濁音・半濁除く）
		table = zenkana.toCharArray();

		// table2[j] - 半角カタカナ(濁音・半濁除く）
		table2 = hankana.toCharArray();
	}

	public static String toUnitCode(char a) {
		String rtn = null;
		Character A = new Character(a);

		// System.out.println("value: " + A.hashCode());

		// the following will return 97. When you convert that to hex which you can
		// do like this:
		rtn = Integer.toHexString(A.hashCode());
		// System.out.println("input character="+a+" return value = "+ "\\u"+rtn);

		return "\\u" + rtn;
	}
}
