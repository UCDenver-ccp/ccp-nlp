/**
 * 
 */
package edu.ucdenver.ccp.uima.ae.printer;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;
import org.junit.Before;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class InlineXmlPrinterTestBase extends DefaultUIMATestCase {
protected static final TypeSystemDescription TSD = TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem","org.cleartk.syntax.TypeSystem");
	
	/**
	 * The document text that is used in this test case
	 */
	                                          /* 012345678901234567890123456789012345678     90123456789012345678901234567890123456789012345678901234567890*/
	public static final String DOCUMENT_TEXT = "The cow jumped over the moon & the nai\u0308ve stars, but the cd2 and cd5 receptors were not blocked.";

	/**
	 * sample class name for animals for use in this test case
	 */
	public static final String ANIMAL_CLASS = "animal";

	/**
	 * sample class name for celestial bodies used in this test case
	 */
	public static final String CELESTIAL_BODY_CLASS = "celestial body";

	/**
	 * sample class name for the moon used in this test case
	 */
	public static final String MOON_CLASS = "moon";

	/**
	 * sample class name used for testing nested annotations
	 */
	public static final String OUTER_1_CLASS = "outer1";

	/**
	 * sample class name used for testing nested annotations
	 */
	public static final String OUTER_2_CLASS = "outer2";

	/**
	 * sample class name used for testing nested annotations
	 */
	public static final String OUTER_3_CLASS = "outer3";

	/**
	 * sample class name used for testing nested annotations
	 */
	public static final String OUTER_4_CLASS = "outer4";

	/**
	 * sample class name used for testing nested annotations
	 */
	public static final String OUTER_5_CLASS = "outer5";
	
	/**
	 * sample class name used for testing split span annotations
	 */
	public static final String SPLIT_CLASS = "split";

	/**
	 * the document ID used in this test case
	 */
	public static final String SAMPLE_DOCUMENT_ID = "12345.utf8";

	/**
	 * 
	 */
	public static final CharacterEncoding SAMPLE_DOCUMENT_ENCODING = CharacterEncoding.UTF_8;

	/**
	 * Temporary folder to place inlined-annotation output files
	 */
	protected File outputDirectory;

	/**
	 * Sets up a temporary output directory and initializes the {@link JCas}
	 * 
	 * @see edu.uchsc.ccp.uima.test.DefaultUIMATestCase#setUp()
	 */
	@Override
	@Before
	public void setUp() throws Exception {
//		super.setUp();
		jcas = JCasFactory.createJCas(TSD);
		initJCas();
		outputDirectory = folder.newFolder("inline-output");
	}

	/**
	 * Initializes the document text, id, and encoding for the {@link JCas}
	 * 
	 * @see edu.uchsc.ccp.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws Exception {
		
		jcas.setDocumentText(DOCUMENT_TEXT);
		UIMA_Util.setDocumentID(jcas, SAMPLE_DOCUMENT_ID);
		UIMA_Util.setDocumentEncoding(jcas, SAMPLE_DOCUMENT_ENCODING);
	}

	/**
	 * Added simple annotations (no overlaps) to the sample jcas to be used in this test. <br>
	 * cow [4..7] = animal moon [24..28] = celestial body
	 */
	protected void addSimpleSampleAnnotations() {
		addTextAnnotationToJCas(4, 7, ANIMAL_CLASS);
		addTextAnnotationToJCas(24, 28, CELESTIAL_BODY_CLASS);
	}

	/**
	 * Added simple annotations (no overlaps) to the sample jcas to be used in this test. <br>
	 * cow [4..7] = animal<br>
	 * moon [24..28] = celestial body<br>
	 * moon [24..28] = moon<br>
	 * cow jumped [4..14] = outer1<br>
	 * The cow jumped [0..14] = outer2<br>
	 * moon & [24..30] = outer3<br>
	 * moon & the nai\u0308ve stars [24..47]= outer4 <br>
	 * _over [14..19] = outer5
	 */
	protected void addOverlappingSampleAnnotations() {
		addTextAnnotationToJCas(4, 7, ANIMAL_CLASS);
		addTextAnnotationToJCas(24, 28, CELESTIAL_BODY_CLASS);
		addTextAnnotationToJCas(24, 28, MOON_CLASS);
		addTextAnnotationToJCas(4, 14, OUTER_1_CLASS);
		addTextAnnotationToJCas(0, 14, OUTER_2_CLASS);
		addTextAnnotationToJCas(24, 30, OUTER_3_CLASS);
		addTextAnnotationToJCas(24, 47, OUTER_4_CLASS);
		addTextAnnotationToJCas(14, 19, OUTER_5_CLASS);
	}
	
	protected void addSplitSpanCoordinatedAnnotations() {
		CCPTextAnnotation ccpTa = addTextAnnotationToJCas(57, 60, SPLIT_CLASS); // cd2
		UIMA_Annotation_Util.addSpan(ccpTa, new Span(69,78), jcas); // receptors
		assertEquals(String.format(""), "cd2 and cd5 receptors", ccpTa.getCoveredText());
		ccpTa = addTextAnnotationToJCas(65, 78, SPLIT_CLASS); // cd5 receptors
		assertEquals(String.format(""), "cd5 receptors", ccpTa.getCoveredText());
	}

	
	protected void addClearTkSentenceAnnotations() {
		Sentence sentence = new Sentence(jcas,0, DOCUMENT_TEXT.length());
		sentence.addToIndexes();
	}
	
	protected void addClearTkTokenAnnotations() {
		createClearTkToken(0,3,"DT","The");
		createClearTkToken(4,7,"NN","cow");
		createClearTkToken(8,14,"VBZ","jumped");
		createClearTkToken(15,19,"IN","over");
		createClearTkToken(20,23,"DT","the");
		createClearTkToken(24,28,"NN","moon");
		createClearTkToken(29,30,"CC","&");
		createClearTkToken(31,34,"DT","the");
		createClearTkToken(35,41,"JJ","nai\u0308ve");
		createClearTkToken(42,47,"NN","stars");
		createClearTkToken(47,48,"COMMA",",");
		createClearTkToken(49,52,"CC","but");
		createClearTkToken(53,56,"DT","the");
		createClearTkToken(57,60,"NN","cd2");
		createClearTkToken(61,64,"CC","and");
		createClearTkToken(65,68,"NN","cd5");
		createClearTkToken(69,78,"NN","receptors");
		createClearTkToken(79,83,"VBZ","were");
		createClearTkToken(84,87,"RB","not");
		createClearTkToken(88,95,"VBZ","blocked");
		createClearTkToken(95,96,"PERIOD",".");
	}
	
	private void createClearTkToken(int begin, int end, String posTag, String expectedCoveredText) {
		Token token = new Token(jcas, begin, end);
		token.setPos(posTag);
		token.addToIndexes();
		assertEquals("Token covered text not as expected", expectedCoveredText, token.getCoveredText());
	}
}
