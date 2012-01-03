package edu.ucdenver.ccp.nlp.core.uima.test;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.After;
import org.junit.Before;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * provides a tsd and jcas for common UIMA tests. (Type System Description, and JCas) has a hook for
 * initializing the jcas with some annotations: initJCas() also has convenience functions for adding
 * various annotations.
 * 
 * @author williamb
 * 
 */
public abstract class DefaultUIMATestCase extends DefaultTestCase {

	protected TypeSystemDescription tsd;
	protected JCas jcas;

	@Before
	public void setUp() throws Exception {
		tsd = getTypeSystem();
		jcas = JCasFactory.createJCas(tsd);
		initJCas();
	}

	/**
	 * Override to set a different type system
	 */
	protected TypeSystemDescription getTypeSystem() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
	}

	@After
	public void tearDown() throws Exception {
		if (jcas != null) {
			jcas.release();
		}
	}

	protected abstract void initJCas() throws Exception;

	protected CCPTextAnnotation addTextAnnotationToJCas(int spanStart, int spanEnd, String classMentionName) {
		return UIMA_Annotation_Util.createCCPTextAnnotation(classMentionName, new int[] { spanStart, spanEnd }, jcas);
	}

	protected CCPTextAnnotation addSentenceAnnotationToJCas(int spanStart, int spanEnd) {
		return addTextAnnotationToJCas(spanStart, spanEnd, ClassMentionTypes.SENTENCE);
	}

	protected CCPTextAnnotation addParagraphAnnotationToJCas(int spanStart, int spanEnd) {
		return addTextAnnotationToJCas(spanStart, spanEnd, ClassMentionTypes.PARAGRAPH);
	}

	protected CCPTextAnnotation addGeneAnnotationToJCas(int spanStart, int spanEnd, int entrezGeneID)
			throws CASException {
		CCPTextAnnotation ccpTA = addTextAnnotationToJCas(spanStart, spanEnd, ClassMentionTypes.GENE);
		UIMA_Util.addSlotValue(ccpTA.getClassMention(), "has Entrez Gene ID", Integer.toString(entrezGeneID));
		return ccpTA;
	}

	protected CCPTextAnnotation addTranscriptAnnotationToJCas(int spanStart, int spanEnd, int entrezGeneID)
			throws CASException {
		CCPTextAnnotation ccpTA = addTextAnnotationToJCas(spanStart, spanEnd, ClassMentionTypes.TRANSCRIPT);
		UIMA_Util.addSlotValue(ccpTA.getClassMention(), "has Entrez Gene ID", Integer.toString(entrezGeneID));
		return ccpTA;
	}

}
