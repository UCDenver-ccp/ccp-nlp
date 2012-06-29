/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDecorator;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public interface GeneIdAnnotationDecorator extends AnnotationDecorator {

	public void addGeneIdentifierAttribute(Annotation annotation, DataSourceIdentifier<?> geneId);
	
	
}
