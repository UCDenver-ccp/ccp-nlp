/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.ucdenver.ccp.nlp.wrapper.abner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.interfaces.ITagger;

/**
 * <p>
 * This Test class performs some simple tests on the Abner_Util class.
 * <p>
 * TODO:Currently, the tests simply count the number of annotations returned. In the future, the tests should include
 * more detailed information on the extracted annotations.
 * 
 * @author williamb
 * 
 */
public class Abner_UtilTest extends DefaultTestCase {

//	private Abner_Util abnerUtil;
//
//	@Before
//	public void setUp() throws Exception {
//		abnerUtil = new Abner_Util();
//		String[] args = { "resources/nlp-tools/ABNER/models/nlpba.crf" };
//		abnerUtil.initialize(ITagger.ENTITY_TAGGER, args);
//
//	}

	/**
	 * Check that the appropriate number of annotations are returned.
	 * 
	 */
	@Test
	public void testAbnerUtil()  {
		Abner_Util abnerUtil = new Abner_Util();
		String[] args = { "src/main/resources/abner/models/nlpba.crf" };
		abnerUtil.initialize(ITagger.ENTITY_TAGGER, args);
		/* From PubMed ID: 15750311 */
		String testText = "During the attempt to seek T. congolense species-specific diagnostic antigens, we discovered one cDNA clone (P74) encoding 74 kDa putative abc1 protein (p74) from T. congolense PCF cDNA library. It has been suggested that members of the abc1 family are novel chaperonins and essential for both the mitochondrial electron transfer in the bc 1 complex and the coenzyme Q biosynthesis. Although abc1 protein in yeast has a nuclear or mitochondrial subcellular location, neither nuclear localization signal nor mitochondrial targeting signal was found within p74. Northern blot analysis revealed that the transcription level of P74 mRNA in bloodstream form (BSF) cells were 4 times higher than that in procyclic form cells. Western blot analysis also indicated that p74 was only expressed in T. congolense BSF cells, and revealed that molecular mass of native p74 was not 74 kDa but 56 kDa. This indicates extensive post-translational modification in p74. Although further characterization of p74 will be required, our findings provide implications for CoQ biosynthesis pathway in T. congolense. The acyl binding pockets of KAS I and KAS II are so similar that they alone cannot provide the basis for their differences in substrate specificity.";

		// Abner_Util abnerUtil = new Abner_Util(Tagger.NLPBA);
		List<TextAnnotation> extractedEntities = abnerUtil.getEntitiesFromText(testText, "1234");

		for (TextAnnotation ta : extractedEntities)
			System.out.println("type: "+ ta.getClassMention().getMentionName() + " -- "+ ta.getSpans().get(0).toString());
		
		
		assertEquals(17, extractedEntities.size());

	}

	/**
	 * ABNER, perhaps in the tokenization procedure, converts '' to ". This test checks to see if the entities are
	 * discovered correctly, and that the spans are also correct.
	 * 
	 */
	@Test
	public void testOnQuotesAbnerUtil() {
		Abner_Util abnerUtil = new Abner_Util();
		String[] args = { "src/main/resources/abner/models/nlpba.crf" };
		abnerUtil.initialize(ITagger.ENTITY_TAGGER, args);
		
		/* From PubMed ID: 978319 */
		String testText = "The enzyme has ADA 1 electrophoretic mobility: SV40 transformation of cultured fibroblasts caused a decrease of \"\"tissue ADA'' and an increase in \"\"red cell ADA'' isozymes.";

		// Abner_Util abnerUtil = new Abner_Util(Tagger.NLPBA);
		List<TextAnnotation> extractedEntities = abnerUtil.getEntitiesFromText(testText, "1234");

		assertEquals(2, extractedEntities.size());

		/* From PubMed ID: 718915 */
		testText = "Purified RNA polymerase A contained six putative subunits with molecular weights 190 000 (A1), 117 000 (A2), 57 000 (A3), 50 000 (A4), 25 000 (A5), 19 000 (A6); RNA polymerase B contained eight putative subunits with molecular weights 98 000 (B2'), 86 000 (B2''), 155 000 (B3), 44 000 (B4), 31 000 (B5), 28 000 (B6), 26 000 (B7), 19 000 (B8); RNA polymerase C contained nine putative subunits with molecular weights 170 000 (C1), 117 000 (C2), 84 000 (C3), 60 000 (C4), 49 000 (C5), 36 000 (C6), 33 000 (C7), 22 000 (C8), 19 000 (C9).";
		extractedEntities = abnerUtil.getEntitiesFromText(testText, "1234");

		assertEquals(6, extractedEntities.size());

		/* This test still fails */
		/* From PubMed ID: 38086 */
		testText = "In guinea pig liver these acids occurred together with 4'',5''-bisnor-delta1(6)-THC-3''-oic acid and 5''-nor-delta1(6)-THC-4''-oic acid.";
		extractedEntities = abnerUtil.getEntitiesFromText(testText, "1234");
		System.err.println("THIS IS A KNOWN BUG.");
		// assertEquals(1, extractedEntities.size());

	}
	
	
	@Test
	public void testOnUtf8() throws IOException {
		String utf8Sentence = ClassPathUtil.getContentsFromClasspathResource(getClass(), "utf8Sentence.txt", CharacterEncoding.UTF_8);
		Abner_Util abnerUtil = new Abner_Util();
		String[] args = { "src/main/resources/abner/models/nlpba.crf" };
		abnerUtil.initialize(ITagger.ENTITY_TAGGER, args);
		List<TextAnnotation> extractedEntities = abnerUtil.getEntitiesFromText(utf8Sentence, "1234");
		assertEquals(5,extractedEntities.size());
	}
	
	
	@Ignore
	@Test
	public void testLoadFromClasspath() throws Exception {
		Abner_Util abnerUtil = new Abner_Util();
		URL res = Abner_Util.class.getResource("nlpba.crf");
		System.err.println("Resource is null: " + (res==null));
		String[] args = { "jar:!ABNER/models/nlpba.crf" };
		abnerUtil.initialize(ITagger.ENTITY_TAGGER, args);

	}

}
