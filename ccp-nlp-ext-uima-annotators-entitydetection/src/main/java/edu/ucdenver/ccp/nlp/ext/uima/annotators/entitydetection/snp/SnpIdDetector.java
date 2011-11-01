/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.snp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ucdenver.ccp.datasource.identifiers.ncbi.snp.SnpRsId;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.exception.InitializationException;
import edu.ucdenver.ccp.nlp.core.interfaces.IEntityTagger;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SnpIdDetector implements IEntityTagger {

	private static final Annotator ANNOTATOR = new Annotator(-1,"Snp ID Detector","Snp ID Detector","CCP");

	private static final Pattern REFERENCE_SNP_ID_PATTERN = Pattern.compile("\\b(rs\\d{4,})\\b");
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.interfaces.ITagger#initialize(int, java.lang.String[])
	 */
	public void initialize(int taggerType, String[] args) throws InitializationException {
		// do nothing - no input args required
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.interfaces.IEntityTagger#getEntitiesFromText(java.lang.String,
	 * java.lang.String)
	 */
	public List<TextAnnotation> getEntitiesFromText(String inputText, String documentId) {
		List<TextAnnotation> taList = new ArrayList<TextAnnotation>();
		Matcher m = REFERENCE_SNP_ID_PATTERN.matcher(inputText);
		while (m.find()) {
			SnpRsId snpId = new SnpRsId(m.group(1));
			taList.add(createSnpIdAnnotation(snpId, m.start(1), m.end(1), documentId));
		}
		return taList;
	}
	
	
	private TextAnnotation createSnpIdAnnotation(SnpRsId snpId, int spanStart, int spanEnd, String documentId) {
		TextAnnotation ta = new DefaultTextAnnotation(spanStart, spanEnd);
		ta.setCoveredText(snpId.getDataElement());
		ta.setDocumentID(documentId);
		ta.setAnnotator(ANNOTATOR);
		ClassMention cm = new DefaultClassMention(snpId.getDataElement());
		ta.setClassMention(cm);
		return ta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.interfaces.IEntityTagger#shutdown()
	 */
	public void shutdown() {
		// do nothing
	}

}
