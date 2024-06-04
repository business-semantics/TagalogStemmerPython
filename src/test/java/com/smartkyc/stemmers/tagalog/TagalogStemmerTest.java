package com.smartkyc.stemmers.tagalog;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagalogStemmerTest
{
	@Test
	void testTagalog() throws IOException
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		String stem1 = stemmer.stem("aariing");
		assertEquals("ari", stem1);
		String stem2 = stemmer.stem("aabot");
		assertEquals("abot", stem2);
		String stem3 = stemmer.stem("aabutin");
		assertEquals("abot", stem3);
		String stem4 = stemmer.stem("aalamin");
		assertEquals("alam", stem4);
		String stem5 = stemmer.stem("aalis");
		assertEquals("alis", stem5);
		String stem6 = stemmer.stem("aalisin");
		assertEquals("alis", stem6);
		String stem7 = stemmer.stem("akitin");
		assertEquals("akit", stem7);
		String stem8 = stemmer.stem("aklasan");
		assertEquals("aklas", stem8);
		String stem9 = stemmer.stem("pag-aalagang");
		assertEquals("alaga", stem9);
		String stem10 = stemmer.stem("aalalahanin");
		assertEquals("alala", stem10);
		String stem11 = stemmer.stem("aalalayan");
		assertEquals("alalay", stem11);
		String stem12 = stemmer.stem("kahuli-hulihang");
		assertEquals("huli", stem12);
		String stem13 = stemmer.stem("aklasan");
		assertEquals("aklas", stem13);
		String stem14 = stemmer.stem("balikan");
		assertEquals("balik", stem14);
		String stem15 = stemmer.stem("dalawang");
		assertEquals("dalawa", stem15);
		String stem16 = stemmer.stem("buo-buong");
		assertEquals("buo", stem16);
		String stem17 = stemmer.stem("bumubuhay");
		assertEquals("buhay", stem17);
		String stem18 = stemmer.stem("bumabaluktot");
		assertEquals("baluktot", stem18);
	}

	@Test
	void testOrdinals() throws IOException
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		String stem18 = stemmer.stem("ikalabing-isa");
		assertEquals("isa", stem18);
		String stem13 = stemmer.stem("ikalabing-anim");
		assertEquals("anim", stem13);
		String stem14 = stemmer.stem("ikalawa");
		assertEquals("dalawa", stem14);
		String stem15 = stemmer.stem("ikalawang");
		assertEquals("dalawa", stem15);
		String stem16 = stemmer.stem("ikawalong");
		assertEquals("walo", stem16);
	}

	@Test
	void testNumbers() throws IOException
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		String stem18 = stemmer.stem("apatnapu");
		assertEquals("apat", stem18);
		String stem1 = stemmer.stem("isang");
		assertEquals("isa", stem1);
		String stem2 = stemmer.stem("dalawang");
		assertEquals("dalawa", stem2);
		String stem3 = stemmer.stem("limang");
		assertEquals("lima", stem3);
		String stem4 = stemmer.stem("pitong");
		assertEquals("pito", stem4);
		String stem5 = stemmer.stem("walong");
		assertEquals("walo", stem5);
		String stem6 = stemmer.stem("tatlumpu");
		assertEquals("tatlo", stem6);
		String stem7 = stemmer.stem("tatlumpung");
		assertEquals("tatlo", stem7);
		String stem8 = stemmer.stem("tatlumpong");
		assertEquals("tatlo", stem8);
		String stem9 = stemmer.stem("pang-dalawa");
		assertEquals("dalawa", stem9);
		String stem10 = stemmer.stem("pang-apat");
		assertEquals("apat", stem10);
		String stem11 = stemmer.stem("limampung");
		assertEquals("lima", stem11);
	}

	@Test
	void testDbecomesR() throws IOException
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		String stem1 = stemmer.stem("daraan");
		assertEquals("daan", stem1);
		String stem2 = stemmer.stem("daraanan");
		assertEquals("daan", stem2);
		String stem3 = stemmer.stem("ipinagdarasal");
		assertEquals("dasal", stem3);
		String stem4 = stemmer.stem("pagdurusa");
		assertEquals("dusa", stem4);
		String stem5 = stemmer.stem("mandirigma");
		assertEquals("digma", stem5);
		String stem6 = stemmer.stem("mandarambong");
		assertEquals("dambong", stem6);
		String stem7 = stemmer.stem("idinaraos");
		assertEquals("daos", stem7);
		String stem8 = stemmer.stem("dinirinig");
		assertEquals("dinig", stem8);
		String stem9 = stemmer.stem("mandirigmang");
		assertEquals("digma", stem9);
		String stem10 = stemmer.stem("pagdurugtong");
		assertEquals("dugtong", stem10);
	}

	@Test
	void testPrefixIBeforeVowel() throws IOException
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		String stem1 = stemmer.stem("iniakyat");
		assertEquals("akyat", stem1);
		String stem2 = stemmer.stem("inialay");
		assertEquals("alay", stem2);
		String stem3 = stemmer.stem("inialok");
		assertEquals("alok", stem3);
		String stem4 = stemmer.stem("inianak");
		assertEquals("anak", stem4);
		String stem5 = stemmer.stem("iniatas");
		assertEquals("atas", stem5);
		String stem6 = stemmer.stem("ialay");
		assertEquals("alay", stem6);
		String stem7 = stemmer.stem("iangat");
		assertEquals("angat", stem7);
		String stem8 = stemmer.stem("iatas");
		assertEquals("atas", stem8);
		String stem9 = stemmer.stem("ilegal");
		assertEquals("ilegal", stem9);
		String stem10 = stemmer.stem("iniakyat");
		assertEquals("akyat", stem10);
		String stem11 = stemmer.stem("iniutos");
		assertEquals("utos", stem11);
		String stem12 = stemmer.stem("iniulat");
		assertEquals("ulat", stem12);
	}

	@Test
	void testPrefix() throws IOException
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		String stem3 = stemmer.stem("namatay");
		assertEquals("patay", stem3);
		String stem1 = stemmer.stem("pakikipamuhayan");
		assertEquals("buhay", stem1);
		String stem2 = stemmer.stem("ikamamatay");
		assertEquals("patay", stem2);
	}

	@Test
	void testPrefix2() throws IOException
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		String stem3 = stemmer.stem("ikamamatay");
		assertEquals("patay", stem3);
		String stem1 = stemmer.stem("pamahala");
		assertEquals("bahala", stem1);
		String stem2 = stemmer.stem("pamatay");
		assertEquals("patay", stem2);
		String stem4 = stemmer.stem("magpapakamatay");
		assertEquals("patay", stem4);
		String stem5 = stemmer.stem("pamahalaaang");
		assertEquals("bahala", stem5);
		String stem6 = stemmer.stem("mananamba");
		assertEquals("samba", stem6);
		String stem7 = stemmer.stem("manahimik");
		assertEquals("tahimik", stem7);
	}

	@Test
	void testPrefix3() throws IOException
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		String stem1 = stemmer.stem("pananampalataya");
		assertEquals("sampalataya", stem1);
		String stem2 = stemmer.stem("mananampalataya");
		assertEquals("sampalataya", stem2);
		String stem4 = stemmer.stem("iisipan");
		assertEquals("isip", stem4);
		String stem5 = stemmer.stem("mabibigyan");
		assertEquals("bigyan", stem5);
	}

	@Test
	void testDuplicateWords() throws IOException
	{
		final TagalogStemmer stemmer = new TagalogStemmer();
		// Partial reduplication "takbo" > "tatakbo"; "tumakbo" > "tumatakbo"
		String stem = stemmer.stem("kahuli-hulihang");
		assertEquals("huli", stem);
		String stem1 = stemmer.stem("tatakbo");
		assertEquals("takbo", stem1);
		String stem2 = stemmer.stem("tumatakbo");
		assertEquals("takbo", stem2);
	}

	@Test
	public void verifyRootWords() throws IOException
	{
		InputStream inputStream = getClass().getResourceAsStream("/root-word.txt");
		final TagalogStemmer stemmer = new TagalogStemmer();
		if (inputStream != null) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				int correctCount = 0;
				int wrongCount = 0;
				while ((line = reader.readLine()) != null) {
					String[] parts = line.split(" : ");
					if (parts.length == 2) {
						String word = parts[0].trim();
						String root = parts[1].trim();
						String stem = stemmer.stem(word.toLowerCase());
						if (stem.equals(root.toLowerCase())) {
							correctCount++;
						} else {
							//							System.out.println("Original word: " + word + " stemmed:" + stem + ". Expected(root): " + root);
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
