/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.common.string.StringUtil;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;

/**
 * Pertains to the {@link SourceDocumentInformation} class that comes with the UIMA Examples type
 * system
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class UimaExampleDocumentMetaDataExtractor implements DocumentMetaDataExtractor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#extractDocumentId(
	 * org.apache.uima.jcas.JCas)
	 */
	@Override
	public String extractDocumentId(JCas jCas) {
		return StringUtil.removePrefix(getSourceDocInfo(jCas).getUri(), "file://");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#extractDocumentEncoding
	 * (org.apache.uima.jcas.JCas)
	 */
	@Override
	public String extractDocumentEncoding(JCas jCas) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store document encoding information");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#setDocumentId(org.
	 * apache.uima.jcas.JCas, java.lang.String)
	 */
	@Override
	public void setDocumentId(JCas jCas, String documentId) {
		getSourceDocInfo(jCas).setUri("file://" + documentId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#setDocumentEncoding
	 * (org.apache.uima.jcas.JCas, java.lang.String)
	 */
	@Override
	public void setDocumentEncoding(JCas jCas, String encoding) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store document encoding information");
	}

	private SourceDocumentInformation getSourceDocInfo(JCas jCas) {
		SourceDocumentInformation sourceDocInfo;
		FSIterator<Annotation> it = jCas.getJFSIndexRepository().getAnnotationIndex(SourceDocumentInformation.type)
				.iterator();
		if (it.hasNext()) {
			sourceDocInfo = (SourceDocumentInformation) it.next();
		} else {
			sourceDocInfo = new SourceDocumentInformation(jCas);
			sourceDocInfo.addToIndexes();
		}
		return sourceDocInfo;
	}

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#getMetaDataContainer(org.apache.uima.jcas.JCas)
	 */
	@Override
	public TOP getMetaDataContainer(JCas jCas) {
		return getSourceDocInfo(jCas);
	}

}
