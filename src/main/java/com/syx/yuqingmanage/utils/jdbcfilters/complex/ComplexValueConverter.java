package com.syx.yuqingmanage.utils.jdbcfilters.complex;


import com.syx.yuqingmanage.utils.jdbcfilters.Filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComplexValueConverter {
//	private static Pattern p = Pattern.compile("[)(+|-]");
	private static Pattern p = Pattern.compile("\\[|\\(|\\)|\\+|-|\\||\\]");

	int start = 0;
	int end;
	String str;

	private IComplexProcess process;

	public ComplexValueConverter(IComplexProcess process) {
		this.process = process;
	}

	public void convert(String s) {
		this.str = s.toLowerCase().replaceAll("（", "(").replaceAll("－", "-").replaceAll("＋", "+").replaceAll("）", ")").replaceAll("｜", "|");
		this.start = 0;
		this.getFilterStr();
		process.processEnd();
	}

	private int findSubEnd(int index, String str) {
		int start = str.indexOf('(', index);
		int end = str.indexOf(')', index);
		while (end > start && start != -1) {

			start = str.indexOf('(', end + 1);
			end = str.indexOf(')', end + 1);

		}
		// if (end == -1)
		// {
		// return str.Length;
		// }
		return end + 1;
	}

	private void getFilterStr() {
		String first = "";
		boolean flag = true;
		int currentRelation = 0;
		boolean reverse = false;
		while (flag) {
			if (process.goout()) {
				start = findSubEnd(start, str);
				break;
			}
			if (start == str.length()) {
				break;
			}

			first = str.substring(start, start + 1);

			if (first.equals("(")) {
				start += 1;
				process.begin(reverse, currentRelation);
				this.getFilterStr();
				process.end(reverse);
				reverse = false;
				currentRelation = 999;
			} else if (first.equals("+")) {
				start += 1;
				currentRelation = Filter.Relation_And;
				process.nextRelation(reverse, currentRelation);
			} else if (first.equals("|")) {
				start += 1;
				currentRelation = Filter.Relation_Or;
				process.nextRelation(reverse, currentRelation);
			} else if (first.equals("-")) {
				start += 1;

				// if (currentRelation == 0)
				// {

				// currentRelation = Filter.Relation_And;
				// }
				if (currentRelation == 999) {
					currentRelation = Filter.Relation_And;
					// process.nextRelation(reverse, currentRelation);
				} else {
					// process.nextRelation(reverse, currentRelation);
				}
				reverse = true;
			} else if (first.equals(")")) {
				start += 1;

				flag = false;
			} else {
				Matcher m = p.matcher(str.substring(start));
				String value = "";

				if (m.find()) {

					end = m.start();

					value = str.substring(start, start + end);
					// System.Console.WriteLine(value);
					start = start + end;
				} else {
					value = str.substring(start);

					start = str.length();

				}

				process.process(reverse, currentRelation, value);

				reverse = false;
				currentRelation = 999;
			}
		}
	} // end method
	
	public static void main(String[] args) {
		String a = "|";
		System.out.println(a);
		System.out.println("|".equals(a));
		
		String ab = "ICBC就在你身边".toLowerCase();
		
		System.out.println(ab);
		
		
		
		
		
	}
}
