package com.smartkyc.stemmers.tagalog;

import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
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

	public static String stem(String token)
	{

		String prefixStem;
		String infixStem;
		String suffixStem;
		String repetitionStem;
		String dulicateStemFirstPass;
		String dulicateStemSecondPass;
		String finalStem;

		List<String> prefixList = new ArrayList<>();
		List<String> infixList = new ArrayList<>();
		List<String> suffixList = new ArrayList<>();
		List<String> duplicateList = new ArrayList<>();
		List<String> repetitionList = new ArrayList<>();
		List<String> cleanersList = new ArrayList<>();


		token = token.toLowerCase();
		dulicateStemFirstPass = cleanDuplication(token, duplicateList);
		prefixStem = cleanPrefix(dulicateStemFirstPass, prefixList);
		repetitionStem = cleanRepetition(prefixStem, repetitionList);
		infixStem = cleanInfix(repetitionStem, infixList);
		repetitionStem = cleanRepetition(infixStem, repetitionList);
		suffixStem = cleanSuffix(repetitionStem, suffixList);

		dulicateStemSecondPass = cleanDuplication(suffixStem, duplicateList);
		finalStem = cleanStemmed(dulicateStemSecondPass, cleanersList, repetitionList);
		finalStem = cleanDuplication(finalStem, duplicateList);

		if (!isInRoots(finalStem)) {
			dulicateStemFirstPass = cleanDuplication(finalStem, duplicateList);
			suffixStem = cleanSuffix(dulicateStemFirstPass, suffixList);
			prefixStem = cleanPrefix(suffixStem, prefixList);
			repetitionStem = cleanRepetition(prefixStem, repetitionList);
			infixStem = cleanInfix(repetitionStem, infixList);
			repetitionStem = cleanRepetition(infixStem, repetitionList);
			dulicateStemSecondPass = cleanDuplication(repetitionStem, duplicateList);
			finalStem = cleanStemmed(dulicateStemSecondPass, cleanersList, repetitionList);
			finalStem = cleanDuplication(finalStem, duplicateList);
		}

		return finalStem;
	}

	private static String cleanDuplication(String token, List<String> duplicates)
	{
		if (isInRoots(token)) {
			return token;
		}

		if (token.contains("-") && token.indexOf("-") != 0 && token.indexOf("-") != token.length() - 1) {
			String[] split = token.split("-");
			if (Arrays.stream(split).allMatch(tok -> tok.length() >= 3)) {
				String partOne = split[0];
				String partTwo = split[1];
				if (partOne.equals(String.valueOf(token.charAt(1))) //
				|| (partOne.endsWith("u") && changeLetter(partOne, -1,'o').equals(partTwo)) //
				|| (partOne.endsWith("u") && changeLetter(partOne, -2, 'o').equals(partTwo)) //
					|| partTwo.length() <= partOne.length() && partOne.contains(partTwo))
				{
					duplicates.add(partOne);
					return partOne;
				} else if (partOne.length() <= partTwo.length() && partOne.equals(partTwo.substring(0, partOne.length()))) {
					duplicates.add(partTwo);
					return partOne;
				} else if (partOne.endsWith("ng")) {
					if (partOne.charAt(partOne.length() - 3) == 'u' && ((partOne.substring(0, partOne.length() - 3) + "o").equals(partTwo))) {
							duplicates.add(partTwo);
							return partTwo;
					}
					if (partOne.substring(0, partOne.length() - 2).equals(partTwo)) {
						duplicates.add(partTwo);
						return partTwo;
					}
				} else if (partTwo.endsWith("ng")) {
					if (partTwo.charAt(partTwo.length() - 3) == 'u' && ((partTwo.substring(0, partTwo.length() - 3) + "o").equals(partOne))) {
							duplicates.add(partOne);
							return partOne;

					}
					if (partTwo.substring(0, partTwo.length() - 2).equals(partOne)) {
						duplicates.add(partOne);
						return partOne;
					}
				}
			} else {
				return String.join("-", split);
			}
		}
		return token;
	}

	private static String cleanRepetition(String token, List<String> repetitionList)
	{
		if (isInRoots(token)) {
			return token;
		}

		if (token.length() >= 4) {
			if (checkVowel(token.charAt(0))) {
				if (token.charAt(0) == token.charAt(1)) {
					repetitionList.add(String.valueOf(token.charAt(0)));
					return token.substring(1);
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

	private static String cleanPrefix(String token, List<String> prefixes)
	{
		if (isInRoots(token)) {
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
						prefixes.add(prefix);
						return tokenParts[1];
					}
					token = String.join("-", tokenParts);
				}

				if (token.startsWith(prefix) && (countVowel(token.substring(prefix.length())) >= 2)) {
						if (prefix.equals("panganga")) {
							prefixes.add(prefix);
							return "ka" + token.substring(prefix.length());
						}
						prefixes.add(prefix);
						return token.substring(prefix.length());
					}
			}
		}
		return token;
	}

	private static String cleanInfix(String token, List<String> infixes)
	{
		if (isInRoots(token)) {
			return token;
		}

		for (String infix : INFIX_SET) {
			if (token.length() - infix.length() >= 3 && countVowel(token.substring(infix.length())) >= 2) {
				if (token.charAt(0) == token.charAt(4) && token.substring(1, 4).equals(infix)) {
					infixes.add(infix);
					return token.substring(4);
				} else if (token.charAt(2) == token.charAt(4) && token.substring(1, 3).equals(infix) //
						|| token.substring(1, 3).equals(infix) && checkVowel(token.charAt(3))) {
					infixes.add(infix);
					return token.charAt(0) + token.substring(3);
				}
			}
		}
		return token;
	}

	private static String cleanSuffix(String token, List<String> suffixes)
	{
		List<String> suffixCandidates = new ArrayList<>();

		if (isInRoots(token)) {
			return token;
		}

		for (String suffix : SUFFIX_SET) {
			if (token.length() - suffix.length() >= 3 //
				&& countVowel(token.substring(0, token.length() - suffix.length())) >= 2 //
				&& (token.endsWith(suffix))) {
				if (suffix.length() == 2 && (countConsonant(token.substring(0, token.length() - suffix.length())) < 1)) {
					continue;
				}

				if (countVowel(token.substring(0, token.length() - suffix.length())) >= 2) {
					if (suffix.equals("ang") && checkConsonant(token.substring(token.length() - 4)) && token.charAt(
							token.length() - 4) != 'r' && token.charAt(token.length() - 5) != 'u') {
						continue;
					}

					if (isInRoots(token.substring(0, token.length() - suffix.length()))) {
						suffixes.add(suffix);
						return (suffix.equals("ita")) ?
								token.substring(0, token.length() - suffix.length()) + 'a' :
								token.substring(0, token.length() - suffix.length());
					} else if (suffixCandidates.isEmpty()) {
						suffixCandidates.add(suffix);
						suffixCandidates.add(token.substring(0, token.length() - suffix.length()));
					}
				}
			}
		}

		if (suffixCandidates.size() == 2) {
			suffixes.add(suffixCandidates.get(0));

			return (suffixCandidates.get(0).equals("ita")) ?
					suffixCandidates.get(1).substring(0, token.length() - suffixCandidates.get(0).length()) + 'a' :
					suffixCandidates.get(1).substring(0, token.length() - suffixCandidates.get(0).length());
		}

		return token;
	}

	private static boolean checkVowel(char letter)
	{
		return VOWELS.contains(String.valueOf(letter));
	}

	private static boolean checkConsonant(String character)
	{
		return CONSONANTS.contains(character);
	}

	private static boolean checkConsonant(char character)
	{
		return CONSONANTS.contains(String.valueOf(character));
	}

	private static int countVowel(String token)
	{
		int count = 0;
		for (int i = 0; i < token.length(); i++) {
			if (checkVowel(token.charAt(i))) {
				count++;
			}
		}
		return count;
	}

	private static int countConsonant(String token)
	{
		int count = 0;
		for (int i = 0; i < token.length(); i++) {
			if (checkConsonant(String.valueOf(token.charAt(i)))) {
				count++;
			}
		}
		return count;
	}

	private static String changeLetter(String token, int index, char letter)
	{
		char[] charArray = token.toCharArray();
		if (index < 0) {
			 charArray[(charArray.length + index)] = letter;
			return new String(charArray);
		}
		charArray[index] = letter;
		return new String(charArray);
	}

	private static String cleanStemmed(String token, List<String> cleaners, List<String> repetition)
	{
		if (isInRoots(token)) {
			return token;
		}

		List<String> exceptions = Arrays.asList("dr", "gl", "gr", "ng", "kr", "kl", "kw", "ts", "tr", "pr", "pl", "pw", "sw", "sy");

		if (!checkVowel(token.charAt(token.length() - 1)) && !checkConsonant(token.substring(token.length() - 1))) {
			cleaners.add(String.valueOf(token.charAt(token.length() - 1)));
			token = token.substring(0, token.length() - 1);
		}

		if (!checkVowel(token.charAt(0)) && !checkConsonant(String.valueOf(token.charAt(0)))) {
			cleaners.add(String.valueOf(token.charAt(0)));
			token = token.substring(1);
		}

		if (isInRoots(token)) {
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

			if (token.length() >= 2 && countVowel(token) >= 3 && (token.endsWith("h") && checkVowel(token.charAt(token.length() - 2)))) {
					cleaners.add("h");
					token = token.substring(0, token.length() - 1);

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

			if (exceptions.stream().noneMatch(token.substring(0, 2)::equals) && checkConsonant(token.substring(0, 2))) {
				cleaners.add(token.substring(0, 2));
				token = token.substring(1);
			}
		}
		return token;
	}

	private static boolean isInRoots(String token)
	{
		return loadFromStream(token);
	}

	private static boolean loadFromStream(String token)
	{
		final String file = "/validation.txt";
		if (rootsForValidating == null) {
			try (InputStream stream = TagalogStemmer.class.getResourceAsStream(file);
					final InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
					BufferedReader in = new BufferedReader(reader)) {
				rootsForValidating = in.lines().map(String::trim).filter(l -> !l.startsWith("#"))
						.filter(StringUtils::isNotBlank).collect(Collectors.toList());
				return rootsForValidating.contains(token);

			} catch (final IOException e) {
				log.error(e.getMessage());
			}
		}
		return rootsForValidating.contains(token);
	}
}