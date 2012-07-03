package edu.ucdenver.ccp.nlp.uima.shims.annotation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.uima.shims.annotation.AnnotationSpanExtractor;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationTypeExtractor;
import edu.ucdenver.ccp.uima.shims.annotation.Span;

/**
 * Base class that combines several interfaces useful for extracting information from UIMA
 * {@link Annotation} instances.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class AnnotationDataExtractor implements AnnotationTypeExtractor, AnnotationSpanExtractor,
		AnnotationCreatorExtractor, ComponentAnnotationExtractor, AnnotationKeyGenerator {

	/**
	 * The {@link AnnotationTypeExtractor} to use for extracting annotation type information
	 */
	private final AnnotationTypeExtractor typeExtractor;

	/**
	 * The {@link AnnotationSpanExtractor} to use for extracting annotation span information
	 */
	private final AnnotationSpanExtractor spanExtractor;

	/**
	 * The {@link AnnotationCreatorExtractor} to use for extracting the annotation annotator
	 */
	private AnnotationCreatorExtractor annotatorExtractor;

	/**
	 * The {@link ComponentAnnotationExtractor} to use for extracting annotations that a given
	 * annotation is based on
	 */
	private ComponentAnnotationExtractor componentAnnotationExtractor;

	/**
	 * The {@link AnnotationKeyGenerator} to use for generating a unique key for a given annotation
	 */
	private AnnotationKeyGenerator annotationKeyGenerator;
	
	
	
	/**
	 * Stores a mapping from {@link AnnotationAttribute} to the {@link AnnotationAttributeExtractor}
	 * implementation to use if that attribute type is requested
	 */
	private Map<AnnotationAttribute, AnnotationAttributeExtractor> attributeExtractors;

	/**
	 * Consolidates several interfaces useful from extracting information from UIMA
	 * {@link Annotation} instances
	 * 
	 * @param typeExtractor
	 *            The {@link AnnotationTypeExtractor} to use for extracting annotation type
	 *            information
	 * @param spanExtractor
	 *            The {@link AnnotationSpanExtractor} to use for extracting annotation span
	 *            information
	 */
	public AnnotationDataExtractor(AnnotationTypeExtractor typeExtractor, AnnotationSpanExtractor spanExtractor) {
		this.typeExtractor = typeExtractor;
		this.spanExtractor = spanExtractor;
		this.attributeExtractors = new HashMap<AnnotationAttribute, AnnotationAttributeExtractor>();
	}

	/**
	 * Adds a new {@link AnnotationAttributeExtractor} to this {@link AnnotationDataExtractor}
	 * extension. Only one extractor per attribute type is permitted.
	 * 
	 * @param extractor
	 *            the {@link AnnotationAttributeExtractor} instance to add
	 * @param attribute
	 *            the {@link AnnotationAttribute} type the extractor returns
	 * @throws IllegalStateException
	 *             if there already exists an extractor that returns the input attribute type
	 */
	public void addAnnotationAttributeExtractor(AnnotationAttributeExtractor extractor, AnnotationAttribute attribute) {
		if (attributeExtractors.containsKey(attribute))
			throw new IllegalArgumentException(
					String.format(
							"The %s extension of AnnotationDataExtractor already has an annotation attribute extractor "
									+ "specified (%s) for the attribute type: %s. Unable to specify another attribute extractor "
									+ "(%s) for the same attribute type.", getClass().getName(), attributeExtractors
									.get(attribute).getClass().getName(), attribute.name(), extractor.getClass()
									.getName()));
		attributeExtractors.put(attribute, extractor);
	}

	/**
	 * @see edu.ucdenver.ccp.uima.shims.annotation.shim.annotation.uchsc.ccp.uima.shim.AnnotationTypeExtractor#getAnnotationType(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public String getAnnotationType(Annotation annotation) {
		return typeExtractor.getAnnotationType(annotation);
	}

	/**
	 * @see AnnotationSpanExtractor#getAnnotationSpans(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public List<Span> getAnnotationSpans(Annotation annotation) {
		return spanExtractor.getAnnotationSpans(annotation);
	}

	/**
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationSpanExtractor#getCoveredText(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public String getCoveredText(Annotation annotation) {
		return spanExtractor.getCoveredText(annotation);
	}

	public void setAnnotationCreatorExtractor(AnnotationCreatorExtractor annotatorExtractor) {
		this.annotatorExtractor = annotatorExtractor;
	}

	@Override
	public Annotator getAnnotator(Annotation annotation) {
		if (annotatorExtractor == null)
			throw new IllegalStateException(
					"Unabled to retrieve annotation creator. This AnnotationDataExtractor implementation ("
							+ getClass().getName() + ") is not outfitted with a AnnotationCreatorExtractor.");
		return annotatorExtractor.getAnnotator(annotation);
	}

	public void setAnnotationKeyGenerator(AnnotationKeyGenerator annotationKeyGenerator) {
		this.annotationKeyGenerator = annotationKeyGenerator;
	}

	@Override
	public String getAnnotationKey(Annotation annotation) {
		if (annotationKeyGenerator == null)
			throw new IllegalStateException(
					"Unabled to generate annotation key. This AnnotationDataExtractor implementation ("
							+ getClass().getName() + ") is not outfitted with a AnnotationKeyGenerator.");
		return annotationKeyGenerator.getAnnotationKey(annotation);
	}

	public void setComponentAnnotationExtractor(ComponentAnnotationExtractor componentAnnotationExctractor) {
		this.componentAnnotationExtractor = componentAnnotationExctractor;
	}

	/**
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.ComponentAnnotationExtractor#getComponentAnnotations(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public Collection<Annotation> getComponentAnnotations(Annotation annotation) {
		if (componentAnnotationExtractor == null)
			throw new IllegalStateException(
					"Unabled to retrieve based-on annotations. This AnnotationDataExtractor implementation ("
							+ getClass().getName() + ") is not outfitted with a BasedOnAnnotationExtractor.");
		return componentAnnotationExtractor.getComponentAnnotations(annotation);
	}

//	/**
//	 * Returns the attributes for the input {@link Annotation} using an implementation of
//	 * {@link AnnotationAttributeExtractor}
//	 * 
//	 * @param annotation
//	 *            the annotation to query for the specified attribute
//	 * @param attribute
//	 *            the attribute type to return
//	 * @return a collection of objects representing annotation attributes
//	 * @throws IllegalArgumentException
//	 *             if this extension of {@link AnnotationDataExtractor} is unable to handle the
//	 *             input {@link AnnotationAttribute} type
//	 */
//	public Collection<Object> getAnnotationAttributes(Annotation annotation, AnnotationAttribute attribute) {
//		if (attributeExtractors.containsKey(attribute))
//			return attributeExtractors.get(attribute).getAnnotationAttributes(annotation);
//		throw new IllegalArgumentException(String.format(
//				"The %s extension of AnnotationDataExtractor is unable to retrieve annotation attributes of type: %s.",
//				getClass().getName(), attribute.name()));
//	}

	public AnnotationAttributeExtractor getAnnotationAttributeExtractor(AnnotationAttribute attribute) {
		if (!attributeExtractors.containsKey(attribute))
			throw new IllegalArgumentException(String.format(
					"The %s extension of AnnotationDataExtractor does not have an annotation attribute extractor "
							+ "specified (%s) for the attribute type: %s.", getClass().getName(), attributeExtractors
							.get(attribute).getClass().getName(), attribute.name()));
		return attributeExtractors.get(attribute);
	}

	

	// /**
	// * @param annotation
	// * @param craftEntrezGeneIdSlotName
	// * @return
	// */
	// public boolean hasAnnotationField(Annotation annotation, String craftEntrezGeneIdSlotName) {
	// // TODO Auto-generated method stub
	// return false;
	// }

}
