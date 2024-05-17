package com.smartkyc.stemmers.tagalog;

import org.apache.commons.lang.NotImplementedException;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.smartkyc.stemmers.tagalog.TagalogStemmer.stem;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagalogStemmerTest
{
	@Test
	void testTagalog() throws IOException
	{
		String stem1 = stem("aariing");
		assertEquals("ari", stem1);
		String stem2 = stem("aabot");
//		assertEquals("abot", stem2);
		String stem3 = stem("aabutin");
//		assertEquals("abot", stem3);
		String stem4 = stem("aalamin");
//		assertEquals("alam", stem4);
		String stem5 = stem("aalis");
//		assertEquals("alis", stem5);
		String stem6 = stem("aalisin");
//		assertEquals("alis", stem6);
		String stem7 = stem("akitin");
//		assertEquals("akit", stem7);
		String stem8 = stem("aklasan");
//		assertEquals("aklas", stem8);
		String stem9 = stem("pag-aalagang");
		assertEquals("alaga", stem9);
		String stem10 = stem("aalalahanin");
		assertEquals("alala", stem10);
		String stem11 = stem("aalalayan");
		assertEquals("alay", stem11);
	}

	@Test
	void testDuplicateCleaner() throws IOException
	{
		String deduplicated6 = TagalogStemmer.cleanDuplication("kahuli-hulihang", new ArrayList<>());
		String deduplicated1 = TagalogStemmer.cleanDuplication("araw-araw", new ArrayList<>());
		String deduplicated2 = TagalogStemmer.cleanDuplication("inaasam-asam", new ArrayList<>());
		String deduplicated3 = TagalogStemmer.cleanDuplication("inyo-inyong", new ArrayList<>());
		String deduplicated4 = TagalogStemmer.cleanDuplication("kaagad-agad", new ArrayList<>());
		String deduplicated5 = TagalogStemmer.cleanDuplication("kaakit-akit", new ArrayList<>());
		assertEquals("araw", deduplicated1);
		assertEquals("kaagad", deduplicated4);
		assertEquals("inyong", deduplicated3);
		assertEquals("inaasam", deduplicated2);
		assertEquals("kaakit", deduplicated5);
		assertEquals("kaakit", deduplicated6);
	}

	@Test
	void testDuplicateWords() throws IOException
	{

			// Partial reduplication "takbo" > "tatakbo"; "tumakbo" > "tumatakbo"
			String stem = stem("kahuli-hulihang");
			assertEquals("huli", stem);


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
							System.out.println("Original word: " + word.toLowerCase() + ", stemmed: " + stem(word.toLowerCase()) + ". Expected(root): " + expectedStem);
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

	@Test
	void testVowelCount()
	{
		throw new NotImplementedException();
	}

	@Test
	void testConsonantCount()
	{
		throw new NotImplementedException();
	}

}
