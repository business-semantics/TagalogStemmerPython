package com.smartkyc.stemmers.tagalog;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TagalogStemmer
{
	private static List<String> rootsForValidating = null;
	private static final String VOWELS = "aeiouAEIOU";

	private static final String CONSONANTS = "bcdfghklmnngpqrstvwyBCDFGHKLMNNGPQRSTVWY";

	private static final Set<String> PREFIX_SET = Sets.newHashSet("nakikipag", "pakikipag", "pinakama", "pagpapa", "pinagka", "panganga",
			"makapag", "packaging", "tagapag", "makipag", "nakipag", "tigapag", "pakiki", "magpa", "napaka", "pinaka", "ipinag",
			"pagka", "pinag", "mapag", "mapa", "taga", "ipag", "tiga", "pala", "pina", "pang", "naka", "nang", "mang", "sing",
			"ipa", "pam", "pan", "pag", "tag", "mai", "mag", "nam", "nag", "man", "may", "ma", "na", "ni", "pa", "ka", "um", "in",
			"i");

	private static final Set<String> INFIX_SET = Sets.newHashSet("um", "in");

	private static final Set<String> SUFFIX_SET = Sets.newHashSet("syon", "dor", "ita", "han", "hin", "ing", "ang", "ng", "an", "in", "g");

	static String stem(String token) throws IOException
	{

		Map<String, Object> word_info = new HashMap<>();
		List<Map<String, Object>> stemmed = new ArrayList<>();
		List<String> word_root = new ArrayList<>();
		List<String> root_only = new ArrayList<>();
		List<String> errors = new ArrayList<>();
		String pre_stem, inf_stem, suf_stem, rep_stem, du1_stem, du2_stem, cle_stem = "-";

		List<String> prefixList = new ArrayList<>();
		List<String> infixList = new ArrayList<>();
		List<String> suffixList = new ArrayList<>();
		List<String> duplicateList = new ArrayList<>();
		List<String> repetitionList = new ArrayList<>();
		List<String> cleanersList = new ArrayList<>();

		word_info.put("word", token);


		token = token.toLowerCase();
		du1_stem = cleanDuplication(token, duplicateList);
		pre_stem = cleanPrefix(du1_stem, prefixList);
		rep_stem = cleanRepetition(pre_stem, repetitionList);
		inf_stem = cleanInfix(rep_stem, infixList);
		rep_stem = cleanRepetition(inf_stem, repetitionList);
		suf_stem = cleanSuffix(rep_stem, suffixList);

		du2_stem = cleanDuplication(suf_stem, duplicateList);
		cle_stem = cleanStemmed(du2_stem, cleanersList, repetitionList);
		cle_stem = cleanDuplication(cle_stem, duplicateList);

//		if (cle_stem.contains("-")) {
//			cle_stem = cle_stem.replace("-", "");
//		}

		if (!checkValidation(cle_stem)) {
			du1_stem = cleanDuplication(cle_stem, duplicateList);
			suf_stem = cleanSuffix(du1_stem, suffixList);
			pre_stem = cleanPrefix(suf_stem, prefixList);
			rep_stem = cleanRepetition(pre_stem, repetitionList);
			inf_stem = cleanInfix(rep_stem, infixList);
			rep_stem = cleanRepetition(inf_stem, repetitionList);
			du2_stem = cleanDuplication(rep_stem, duplicateList);
			cle_stem = cleanStemmed(du2_stem, cleanersList, repetitionList);
			cle_stem = cleanDuplication(cle_stem, duplicateList);
		}

		return cle_stem;
	}

	public static String cleanDuplication(String token, List<String> DUPLICATE) throws IOException
	{
		if (checkValidation(token)) {
			return token;
		}

		if (token.contains("-") && token.indexOf("-") != 0 && token.indexOf("-") != token.length() - 1) {
			String[] split = token.split("-");
			if (Arrays.stream(split).allMatch(tok -> tok.length() >= 3)) {
				String partOne = split[0];
				String partTwo = split[1];
				if (partOne.equals(String.valueOf(token.charAt(1))) //
				|| (partOne.endsWith("u") && changeLetter(partOne, -1,'o').equals(partTwo)) //
				|| (partOne.endsWith("u") && changeLetter(partOne, -2, 'o').equals(partTwo)))
				{
					DUPLICATE.add(partOne);
					return partOne;
				} else if (partOne.length() <= partTwo.length() && partOne.equals(partTwo.substring(0, partOne.length()))) {
					DUPLICATE.add(partTwo);
					return partOne;
				} else if (partTwo.length() <= partOne.length() && partOne.contains(partTwo)) {
					DUPLICATE.add(partOne);
					return partOne;
				} else if (partOne.endsWith("ng")) {
					if (partOne.charAt(partOne.length() - 3) == 'u' && ((partOne.substring(0, partOne.length() - 3) + "o").equals(partTwo))) {
							DUPLICATE.add(partTwo);
							return partTwo;

					}
					if (partOne.substring(0, partOne.length() - 2).equals(partTwo)) {
						DUPLICATE.add(partTwo);
						return partTwo;
					}
				} else if (partTwo.endsWith("ng")) {
					if (partTwo.charAt(partTwo.length() - 3) == 'u' && ((partTwo.substring(0, partTwo.length() - 3) + "o").equals(partOne))) {
							DUPLICATE.add(partOne);
							return partOne;

					}
					if (partTwo.substring(0, partTwo.length() - 2).equals(partOne)) {
						DUPLICATE.add(partOne);
						return partOne;
					}
				}
			} else {
				return String.join("-", split);
			}
		}
		return token;
	}

	static String cleanRepetition(String token, List<String> repetitionList) throws IOException
	{
		if (checkValidation(token)) {
			return token;
		}

		try {
			if (token.length() >= 4) {
				if (checkVowel(token.charAt(0))) {
					if (token.charAt(0) == token.charAt(1)) {
						repetitionList.add(String.valueOf(token.charAt(0)));
						String substring = token.substring(1);
						return substring;
					}
				} else if (checkConsonant(String.valueOf(token.charAt(0))) && countVowel(token) >= 2) {
					if (token.substring(0, 2).equals(token.substring(2, 4)) && token.length() - 2 >= 4) {
						repetitionList.add(token.substring(2, 4));
						return token.substring(2);
					} else if (token.length() - 3 >= 4 && token.substring(0, 3).equals(token.substring(3, 6))) {
						repetitionList.add(token.substring(3, 6));
						return token.substring(3);
					}
				}
			}
			return token;
		}
		catch (StringIndexOutOfBoundsException e) {
			System.err.println(e.getMessage());
			System.err.println(token);
			}
		return token;
	}

	static String cleanPrefix(String token, List<String> PREFIX) throws IOException
	{
		if (checkValidation(token)) {
			return token;
		}

		for (String prefix : PREFIX_SET) {
			if (token.length() - prefix.length() >= 3 && countVowel(token.substring(prefix.length())) >= 2) {
				if (prefix.equals("i") && checkConsonant(String.valueOf(token.charAt(2)))) {
					continue;
				}

				if (token.contains("-")) {
					String[] tokenParts = token.split("-");
					if (tokenParts[0].equals(prefix) && checkVowel(tokenParts[1].charAt(0))) {
						PREFIX.add(prefix);
						return tokenParts[1];
					}
					token = String.join("-", tokenParts);
				}

				if (token.startsWith(prefix)) {
					if (countVowel(token.substring(prefix.length())) >= 2) {
						if (prefix.equals("panganga")) {
							PREFIX.add(prefix);
							return "ka" + token.substring(prefix.length());
						}
						PREFIX.add(prefix);
						return token.substring(prefix.length());
					}
				}
			}
		}
		return token;
	}

	static String cleanInfix(String token, List<String> INFIX) throws IOException
	{
		if (checkValidation(token)) {
			return token;
		}

		for (String infix : INFIX_SET) {
			if (token.length() - infix.length() >= 3 && countVowel(token.substring(infix.length())) >= 2) {
				if (token.charAt(0) == token.charAt(4) && token.substring(1, 4).equals(infix)) {
					INFIX.add(infix);
					return token.substring(4);
				} else if (token.charAt(2) == token.charAt(4) && token.substring(1, 3).equals(infix)) {
					INFIX.add(infix);
					return token.charAt(0) + token.substring(3);
				} else if (token.substring(1, 3).equals(infix) && checkVowel(token.charAt(3))) {
					INFIX.add(infix);
					return token.charAt(0) + token.substring(3);
				}
			}
		}
		return token;
	}

	static String cleanSuffix(String token, List<String> SUFFIX) throws IOException
	{
		List<String> SUF_CANDIDATE = new ArrayList<>();

		if (checkValidation(token)) {
			return token;
		}

		for (String suffix : SUFFIX_SET) {
			if (token.length() - suffix.length() >= 3 && countVowel(token.substring(0, token.length() - suffix.length())) >= 2) {
				if (token.substring(token.length() - suffix.length(), token.length()).equals(suffix)) {
					if (suffix.length() == 2 && !(countConsonant(token.substring(0, token.length() - suffix.length())) >= 1)) {
						continue;
					}

					if (countVowel(token.substring(0, token.length() - suffix.length())) >= 2) {
						if (suffix.equals("ang") && checkConsonant(token.substring(token.length() - 4)) && token.charAt(
								token.length() - 4) != 'r' && token.charAt(token.length() - 5) != 'u') {
							continue;
						}

						if (checkValidation(token.substring(0, token.length() - suffix.length()))) {
							SUFFIX.add(suffix);
							return (suffix.equals("ita")) ?
									token.substring(0, token.length() - suffix.length()) + 'a' :
									token.substring(0, token.length() - suffix.length());
						} else if (SUF_CANDIDATE.isEmpty()) {
							SUF_CANDIDATE.add(suffix);
							SUF_CANDIDATE.add(token.substring(0, token.length() - suffix.length()));
						}
					}
				}
			}
		}

		if (SUF_CANDIDATE.size() == 2) {
			SUFFIX.add(SUF_CANDIDATE.get(0));
			final String removedSuffixToken = (SUF_CANDIDATE.get(0).equals("ita")) ?
					SUF_CANDIDATE.get(1).substring(0, token.length() - SUF_CANDIDATE.get(0).length()) + 'a' :
					SUF_CANDIDATE.get(1).substring(0, token.length() - SUF_CANDIDATE.get(0).length());

			return removedSuffixToken;
		}

		return token;
	}

	static boolean checkVowel(char letter)
	{
		return VOWELS.contains(String.valueOf(letter));
	}

	static boolean checkConsonant(String character)
	{
		return CONSONANTS.contains(character);
	}

	static boolean checkConsonant(char character)
	{
		return CONSONANTS.contains(String.valueOf(character));
	}

	static int countVowel(String token)
	{
		int count = 0;
		for (int i = 0; i < token.length(); i++) {
			if (checkVowel(token.charAt(i))) {
				count++;
			}
		}
		return count;
	}

	static int countConsonant(String token)
	{
		int count = 0;
		for (int i = 0; i < token.length(); i++) {
			if (checkConsonant(String.valueOf(token.charAt(i)))) {
				count++;
			}
		}
		return count;
	}

	static String changeLetter(String token, int index, char letter)
	{
		char[] charArray = token.toCharArray();
		if (index < 0) {
			 charArray[(charArray.length + index)] = letter;
			return new String(charArray);
		}
		charArray[index] = letter;
		return new String(charArray);
	}

	static String cleanStemmed(String token, List<String> cleaners, List<String> repetition) throws IOException
	{
		if (checkValidation(token)) {
			return token;
		}

		List<String> CC_EXP = Arrays.asList("dr", "gl", "gr", "ng", "kr", "kl", "kw", "ts", "tr", "pr", "pl", "pw", "sw", "sy");

		if (!checkVowel(token.charAt(token.length() - 1)) && !checkConsonant(token.substring(token.length() - 1))) {
			cleaners.add(String.valueOf(token.charAt(token.length() - 1)));
			token = token.substring(0, token.length() - 1);
		}

		if (!checkVowel(token.charAt(0)) && !checkConsonant(String.valueOf(token.charAt(0)))) {
			cleaners.add(String.valueOf(token.charAt(0)));
			token = token.substring(1);
		}

		if (checkValidation(token)) {
			return token;
		}

		if (token.length() >= 3 && countVowel(token) >= 2) {
			token = cleanRepetition(token, repetition);

			if (checkConsonant(token.charAt(token.length() - 1)) && token.charAt(token.length() - 2) == 'u') {
				cleaners.add("u");
				token = changeLetter(token, token.length() - 2, 'o');
			}

			if (token.endsWith("u")) {
				cleaners.add("u");
				token = changeLetter(token, token.length() - 1, 'o');
			}

			if (token.endsWith("r")) {
				cleaners.add("r");
				token = changeLetter(token, token.length() - 1, 'd');
			}

			if (token.endsWith("h") && checkVowel(token.charAt(token.length() - 1))) {
				cleaners.add("h");
				token = token.substring(0, token.length() - 1);
			}

			if (token.charAt(0) == token.charAt(1)) {
				cleaners.add(String.valueOf(token.charAt(0)));
				token = token.substring(1);
			}

			if ((token.startsWith("ka") || token.startsWith("pa")) && checkConsonant(String.valueOf(token.charAt(2))) && countVowel(
					token) >= 3) {
				cleaners.add(token.substring(0, 2));
				token = token.substring(2);
			}

			if (token.endsWith("han") && countVowel(token.substring(0, token.length() - 3)) == 1) {
				cleaners.add("han");
				token = token.substring(0, token.length() - 3) + "i";
			}

			if (token.endsWith("han") && countVowel(token.substring(0, token.length() - 3)) > 1) {
				cleaners.add("han");
				token = token.substring(0, token.length() - 3);
			}

			if (token.length() >= 2 && countVowel(token) >= 3) {
				if (token.endsWith("h") && checkVowel(token.charAt(token.length() - 2))) {
					cleaners.add("h");
					token = token.substring(0, token.length() - 1);
				}
			}

			if (token.length() >= 6 && token.substring(0, 2).equals(token.substring(2, 4))) {
				cleaners.add("0:2");
				token = token.substring(2);
			}

			for (String REP : repetition) {
				if (REP.charAt(0) == 'r') {
					cleaners.add("r");
					token = changeLetter(token, 0, 'd');
				}
			}

			if (token.endsWith("ng") && token.charAt(token.length() - 3) == 'u') {
				cleaners.add("u");
				token = changeLetter(token, token.length() - 3, 'o');
			}

			if (token.endsWith("h")) {
				cleaners.add("h");
				token = token.substring(0, token.length() - 1);
			}

			if (CC_EXP.stream().noneMatch(token.substring(0, 2)::equals) && checkConsonant(token.substring(0, 2))) {
				cleaners.add(token.substring(0, 2));
				token = token.substring(1);
			}
		}
		return token;
	}

	static boolean checkValidation(String token) throws IOException
	{
		return loadFromStream(token);
	}

	public static boolean loadFromStream(String token) throws IOException
	{
		final String file = "/validation.txt";
		if (rootsForValidating == null) {
			try (InputStream stream = TagalogStemmer.class.getResourceAsStream(file);
					final InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
					BufferedReader in = new BufferedReader(reader);) {
				rootsForValidating = in.lines().map(String::trim).filter(l -> !l.startsWith("#"))
						.filter(StringUtils::isNotBlank).collect(Collectors.toList());
				return rootsForValidating.contains(token);

			} catch (final FileNotFoundException e) {
				throw new FileNotFoundException(file);
			}
		}
		return rootsForValidating.contains(token);
	}
}