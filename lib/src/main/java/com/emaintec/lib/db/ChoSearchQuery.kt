package com.emaintec.lib.db

import android.util.Log

object ChoSearchQuery {
	val EVENT_CODE_LENGTH = 6

	val DIGIT_BEGIN_UNICODE = 0x30 //0
	val DIGIT_END_UNICODE = 0x3A //9

	val QUERY_DELIM = 39//'
	val LARGE_ALPHA_BEGIN_UNICODE = 0

	val HANGUL_BEGIN_UNICODE = 0xAC00 // 가
	val HANGUL_END_UNICODE = 0xD7A3 // ?
	val HANGUL_CHO_UNIT = 588 //한글 초성글자간 간격
	val HANGUL_JUNG_UNIT = 28 //한글 중성글자간 간격

	val CHO_LIST = charArrayOf('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')
	val CHO_SEARCH_LIST = booleanArrayOf( true, false, true, true, false, true, true, true, false, true, false, true, true, false, true, true, true, true, true )

	/**
	 * 문자를 유니코드(10진수)로 변환 후 반환한다.
	 * @param ch 문자
	 * @return 10진수 유니코드
	 */
	fun convertCharToUnicode(ch: Char): Int {
		return ch.toInt()
	}

	/**
	 * 10진수를 16진수 문자열로 변환한다.
	 * @param decimal 10진수 숫자
	 * @return 16진수 문자열
	 */
	private fun toHexString(decimal: Int): String {
		return java.lang.Long.toHexString(decimal.toLong())
	}

	/**
	 * 유니코드(16진수)를 문자로 변환 후 반환한다.
	 * @param hexUnicode Unicode Hex String
	 * @return 문자값
	 */
	fun convertUnicodeToChar(hexUnicode: String): Char {
		return Integer.parseInt(hexUnicode, 16).toChar()
	}

	/**
	 * 유니코드(10진수)를 문자로 변환 후 반환한다.
	 * @param unicode
	 * @return 문자값
	 */
	fun convertUnicodeToChar(unicode: Int): Char {
		return convertUnicodeToChar(toHexString(unicode))
	}

	/**
	 * 검색 문자열을 파싱해서 SQL Query 조건 문자열을 만든다.
	 * @param strSearch 검색 문자열
	 * @return SQL Query 조건 문자열
	 */
	fun makeQuery(fieldName: String, strSearch: String): String {
		var strSearch = strSearch
		strSearch = strSearch.trim { it <= ' ' } ?: "null"

		val retQuery = StringBuilder()

		var nChoPosition: Int
		var nNextChoPosition: Int
		var StartUnicode: Int
		var EndUnicode: Int

		var nQueryIndex = 0
		//            boolean bChosung = false;
		val stringBuilder = StringBuilder()
		for (nIndex in 0 until strSearch.length) {
			nChoPosition = -1
			nNextChoPosition = -1
			StartUnicode = -1
			EndUnicode = -1

			if (strSearch[nIndex].toInt() == QUERY_DELIM)
				continue

			if (nQueryIndex != 0) {
				stringBuilder.append(" AND ")
			}

			for (nChoIndex in CHO_LIST.indices) {
				if (strSearch[nIndex] == CHO_LIST[nChoIndex]) {
					nChoPosition = nChoIndex
					nNextChoPosition = nChoPosition + 1
					while (nNextChoPosition < CHO_SEARCH_LIST.size) {
						if (CHO_SEARCH_LIST[nNextChoPosition])
							break
						nNextChoPosition++
					}
					break
				}
			}

			if (nChoPosition >= 0) {                                                //초성이 있을 경우
				//              bChosung = true;
				StartUnicode = HANGUL_BEGIN_UNICODE + nChoPosition * HANGUL_CHO_UNIT
				EndUnicode = HANGUL_BEGIN_UNICODE + nNextChoPosition * HANGUL_CHO_UNIT
			} else {
				val Unicode = convertCharToUnicode(strSearch[nIndex])
				if (Unicode >= HANGUL_BEGIN_UNICODE && Unicode <= HANGUL_END_UNICODE) {
					val Jong = (Unicode - HANGUL_BEGIN_UNICODE) % HANGUL_CHO_UNIT % HANGUL_JUNG_UNIT

					if (Jong == 0) {                                                // 초성+중성으로 되어 있는 경우
						StartUnicode = Unicode
						EndUnicode = Unicode + HANGUL_JUNG_UNIT
					} else {
						StartUnicode = Unicode
						EndUnicode = Unicode
					}
				}
			}

			//Log.d("SearchQuery","stringBuilder "+strSearch.codePointAt(nIndex));
			if (StartUnicode > 0 && EndUnicode > 0) {
				if (StartUnicode == EndUnicode)
					stringBuilder.append("substr(" + fieldName + "," + (nIndex + 1) + ",1)='" + strSearch[nIndex] + "'")
				else
					stringBuilder.append(
						"(substr(" + fieldName + "," + (nIndex + 1) + ",1)>='" + convertUnicodeToChar(StartUnicode)
								+ "' AND substr(" + fieldName + "," + (nIndex + 1) + ",1)<'" + convertUnicodeToChar(
							EndUnicode
						) + "')"
					)
			} else {
				if (Character.isLowerCase(strSearch[nIndex])) {             // 영문 소문자
					stringBuilder.append(
						"(substr(" + fieldName + "," + (nIndex + 1) + ",1)='" + strSearch[nIndex] + "'"
								+ " OR substr(" + fieldName + "," + (nIndex + 1) + ",1)='" + Character.toUpperCase(
							strSearch[nIndex]
						) + "')"
					)
				} else if (Character.isUpperCase(strSearch[nIndex])) {      // 영문 대문자
					stringBuilder.append(
						"(substr(" + fieldName + "," + (nIndex + 1) + ",1)='" + strSearch[nIndex] + "'"
								+ " OR substr(" + fieldName + "," + (nIndex + 1) + ",1)='" + Character.toLowerCase(
							strSearch[nIndex]
						) + "')"
					)
				} else {                                                            // 기타 문자
					stringBuilder.append("substr(" + fieldName + "," + (nIndex + 1) + ",1)='" + strSearch[nIndex] + "'")
				}
			}

			nQueryIndex++
		}

		if (stringBuilder.length > 0 && strSearch.trim { it <= ' ' }.length > 0) {
			retQuery.append("($stringBuilder)")

			if (strSearch.contains(" ")) {
				// 공백 구분 단어에 대해 단어 모두 포함 검색
				val tokens = strSearch.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
				retQuery.append(" OR (")
				var i = 0
				val isize = tokens.size
				while (i < isize) {
					val token = tokens[i]
					if (i != 0) {
						retQuery.append(" AND ")
					}
					retQuery.append("$fieldName like '%$token%'")
					i++
				}
				retQuery.append(")")
			} else {
				// LIKE 검색 추가
				retQuery.append(" OR $fieldName like '%$strSearch%'")
			}
		} else {
			retQuery.append(stringBuilder.toString())
		}
		Log.d("SearchQuery", "stringBuilder $retQuery")
		return retQuery.toString()
	}
}
