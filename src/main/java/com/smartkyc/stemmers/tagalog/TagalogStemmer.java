package com.smartkyc.stemmers.tagalog;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class TagalogStemmer
{
	public TagalogStemmer() throws IOException
	{
		try (InputStream stream = TagalogStemmer.class.getResourceAsStream("/roots.txt")) {
			if (stream == null) {
				throw new IOException("Unable to create input stream from resource.");
			}
			try (final InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
					BufferedReader in = new BufferedReader(reader)) {
				rootsForValidating = in.lines().map(String::trim).map(String::toLowerCase).filter(l -> !l.startsWith("#"))
						.filter(StringUtils::isNotBlank).collect(Collectors.toList());
			}
		}
	}

	private List<String> rootsForValidating;

	private final String CONSONANTS = "bcdfghklmnngpqrstvwyBCDFGHKLMNNGPQRSTVWY";

	public String stem(String token)
	{
		List<String> repetitions = new ArrayList<>();
		List<String> cleaners = new ArrayList<>();

		if (isInRoots(token)) {
			return token;
		}

		token = cleanOrdinals(token.toLowerCase());
		token = cleanNumbers(token.toLowerCase());
		token = cleanIPrefix(token);
		token = cleanDuplication(token.toLowerCase());
		token = cleanRepeatingSubstrings(token);
		token = cleanRepetition(token, repetitions);
		token = cleanPrefixWithDbecomesR(token);
		token = cleanPrefixWithPBtoM(token);
		token = cleanPrefixWithSDTtoN(token);
		token = cleanPrefixWithKtoNg(token);
		token = cleanPrefix(token);
		token = cleanPrefix(token);
		token = cleanInfix(token);
		token = cleanRepetition(token, repetitions);

		token = cleanDuplication(token);
		token = cleanRBetweenVowels(token);

		token = cleanSuffix(token);
		token = cleanDuplication(token);
		token = cleanStemmed(token, cleaners, repetitions);

		if (!isInRoots(token)) {
			token = cleanDuplication(token.toLowerCase());
			token = cleanRepeatingSubstrings(token);
			token = cleanRepetition(token, repetitions);
			token = cleanPrefixWithPBtoM(token);
			token = cleanPrefixWithSDTtoN(token);
			token = cleanPrefixWithKtoNg(token);
			token = cleanPrefix(token);
			token = cleanPrefix(token);
			token = cleanInfix(token);
			token = cleanRepetition(token, repetitions);

			token = cleanDuplication(token);
			token = cleanRBetweenVowels(token);

			token = cleanSuffix(token);
			token = cleanDuplication(token);
			token = cleanStemmed(token, cleaners, repetitions);
		}

		return token;
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

	private String cleanOrdinals(String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final Map<String, String> numberRoots = new HashMap<>();
		numberRoots.put("isa", "isa");
		numberRoots.put("lawa", "dalawa");
		numberRoots.put("tlo", "tatlo");
		numberRoots.put("apat", "apat");
		numberRoots.put("lima", "lima");
		numberRoots.put("anim", "anim");
		numberRoots.put("pito", "pito");
		numberRoots.put("walo", "walo");
		numberRoots.put("siyam", "siyam");
		numberRoots.put("sampu", "sampu");

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
			for (Map.Entry<String, String> entry : numberRoots.entrySet()) {
				if (potentialRoot.startsWith(entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		return token;
	}

	private String cleanNumbers(String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final Map<String, String> numberRoots = new HashMap<>();
		numberRoots.put("dalawa", "dalawa");
		numberRoots.put("tatlu", "tatlo");
		numberRoots.put("apatnapu", "apat");
		numberRoots.put("limampu", "lima");
		numberRoots.put("animnapu", "anim");
		numberRoots.put("pitumpu", "pito");
		numberRoots.put("walumpu", "walo");
		numberRoots.put("siyamnapu", "siyam");
		for (Map.Entry<String, String> entry : numberRoots.entrySet()) {
			if (token.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return token;
	}

	private String cleanDuplication(String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		if (token.contains("-") && token.indexOf("-") != 0 && token.indexOf("-") != token.length() - 1) {
			String[] split = token.split("-");
			if (Arrays.stream(split).allMatch(s -> s.length() >= 3)) {
				String partOne = split[0];
				String partTwo = split[1];
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

	private String cleanRepetition(String token, List<String> repetitionList)
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
			String currentSubstring = token.substring(i - 1, i + 1);

			if (token.length() % currentSubstring.length() == 0 && //
					(currentSubstring.startsWith("m") || currentSubstring.startsWith("n"))) {
				StringBuilder repeatedString = new StringBuilder();
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
		final Set<String> prefixSet = Sets.newLinkedHashSet(
				Arrays.asList("nakikipag", "pakikipag", "pinakama", "pagpapa", "pinagka", "maisasa", "panganga", "makapag",
						"makipag", "packaging", "tagapag", "makipag", "nakipag", "pinaki", "tigapag", "mangaka", "isinasa", "maisa",
						"naisa", "magsi", "nagsi", "pakiki", "magpa", "napaka", "pinaka", "ipinag", "pagka", "pinag", "mapag",
						"mapa", "taga", "ipag", "tiga", "pala", "pina", "pang", "paki", "naka", "naki", "nang", "mang", "maka",
						"maki", "sing", "ipa", "isa", "pam", "pan", "pag", "tag", "mai", "mag", "nam", "nag", "man", "may", "ma",
						"na", "ni", "pa", "ka", "um", "in", "i"));

		if (isInRoots(token)) {
			return token;
		}

		for (String prefix : prefixSet) {
			if (token.length() - prefix.length() >= 3 && countVowel(token.substring(prefix.length())) >= 2) {
				if (prefix.equals("i") && isConsonant(String.valueOf(token.charAt(2)))) {
					continue;
				}
				if (token.contains("-")) {
					String[] tokenParts = token.split("-");
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
		String potentialCleanedSuffixForm = cleanSuffix(token);
		if (isInRoots(potentialCleanedSuffixForm)) {
			return potentialCleanedSuffixForm;
		}
		return token;
	}

	private String cleanPrefixWithPBtoM(String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final Set<String> prefixSet = Sets.newLinkedHashSet(
				Arrays.asList("magpapaka", "magpaka", "magpapa", "nangaka", "mag", "pama", "maka", "naka", "na", "ma", "pa", "ka",
						"ika", "kina", "pagka", "pakikipa"));

		for (String prefix : prefixSet) {
			if (token.length() - 2 >= 3 && countVowel(token.substring(2)) >= 2) {
				if ((token.startsWith(prefix) && token.charAt(prefix.length()) == 'm')) {
					String tokenWithoutPrefix = token.substring(prefix.length());
					if (isInRoots(tokenWithoutPrefix)) {
						return tokenWithoutPrefix;
					}
					String potentialFormSwapP = revertToRoot(tokenWithoutPrefix, 'p');
					if (!potentialFormSwapP.equals(tokenWithoutPrefix)) {
						return potentialFormSwapP;
					}
					String potentialFormSwapB = revertToRoot(tokenWithoutPrefix, 'b');
					if (!potentialFormSwapB.equals(tokenWithoutPrefix)) {
						return potentialFormSwapB;
					}
				}
			}
		}
		return token;
	}

	private String revertToRoot(String tokenWithoutPrefix, char potentialChar)
	{
		if (isInRoots(tokenWithoutPrefix)) {
			return tokenWithoutPrefix;
		}
		String potentialForm = swapCharAt(tokenWithoutPrefix, potentialChar, 0);
		if (isInRoots(potentialForm)) {
			return potentialForm;
		}
		String potentialFormWithoutSuffix = cleanSuffix(potentialForm);
		if (isInRoots(potentialFormWithoutSuffix)) {
			return potentialFormWithoutSuffix;
		}
		return tokenWithoutPrefix;
	}

	private String cleanPrefixWithSDTtoN(String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final Set<String> prefixSet = Sets.newLinkedHashSet(Arrays.asList("mana", "pana", "nana", "ma", "pa", "na"));
		for (String prefix : prefixSet) {
			if (token.length() - 2 >= 3 && countVowel(token.substring(2)) >= 2) {

				if ((token.startsWith(prefix)) && token.charAt(prefix.length()) == 'n') {
					String tokenWithoutPrefix = token.substring(prefix.length());
					if (isInRoots(tokenWithoutPrefix)) {
						return tokenWithoutPrefix;
					}
					String potentialFormSwapS = revertToRoot(tokenWithoutPrefix, 's');
					if (!potentialFormSwapS.equals(tokenWithoutPrefix)) {
						return potentialFormSwapS;
					}
					String potentialFormSwapD = revertToRoot(tokenWithoutPrefix, 'd');
					if (!potentialFormSwapD.equals(tokenWithoutPrefix)) {
						return potentialFormSwapD;
					}
					String potentialFormSwapT = revertToRoot(tokenWithoutPrefix, 't');
					if (!potentialFormSwapT.equals(tokenWithoutPrefix)) {
						return potentialFormSwapT;
					}
				}
			}
		}
		return token;
	}

	private String cleanPrefixWithDbecomesR(String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final Set<String> prefixSet = Sets.newLinkedHashSet(
				Arrays.asList("magpa", "nagpa", "kina", "maka", "naka", "mapa", "ipa", "napa", "ka", "ma"));
		for (String prefix : prefixSet) {
			if (token.length() - 2 >= 3 && countVowel(token.substring(2)) >= 2) {
				if ((token.startsWith(prefix)) && token.charAt(prefix.length()) == 'r') {
					String tokenWithoutPrefix = token.substring(prefix.length());
					if (isInRoots(tokenWithoutPrefix)) {
						return tokenWithoutPrefix;
					}
					String potentialFormSwapS = revertToRoot(tokenWithoutPrefix, 'd');
					if (!potentialFormSwapS.equals(tokenWithoutPrefix)) {
						return potentialFormSwapS;
					}
				}
			}
		}
		return token;
	}

	private String cleanPrefixWithKtoNg(String token)
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

	private String cleanInfix(String token)
	{
		if (isInRoots(token)) {
			return token;
		}

		final Set<String> infixSet = Sets.newLinkedHashSet(Arrays.asList("um", "in"));

		for (String infix : infixSet) {
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

	private String cleanSuffix(String token)
	{
		List<String> suffixCandidates = new ArrayList<>();

		final Set<String> suffixSet = Sets.newLinkedHashSet(
				Arrays.asList("syon", "dor", "ita", "han", "hin", "ing", "aang", "ang", "ng", "an", "in", "g"));

		if (isInRoots(token)) {
			return token;
		}
		if (token.contains("syon") && token.endsWith("ng")) {
			String candidate = token.substring(0, token.length() - 1);
			if (isInRoots(candidate)) {
				return candidate;
			}
		}

		for (String suffix : suffixSet) {
			if (token.length() - suffix.length() >= 3) //
			{
				String substring = token.substring(0, token.length() - suffix.length());
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

	private boolean isVowel(char letter)
	{
		String VOWELS = "aeiouAEIOU";
		return VOWELS.contains(String.valueOf(letter));
	}

	private boolean isConsonant(String character)
	{
		return CONSONANTS.contains(character);
	}

	private boolean isConsonant(char character)
	{
		return CONSONANTS.contains(String.valueOf(character));
	}

	private int countVowel(String token)
	{
		int count = 0;
		for (int i = 0; i < token.length(); i++) {
			if (isVowel(token.charAt(i))) {
				count++;
			}
		}
		return count;
	}

	private int countConsonant(String token)
	{
		int count = 0;
		for (int i = 0; i < token.length(); i++) {
			if (isConsonant(String.valueOf(token.charAt(i)))) {
				count++;
			}
		}
		return count;
	}

	private String swapCharAt(String token, char letter, int index)
	{
		char[] charArray = token.toCharArray();
		if (index < 0) {
			charArray[(charArray.length + index)] = letter;
			return new String(charArray);
		}
		charArray[index] = letter;
		return new String(charArray);
	}

	private String cleanStemmed(String token, List<String> cleaners, List<String> repetition)
	{
		if (isInRoots(token)) {
			return token;
		}

		List<String> exceptions = Arrays.asList("dr", "gl", "gr", "ng", "kr", "kl", "kw", "ts", "tr", "pr", "pl", "pw", "sw", "sy");

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

			for (String REP : repetition) {
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

			if (exceptions.stream().noneMatch(token.substring(0, 2)::equals) && isConsonant(token.substring(0, 2))) {
				cleaners.add(token.substring(0, 2));
				token = token.substring(1);
			}
		}
		return token;
	}

	private boolean isInRoots(String token)
	{
		final String file = "/roots.txt";
		if (rootsForValidating == null) {
			try (InputStream stream = TagalogStemmer.class.getResourceAsStream(file);
					final InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
					BufferedReader in = new BufferedReader(reader)) {
				rootsForValidating = in.lines().map(String::trim).map(String::toLowerCase).filter(l -> !l.startsWith("#"))
						.filter(StringUtils::isNotBlank).collect(Collectors.toList());
				return rootsForValidating.contains(token.toLowerCase());
			} catch (final IOException e) {
				log.error(e.getMessage());
			}
		}
		return rootsForValidating.contains(token.toLowerCase());
	}
}