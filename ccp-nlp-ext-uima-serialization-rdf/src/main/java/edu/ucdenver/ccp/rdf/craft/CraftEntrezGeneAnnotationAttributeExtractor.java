/**
 * 
 */
package edu.ucdenver.ccp.rdf.craft;

import java.util.Collection;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.EntrezGeneID;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationAttributeExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface CraftEntrezGeneAnnotationAttributeExtractor extends AnnotationAttributeExtractor<EntrezGeneID> {

	/**
	 * Should return null if the input annotation does not contain a container for EntrezGene IDs,
	 * e.g. if it is not a gene annotation
	 */
	@Override
	public Collection<EntrezGeneID> getAnnotationAttributes(Annotation annotation);
}
