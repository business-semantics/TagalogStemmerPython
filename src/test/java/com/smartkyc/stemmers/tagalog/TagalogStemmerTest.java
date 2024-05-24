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
		assertEquals("alalay", stem11);
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
	void testOrdinals()
	{
		String stem18 = stem("ikalabing-isa");
		assertEquals("isa", stem18);
		String stem13 = stem("ikalabing-anim");
		assertEquals("anim", stem13);
		String stem14 = stem("ikalawa");
		assertEquals("dalawa", stem14);
		String stem15 = stem("ikalawang");
		assertEquals("dalawa", stem15);
		String stem16 = stem("ikawalong");
		assertEquals("walo", stem16);
	}

	@Test
	void testNumbers()
	{
		String stem18 = stem("apatnapu");
		assertEquals("apat", stem18);
		String stem1 = stem("isang");
		assertEquals("isa", stem1);
		String stem2 = stem("dalawang");
		assertEquals("dalawa", stem2);
		String stem3 = stem("limang");
		assertEquals("lima", stem3);
		String stem4 = stem("pitong");
		assertEquals("pito", stem4);
		String stem5 = stem("walong");
		assertEquals("walo", stem5);
		String stem6 = stem("tatlumpu");
		assertEquals("tatlo", stem6);
		String stem7 = stem("tatlumpung");
		assertEquals("tatlo", stem7);
		String stem8 = stem("tatlumpong");
		assertEquals("tatlo", stem8);
		String stem9 = stem("pang-dalawa");
		assertEquals("dalawa", stem9);
		String stem10 = stem("pang-apat");
		assertEquals("apat", stem10);
		String stem11 = stem("limampung");
		assertEquals("lima", stem11);
	}

	@Test
	void testDbecomesR()
	{
		String stem1 = stem("daraan");
		assertEquals("daan", stem1);
		String stem2 = stem("daraanan");
		assertEquals("daan", stem2);
		String stem3 = stem("ipinagdarasal");
		assertEquals("dasal", stem3);
		String stem4 = stem("pagdurusa");
		assertEquals("dusa", stem4);
		String stem5 = stem("mandirigma");
		assertEquals("digma", stem5);
		String stem6 = stem("mandarambong");
		assertEquals("dambong", stem6);
		String stem7 = stem("idinaraos");
		assertEquals("daos", stem7);
		String stem8 = stem("dinirinig");
		assertEquals("dinig", stem8);
		String stem9 = stem("mandirigmang");
		assertEquals("digma", stem9);
		String stem10 = stem("pagdurugtong");
		assertEquals("dugtong", stem10);
	}

	@Test
	void testPrefixIBeforeVowel()
	{
		String stem1 = stem("iniakyat");
		assertEquals("akyat", stem1);
		String stem2 = stem("inialay");
		assertEquals("alay", stem2);
		String stem3 = stem("inialok");
		assertEquals("alok", stem3);
		String stem4 = stem("inianak");
		assertEquals("anak", stem4);
		String stem5 = stem("iniatas");
		assertEquals("atas", stem5);
		String stem6 = stem("ialay");
		assertEquals("alay", stem6);
		String stem7 = stem("iangat");
		assertEquals("angat", stem7);
		String stem8 = stem("iatas");
		assertEquals("atas", stem8);
		String stem9 = stem("ilegal");
		assertEquals("ilegal", stem9);
		String stem10 = stem("iniakyat");
		assertEquals("akyat", stem10);
		String stem11 = stem("iniutos");
		assertEquals("utos", stem11);
		String stem12 = stem("iniulat");
		assertEquals("ulat", stem12);
	}

	@Test
	void testPrefix()
	{
		String stem3 = stem("namatay");
		assertEquals("patay", stem3);
		String stem1 = stem("pakikipamuhayan");
		assertEquals("buhay", stem1);
		String stem2 = stem("ikamamatay");
		assertEquals("patay", stem2);
	}

	@Test
	void testPrefix2()
	{
		String stem3 = stem("ikamamatay");
		assertEquals("patay", stem3);
		String stem1 = stem("pamahala");
		assertEquals("bahala", stem1);
		String stem2 = stem("pamatay");
		assertEquals("patay", stem2);
		String stem4 = stem("magpapakamatay");
		assertEquals("patay", stem4);
		String stem5 = stem("pamahalaaang");
		assertEquals("bahala", stem5);
		String stem6 = stem("mananamba");
		assertEquals("samba", stem6);
		String stem7 = stem("manahimik");
		assertEquals("tahimik", stem7);
	}

	@Test
	void testPrefix3()
	{
		String stem1 = stem("pananampalataya");
		assertEquals("sampalataya", stem1);
		String stem2 = stem("mananampalataya");
		assertEquals("sampalataya", stem2);
		String stem4 = stem("iisipan");
		assertEquals("isip", stem4);
	}

	@Test
	void testDuplicateWords() throws IOException
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
	public void verifyRootWords()
	{
		InputStream inputStream = getClass().getResourceAsStream("/root-word.txt");
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
						String stem = stem(word.toLowerCase());
						if (stem.equals(root.toLowerCase())) {
							correctCount++;
						} else {
							System.out.println("Original word: " + word + " stemmed:" + stem + ". Expected(root): " + root);
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
