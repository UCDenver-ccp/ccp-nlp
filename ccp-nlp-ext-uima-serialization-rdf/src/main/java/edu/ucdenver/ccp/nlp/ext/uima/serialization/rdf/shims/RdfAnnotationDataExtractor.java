/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.shims;

import java.util.Collection;
import java.util.Map;

import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationSpanExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationTypeExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public abstract class RdfAnnotationDataExtractor extends AnnotationDataExtractor {

	/**
	 * The {@link SemanticStatementGenerator} to use for generating the statements that represent the semantics of a given annotation
	 */
	private SemanticStatementGenerator semanticStatementGenerator;

	
	/**
	 * @param typeExtractor
	 * @param spanExtractor
	 */
	public RdfAnnotationDataExtractor(AnnotationTypeExtractor typeExtractor, AnnotationSpanExtractor spanExtractor) {
		super(typeExtractor, spanExtractor);
	}
	
	/**
	 * @param annotation
	 * @param graphUri
	 * @return
	 */
	public Collection<? extends Statement> getSemanticStatements(Annotation annotation, URI graphUri, Map<String, URI> annotationKeyToSemanticInstanceUriMap) {
		return getSemanticStatementGenerator().getSemanticStatements(annotation, graphUri,annotationKeyToSemanticInstanceUriMap );
	}

	/**
	 * @return the semanticStatementGenerator
	 */
	public SemanticStatementGenerator getSemanticStatementGenerator() {
		return semanticStatementGenerator;
	}

	/**
	 * @param semanticStatementGenerator the semanticStatementGenerator to set
	 */
	public void setSemanticStatementGenerator(SemanticStatementGenerator semanticStatementGenerator) {
		this.semanticStatementGenerator = semanticStatementGenerator;
	}

}
