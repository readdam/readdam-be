package com.kosta.readdam.util;

public class IsbnUtils {
	
	 public static String to10(String isbn13) {
	        String s = isbn13.replaceAll("[\\s-]", "");   // 공백·하이픈 제거
	        if (s.length() != 13 || !s.startsWith("978"))
	            throw new IllegalArgumentException("13자리 978 ISBN만 변환 가능: " + s);

	        String core = s.substring(3, 12);             // 체크디짓 제외
	        int sum = 0;
	        for (int i = 0; i < core.length(); i++) {
	            sum += (core.charAt(i) - '0') * (10 - i);
	        }
	        int mod = 11 - (sum % 11);
	        char check = (mod == 10) ? 'X' : (mod == 11) ? '0' : (char) ('0' + mod);
	        return core + check;
	    }

	    private IsbnUtils() {}

}
