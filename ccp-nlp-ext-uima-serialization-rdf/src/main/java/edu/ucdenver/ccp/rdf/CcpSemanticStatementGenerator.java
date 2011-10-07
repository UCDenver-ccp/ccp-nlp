/**
 * 
 */
package edu.ucdenver.ccp.rdf;

import java.util.Collection;
import java.util.Map;

import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.shims.SemanticStatementGenerator;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public abstract class CcpSemanticStatementGenerator implements SemanticStatementGenerator {

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.shims.SemanticStatementGenerator#getSemanticStatements(org.apache.uima.jcas.tcas.Annotation, org.openrdf.model.URI)
	 */
	@Override
	public Collection<? extends Statement> getSemanticStatements(Annotation annotation, URI graphUri, Map<String, URI> annotationKeyToSemanticInstanceUriMap) {
		if (!(annotation instanceof CCPTextAnnotation))
			throw new IllegalArgumentException(this.getClass().getName() + ".getSemanticStatements()"
					+ " is unable to handle annotation classes other than CCPTextAnnotations. Use was attempted with: "
					+ annotation.getClass().getName());
		WrappedCCPTextAnnotation ccpTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
		return generateSemanticStatements(ccpTa, graphUri, annotationKeyToSemanticInstanceUriMap);
	}

	/**
	 * @param ccpTa
	 * @param graphUri
	 * @return
	 */
	protected abstract Collection<? extends Statement> generateSemanticStatements(WrappedCCPTextAnnotation ccpTa, URI graphUri, Map<String, URI> annotationKeyToSemanticInstanceUriMap);

}
