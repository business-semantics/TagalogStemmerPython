package com.smartkyc.stemmers.tagalog;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.smartkyc.stemmers.tagalog.TagalogStemmer.stem;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagalogStemmerTest
{
	@Test
	void testTagalog()
	{
		String stem1 = stem("aariing");
		assertEquals("ari", stem1);
		String stem2 = stem("aabot");
		assertEquals("abot", stem2);
		String stem3 = stem("aabutin");
		assertEquals("abot", stem3);
		String stem4 = stem("aalamin");
		assertEquals("alam", stem4);
		String stem5 = stem("aalis");
		assertEquals("alis", stem5);
		String stem6 = stem("aalisin");
		assertEquals("alis", stem6);
		String stem7 = stem("akitin");
		assertEquals("akit", stem7);
		String stem8 = stem("aklasan");
		assertEquals("aklas", stem8);
		String stem9 = stem("pag-aalagang");
		assertEquals("alaga", stem9);
		String stem10 = stem("aalalahanin");
		assertEquals("alala", stem10);
		String stem11 = stem("aalalayan");
		assertEquals("alay", stem11);
		String stem12 = stem("kahuli-hulihang");
		assertEquals("huli", stem12);
		String stem13 = stem("aklasan");
		assertEquals("aklas", stem13);
		String stem14 = stem("balikan");
		assertEquals("balik", stem14);
		String stem15 = stem("dalawang");
		assertEquals("dalawa", stem15);
		String stem16 = stem("buo-buong");
		assertEquals("buo", stem16);
		String stem17 = stem("bumubuhay");
		assertEquals("buhay", stem17);
		String stem18 = stem("bumabaluktot");
		assertEquals("baluktot", stem18);
	}

	@Test
	void testDuplicateWords()
	{
		// Partial reduplication "takbo" > "tatakbo"; "tumakbo" > "tumatakbo"
		String stem = stem("kahuli-hulihang");
		assertEquals("huli", stem);
		String stem1 = stem("tatakbo");
		assertEquals("takbo", stem1);
		String stem2 = stem("tumatakbo");
		assertEquals("takbo", stem2);
	}

	@Test
	public void verifyRootWords() {

		InputStream inputStream = getClass().getResourceAsStream("/root_word_partial.txt");
		if (inputStream != null) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				int correctCount = 0;
				int wrongCount = 0;
				while ((line = reader.readLine()) != null) {
					String[] parts = line.split(" : ");
					if (parts.length == 2) {
						String word = parts[0].trim();
						String expectedStem = parts[1].trim();
						if (stem(word.toLowerCase()).equals(expectedStem.toLowerCase())) {
							correctCount++;
						} else {
							wrongCount++;
						}
					}
				}
				System.out.println("Correct count: " + correctCount);
				System.out.println("Wrong count: " + wrongCount);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Could not load resource file.");
		}
	}
}
