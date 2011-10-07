/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.common.collections.tree.TreeNode;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.Mention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.ComponentAnnotationExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpComponentAnnotationExctractor implements ComponentAnnotationExtractor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.ComponentAnnotationExtractor#getBasedOnAnnotations
	 * (org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public Collection<Annotation> getComponentAnnotations(Annotation annotation) {
		if (!(annotation instanceof CCPTextAnnotation))
			throw new IllegalArgumentException(
					String.format(
							"This ComponentAnnotationExtractor (%s) cannot extract component annotations from the non-CCPTextAnnotation annotation type: ",
							this.getClass().getName(), annotation.getClass().getName()));
		Collection<Annotation> componentAnnotations = new ArrayList<Annotation>();
		WrappedCCPTextAnnotation wrappedTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
		for (Iterator<TreeNode<Mention>> mentionIter = Mention.getMentionTreeNodeIterator(wrappedTa.getClassMention()); mentionIter
				.hasNext();) {
			Mention mention = mentionIter.next().getNodeValue();
			if (mention instanceof ClassMention) {
				ClassMention cm = (ClassMention) mention;
				if (!cm.equals(wrappedTa.getClassMention()))
					componentAnnotations.add(((WrappedCCPTextAnnotation) cm.getTextAnnotation()).getWrappedObject());
			}
		}
		return componentAnnotations;
	}

}
