package com.smartkyc.stemmers.tagalog;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TagalogStemmerTest
{
	@Test
	void verifyRootWords()
	{
		final InputStream inputStream = getClass().getResourceAsStream("/root-word.txt");
		final TagalogStemmer stemmer = new TagalogStemmer();
		if (inputStream != null) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				int correctCount = 0;
				int wrongCount = 0;
				while ((line = reader.readLine()) != null) {
					final String[] parts = line.split(" : ");
					if (parts.length == 2) {
						final String word = parts[0].trim();
						final String root = parts[1].trim();
						final String stem = stemmer.stem(word.toLowerCase());
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
			} catch (final IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Could not load resource file.");
		}
	}

	@Test
	void testTagalog()
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		final String stem1 = stemmer.stem("aariing");
		assertEquals("ari", stem1);
		final String stem2 = stemmer.stem("aabot");
		assertEquals("abot", stem2);
		final String stem3 = stemmer.stem("aabutin");
		assertEquals("abot", stem3);
		final String stem4 = stemmer.stem("aalamin");
		assertEquals("alam", stem4);
		final String stem5 = stemmer.stem("aalis");
		assertEquals("alis", stem5);
		final String stem6 = stemmer.stem("aalisin");
		assertEquals("alis", stem6);
		final String stem7 = stemmer.stem("akitin");
		assertEquals("akit", stem7);
		final String stem8 = stemmer.stem("aklasan");
		assertEquals("aklas", stem8);
		final String stem9 = stemmer.stem("pag-aalagang");
		assertEquals("alaga", stem9);
		final String stem10 = stemmer.stem("aalalahanin");
		assertEquals("alala", stem10);
		final String stem11 = stemmer.stem("aalalayan");
		assertEquals("alalay", stem11);
		final String stem12 = stemmer.stem("kahuli-hulihang");
		assertEquals("huli", stem12);
		final String stem13 = stemmer.stem("aklasan");
		assertEquals("aklas", stem13);
		final String stem14 = stemmer.stem("balikan");
		assertEquals("balik", stem14);
		final String stem15 = stemmer.stem("dalawang");
		assertEquals("dalawa", stem15);
		final String stem16 = stemmer.stem("buo-buong");
		assertEquals("buo", stem16);
		final String stem17 = stemmer.stem("bumubuhay");
		assertEquals("buhay", stem17);
		final String stem18 = stemmer.stem("bumabaluktot");
		assertEquals("baluktot", stem18);
		final String stem19 = stemmer.stem(null);
		assertNull(stem19);
		final String stem20 = stemmer.stem("");
		assertEquals("", stem20);
		final String stem21 = stemmer.stem("    ");
		assertEquals("    ", stem21);
		final String stem22 = stemmer.stem("1%");
		assertEquals("1%", stem22);
	}

	@Test
	void testOrdinals()
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		final String stem18 = stemmer.stem("ikalabing-isa");
		assertEquals("isa", stem18);
		final String stem13 = stemmer.stem("ikalabing-anim");
		assertEquals("anim", stem13);
		final String stem14 = stemmer.stem("ikalawa");
		assertEquals("dalawa", stem14);
		final String stem15 = stemmer.stem("ikalawang");
		assertEquals("dalawa", stem15);
		final String stem16 = stemmer.stem("ikawalong");
		assertEquals("walo", stem16);
	}

	@Test
	void testNumbers()
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		final String stem18 = stemmer.stem("apatnapu");
		assertEquals("apat", stem18);
		final String stem1 = stemmer.stem("isang");
		assertEquals("isa", stem1);
		final String stem2 = stemmer.stem("dalawang");
		assertEquals("dalawa", stem2);
		final String stem3 = stemmer.stem("limang");
		assertEquals("lima", stem3);
		final String stem4 = stemmer.stem("pitong");
		assertEquals("pito", stem4);
		final String stem5 = stemmer.stem("walong");
		assertEquals("walo", stem5);
		final String stem6 = stemmer.stem("tatlumpu");
		assertEquals("tatlo", stem6);
		final String stem7 = stemmer.stem("tatlumpung");
		assertEquals("tatlo", stem7);
		final String stem8 = stemmer.stem("tatlumpong");
		assertEquals("tatlo", stem8);
		final String stem9 = stemmer.stem("pang-dalawa");
		assertEquals("dalawa", stem9);
		final String stem10 = stemmer.stem("pang-apat");
		assertEquals("apat", stem10);
		final String stem11 = stemmer.stem("limampung");
		assertEquals("lima", stem11);
	}

	@Test
	void testDbecomesR()
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		final String stem1 = stemmer.stem("daraan");
		assertEquals("daan", stem1);
		final String stem2 = stemmer.stem("daraanan");
		assertEquals("daan", stem2);
		final String stem3 = stemmer.stem("ipinagdarasal");
		assertEquals("dasal", stem3);
		final String stem4 = stemmer.stem("pagdurusa");
		assertEquals("dusa", stem4);
		final String stem5 = stemmer.stem("mandirigma");
		assertEquals("digma", stem5);
		final String stem6 = stemmer.stem("mandarambong");
		assertEquals("dambong", stem6);
		final String stem7 = stemmer.stem("idinaraos");
		assertEquals("daos", stem7);
		final String stem8 = stemmer.stem("dinirinig");
		assertEquals("dinig", stem8);
		final String stem9 = stemmer.stem("mandirigmang");
		assertEquals("digma", stem9);
		final String stem10 = stemmer.stem("pagdurugtong");
		assertEquals("dugtong", stem10);
	}

	@Test
	void testPrefixIBeforeVowel()
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		final String stem1 = stemmer.stem("iniakyat");
		assertEquals("akyat", stem1);
		final String stem2 = stemmer.stem("inialay");
		assertEquals("alay", stem2);
		final String stem3 = stemmer.stem("inialok");
		assertEquals("alok", stem3);
		final String stem4 = stemmer.stem("inianak");
		assertEquals("anak", stem4);
		final String stem5 = stemmer.stem("iniatas");
		assertEquals("atas", stem5);
		final String stem6 = stemmer.stem("ialay");
		assertEquals("alay", stem6);
		final String stem7 = stemmer.stem("iangat");
		assertEquals("angat", stem7);
		final String stem8 = stemmer.stem("iatas");
		assertEquals("atas", stem8);
		final String stem9 = stemmer.stem("ilegal");
		assertEquals("ilegal", stem9);
		final String stem10 = stemmer.stem("iniakyat");
		assertEquals("akyat", stem10);
		final String stem11 = stemmer.stem("iniutos");
		assertEquals("utos", stem11);
		final String stem12 = stemmer.stem("iniulat");
		assertEquals("ulat", stem12);
	}

	@Test
	void testPrefix()
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		final String stem3 = stemmer.stem("namatay");
		assertEquals("patay", stem3);
		final String stem1 = stemmer.stem("pakikipamuhayan");
		assertEquals("buhay", stem1);
		final String stem2 = stemmer.stem("ikamamatay");
		assertEquals("patay", stem2);
	}

	@Test
	void testPrefix2()
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		final String stem3 = stemmer.stem("ikamamatay");
		assertEquals("patay", stem3);
		final String stem1 = stemmer.stem("pamahala");
		assertEquals("bahala", stem1);
		final String stem2 = stemmer.stem("pamatay");
		assertEquals("patay", stem2);
		final String stem4 = stemmer.stem("magpapakamatay");
		assertEquals("patay", stem4);
		final String stem5 = stemmer.stem("pamahalaaang");
		assertEquals("bahala", stem5);
		final String stem6 = stemmer.stem("mananamba");
		assertEquals("samba", stem6);
		final String stem7 = stemmer.stem("manahimik");
		assertEquals("tahimik", stem7);
	}

	@Test
	void testPrefix3()
	{
		final TagalogStemmer stemmer = new TagalogStemmer();

		final String stem1 = stemmer.stem("pananampalataya");
		assertEquals("sampalataya", stem1);
		final String stem2 = stemmer.stem("mananampalataya");
		assertEquals("sampalataya", stem2);
		final String stem4 = stemmer.stem("iisipan");
		assertEquals("isip", stem4);
		final String stem5 = stemmer.stem("mabibigyan");
		assertEquals("bigyan", stem5);
	}

	@Test
	void testDuplicateWords()
	{
		final TagalogStemmer stemmer = new TagalogStemmer();
		// Partial reduplication "takbo" > "tatakbo"; "tumakbo" > "tumatakbo"
		final String stem = stemmer.stem("kahuli-hulihang");
		assertEquals("huli", stem);
		final String stem1 = stemmer.stem("tatakbo");
		assertEquals("takbo", stem1);
		final String stem2 = stemmer.stem("tumatakbo");
		assertEquals("takbo", stem2);
	}

	@Test
	void testShortTokens()
	{
		final TagalogStemmer stemmer = new TagalogStemmer();
		final String stem = stemmer.stem("1%");
		assertEquals("1%", stem);
		final String stem1 = stemmer.stem("1");
		assertEquals("1", stem1);
		final String stem2 = stemmer.stem("&");
		assertEquals("&", stem2);
		final String stem5 = stemmer.stem("%");
		assertEquals("%", stem5);
		final String stem6 = stemmer.stem("ii");
		assertEquals("ii", stem6);
		final String stem7 = stemmer.stem("4");
		assertEquals("4", stem7);
	}
}
