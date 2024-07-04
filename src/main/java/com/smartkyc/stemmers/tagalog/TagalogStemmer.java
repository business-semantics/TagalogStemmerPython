package com.smartkyc.stemmers.tagalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TagalogStemmer
{

	private static final Logger log = LoggerFactory.getLogger(TagalogStemmer.class);

	private static final Set<String> ROOTS_FOR_VALIDATING;

	private static final Map<String, String> ORDINAL_NUMBERS_ROOTS;

	private static final Map<String, String> NUMBER_ROOTS;

	private static final Set<String> PREFIX_SET = Collections.unmodifiableSet(new LinkedHashSet<>(
			(Arrays.asList("nakikipag", "pakikipag", "pinakama", "pagpapa", "pinagka", "maisasa", "panganga", "makapag", "makipag",
					"packaging", "tagapag", "makipag", "nakipag", "pinaki", "tigapag", "mangaka", "isinasa", "maisa", "naisa",
					"magsi", "nagsi", "pakiki", "magpa", "napaka", "pinaka", "ipinag", "pagka", "pinag", "mapag", "mapa", "taga",
					"ipag", "tiga", "pala", "pina", "pang", "paki", "naka", "naki", "nang", "mang", "maka", "maki", "sing", "ipa",
					"isa", "pam", "pan", "pag", "tag", "mai", "mag", "nam", "nag", "man", "may", "ma", "na", "ni", "pa", "ka", "um",
					"in", "i"))));

	private static final Set<String> SUFFIX_SET = Collections.unmodifiableSet(
			new LinkedHashSet<>(Arrays.asList("syon", "dor", "ita", "han", "hin", "ing", "aang", "ang", "ng", "an", "in", "g")));

	private static final List<String> EXCEPTIONS = Collections.unmodifiableList(
			Arrays.asList("dr", "gl", "gr", "ng", "kr", "kl", "kw", "ts", "tr", "pr", "pl", "pw", "sw", "sy"));

	private static final String CONSONANTS = "bcdfghklmnngpqrstvwyBCDFGHKLMNNGPQRSTVWY";

	static {
		try (InputStream stream = TagalogStemmer.class.getResourceAsStream(
				"/tagalogWordsRoots.txt")) { //rename this file and move to folder
			if (stream == null) {
				throw new IOException("Unable to create input stream from resource.");
			}
			try (final InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
					BufferedReader in = new BufferedReader(reader)) {
				ROOTS_FOR_VALIDATING = Collections.unmodifiableSet(
						in.lines().map(String::trim).map(String::toLowerCase).filter(l -> !l.startsWith("#"))
								.filter(TagalogStemmer::isNotBlank).collect(Collectors.toSet()));
			}
		} catch (final IOException e) {
			throw new IllegalStateException("Failed to read tagalog roots file", e);
		}

		final Map<String, String> ordinalNumbersRoots = new HashMap<>();
		ordinalNumbersRoots.put("isa", "isa");
		ordinalNumbersRoots.put("lawa", "dalawa");
		ordinalNumbersRoots.put("tlo", "tatlo");
		ordinalNumbersRoots.put("apat", "apat");
		ordinalNumbersRoots.put("lima", "lima");
		ordinalNumbersRoots.put("anim", "anim");
		ordinalNumbersRoots.put("pito", "pito");
		ordinalNumbersRoots.put("walo", "walo");
		ordinalNumbersRoots.put("siyam", "siyam");
		ordinalNumbersRoots.put("sampu", "sampu");
		ORDINAL_NUMBERS_ROOTS = Collections.unmodifiableMap(ordinalNumbersRoots);

		final Map<String, String> numberRoots = new HashMap<>();
		numberRoots.put("dalawa", "dalawa");
		numberRoots.put("tatlu", "tatlo");
		numberRoots.put("apatnapu", "apat");
		numberRoots.put("limampu", "lima");
		numberRoots.put("animnapu", "anim");
		numberRoots.put("pitumpu", "pito");
		numberRoots.put("walumpu", "walo");
		numberRoots.put("siyamnapu", "siyam");
		NUMBER_ROOTS = Collections.unmodifiableMap(numberRoots);
	}

	public String stem(final String token)
	{
		if (isBlank(token) || isInRoots(token)) {
			return token;
		}

		try {
			final List<String> repetitions = new ArrayList<>();
			final List<String> cleaners = new ArrayList<>();

			String cleanedToken = cleanOrdinals(token.toLowerCase());
			cleanedToken = cleanNumbers(cleanedToken.toLowerCase());
			cleanedToken = cleanIPrefix(cleanedToken);
			cleanedToken = cleanDuplication(cleanedToken.toLowerCase());
			cleanedToken = cleanRepeatingSubstrings(cleanedToken);
			cleanedToken = cleanRepetition(cleanedToken, repetitions);
			cleanedToken = cleanPrefixWithDbecomesR(cleanedToken);
			cleanedToken = cleanPrefixWithPBtoM(cleanedToken);
			cleanedToken = cleanPrefixWithSDTtoN(cleanedToken);
			cleanedToken = cleanPrefixWithKtoNg(cleanedToken);
			cleanedToken = cleanPrefix(cleanedToken);
			cleanedToken = cleanPrefix(cleanedToken);
			cleanedToken = cleanInfix(cleanedToken);
			cleanedToken = cleanRepetition(cleanedToken, repetitions);

			cleanedToken = cleanDuplication(cleanedToken);
			cleanedToken = cleanRBetweenVowels(cleanedToken);

			cleanedToken = cleanSuffix(cleanedToken);
			cleanedToken = cleanDuplication(cleanedToken);
			cleanedToken = cleanStemmed(cleanedToken, cleaners, repetitions);

			if (!isInRoots(cleanedToken)) {
				cleanedToken = cleanDuplication(cleanedToken.toLowerCase());
				cleanedToken = cleanRepeatingSubstrings(cleanedToken);
				cleanedToken = cleanRepetition(cleanedToken, repetitions);
				cleanedToken = cleanPrefixWithPBtoM(cleanedToken);
				cleanedToken = cleanPrefixWithSDTtoN(cleanedToken);
				cleanedToken = cleanPrefixWithKtoNg(cleanedToken);
				cleanedToken = cleanPrefix(cleanedToken);
				cleanedToken = cleanPrefix(cleanedToken);
				cleanedToken = cleanInfix(cleanedToken);
				cleanedToken = cleanRepetition(cleanedToken, repetitions);

				cleanedToken = cleanDuplication(cleanedToken);
				cleanedToken = cleanRBetweenVowels(cleanedToken);

				cleanedToken = cleanSuffix(cleanedToken);
				cleanedToken = cleanDuplication(cleanedToken);
				cleanedToken = cleanStemmed(cleanedToken, cleaners, repetitions);
			}

			return cleanedToken;
		} catch (final Exception e) {
			log.debug("Failed to stem token: {}", token, e);
			return token;
		}

	}

	private String cleanRBetweenVowels(String token)
	{
		if (token.length() > 4 && token.startsWith("d") && isVowel(token.charAt(1)) && token.charAt(2) == 'r' && isVowel(
				token.charAt(3))) {
			token = token.charAt(0) + token.substring(3);
			return token;
		}
		return token;
	}

	private String cleanOrdinals(final String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		if (token.startsWith("pangatlo")) {
			return "tatlo";
		}

		if (token.startsWith("ika") && token.length() > 3) {
			String potentialRoot = token.substring(3);
			if (potentialRoot.startsWith("labing") && token.length() > 6) {
				potentialRoot = potentialRoot.replace("-", "");
				potentialRoot = potentialRoot.substring(6);
			}
			if (potentialRoot.startsWith("labin") && token.length() > 6) {
				potentialRoot = potentialRoot.replace("-", "");
				potentialRoot = potentialRoot.substring(6);
			}
			for (final Map.Entry<String, String> entry : ORDINAL_NUMBERS_ROOTS.entrySet()) {
				if (potentialRoot.startsWith(entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		return token;
	}

	private String cleanNumbers(final String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		for (final Map.Entry<String, String> entry : NUMBER_ROOTS.entrySet()) {
			if (token.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return token;
	}

	private String cleanDuplication(final String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		if (token.contains("-") && token.indexOf("-") != 0 && token.indexOf("-") != token.length() - 1) {
			final String[] split = token.split("-");
			if (Arrays.stream(split).allMatch(s -> s.length() >= 3)) {
				final String partOne = split[0];
				final String partTwo = split[1];
				if (partOne.equals(String.valueOf(token.charAt(1))) //
						|| (partOne.endsWith("u") && swapCharAt(partOne, 'o', -1).equals(partTwo)) //
						|| (partOne.endsWith("u") && swapCharAt(partOne, 'o', -2).equals(partTwo)) //
						|| partTwo.length() <= partOne.length() && partOne.contains(partTwo)) {
					return partOne;
				} else if (partOne.length() <= partTwo.length() && partOne.equals(partTwo.substring(0, partOne.length()))) {
					return partOne;
				} else if (partOne.endsWith("ng")) {
					if (partOne.charAt(partOne.length() - 3) == 'u' && ((partOne.substring(0, partOne.length() - 3) + "o").equals(
							partTwo))) {
						return partTwo;
					}
					if (partOne.substring(0, partOne.length() - 2).equals(partTwo)) {
						return partTwo;
					}
				} else if (partTwo.endsWith("ng")) {
					if (partTwo.charAt(partTwo.length() - 3) == 'u' && ((partTwo.substring(0, partTwo.length() - 3) + "o").equals(
							partOne))) {
						return partOne;
					}
					if (partTwo.substring(0, partTwo.length() - 2).equals(partOne)) {
						return partOne;
					}
				}
			} else {
				return String.join("-", split);
			}
		}
		return token;
	}

	private String cleanRepetition(final String token, final List<String> repetitionList)
	{
		if (isInRoots(token)) {
			return token;
		}

		if (token.length() >= 4) {
			if (isVowel(token.charAt(0))) {
				if (token.charAt(0) == token.charAt(1)) {
					repetitionList.add(String.valueOf(token.charAt(0)));
					return token.substring(1);
				}
			} else if (isConsonant(String.valueOf(token.charAt(0))) && countVowel(token) >= 2) {
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

	private String cleanRepeatingSubstrings(String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		for (int i = 1; i < token.length(); i++) {
			final String currentSubstring = token.substring(i - 1, i + 1);

			if (token.length() % currentSubstring.length() == 0 && //
					(currentSubstring.startsWith("m") || currentSubstring.startsWith("n"))) {
				final StringBuilder repeatedString = new StringBuilder();
				repeatedString.append(currentSubstring).append(currentSubstring);
				if (token.contains(repeatedString)) {
					token = token.substring(0, i - 1) + token.substring(i + 1);
					return token;
				}
			}
		}
		return token;
	}

	private String cleanPrefix(String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		for (final String prefix : PREFIX_SET) {
			if (token.length() - prefix.length() >= 3 && countVowel(token.substring(prefix.length())) >= 2) {
				if (prefix.equals("i") && isConsonant(String.valueOf(token.charAt(2)))) {
					continue;
				}
				if (token.contains("-")) {
					final String[] tokenParts = token.split("-");
					if (tokenParts[0].equals(prefix) && isVowel(tokenParts[1].charAt(0))) {
						return tokenParts[1];
					}
					token = String.join("-", tokenParts);
				}

				if (token.startsWith(prefix) && (countVowel(token.substring(prefix.length())) >= 2)) {
					if (prefix.equals("panganga")) {
						return "ka" + token.substring(prefix.length());
					}
					return token.substring(prefix.length());
				}
			}
		}
		return token;
	}

	private String cleanIPrefix(String token)
	{
		if (token.startsWith("i") && isVowel(token.charAt(1)) && isConsonant(token.charAt(2))) {
			token = token.substring(1);
		}
		final String potentialCleanedSuffixForm = cleanSuffix(token);
		if (isInRoots(potentialCleanedSuffixForm)) {
			return potentialCleanedSuffixForm;
		}
		return token;
	}

	private String cleanPrefixWithPBtoM(final String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final Set<String> prefixSet = new LinkedHashSet<>(
				Arrays.asList("magpapaka", "magpaka", "magpapa", "nangaka", "mag", "pama", "maka", "naka", "na", "ma", "pa", "ka",
						"ika", "kina", "pagka", "pakikipa"));

		for (final String prefix : prefixSet) {
			if (token.length() - 2 >= 3 && countVowel(token.substring(2)) >= 2) {
				if ((token.startsWith(prefix) && token.charAt(prefix.length()) == 'm')) {
					final String tokenWithoutPrefix = token.substring(prefix.length());
					if (isInRoots(tokenWithoutPrefix)) {
						return tokenWithoutPrefix;
					}
					final String potentialFormSwapP = revertToRoot(tokenWithoutPrefix, 'p');
					if (!potentialFormSwapP.equals(tokenWithoutPrefix)) {
						return potentialFormSwapP;
					}
					final String potentialFormSwapB = revertToRoot(tokenWithoutPrefix, 'b');
					if (!potentialFormSwapB.equals(tokenWithoutPrefix)) {
						return potentialFormSwapB;
					}
				}
			}
		}
		return token;
	}

	private String revertToRoot(final String tokenWithoutPrefix, final char potentialChar)
	{
		if (isInRoots(tokenWithoutPrefix)) {
			return tokenWithoutPrefix;
		}
		final String potentialForm = swapCharAt(tokenWithoutPrefix, potentialChar, 0);
		if (isInRoots(potentialForm)) {
			return potentialForm;
		}
		final String potentialFormWithoutSuffix = cleanSuffix(potentialForm);
		if (isInRoots(potentialFormWithoutSuffix)) {
			return potentialFormWithoutSuffix;
		}
		return tokenWithoutPrefix;
	}

	private String cleanPrefixWithSDTtoN(final String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final Set<String> prefixSet = new LinkedHashSet<>(Arrays.asList("mana", "pana", "nana", "ma", "pa", "na"));
		for (final String prefix : prefixSet) {
			if (token.length() - 2 >= 3 && countVowel(token.substring(2)) >= 2) {

				if ((token.startsWith(prefix)) && token.charAt(prefix.length()) == 'n') {
					final String tokenWithoutPrefix = token.substring(prefix.length());
					if (isInRoots(tokenWithoutPrefix)) {
						return tokenWithoutPrefix;
					}
					final String potentialFormSwapS = revertToRoot(tokenWithoutPrefix, 's');
					if (!potentialFormSwapS.equals(tokenWithoutPrefix)) {
						return potentialFormSwapS;
					}
					final String potentialFormSwapD = revertToRoot(tokenWithoutPrefix, 'd');
					if (!potentialFormSwapD.equals(tokenWithoutPrefix)) {
						return potentialFormSwapD;
					}
					final String potentialFormSwapT = revertToRoot(tokenWithoutPrefix, 't');
					if (!potentialFormSwapT.equals(tokenWithoutPrefix)) {
						return potentialFormSwapT;
					}
				}
			}
		}
		return token;
	}

	private String cleanPrefixWithDbecomesR(final String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final Set<String> prefixSet = new LinkedHashSet<>(
				Arrays.asList("magpa", "nagpa", "kina", "maka", "naka", "mapa", "ipa", "napa", "ka", "ma"));
		for (final String prefix : prefixSet) {
			if (token.length() - 2 >= 3 && countVowel(token.substring(2)) >= 2) {
				if ((token.startsWith(prefix)) && token.charAt(prefix.length()) == 'r') {
					final String tokenWithoutPrefix = token.substring(prefix.length());
					if (isInRoots(tokenWithoutPrefix)) {
						return tokenWithoutPrefix;
					}
					final String potentialFormSwapS = revertToRoot(tokenWithoutPrefix, 'd');
					if (!potentialFormSwapS.equals(tokenWithoutPrefix)) {
						return potentialFormSwapS;
					}
				}
			}
		}
		return token;
	}

	private String cleanPrefixWithKtoNg(final String token)
	{
		if (isInRoots(token)) {
			return token;
		}
		if (token.length() - 2 >= 3 && countVowel(token.substring(2)) >= 2) {
			if ((token.startsWith("ma") || token.startsWith("na") || token.startsWith("pa")) //
					&& token.charAt(2) == 'n' && token.charAt(3) == 'g') {
				String potentialForm = token.substring(3);
				if (isInRoots(potentialForm)) {
					return potentialForm;
				}
				potentialForm = swapCharAt(potentialForm, 'k', 0);
				if (isInRoots(potentialForm)) {
					return potentialForm;
				}
			}
		}
		return token;
	}

	private String cleanInfix(final String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final Set<String> infixSet = new LinkedHashSet<>(Arrays.asList("um", "in"));

		for (final String infix : infixSet) {
			if (token.length() - infix.length() >= 3 && countVowel(token.substring(infix.length())) >= 2) {
				if (token.charAt(0) == token.charAt(4) && token.substring(1, 4).equals(infix)) {
					return token.substring(4);
				} else if (token.charAt(2) == token.charAt(4) && token.substring(1, 3).equals(infix) //
						|| token.substring(1, 3).equals(infix) && isVowel(token.charAt(3))) {
					return token.charAt(0) + token.substring(3);
				}
			}
		}
		return token;
	}

	private String cleanSuffix(final String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final List<String> suffixCandidates = new ArrayList<>();
		if (token.contains("syon") && token.endsWith("ng")) {
			final String candidate = token.substring(0, token.length() - 1);
			if (isInRoots(candidate)) {
				return candidate;
			}
		}

		for (final String suffix : SUFFIX_SET) {
			if (token.length() - suffix.length() >= 3) //
			{
				final String substring = token.substring(0, token.length() - suffix.length());
				if (countVowel(substring) >= 2 && (token.endsWith(suffix))) {
					if (suffix.length() == 2 && (countConsonant(substring) < 1)) {
						continue;
					}
					if (countVowel(substring) >= 2) {
						if (suffix.equals("ang") && isConsonant(token.substring(token.length() - 4)) && token.charAt(
								token.length() - 4) != 'r' && token.charAt(token.length() - 5) != 'u') {
							continue;
						}

						if (isInRoots(substring)) {
							return (suffix.equals("ita")) ? substring + 'a' : substring;
						} else if (suffixCandidates.isEmpty()) {
							suffixCandidates.add(suffix);
							suffixCandidates.add(substring);
						}
					}
				}
			}
		}

		if (suffixCandidates.size() == 2) {
			return (suffixCandidates.get(0).equals("ita")) ?
					suffixCandidates.get(1).substring(0, token.length() - suffixCandidates.get(0).length()) + 'a' :
					suffixCandidates.get(1).substring(0, token.length() - suffixCandidates.get(0).length());
		}

		return token;
	}

	private boolean isVowel(final char letter)
	{
		final String vowels = "aeiouAEIOU";
		return vowels.contains(String.valueOf(letter));
	}

	private boolean isConsonant(final String character)
	{
		return CONSONANTS.contains(character);
	}

	private boolean isConsonant(final char character)
	{
		return CONSONANTS.contains(String.valueOf(character));
	}

	private int countVowel(final String token)
	{
		int count = 0;
		for (int i = 0; i < token.length(); i++) {
			if (isVowel(token.charAt(i))) {
				count++;
			}
		}
		return count;
	}

	private int countConsonant(final String token)
	{
		int count = 0;
		for (int i = 0; i < token.length(); i++) {
			if (isConsonant(String.valueOf(token.charAt(i)))) {
				count++;
			}
		}
		return count;
	}

	private String swapCharAt(final String token, final char letter, final int index)
	{
		final char[] charArray = token.toCharArray();
		if (index < 0) {
			charArray[(charArray.length + index)] = letter;
			return new String(charArray);
		}
		charArray[index] = letter;
		return new String(charArray);
	}

	private String cleanStemmed(String token, final List<String> cleaners, final List<String> repetition)
	{
		if (isInRoots(token)) {
			return token;
		}

		if (!isVowel(token.charAt(token.length() - 1)) && !isConsonant(token.substring(token.length() - 1))) {
			cleaners.add(String.valueOf(token.charAt(token.length() - 1)));
			token = token.substring(0, token.length() - 1);
		}

		if (!isVowel(token.charAt(0)) && !isConsonant(String.valueOf(token.charAt(0)))) {
			cleaners.add(String.valueOf(token.charAt(0)));
			token = token.substring(1);
		}

		if (isInRoots(token)) {
			return token;
		}

		if (token.length() >= 3 && countVowel(token) >= 2) {
			token = cleanRepetition(token, repetition);

			if (isConsonant(token.charAt(token.length() - 1)) && token.charAt(token.length() - 2) == 'u') {
				cleaners.add("u");
				token = swapCharAt(token, 'o', token.length() - 2);
			}

			if (token.endsWith("u")) {
				cleaners.add("u");
				token = swapCharAt(token, 'o', token.length() - 1);
			}

			if (token.endsWith("r")) {
				cleaners.add("r");
				token = swapCharAt(token, 'd', token.length() - 1);
			}

			if (token.endsWith("h") && isVowel(token.charAt(token.length() - 1))) {
				cleaners.add("h");
				token = token.substring(0, token.length() - 1);
			}

			if (token.charAt(0) == token.charAt(1)) {
				cleaners.add(String.valueOf(token.charAt(0)));
				token = token.substring(1);
			}

			if ((token.startsWith("ka") || token.startsWith("pa")) && isConsonant(String.valueOf(token.charAt(2))) && countVowel(
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

			if (token.length() >= 2 && countVowel(token) >= 3 && (token.endsWith("h") && isVowel(
					token.charAt(token.length() - 2)))) {
				cleaners.add("h");
				token = token.substring(0, token.length() - 1);

			}

			if (token.length() >= 6 && token.substring(0, 2).equals(token.substring(2, 4))) {
				cleaners.add("0:2");
				token = token.substring(2);
			}

			for (final String REP : repetition) {
				if (REP.charAt(0) == 'r') {
					cleaners.add("r");
					token = swapCharAt(token, 'd', 0);
				}
			}

			if (token.endsWith("ng") && token.charAt(token.length() - 3) == 'u') {
				cleaners.add("u");
				token = swapCharAt(token, 'o', token.length() - 3);
			}

			if (token.endsWith("h")) {
				cleaners.add("h");
				token = token.substring(0, token.length() - 1);
			}

			if (token.startsWith("i") && isVowel(token.charAt(1)) && isConsonant(token.charAt(2))) {
				token = token.substring(1);
			}

			if (EXCEPTIONS.stream().noneMatch(token.substring(0, 2)::equals) && isConsonant(token.substring(0, 2))) {
				cleaners.add(token.substring(0, 2));
				token = token.substring(1);
			}
		}
		return token;
	}

	private boolean isInRoots(final String token)
	{
		return ROOTS_FOR_VALIDATING.contains(token.toLowerCase());
	}

	private static boolean isNotBlank(final String str)
	{
		return !isBlank(str);
	}

	private static boolean isBlank(final String str)
	{
		final int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((!Character.isWhitespace(str.charAt(i)))) {
				return false;
			}
		}
		return true;
	}
}