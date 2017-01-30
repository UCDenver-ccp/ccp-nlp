package edu.ucdenver.ccp.nlp.doc2txt.pmc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.uima.util.View;
import edu.ucdenver.ccp.nlp.uima.util.View_Util;

public class PmcDocumentConverterAETest extends DefaultUIMATestCase {

	@Override
	protected void initJCas() throws IOException, CASException {
		String nxml = ClassPathUtil.getContentsFromClasspathResource(getClass(), "/14607334.xml",
				CharacterEncoding.UTF_8);
		JCas xmlView = View_Util.getView(jcas, View.XML);
		xmlView.setDocumentText(nxml);
		UIMA_Util.setDocumentID(xmlView, "14607334");

	}

	@Test
	public void testNxmlToTxtConversion() throws AnalysisEngineProcessException, ResourceInitializationException {
		AnalysisEngineDescription converterDesc = PmcDocumentConverterAE.getDescription(getTypeSystem());
		AnalysisEngine converterEngine = AnalysisEngineFactory.createPrimitive(converterDesc);
		converterEngine.process(jcas);

		assertNotNull(jcas.getDocumentText());
		assertEquals("14607334", UIMA_Util.getDocumentID(jcas));
	}

}
