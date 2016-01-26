package edu.ucdenver.ccp.nlp.uima.util;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.metadata.impl.FlowControllerDeclaration_impl;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.ConstraintFactory;
import org.apache.uima.cas.FSIntConstraint;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FSMatchConstraint;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeaturePath;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FloatArray;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.LongArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.impl.ProcessingResourceMetaData_impl;
import org.apache.uima.resource.metadata.impl.ResourceManagerConfiguration_impl;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.collections.LegacyCollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.Mention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.BooleanSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.DoubleSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.FloatSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.SlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPDocumentInformation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationMetadataProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.EvaluationResultProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.FalseNegativeProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.FalsePositiveProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.TruePositiveProperty;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPBooleanSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPDoubleSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPFloatSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPPrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPStringSlotMention;
import edu.ucdenver.ccp.nlp.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.mention.impl.CCPPrimitiveSlotMentionFactory;
import edu.ucdenver.ccp.nlp.uima.mention.impl.WrappedCCPFloatSlotMention;
import edu.ucdenver.ccp.nlp.uima.mention.impl.WrappedCCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.uima.mention.impl.WrappedCCPStringSlotMention;

/**
 * This is a utility class meant to streamline interaction with the <code>CCPTextAnnotation</code>
 * class.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class UIMA_Util {

	private static Logger logger = Logger.getLogger(UIMA_Util.class);

	public static void outputDescriptorToFile(AnalysisEngineDescription desc, File outputFile) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = FileWriterUtil.initBufferedWriter(outputFile, CharacterEncoding.UTF_8, WriteMode.OVERWRITE,
					FileSuffixEnforcement.OFF);

//			FlowControllerDeclaration flowControllerDeclaration = desc.getFlowControllerDeclaration();
//			System.out.println("FlowControllerDecl == null: " + (flowControllerDeclaration == null));
//			if (flowControllerDeclaration == null) {
				desc.setFlowControllerDeclaration(new FlowControllerDeclaration_impl());
//			}
//			ResourceMetaData metaData = desc.getMetaData();
//			System.out.println("MetaData == null: " + (metaData == null));
//			System.out.println(metaData.toString());
//			if (metaData.getUUID() == null) {
//				metaData.setUUID("");
//			}
//			if (metaData.getDescription() == null) {
//				metaData.setDescription("");
//			}
//			if (metaData.getConfigurationParameterDeclarations().getDefaultGroupName() == null) {
//				metaData.getConfigurationParameterDeclarations().setDefaultGroupName("");
//			}
//			if (metaData.getConfigurationParameterDeclarations().getSearchStrategy() == null) {
//				metaData.getConfigurationParameterDeclarations().setSearchStrategy("");
//			}
//			if (metaData.getCopyright()==null) {
//				metaData.setCopyright("");
//			}
//			if (metaData.getVendor()==null) {
//				metaData.setVendor("");
//			}
//			if (metaData.getVersion()==null) {
//				metaData.setVersion("");
//			}
//			if (metaData.getName() == null) {
//				metaData.setName("");
//			}
//			if (metaData.getAttributeValue("flowConstraints") == null) {
//				metaData.setAttributeValue("flowConstraints", new FlowConstraints);
//			}
//			
//			
//System.out.println("********************************************");			
//			System.out.println(metaData.toString());
//			System.out.println("********************************************");			
//			System.out.println(metaData.listAttributes());
			
			desc.setMetaData(new ProcessingResourceMetaData_impl());
			
//			ResourceManagerConfiguration resourceManagerConfiguration = desc.getResourceManagerConfiguration();
//			System.out.println("ResourceManagerConfig == null: " + (resourceManagerConfiguration == null));
//			if (resourceManagerConfiguration == null) {
				desc.setResourceManagerConfiguration(new ResourceManagerConfiguration_impl());
//			}
			desc.toXML(writer);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * Returns the documentID from the CCPDocumentInformation annotation if there is one. Returns
	 * "-1" otherwise.
	 * 
	 * @param jcas
	 * @return
	 */
	public static String getDocumentID(JCas jcas) {
		String documentID;
		FSIterator it = jcas.getJFSIndexRepository().getAllIndexedFS(CCPDocumentInformation.type);
		if (it.hasNext()) { /* there will be at most one CCPDocumentInformation annotation */
			CCPDocumentInformation docInfo = (CCPDocumentInformation) it.next();
			documentID = docInfo.getDocumentID();
			return documentID;
		}
		logger.warn("No document ID found, returning -1.");
		return "-1";
	}

	public static CCPDocumentInformation getDocumentInfo(JCas jcas) {
		FSIterator it = jcas.getJFSIndexRepository().getAllIndexedFS(CCPDocumentInformation.type);
		if (it.hasNext()) { /* there will be at most one CCPDocumentInformation annotation */
			CCPDocumentInformation docInfo = (CCPDocumentInformation) it.next();
			return docInfo;
		}
		logger.warn("No document ID found, returning -1.");
		return null;
	}

	/**
	 * Retrieves the document encoding for the document text stored in the JCas
	 * 
	 * @param jCas
	 * @return
	 * @throws IllegalStateException
	 *             if the encoding has not been set
	 */
	public static CharacterEncoding getDocumentEncoding(JCas jCas) {
		String encoding = getCcpDocumentInformation(jCas).getEncoding();
		if (encoding == null)
			throw new IllegalStateException(
					"The encoding field has not been set in the CCPDocumentInformation instance. "
							+ "The most likely reason for this is the collection reader implementation you are "
							+ "using did not set the encoding value. Use the FileSystemCollectionReader or "
							+ "adjust your collection reader accordingly.");
		encoding = encoding.replaceAll("-", "_");
		return CharacterEncoding.valueOf(encoding);
	}

	/**
	 * Sets the encoding field for the meta data. This encoding should correspond to the encoding
	 * used by the document text stored in the JCas.
	 * 
	 * @param jCas
	 * @param encoding
	 */
	public static void setDocumentEncoding(JCas jCas, CharacterEncoding encoding) {
		getCcpDocumentInformation(jCas).setEncoding(encoding.name());
	}

	/**
	 * Returns the documentID from the CCPDocumentInformation annotation if there is one. Returns
	 * "-1" otherwise.
	 * 
	 * @param jcas
	 * @return
	 */
	public static int getDocumentCollectionID(JCas jcas) {
		int documentCollectionID;
		FSIterator it = jcas.getJFSIndexRepository().getAllIndexedFS(CCPDocumentInformation.type);
		if (it.hasNext()) { /* there will be at most one CCPDocumentInformation annotation */
			CCPDocumentInformation docInfo = (CCPDocumentInformation) it.next();
			documentCollectionID = docInfo.getDocumentCollectionID();
			return documentCollectionID;
		} else {
			logger.warn("No document collection ID found, returning -1.");
			return -1;
		}
	}

	/**
	 * Returns an Iterator over CCPTextAnnotations that are in the CAS.
	 * 
	 * @param jcas
	 * @param classType
	 * @return
	 */
	public static Iterator<CCPTextAnnotation> getTextAnnotationIterator(JCas jcas) {
		return getTextAnnotationIterator(jcas, (String[]) null);
	}

	/**
	 * Returns an Iterator over CCPTextAnnotations that have a give class type (class mention name).
	 * 
	 * @param jcas
	 * @param classType
	 * @return
	 */
	public static Iterator<CCPTextAnnotation> getTextAnnotationIterator(JCas jcas, final String... classTypes) {
		Set<String> tempClassTypesSet;
		if (classTypes == null)
			tempClassTypesSet = new HashSet<String>();
		else
			tempClassTypesSet = CollectionsUtil.array2Set(classTypes);

		final FSIterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		final Set<String> classTypesSet = tempClassTypesSet;

		return new Iterator<CCPTextAnnotation>() {
			private CCPTextAnnotation nextAnnot = null;

			public boolean hasNext() {
				if (nextAnnot == null) {
					if (annotIter.hasNext()) {
						CCPTextAnnotation ccpTA = (CCPTextAnnotation) annotIter.next();
						if (checkForCorrectClassType(ccpTA)) {
							nextAnnot = ccpTA;
						} else {
							return hasNext();
						}
					} else {
						return false;
					}
				}
				return true;
			}

			public CCPTextAnnotation next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				CCPTextAnnotation annotToReturn = nextAnnot;
				nextAnnot = null;
				return annotToReturn;
			}

			public void remove() {
				throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
			}

			/**
			 * If the input CCPTextAnnotation is not null, and the classType is not null, then this
			 * method returns true if the classType matches the class mention name for the
			 * CCPTextAnnotation. If classType is null, then this method returns true as long as the
			 * input CCPTextAnnotation is not null.
			 * 
			 * @param ccpTA
			 * @return
			 */
			private boolean checkForCorrectClassType(CCPTextAnnotation ccpTA) {
				if (classTypes == null || (classTypesSet.contains(ccpTA.getClassMention().getMentionName()))) {
					return true;
				}
				return false;
			}
		};
	}

	public static List<TextAnnotation> getAnnotationsFromCas(JCas jcas) {
		return getAnnotationsFromCas(jcas, (String[]) null);
	}

	public static List<TextAnnotation> getAnnotationsFromCas(JCas jcas, String... classTypes) {
		List<TextAnnotation> annotationsToReturn = new ArrayList<TextAnnotation>();
		Iterator<CCPTextAnnotation> annotIter = getTextAnnotationIterator(jcas, classTypes);
		while (annotIter.hasNext()) {
			annotationsToReturn.add(new WrappedCCPTextAnnotation(annotIter.next()));
		}
		System.err.println(String.format("Returning %d annotations from the JCAS", annotationsToReturn.size()));
		logger.info(String.format("Returning %d annotations from the JCAS", annotationsToReturn.size()));
		return annotationsToReturn;
	}

	/**
	 * 
	 * Sets the CCPDocumentInformation documentID field
	 * 
	 * @param jcas
	 * @param documentID
	 */
	public static void setDocumentID(JCas jcas, String documentID) {
		CCPDocumentInformation docInfo = getCcpDocumentInformation(jcas);
		docInfo.setDocumentID(documentID);
	}

	public static void setDocumentCollectionID(JCas jcas, int documentCollectionID) {
		CCPDocumentInformation docInfo = getCcpDocumentInformation(jcas);
		docInfo.setDocumentCollectionID(documentCollectionID);
	}

	private static CCPDocumentInformation getCcpDocumentInformation(JCas jcas) {
		CCPDocumentInformation docInfo;
		FSIterator it = jcas.getJFSIndexRepository().getAllIndexedFS(CCPDocumentInformation.type);
		if (it.hasNext()) { /* there should be at most one CCPDocumentInformation annotation */
			docInfo = (CCPDocumentInformation) it.next();
		} else {
			docInfo = new CCPDocumentInformation(jcas);
			docInfo.addToIndexes();
		}
		return docInfo;
	}

	public static void swapDocumentInfo(JCas fromJcas, GenericDocument toGD) {
		FSIterator docInfoIter = fromJcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentInformation.type)
				.iterator();
		// print document information
		String docID = "-1";
		int docCollectionID = -1;
		if (docInfoIter.hasNext()) {
			CCPDocumentInformation docInfo = (CCPDocumentInformation) docInfoIter.next();
			docID = docInfo.getDocumentID();
			docCollectionID = docInfo.getDocumentCollectionID();
			/* Get any secondary document IDs */
			StringArray secondaryIDsArray = docInfo.getSecondaryDocumentIDs();
			if (secondaryIDsArray != null) {
				for (int i = 0; i < secondaryIDsArray.size(); i++) {
					toGD.addSecondaryDocumentID(secondaryIDsArray.get(i));
				}
			}
		}

		toGD.setDocumentID(docID);
		toGD.setDocumentCollectionID(docCollectionID);

		// set the document text
		toGD.setDocumentText(fromJcas.getDocumentText());

		// add the annotations to the Generic Document
		// UIMA_Util uimaUtil = new UIMA_Util();
		List<TextAnnotation> annotations = getAnnotationsFromCas(fromJcas);
		toGD.setAnnotations(annotations);
	}

	/**
	 * This method transfers general annotation info, i.e. span, annotator, etc. from a
	 * TextAnnotation to a CCPTextAnnotation.
	 * 
	 * @param ta
	 * @param ccpAnnotation
	 */
	public static void swapAnnotationInfo(TextAnnotation fromTA, CCPTextAnnotation toUIMA, JCas jcas) {
		// set the Annotation ID
		toUIMA.setAnnotationID(fromTA.getAnnotationID());

		// set the Annotation Sets
		Set<AnnotationSet> annotationSets = fromTA.getAnnotationSets();
		FSArray ccpAnnotationSets = new FSArray(jcas, annotationSets.size());
		int index = 0;
		for (AnnotationSet aSet : annotationSets) {
			CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
			UIMA_Util.swapAnnotationSetInfo(aSet, ccpAnnotationSet);
			ccpAnnotationSets.set(index++, ccpAnnotationSet);
		}
		toUIMA.setAnnotationSets(ccpAnnotationSets);
		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);
		UIMA_Util.swapAnnotatorInfo(fromTA.getAnnotator(), ccpAnnotator);
		toUIMA.setAnnotator(ccpAnnotator);

		/* Swap metadata info */
		edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata annotationMetadata = fromTA
				.getAnnotationMetadata();
		edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata ccpAnnotationMetadata = new edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata(
				jcas);
		UIMA_Util.swapAnnotationMetadata(annotationMetadata, ccpAnnotationMetadata, jcas);

		/*
		 * set the Span(s) A CCPSpan object is created for each Span object associated with the
		 * TextAnnotation. Also, the default Begin and End fields for the UIMA annotation are set to
		 * the min and max indexes of all Span objects.
		 */
		int minSpanIndex = Integer.MAX_VALUE;
		int maxSpanIndex = Integer.MIN_VALUE;
		ArrayList<Span> spans = (ArrayList<Span>) fromTA.getSpans();

		toUIMA.setNumberOfSpans(spans.size());
		FSArray supplementarySpans = new FSArray(jcas, spans.size());
		for (int i = 0; i < spans.size(); i++) {
			Span span = spans.get(i);
			if (minSpanIndex > span.getSpanStart()) {
				minSpanIndex = span.getSpanStart();
			}
			if (maxSpanIndex < span.getSpanEnd()) {
				maxSpanIndex = span.getSpanEnd();
			}

			CCPSpan uimaSpan = new CCPSpan(jcas);
			uimaSpan.setSpanStart(span.getSpanStart());
			uimaSpan.setSpanEnd(span.getSpanEnd());
			supplementarySpans.set(i, uimaSpan);
		}
		toUIMA.setSpans(supplementarySpans);

		if (minSpanIndex == Integer.MAX_VALUE) {
			minSpanIndex = 0;
			maxSpanIndex = 0;
		}
		toUIMA.setBegin(minSpanIndex);
		toUIMA.setEnd(maxSpanIndex);

		if (supplementarySpans.size() == 0) {
			toUIMA.setBegin(0);
			toUIMA.setEnd(0);
		}

		// set the DocumentSection ID
		toUIMA.setDocumentSectionID(fromTA.getDocumentSectionID());
	}

	/**
	 * This method transfers general annotation info, i.e. span, annotator, etc. from a
	 * CCPTextAnnotation to a TextAnnotation
	 * 
	 * @param ccpAnnotation
	 * @param ta
	 */
	public static void swapAnnotationInfo(CCPTextAnnotation fromUIMA, TextAnnotation toTA, JCas jcas) {
		// set the Annotation ID
		toTA.setAnnotationID(fromUIMA.getAnnotationID());

		// set the Annotation Sets

		FSArray ccpAnnotationSets = fromUIMA.getAnnotationSets();
		Set<AnnotationSet> annotationSets = new HashSet<AnnotationSet>();
		if (ccpAnnotationSets != null) {
			for (int i = 0; i < ccpAnnotationSets.size(); i++) {
				AnnotationSet annotationSet = new AnnotationSet(new Integer(-1), "", "");
				UIMA_Util.swapAnnotationSetInfo((CCPAnnotationSet) ccpAnnotationSets.get(i), annotationSet);
				annotationSets.add(annotationSet);
			}
		}
		toTA.setAnnotationSets(annotationSets);

		// set the Annotator ID
		CCPAnnotator ccpAnnotator = fromUIMA.getAnnotator();
		Annotator annotator = new Annotator(new Integer(-1), "", "", "");
		UIMA_Util.swapAnnotatorInfo(ccpAnnotator, annotator);
		toTA.setAnnotator(annotator);

		/* swap metadata */
		edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata annotationMetadata = new edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata();
		edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata ccpAnnotationMetadata = fromUIMA
				.getAnnotationMetadata();
		UIMA_Util.swapAnnotationMetadata(ccpAnnotationMetadata, annotationMetadata, jcas);
		toTA.setAnnotationMetadata(annotationMetadata);

		// set the Span(s)
		List<Span> spans = new ArrayList<Span>();
		FSArray ccpSpans = fromUIMA.getSpans();
		/*
		 * if there are no explicit spans in the CCPTextAnnotation, then we need to create a default
		 * span using the start/end indexes
		 */
		try {
			if (ccpSpans.size() == 0) {
				Span span = new Span(fromUIMA.getStart(), fromUIMA.getEnd());
				spans.add(span);
			} else {
				for (int i = 0; i < ccpSpans.size(); i++) {// FeatureStructure fs : fsArray) {
					CCPSpan ccpSpan = (CCPSpan) ccpSpans.get(i);
					Span span = new Span(ccpSpan.getSpanStart(), ccpSpan.getSpanEnd());
					spans.add(span);
				}
			}
		} catch (InvalidSpanException e) {
			e.printStackTrace();
		}

		toTA.setSpans(spans);

		// set the DocumentSection ID
		toTA.setDocumentSectionID(fromUIMA.getDocumentSectionID());

		// set the Document ID and Document Collection ID
		String docID = getDocumentID(jcas);
		int docCollectionID = getDocumentCollectionID(jcas);
		toTA.setDocumentID(docID);
		toTA.setDocumentCollectionID(docCollectionID);

		// set the Covered Text
		try {
			toTA.setCoveredText(fromUIMA.getCoveredText());
		} catch (Exception e) {
			System.err.println("EXCEPTION: " + jcas.getDocumentText());
		}
	}

	/**
	 * Swap from UIMA Annotation to another UIMA Annotation
	 * 
	 * @param fromUIMA
	 * @param toUIMA
	 * @param jcas
	 */
	public static void swapAnnotationInfo(CCPTextAnnotation fromUIMA, CCPTextAnnotation toUIMA) throws CASException {
		// set the Annotation ID
		toUIMA.setAnnotationID(fromUIMA.getAnnotationID());

		// set the Annotation Sets
		toUIMA.setAnnotationSets(fromUIMA.getAnnotationSets());

		// set the Annotator ID
		toUIMA.setAnnotator(fromUIMA.getAnnotator());

		/* set the annotation metadata */
		toUIMA.setAnnotationMetadata(fromUIMA.getAnnotationMetadata());

		// set the Span(s)
		toUIMA.setSpans(fromUIMA.getSpans());

		/* set the number of spans */
		toUIMA.setNumberOfSpans(fromUIMA.getNumberOfSpans());

		// set the default Begin and End fields
		toUIMA.setBegin(fromUIMA.getBegin());
		toUIMA.setEnd(fromUIMA.getEnd());

		// set the DocumentSection ID
		toUIMA.setDocumentSectionID(fromUIMA.getDocumentSectionID());

		/* swap class mentions */
		CCPClassMention ccpCM = new CCPClassMention(fromUIMA.getCAS().getJCas());
		UIMA_Util.swapClassMentionInfo(fromUIMA.getClassMention(), ccpCM);
		toUIMA.setClassMention(ccpCM);
		ccpCM.setCcpTextAnnotation(toUIMA);
	}

	/**
	 * Clones a CCPTextAnnotation
	 * 
	 * @param ccpTA
	 * @param jcas
	 * @return
	 * @throws CASException
	 */
	public static CCPTextAnnotation cloneAnnotation(CCPTextAnnotation ccpTA, JCas jcas) throws CASException {
		CCPTextAnnotation newCCPTA = new CCPTextAnnotation(jcas);
		UIMA_Util.swapAnnotationInfo(ccpTA, newCCPTA);
		return newCCPTA;
	}

	public static void swapClassMentionInfo(CCPClassMention fromCM, CCPClassMention toCM) throws CASException {
		toCM.setMentionName(fromCM.getMentionName());
		JCas jcas = fromCM.getCAS().getJCas();
		FSArray fromSlotMentions = fromCM.getSlotMentions();
		if (fromSlotMentions != null) {
			FSArray toSlotMentions = new FSArray(jcas, fromSlotMentions.size());
			for (int i = 0; i < fromSlotMentions.size(); i++) {
				if (fromSlotMentions.get(i) instanceof CCPPrimitiveSlotMention) {
					toSlotMentions.set(i,
							copyCCPPrimitiveSlotMention((CCPPrimitiveSlotMention) fromSlotMentions.get(i)));
				} else if (fromSlotMentions.get(i) instanceof CCPComplexSlotMention) {
					CCPComplexSlotMention toSM = new CCPComplexSlotMention(jcas);
					UIMA_Util.swapComplexSlotMentionInfo((CCPComplexSlotMention) fromSlotMentions.get(i), toSM);
					toSlotMentions.set(i, toSM);
				} else {
					System.err.println("Expecting CCPNonComplexSlotMention of CCPComplexSlotMention but got: "
							+ fromSlotMentions.get(i).getClass().getName());
				}
			}
			toCM.setSlotMentions(toSlotMentions);
		}

		CCPTextAnnotation fromTextAnnotation = fromCM.getCcpTextAnnotation();
		if (fromTextAnnotation != null) {
			toCM.setCcpTextAnnotation(fromTextAnnotation);
		}
	}

	private static CCPPrimitiveSlotMention copyCCPPrimitiveSlotMention(CCPPrimitiveSlotMention fromSM)
			throws CASException {
		JCas jcas = fromSM.getCAS().getJCas();
		try {
			if (fromSM instanceof CCPStringSlotMention) {
				return CCPPrimitiveSlotMentionFactory.createCCPStringSlotMention(fromSM.getMentionName(),
						convertToCollection(((CCPStringSlotMention) fromSM).getSlotValues()), jcas);
			} else if (fromSM instanceof CCPIntegerSlotMention) {
				return CCPPrimitiveSlotMentionFactory.createCCPIntegerSlotMention(fromSM.getMentionName(),
						convertToCollection(((CCPIntegerSlotMention) fromSM).getSlotValues()), jcas);
			} else if (fromSM instanceof CCPFloatSlotMention) {
				return CCPPrimitiveSlotMentionFactory.createCCPFloatSlotMention(fromSM.getMentionName(),
						convertToCollection(((CCPFloatSlotMention) fromSM).getSlotValues()), jcas);
			} else if (fromSM instanceof CCPBooleanSlotMention) {
				return CCPPrimitiveSlotMentionFactory.createCCPBooleanSlotMention(fromSM.getMentionName(),
						((CCPBooleanSlotMention) fromSM).getSlotValue(), jcas);
			} else {
				throw new KnowledgeRepresentationWrapperException("Unknown CCP Primitive Slot Mention type: "
						+ fromSM.getClass().getName() + " Cannot copy CCPPrimitiveSlotMention.");
			}

		} catch (KnowledgeRepresentationWrapperException e) {
			throw new CASException(e);
		}
	}

	private static Collection<Integer> convertToCollection(IntegerArray iArray) {
		Collection<Integer> iCollection = new ArrayList<Integer>();
		for (int i = 0; i < iArray.size(); i++) {
			iCollection.add(iArray.get(i));
		}
		return iCollection;
	}

	private static Collection<String> convertToCollection(StringArray iArray) {
		Collection<String> iCollection = new ArrayList<String>();
		for (int i = 0; i < iArray.size(); i++) {
			iCollection.add(iArray.get(i));
		}
		return iCollection;
	}

	private static Collection<Float> convertToCollection(FloatArray iArray) {
		Collection<Float> iCollection = new ArrayList<Float>();
		for (int i = 0; i < iArray.size(); i++) {
			iCollection.add(iArray.get(i));
		}
		return iCollection;
	}

	public static void swapComplexSlotMentionInfo(CCPComplexSlotMention fromSM, CCPComplexSlotMention toSM)
			throws CASException {
		toSM.setMentionName(fromSM.getMentionName());
		JCas jcas = fromSM.getCAS().getJCas();
		FSArray fromClassMentions = fromSM.getClassMentions();
		if (fromClassMentions != null) {
			FSArray toClassMentions = new FSArray(jcas, fromClassMentions.size());
			for (int i = 0; i < fromClassMentions.size(); i++) {
				CCPClassMention toCM = new CCPClassMention(jcas);
				UIMA_Util.swapClassMentionInfo((CCPClassMention) fromClassMentions.get(i), toCM);
				toClassMentions.set(i, toCM);
			}
			toSM.setClassMentions(toClassMentions);
		}
	}

	public static void swapAnnotatorInfo(CCPAnnotator ccpAnnotator, Annotator annotator) {
		if (ccpAnnotator != null) {
			annotator.setAnnotatorID(new Integer(ccpAnnotator.getAnnotatorID()));
			annotator.setFirstName(ccpAnnotator.getFirstName());
			annotator.setLastName(ccpAnnotator.getLastName());
			annotator.setAffiliation(ccpAnnotator.getAffiliation());
		}
	}

	public static void swapAnnotatorInfo(Annotator annotator, CCPAnnotator ccpAnnotator) {
		if (annotator != null) {
			ccpAnnotator.setAnnotatorID(annotator.getAnnotatorID().intValue());
			ccpAnnotator.setFirstName(annotator.getFirstName());
			ccpAnnotator.setLastName(annotator.getLastName());
			ccpAnnotator.setAffiliation(annotator.getAffiliation());
		}
	}

	public static void swapAnnotationSetInfo(CCPAnnotationSet ccpAnnotationSet, AnnotationSet annotationSet) {
		if (ccpAnnotationSet != null) {
			annotationSet.setAnnotationSetID(new Integer(ccpAnnotationSet.getAnnotationSetID()));
			annotationSet.setAnnotationSetName(ccpAnnotationSet.getAnnotationSetName());
			annotationSet.setAnnotationSetDescription(ccpAnnotationSet.getAnnotationSetDescription());
		}
	}

	public static void swapAnnotationSetInfo(AnnotationSet annotationSet, CCPAnnotationSet ccpAnnotationSet) {
		if (annotationSet != null) {
			ccpAnnotationSet.setAnnotationSetID(annotationSet.getAnnotationSetID().intValue());
			ccpAnnotationSet.setAnnotationSetName(annotationSet.getAnnotationSetName());
			ccpAnnotationSet.setAnnotationSetDescription(annotationSet.getAnnotationSetDescription());
		}
	}

	public static void swapAnnotationMetadata(
			edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata annotationMetadata,
			edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata ccpAnnotationMetadata, JCas jcas) {
		if (annotationMetadata != null) {
			/* See if there is an EvaluationResultProperty */
			List<AnnotationMetadataProperty> ccpMetadataPropertiesToAdd = new ArrayList<AnnotationMetadataProperty>();
			EvaluationResultProperty erp = null;
			if (annotationMetadata.isTruePositive()) {
				erp = new TruePositiveProperty(jcas);
			} else if (annotationMetadata.isFalsePositive()) {
				erp = new FalsePositiveProperty(jcas);
			} else if (annotationMetadata.isFalseNegative()) {
				erp = new FalseNegativeProperty(jcas);
			}
			if (erp != null) {
				ccpMetadataPropertiesToAdd.add(erp);
			}

			AnnotationCommentProperty annotationCommentProp = null;
			if (annotationMetadata.getAnnotationComment() != null) {
				annotationCommentProp = new AnnotationCommentProperty(jcas);
				annotationCommentProp.setComment(annotationMetadata.getAnnotationComment());
			}
			if (annotationCommentProp != null) {
				ccpMetadataPropertiesToAdd.add(annotationCommentProp);
			}

			/* Swap properties here */
			FSArray metaDataProperties = ccpAnnotationMetadata.getMetadataProperties();
			int propertiesToAddCount = ccpMetadataPropertiesToAdd.size();
			if (metaDataProperties == null) {
				metaDataProperties = new FSArray(jcas, propertiesToAddCount);
				for (int i = 0; i < ccpMetadataPropertiesToAdd.size(); i++) {
					metaDataProperties.set(i, ccpMetadataPropertiesToAdd.get(i));
				}
				ccpAnnotationMetadata.setMetadataProperties(metaDataProperties);
			} else {
				/* add the properties that already exists */
				FSArray newMetaDataProperties = new FSArray(jcas, metaDataProperties.size() + propertiesToAddCount);
				for (int i = 0; i < metaDataProperties.size(); i++) {
					newMetaDataProperties.set(i, metaDataProperties.get(i));
				}
				/* now add the properties that are being transferred -- this could cause duplicates */
				int addIndex = metaDataProperties.size() - 1;
				for (int i = 0; i < ccpMetadataPropertiesToAdd.size(); i++) {
					newMetaDataProperties.set(i + addIndex, metaDataProperties.get(i));
				}
			}
		}
	}

	public static void swapAnnotationMetadata(
			edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata ccpAnnotationMetadata,
			edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata annotationMetadata, JCas jcas) {
		if (ccpAnnotationMetadata != null) {
			FSArray metadataProperties = ccpAnnotationMetadata.getMetadataProperties();
			if (metadataProperties != null) {
				for (int i = 0; i < metadataProperties.size(); i++) {
					edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationMetadataProperty amp = (edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationMetadataProperty) metadataProperties
							.get(i);
					if (amp instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty) {
						edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty ccpProp = (edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty) amp;
						edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationCommentProperty prop = new edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationCommentProperty(
								ccpProp.getComment());
						annotationMetadata.addMetadataProperty(prop);
					} else if (amp instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.TruePositiveProperty) {
						edu.ucdenver.ccp.nlp.core.annotation.metadata.TruePositiveProperty prop = new edu.ucdenver.ccp.nlp.core.annotation.metadata.TruePositiveProperty();
						annotationMetadata.addMetadataProperty(prop);
					} else if (amp instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.FalsePositiveProperty) {
						edu.ucdenver.ccp.nlp.core.annotation.metadata.FalsePositiveProperty prop = new edu.ucdenver.ccp.nlp.core.annotation.metadata.FalsePositiveProperty();
						annotationMetadata.addMetadataProperty(prop);
					} else if (amp instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.FalseNegativeProperty) {
						edu.ucdenver.ccp.nlp.core.annotation.metadata.FalseNegativeProperty prop = new edu.ucdenver.ccp.nlp.core.annotation.metadata.FalseNegativeProperty();
						annotationMetadata.addMetadataProperty(prop);
					} else {
						logger.error("Swapping of AnnotationMetadataProperty: " + amp.getClass().getName());
					}
				}
			}
		}
	}

	public static void addAnnotationSet(CCPTextAnnotation ccpTA, CCPAnnotationSet annotationSet, JCas jcas) {
		FSArray updatedAnnotationSets = null;
		FSArray annotationSets = ccpTA.getAnnotationSets();
		if (annotationSets != null) {
			updatedAnnotationSets = new FSArray(jcas, annotationSets.size() + 1);
			for (int i = 0; i < annotationSets.size(); i++) {
				updatedAnnotationSets.set(i, annotationSets.get(i));
			}
		} else {
			updatedAnnotationSets = new FSArray(jcas, 1);
		}

		updatedAnnotationSets.set(updatedAnnotationSets.size() - 1, annotationSet);
		ccpTA.setAnnotationSets(updatedAnnotationSets);
	}

	/**
	 * @param ccpTa
	 * @param setId
	 * @return true if the input {@link CCPTextAnnotation} is associated with the input annotation
	 *         set identifier
	 */
	public static boolean hasAnnotationSet(CCPTextAnnotation ccpTa, int setId) {
		FSArray annotationSets = ccpTa.getAnnotationSets();
		if (annotationSets != null) {
			for (int i = 0; i < annotationSets.size(); i++) {
				CCPAnnotationSet set = (CCPAnnotationSet) annotationSets.get(i);
				if (set.getAnnotationSetID() == setId) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes the annotation set identified by the setIdToRemove identifier from the input
	 * {@link CCPTextAnnotation} if that set is present
	 * 
	 * @param ccpTa
	 * @param setIdToRemove
	 * @param jcas
	 */
	public static void removeAnnotationSet(CCPTextAnnotation ccpTa, int setIdToRemove, JCas jcas) {
		if (hasAnnotationSet(ccpTa, setIdToRemove)) {
			FSArray updatedAnnotationSets = null;
			FSArray annotationSets = ccpTa.getAnnotationSets();
			if (annotationSets != null) {
				updatedAnnotationSets = new FSArray(jcas, annotationSets.size() - 1);
				int index = 0;
				for (int i = 0; i < annotationSets.size(); i++) {
					CCPAnnotationSet set = (CCPAnnotationSet) annotationSets.get(i);
					if (set.getAnnotationSetID() != setIdToRemove) {
						updatedAnnotationSets.set(index++, annotationSets.get(i));
					}
				}
				ccpTa.setAnnotationSets(updatedAnnotationSets);
			}
		}
	}

	public void putTextAnnotationsIntoJCas(JCas jcas, Collection<TextAnnotation> textAnnotations) {
		HashMap<String, String> alreadyCreatedAnnotations = new HashMap<String, String>();
		HashMap<String, CCPClassMention> alreadyCreatedMentions = new HashMap<String, CCPClassMention>();

		for (TextAnnotation ta : textAnnotations) {
			createUIMAAnnotation(ta, jcas, alreadyCreatedAnnotations, alreadyCreatedMentions, null);
		}
	}

	private void createUIMAAnnotation(TextAnnotation ta, JCas jcas, HashMap<String, String> alreadyCreatedAnnotations,
			HashMap<String, CCPClassMention> alreadyCreatedMentions, CCPClassMention classMention) {

		if (!alreadyCreatedAnnotations.containsKey(ta.getSingleLineRepresentation())) {

			CCPTextAnnotation ccpTextAnnotation = new CCPTextAnnotation(jcas);

			// extract the annotation information from the TextAnnotation and
			// fill the corresponding fields in the SemanticAnnotation
			UIMA_Util.swapAnnotationInfo(ta, ccpTextAnnotation, jcas);

			/**
			 * This has the potential to introduce a duplicate comment, however when working with
			 * knowtator annotations - where there is no meta data, this is the only way to transfer
			 * comments
			 */
			String comment = ta.getAnnotationComment();
			if (comment != null)
				UIMA_Annotation_Util.addAnnotationCommentProperty(ccpTextAnnotation, comment, jcas);

			// add key to alreadyAddedAnnotations
			alreadyCreatedAnnotations.put(ta.getSingleLineRepresentation(), "");

			// if mention is null, then create a new UIMA mention from the
			// associated ClassMention, else, use mention
			// this prevents infinite loops from occurring when creating an
			// annotation from a ClassMention that was found in a
			// complexSlotMention.
			if (classMention == null) {
				// create associated class mention
				ClassMention cm = ta.getClassMention();
				classMention = (CCPClassMention) this.createUIMAMention(cm, jcas, alreadyCreatedAnnotations,
						alreadyCreatedMentions);
			}

			if (classMention == null) {
				System.err.println("CLASSMENTION IS NULL!!!!");
			}
			classMention.setCcpTextAnnotation(ccpTextAnnotation);

			ccpTextAnnotation.setClassMention(classMention);

			// add the SemanticAnnotation to the CAS indexes
			ccpTextAnnotation.addToIndexes();
		} else {
			// do nothing, this annotation has already been created in the
			// Knowtator project
		}
	}

	private CCPMention createUIMAMention(Mention mentionToAdd, JCas jcas,
			HashMap<String, String> alreadyCreatedAnnotations, HashMap<String, CCPClassMention> alreadyCreatedMentions) {

		CCPMention returnMention;

		if (mentionToAdd instanceof ClassMention) {
			returnMention = processClassMention((ClassMention) mentionToAdd, jcas, alreadyCreatedAnnotations,
					alreadyCreatedMentions);
		} else if (mentionToAdd instanceof ComplexSlotMention) {
			returnMention = processComplexSlotMention((ComplexSlotMention) mentionToAdd, jcas,
					alreadyCreatedAnnotations, alreadyCreatedMentions);
		} else if (mentionToAdd instanceof SlotMention) {
			returnMention = processSlotMention((SlotMention) mentionToAdd, jcas, alreadyCreatedAnnotations,
					alreadyCreatedMentions);
		} else {
			System.err.println("The mention you are trying to create is an instance of: "
					+ mentionToAdd.getClass().getName());
			System.err.println("Currently, only ClassMentions, ComplexSlotMentions, and SlotMentions can be added.");
			returnMention = null;
		}

		return returnMention;
	}

	private CCPClassMention processClassMention(ClassMention classMention, JCas jcas,
			HashMap<String, String> alreadyCreatedAnnotations, HashMap<String, CCPClassMention> alreadyCreatedMentions) {

		CCPClassMention ccpClassMention;

		// check to see if this mention has already been created
		if (alreadyCreatedMentions.containsKey(classMention.getSingleLineRepresentation())) {
			ccpClassMention = alreadyCreatedMentions.get(classMention.getSingleLineRepresentation());
		} else {
			// create a new Knowtator mention

			ccpClassMention = new CCPClassMention(jcas);
			ccpClassMention.setMentionName(classMention.getMentionName());

			/**
			 * should this be at the end??? No, it doesn't matter because classMention.getHashKey is
			 * complete
			 */
			// add the knowtator to the alreadyCreatedMentions hash
			alreadyCreatedMentions.put(classMention.getSingleLineRepresentation(), ccpClassMention);

			// for each complexSlotMention, add a new knowtator class
			// mention
			Collection<ComplexSlotMention> complexSlotMentions = classMention.getComplexSlotMentions();
			Collection<PrimitiveSlotMention> slotMentions = null;
			try {
				slotMentions = classMention.getPrimitiveSlotMentions();
			} catch (KnowledgeRepresentationWrapperException e) {
				e.printStackTrace();
			}

			int nonEmptyComplexSlotMentions = SlotMention.nonEmptySlotMentionCount(complexSlotMentions);
			int nonEmptyPrimitiveSlotMentions = SlotMention.nonEmptySlotMentionCount(slotMentions);
			FSArray slotMentionFSArray = new FSArray(jcas, nonEmptyComplexSlotMentions + nonEmptyPrimitiveSlotMentions);
			int fsArrayIndex = 0;
			for (ComplexSlotMention csm : complexSlotMentions) {
				CCPComplexSlotMention ccpComplexSlotMention = (CCPComplexSlotMention) createUIMAMention(csm, jcas,
						alreadyCreatedAnnotations, alreadyCreatedMentions);

				if (ccpComplexSlotMention != null) {
					slotMentionFSArray.set(fsArrayIndex++, ccpComplexSlotMention);
				}
			}

			for (SlotMention sm : slotMentions) {
				CCPPrimitiveSlotMention ccpNonComplexSlotMention = (CCPPrimitiveSlotMention) createUIMAMention(sm,
						jcas, alreadyCreatedAnnotations, alreadyCreatedMentions);

				if (ccpNonComplexSlotMention != null) {
					slotMentionFSArray.set(fsArrayIndex++, ccpNonComplexSlotMention);
				}
			}

			// add slot mentions array to classmention
			ccpClassMention.setSlotMentions(slotMentionFSArray);

			// create Knowtator annotation of TextAnnotation(s) associated with
			// this ClassMention
			createUIMAAnnotation(classMention.getTextAnnotation(), jcas, alreadyCreatedAnnotations,
					alreadyCreatedMentions, ccpClassMention);
		}
		return ccpClassMention;
	}

	private CCPComplexSlotMention processComplexSlotMention(ComplexSlotMention complexSlotMention, JCas jcas,
			HashMap<String, String> alreadyCreatedAnnotations, HashMap<String, CCPClassMention> alreadyCreatedMentions) {

		// create a new Knowtator mention
		CCPComplexSlotMention ccpComplexSlotMention = new CCPComplexSlotMention(jcas);
		ccpComplexSlotMention.setMentionName(complexSlotMention.getMentionName());

		// for each of its classMentions, create a new Knowtator mention
		Collection<ClassMention> classMentions = complexSlotMention.getClassMentions();

		if (classMentions.size() == 0) {
			return null;
		}

		FSArray classMentionFSArray = new FSArray(jcas, classMentions.size());
		int fsArrayIndex = 0;

		for (ClassMention cm : classMentions) {
			CCPClassMention ccpClassMention = (CCPClassMention) createUIMAMention(cm, jcas, alreadyCreatedAnnotations,
					alreadyCreatedMentions);
			classMentionFSArray.set(fsArrayIndex++, ccpClassMention);
		}

		// add slot mentions array to classmention
		ccpComplexSlotMention.setClassMentions(classMentionFSArray);

		return ccpComplexSlotMention;
	}

	private CCPPrimitiveSlotMention processSlotMention(SlotMention slotMention, JCas jcas,
			HashMap<String, String> alreadyCreatedAnnotations, HashMap<String, CCPClassMention> alreadyCreatedMentions) {

		Collection<Object> slotValues = slotMention.getSlotValues();
		CCPPrimitiveSlotMention ccpPSM = null;
		try {
			ccpPSM = CCPPrimitiveSlotMentionFactory.createCCPPrimitiveSlotMention(slotMention.getMentionName(),
					slotValues, jcas);
		} catch (KnowledgeRepresentationWrapperException e) {
			e.printStackTrace();
		}

		return ccpPSM;
	}

	public static void printCCPTextAnnotation(CCPTextAnnotation ccpTA, PrintStream ps) {
		TextAnnotation ta = new WrappedCCPTextAnnotation(ccpTA);
		ps.println(ta.toString());
	}

	public static CCPSlotMention getSlotMentionByName(CCPTextAnnotation ccpTextAnnotation, String slotMentionName) {
		return getSlotMentionByName(ccpTextAnnotation.getClassMention(), slotMentionName);
	}

	public static CCPSlotMention getSlotMentionByName(CCPClassMention ccpClassMention, String slotMentionName) {
		FSArray slotMentionsArray = ccpClassMention.getSlotMentions();
		if (slotMentionsArray != null) {
			CCPSlotMention returnSlotMention = null;
			for (int i = 0; i < slotMentionsArray.size(); i++) {
				FeatureStructure fs = slotMentionsArray.get(i);
				if (fs instanceof CCPSlotMention) {
					CCPSlotMention ccpSlotMention = (CCPSlotMention) fs;
					if (ccpSlotMention.getMentionName().equals(slotMentionName)) {
						returnSlotMention = ccpSlotMention;
						break;
					}
				} else {
					logger.error("Expecting CCPSlotMention but got a : " + fs.getClass().getName());
				}
			}
			return returnSlotMention;
		} else {
			return null;
		}
	}

	public static CCPPrimitiveSlotMention getPrimitiveSlotMentionByName(CCPClassMention ccpClassMention,
			String slotMentionName) {
		FSArray slotMentionsArray = ccpClassMention.getSlotMentions();
		if (slotMentionsArray != null) {
			CCPPrimitiveSlotMention returnSlotMention = null;
			for (int i = 0; i < slotMentionsArray.size(); i++) {
				FeatureStructure fs = slotMentionsArray.get(i);
				if (fs instanceof CCPPrimitiveSlotMention) {
					CCPPrimitiveSlotMention ccpSlotMention = (CCPPrimitiveSlotMention) fs;
					if (ccpSlotMention.getMentionName().equals(slotMentionName)) {
						returnSlotMention = ccpSlotMention;
						break;
					}
				}
			}
			return returnSlotMention;
		} else {
			return null;
		}
	}

	public static CCPComplexSlotMention getComplexSlotMentionByName(CCPTextAnnotation ccpTA, String slotMentionName) {
		return getComplexSlotMentionByName(ccpTA.getClassMention(), slotMentionName);
	}

	public static CCPComplexSlotMention getComplexSlotMentionByName(CCPClassMention ccpClassMention,
			String slotMentionName) {

		FSArray slotMentionsArray = ccpClassMention.getSlotMentions();
		if (slotMentionsArray != null) {
			CCPComplexSlotMention returnSlotMention = null;
			for (int i = 0; i < slotMentionsArray.size(); i++) {
				FeatureStructure fs = slotMentionsArray.get(i);
				if (fs instanceof CCPComplexSlotMention) {
					CCPComplexSlotMention ccpSlotMention = (CCPComplexSlotMention) fs;
					if (ccpSlotMention.getMentionName().equals(slotMentionName)) {
						returnSlotMention = ccpSlotMention;
						break;
					}
				}
			}
			return returnSlotMention;
		} else {
			return null;
		}
	}

	public static void removeSlotMentions(CCPClassMention ccpCM, Class slotType, JCas jcas) {
		List<CCPSlotMention> slotMentionsToKeep = new ArrayList<CCPSlotMention>();
		FSArray slotMentions = ccpCM.getSlotMentions();
		if (slotMentions != null) {
			for (int i = 0; i < slotMentions.size(); i++) {
				if (!(slotType.isInstance(slotMentions.get(i)))) {
					slotMentionsToKeep.add((CCPSlotMention) slotMentions.get(i));
				}
			}
		}
		FSArray updatedSlotMentions = new FSArray(jcas, slotMentionsToKeep.size());
		for (int i = 0; i < slotMentionsToKeep.size(); i++) {
			updatedSlotMentions.set(i, slotMentionsToKeep.get(i));
		}
		ccpCM.setSlotMentions(updatedSlotMentions);
	}

	public static void addSlotMentions(CCPClassMention ccpCM, Collection<CCPSlotMention> slotMentions, JCas jcas) {
		FSArray updatedSlotMentions = ccpCM.getSlotMentions();
		for (CCPSlotMention ccpSM : slotMentions) {
			updatedSlotMentions = addToFSArray(updatedSlotMentions, ccpSM, jcas);
		}
		ccpCM.setSlotMentions(updatedSlotMentions);
	}

	/**
	 * Returns a list of the CCPClassMention objects that are the slot fillers for the named slot
	 * 
	 * @param ccpTA
	 * @param slotMentionName
	 * @return
	 */
	public static List<CCPClassMention> getComplexSlotValues(CCPTextAnnotation ccpTA, String slotMentionName) {
		return getComplexSlotValues(ccpTA.getClassMention(), slotMentionName);
	}

	/**
	 * Returns a list of the CCPClassMention objects that are the slot fillers for the named slot
	 * 
	 * @param ccpClassMention
	 * @param slotMentionName
	 * @return
	 */
	public static List<CCPClassMention> getComplexSlotValues(CCPClassMention ccpClassMention, String slotMentionName) {
		List<CCPClassMention> slotValuesToReturn = new ArrayList<CCPClassMention>();
		CCPSlotMention slotMention = getSlotMentionByName(ccpClassMention, slotMentionName);
		if (slotMention != null) {
			if (slotMention instanceof CCPComplexSlotMention) {
				CCPComplexSlotMention ccpCSM = (CCPComplexSlotMention) slotMention;
				slotValuesToReturn = fsarrayToList(ccpCSM.getClassMentions());
			} else {
				logger.warn("Slot: " + slotMentionName + " is not a complex slot (It is a "
						+ slotMention.getClass().getName() + "), therefore no slot values are being returned.");
			}
		}
		return slotValuesToReturn;
	}

	public static Set<String> getSlotNames(CCPClassMention ccpCM, Class slotType) {
		Set<String> slotNames = new HashSet<String>();
		FSArray ccpSlotMentions = ccpCM.getSlotMentions();
		if (ccpSlotMentions != null) {
			for (int i = 0; i < ccpSlotMentions.size(); i++) {
				if (slotType.isInstance(ccpSlotMentions.get(i))) {
					slotNames.add(((CCPSlotMention) ccpSlotMentions.get(i)).getMentionName());
				}
			}
		}
		return slotNames;
	}

	public static Collection<CCPComplexSlotMention> getComplexSlotMentions(CCPClassMention ccpCM) {
		Collection<CCPComplexSlotMention> slotMentions = new ArrayList<CCPComplexSlotMention>();
		FSArray ccpSlotMentions = ccpCM.getSlotMentions();
		if (ccpSlotMentions != null) {
			for (int i = 0; i < ccpSlotMentions.size(); i++) {
				if (ccpSlotMentions.get(i) instanceof CCPComplexSlotMention) {
					slotMentions.add(((CCPComplexSlotMention) ccpSlotMentions.get(i)));
				}
			}
		}
		return slotMentions;
	}

	public static Collection<CCPPrimitiveSlotMention> getPrimitiveSlotMentions(CCPClassMention ccpCM) {
		Collection<CCPPrimitiveSlotMention> slotMentions = new ArrayList<CCPPrimitiveSlotMention>();
		FSArray ccpSlotMentions = ccpCM.getSlotMentions();
		if (ccpSlotMentions != null) {
			for (int i = 0; i < ccpSlotMentions.size(); i++) {
				if (ccpSlotMentions.get(i) instanceof CCPPrimitiveSlotMention) {
					slotMentions.add(((CCPPrimitiveSlotMention) ccpSlotMentions.get(i)));
				}
			}
		}
		return slotMentions;
	}

	/**
	 * Converts an FSArray to a List<>
	 * 
	 * @param <T>
	 * @param fsArray
	 * @return
	 */
	public static <T extends FeatureStructure> List<T> fsarrayToList(FSArray fsArray) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < fsArray.size(); i++) {
			list.add((T) fsArray.get(i));
		}
		return list;
	}

	/**
	 * Converts a list of FeatureStructure objects into an FSArray
	 * 
	 * @param list
	 * @param jcas
	 * @return
	 */
	public static FSArray listToFsarray(List<FeatureStructure> list, JCas jcas) {
		FSArray fsarray = new FSArray(jcas, list.size());
		for (int i = 0; i < list.size(); i++) {
			fsarray.set(i, list.get(i));
		}
		return fsarray;
	}

	/**
	 * Returns a new FSArray consisting of the contents of the input FSArray and the Collection of
	 * FeatureStructure objects
	 * 
	 * @param fsArray
	 * @param featureStructuresToAdd
	 * @return
	 * @throws CASException
	 */
	public static FSArray addToFSArray(FSArray fsArray, Collection<TOP> featureStructuresToAdd, JCas jcas) {
		if (fsArray == null) {
			fsArray = new FSArray(jcas, 0);
		}
		FSArray fsArrayToReturn = new FSArray(jcas, fsArray.size() + featureStructuresToAdd.size());
		for (int i = 0; i < fsArray.size(); i++) {
			fsArrayToReturn.set(i, fsArray.get(i));
		}
		int index = fsArray.size();
		for (TOP fs : featureStructuresToAdd) {
			fsArrayToReturn.set(index++, fs);
		}
		return fsArrayToReturn;
	}

	/**
	 * Adds a single feature structure to a FSArray
	 * 
	 * @param fsArray
	 * @param featureStructureToAdd
	 * @param jcas
	 * @return
	 */
	public static FSArray addToFSArray(FSArray fsArray, TOP featureStructureToAdd, JCas jcas) {
		if (fsArray == null) {
			fsArray = new FSArray(jcas, 0);
		}
		FSArray fsArrayToReturn = new FSArray(jcas, fsArray.size() + 1);
		for (int i = 0; i < fsArray.size(); i++) {
			fsArrayToReturn.set(i, fsArray.get(i));
		}
		fsArrayToReturn.set(fsArray.size(), featureStructureToAdd);
		return fsArrayToReturn;
	}

	public static StringArray addToStringArray(StringArray stringArray, String stringToAdd, JCas jcas) {
		if (stringArray == null) {
			stringArray = new StringArray(jcas, 0);
		}
		StringArray stringArrayToReturn = new StringArray(jcas, stringArray.size() + 1);
		for (int i = 0; i < stringArray.size(); i++) {
			stringArrayToReturn.set(i, stringArray.get(i));
		}
		stringArrayToReturn.set(stringArray.size(), stringToAdd);
		return stringArrayToReturn;
	}

	public static IntegerArray addToIntegerArray(IntegerArray integerArray, Integer integerToAdd, JCas jcas) {
		if (integerArray == null) {
			integerArray = new IntegerArray(jcas, 0);
		}
		IntegerArray integerArrayToReturn = new IntegerArray(jcas, integerArray.size() + 1);
		for (int i = 0; i < integerArray.size(); i++) {
			integerArrayToReturn.set(i, integerArray.get(i));
		}
		integerArrayToReturn.set(integerArray.size(), integerToAdd);
		return integerArrayToReturn;
	}

	public static LongArray addToLongArray(LongArray longArray, Long longToAdd, JCas jcas) {
		if (longArray == null) {
			longArray = new LongArray(jcas, 0);
		}
		LongArray longArrayToReturn = new LongArray(jcas, longArray.size() + 1);
		for (int i = 0; i < longArray.size(); i++) {
			longArrayToReturn.set(i, longArray.get(i));
		}
		longArrayToReturn.set(longArray.size(), longToAdd);
		return longArrayToReturn;
	}

	public static FloatArray addToFloatArray(FloatArray floatArray, Float floatToAdd, JCas jcas) {
		if (floatArray == null) {
			floatArray = new FloatArray(jcas, 0);
		}
		FloatArray floatArrayToReturn = new FloatArray(jcas, floatArray.size() + 1);
		for (int i = 0; i < floatArray.size(); i++) {
			floatArrayToReturn.set(i, floatArray.get(i));
		}
		floatArrayToReturn.set(floatArray.size(), floatToAdd);
		return floatArrayToReturn;
	}

	public static DoubleArray addToDoubleArray(DoubleArray doubleArray, Double doubleToAdd, JCas jcas) {
		if (doubleArray == null) {
			doubleArray = new DoubleArray(jcas, 0);
		}
		DoubleArray doubleArrayToReturn = new DoubleArray(jcas, doubleArray.size() + 1);
		for (int i = 0; i < doubleArray.size(); i++) {
			doubleArrayToReturn.set(i, doubleArray.get(i));
		}
		doubleArrayToReturn.set(doubleArray.size(), doubleToAdd);
		return doubleArrayToReturn;
	}

	public static int indexOf(IntegerArray intArray, Integer intValue) {
		if (intArray == null) {
			return -1;
		}
		for (int i = 0; i < intArray.size(); i++) {
			if (intArray.get(i) == intValue) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(StringArray strArray, String strValue) {
		if (strArray == null) {
			return -1;
		}
		for (int i = 0; i < strArray.size(); i++) {
			if (strArray.get(i).equals(strValue)) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(LongArray longArray, Long longValue) {
		if (longArray == null) {
			return -1;
		}
		for (int i = 0; i < longArray.size(); i++) {
			if (longArray.get(i) == longValue) {
				return i;
			}
		}
		return -1;
	}

	public static IntegerArray removeArrayIndex(IntegerArray intArray, int index, JCas jcas) {
		// logger.debug("RemoveArrayIndex: intArray size: " + intArray.size() + " index: " + index);
		IntegerArray updatedArray = null;
		if (intArray != null) {
			if (index < intArray.size()) {
				int newSize = intArray.size() - 1;
				updatedArray = new IntegerArray(jcas, newSize);
				for (int i = 0; i < updatedArray.size(); i++) {
					if (i >= index & intArray.size() > 1) {
						updatedArray.set(i, intArray.get(i + 1));
					} else {
						updatedArray.set(i, intArray.get(i));
					}
				}
			} else {
				updatedArray = intArray;
			}
		}
		return updatedArray;
	}

	public static StringArray removeArrayIndex(StringArray strArray, int index, JCas jcas) {
		// logger.debug("RemoveArrayIndex: strArray size: " + strArray.size() + " index: " + index);
		StringArray updatedArray = null;
		if (strArray != null) {
			if (index < strArray.size()) {
				int newSize = strArray.size() - 1;
				updatedArray = new StringArray(jcas, newSize);
				for (int i = 0; i < updatedArray.size(); i++) {
					if (i >= index & strArray.size() > 1) {
						updatedArray.set(i, strArray.get(i + 1));
					} else {
						updatedArray.set(i, strArray.get(i));
					}
				}
			} else {
				updatedArray = strArray;
			}
		}
		return updatedArray;
	}

	public static LongArray removeArrayIndex(LongArray longArray, int index, JCas jcas) {
		LongArray updatedArray = null;
		if (longArray != null) {
			if (index < longArray.size()) {
				int newSize = longArray.size() - 1;
				updatedArray = new LongArray(jcas, newSize);
				for (int i = 0; i < updatedArray.size(); i++) {
					if (i >= index & longArray.size() > 1) {
						updatedArray.set(i, longArray.get(i + 1));
					} else {
						updatedArray.set(i, longArray.get(i));
					}
				}
			} else {
				updatedArray = longArray;
			}
		}
		return updatedArray;
	}

	public static List<CCPSlotMention> getMultipleSlotMentionsByName(CCPTextAnnotation ccpTextAnnotation,
			String slotMentionName) {
		return getMultipleSlotMentionsByName(ccpTextAnnotation.getClassMention(), slotMentionName);
	}

	public static List<CCPSlotMention> getMultipleSlotMentionsByName(CCPClassMention ccpClassMention,
			String slotMentionName) {
		List<CCPSlotMention> returnSlotMentions = new ArrayList<CCPSlotMention>();
		FSArray slotMentionsArray = ccpClassMention.getSlotMentions();
		if (slotMentionsArray != null) {
			FeatureStructure[] slotMentions = slotMentionsArray.toArray();
			for (FeatureStructure fs : slotMentions) {
				CCPSlotMention ccpSlotMention = (CCPSlotMention) fs;
				if (ccpSlotMention.getMentionName().equals(slotMentionName)) {
					returnSlotMentions.add(ccpSlotMention);
				}
			}
			return returnSlotMentions;
		} else {
			return null;
		}
	}

	public static String getFirstSlotValue(CCPStringSlotMention ccpSSM) {
		StringArray slotArray = ccpSSM.getSlotValues();
		if (slotArray != null && slotArray.size() > 0) {
			return slotArray.get(0);
		}
		return null;
	}
	public static Double getFirstSlotValue(CCPDoubleSlotMention ccpSSM) {
		DoubleArray slotArray = ccpSSM.getSlotValues();
		if (slotArray != null && slotArray.size() > 0) {
			return slotArray.get(0);
		}
		return null;
	}
	public static boolean getFirstSlotValue(CCPBooleanSlotMention ccpSSM) {
		return ccpSSM.getSlotValue();
	}

	public static Integer getFirstSlotValue(CCPIntegerSlotMention ccpISM) {
		IntegerArray slotArray = ccpISM.getSlotValues();
		if (slotArray != null && slotArray.size() > 0) {
			return slotArray.get(0);
		}
		return null;
	}

	public static Float getFirstSlotValue(CCPFloatSlotMention ccpFSM) {
		FloatArray slotArray = ccpFSM.getSlotValues();
		if (slotArray != null && slotArray.size() > 0) {
			return slotArray.get(0);
		}
		return null;
	}

	public static void addSlotValue(CCPClassMention ccpClassMention, String slotMentionName, String slotValue)
			throws CASException {
		JCas jcas = ccpClassMention.getCAS().getJCas();
		CCPSlotMention slotMention = UIMA_Util.getSlotMentionByName(ccpClassMention, slotMentionName);
		if (slotMention != null) {
			if (slotMention instanceof CCPStringSlotMention) {
				CCPStringSlotMention ccpSSM = (CCPStringSlotMention) slotMention;
				StringArray slotValues = ccpSSM.getSlotValues();
				slotValues = addToStringArray(slotValues, slotValue, jcas);
				ccpSSM.setSlotValues(slotValues);
			} else {
				throw new CASException(new KnowledgeRepresentationWrapperException("Cannot store a String in a "
						+ slotMention.getClass().getName()));
			}
		} else {
			CCPStringSlotMention ccpSSM = new CCPStringSlotMention(jcas);
			ccpSSM.setMentionName(slotMentionName);
			addSlotMention(ccpClassMention, ccpSSM);
			StringArray slotValues = new StringArray(jcas, 1);
			slotValues.set(0, slotValue);
			ccpSSM.setSlotValues(slotValues);
		}

	}

	public static void addSlotValue(CCPClassMention ccpClassMention, String slotMentionName,
			CCPClassMention slotFillerCM) throws CASException {
		CCPComplexSlotMention ccpComplexSlotMention = (CCPComplexSlotMention) UIMA_Util.getSlotMentionByName(
				ccpClassMention, slotMentionName);
		if (ccpComplexSlotMention == null) { // then we need to create a new
			// CCPNonComplexSlotMention
			JCas jcas = ccpClassMention.getCAS().getJCas();
			ccpComplexSlotMention = new CCPComplexSlotMention(jcas);
			ccpComplexSlotMention.setMentionName(slotMentionName);
			FSArray classMentions = new FSArray(jcas, 1);
			classMentions.set(0, slotFillerCM);
			ccpComplexSlotMention.setClassMentions(classMentions);
			addSlotMention(ccpClassMention, ccpComplexSlotMention);
		} else {
			addClassMentionAsCSMSlotFiller(ccpComplexSlotMention, slotFillerCM);
		}
	}

	/**
	 * Sets a non-complex slot value (overwrites any previous slot values). Adds a new slot if one
	 * was not there prior
	 * 
	 * @param ccpClassMention
	 * @param slotMentionName
	 * @param slotValue
	 * @throws CASException
	 */
	public static void setSlotValue(CCPClassMention ccpClassMention, String slotMentionName, Object slotValue)
			throws CASException {
		JCas jcas = ccpClassMention.getCAS().getJCas();
		CCPSlotMention ccpSlotMention = UIMA_Util.getSlotMentionByName(ccpClassMention, slotMentionName);
		try {
			if (ccpSlotMention == null) {
				CCPPrimitiveSlotMention newPrimitiveSlotMention = CCPPrimitiveSlotMentionFactory
						.createCCPPrimitiveSlotMention(slotMentionName, slotValue, jcas);
				addSlotMention(ccpClassMention, newPrimitiveSlotMention);
			} else {
				if (ccpSlotMention instanceof CCPStringSlotMention) {
					if (slotValue instanceof String) {
						CCPStringSlotMention ccpSSM = (CCPStringSlotMention) ccpSlotMention;
						new WrappedCCPStringSlotMention(ccpSSM).overwriteSlotValues((String) slotValue);
					} else {
						throw new CASException(new KnowledgeRepresentationWrapperException("Cannot use a "
								+ slotValue.getClass().getName() + " as a slot value in a "
								+ ccpSlotMention.getClass().getName()));
					}
				} else if (ccpSlotMention instanceof CCPFloatSlotMention) {
					if (slotValue instanceof Float) {
						CCPFloatSlotMention ccpSSM = (CCPFloatSlotMention) ccpSlotMention;
						new WrappedCCPFloatSlotMention(ccpSSM).overwriteSlotValues((Float) slotValue);
					} else {
						throw new CASException(new KnowledgeRepresentationWrapperException("Cannot use a "
								+ slotValue.getClass().getName() + " as a slot value in a "
								+ ccpSlotMention.getClass().getName()));
					}
				} else if (ccpSlotMention instanceof CCPIntegerSlotMention) {
					if (slotValue instanceof Integer) {
						CCPIntegerSlotMention ccpSSM = (CCPIntegerSlotMention) ccpSlotMention;
						new WrappedCCPIntegerSlotMention(ccpSSM).overwriteSlotValues((Integer) slotValue);
					} else {
						throw new CASException(new KnowledgeRepresentationWrapperException("Cannot use a "
								+ slotValue.getClass().getName() + " as a slot value in a "
								+ ccpSlotMention.getClass().getName()));
					}
				} else if (ccpSlotMention instanceof CCPBooleanSlotMention) {
					if (slotValue instanceof Boolean) {
						CCPBooleanSlotMention ccpSSM = (CCPBooleanSlotMention) ccpSlotMention;
						ccpSSM.setSlotValue((Boolean) slotValue);
					} else {
						throw new CASException(new KnowledgeRepresentationWrapperException("Cannot use a "
								+ slotValue.getClass().getName() + " as a slot value in a "
								+ ccpSlotMention.getClass().getName()));
					}
				} else {
					throw new CASException(new KnowledgeRepresentationWrapperException(
							"Unknown Primitive Slot Mention type: " + ccpSlotMention.getClass().getName()));
				}
			}
		} catch (KnowledgeRepresentationWrapperException krwe) {
			throw new CASException(krwe);
		} catch (InvalidInputException e) {
			throw new CASException(e);
		}
	}

	private static void addClassMentionAsCSMSlotFiller(CCPComplexSlotMention ccpCSM, CCPClassMention ccpCM)
			throws CASException {
		FSArray slotFillerCMs = ccpCSM.getClassMentions();
		if (slotFillerCMs == null) {
			slotFillerCMs = new FSArray(ccpCSM.getCAS().getJCas(), 1);
			slotFillerCMs.set(0, ccpCM);
			ccpCSM.setClassMentions(slotFillerCMs);
		} else {
			FeatureStructure[] featureStructures = slotFillerCMs.toArray();
			FSArray fsArray = new FSArray(ccpCSM.getCAS().getJCas(), featureStructures.length + 1);
			for (int i = 0; i < featureStructures.length; i++) {
				fsArray.set(i, featureStructures[i]);
			}
			fsArray.set(fsArray.size() - 1, ccpCM);
			ccpCSM.setClassMentions(fsArray);
		}
	}

	private static void addSlotMention(CCPClassMention ccpClassMention, CCPSlotMention ccpSlotMention)
			throws CASException {
		FSArray slotMentions = ccpClassMention.getSlotMentions();
		if (slotMentions == null) {
			slotMentions = new FSArray(ccpClassMention.getCAS().getJCas(), 1);
			slotMentions.set(0, ccpSlotMention);
		} else {
			FeatureStructure[] featureStructures = slotMentions.toArray();
			int index = 0;
			slotMentions = new FSArray(ccpClassMention.getCAS().getJCas(), featureStructures.length + 1);
			for (FeatureStructure fs : featureStructures) {
				slotMentions.set(index++, fs);
			}
			slotMentions.set(index, ccpSlotMention);
		}
		ccpClassMention.setSlotMentions(slotMentions);
	}

	/**
	 * Create a string that includes all of the spans within this mention. This includes recursively
	 * pulling out slot spans as well.
	 * 
	 * @param mention
	 *            The mention to process
	 * @return A string summarizing all the text spans included in this mention
	 */
	private static String getCCPClassMentionString(CCPClassMention mention) {
		// Create text spans for the base annotation and all the slots
		ArrayList<TextSpan> textSpans = new ArrayList<TextSpan>();
		addClassMentionSpans(textSpans, mention);
		// Assemble the sorted spans into a single string
		StringBuffer sb = new StringBuffer();
		TextSpan previous = null;
		for (TextSpan span : textSpans) {
			if (previous == null) {
				sb.append(span.text);
			} else if ((span.start - previous.end) <= 1) {
				sb.append(" ");
				sb.append(span.text);
			} else {
				sb.append(" ... ");
				sb.append(span.text);
			}
			previous = span;
		}
		return sb.toString();
	}

	/**
	 * Add all of the text spans that take part in a mention into a sorted list of text spans.
	 * 
	 * @param textSpans
	 *            The sorted list of text spans
	 * @param mention
	 *            The mention from which spans are to be extracted
	 * @return The sorted list of text spans
	 */
	private static ArrayList<TextSpan> addClassMentionSpans(ArrayList<TextSpan> textSpans, CCPClassMention mention) {
		// Grab the original document text from the JCas
		String data = null;
		try {
			data = mention.getCAS().getJCas().getDocumentText();
		} catch (CASException e) {
			return textSpans;
		}
		// Get the base annotation for this mention (it should point to a
		// CCPTextAnnotation)
		CCPTextAnnotation annotation = mention.getCcpTextAnnotation();
		// FSArray annotations = mention.getCcpTextAnnotations();
		if ((annotation == null)) {
			return textSpans;
		}

		// Get all the spans from the base annotation
		FSArray spans = annotation.getSpans();
		if (spans == null) {
			sortIn(textSpans,
					new TextSpan(annotation.getBegin(), annotation.getEnd(), data.substring(annotation.getBegin(),
							annotation.getEnd())));
		} else {
			for (int i = 0; i < spans.size(); i++) {
				Object jcasSpan = spans.get(i);
				CCPSpan span = null;
				if (jcasSpan instanceof CCPSpan) {
					span = (CCPSpan) jcasSpan;
				}
				if (span != null) {
					sortIn(textSpans,
							new TextSpan(span.getSpanStart(), span.getSpanEnd(), data.substring(span.getSpanStart(),
									span.getSpanEnd())));
				}
			}
		}
		// Now sort in all the spans from the mention slots
		FSArray slots = mention.getSlotMentions();
		if (slots != null) {
			for (int i = 0; i < slots.size(); i++) {
				Object jcasSlotMention = slots.get(i);
				CCPComplexSlotMention slot = null;
				if (jcasSlotMention instanceof CCPComplexSlotMention)
					slot = (CCPComplexSlotMention) jcasSlotMention;
				if (slot != null) {
					FSArray slotClassMentions = slot.getClassMentions();
					for (int j = 0; j < slotClassMentions.size(); j++) {
						Object slotClassMention = slotClassMentions.get(j);
						CCPClassMention classMention = null;
						if (slotClassMention instanceof CCPClassMention)
							classMention = (CCPClassMention) slotClassMention;
						if (classMention != null) {
							addClassMentionSpans(textSpans, classMention);
						}
					}
				}
			}
		}
		// Done
		return textSpans;
	}

	/**
	 * Sort a new text span into a list of text spans.
	 * 
	 * @param spans
	 *            The sorted list of spans
	 * @param span
	 *            The new span to add
	 * @return The sorted list of spans
	 */
	private static ArrayList<TextSpan> sortIn(ArrayList<TextSpan> spans, TextSpan span) {
		if (spans.isEmpty()) {
			spans.add(span);
		} else {
			boolean found = false;
			for (int i = 0; i < spans.size(); i++) {
				if (span.end <= spans.get(i).start) {
					spans.add(i, span);
					found = true;
					break;
				}
			}
			if (!found)
				spans.add(span);
		}
		return spans;
	}

	public static Iterator<CCPTextAnnotation> getAnnotationsWithinSpan(Span span, JCas jcas) {
		return LegacyCollectionsUtil.checkIterator(getAnnotationsWithinSpan(span, jcas, CCPTextAnnotation.type),
				CCPTextAnnotation.class);
	}

	/**
	 * return all annotations within the exact same span as the input span
	 * 
	 * @param span
	 * @param jcas
	 * @return
	 */
	public static Iterator<Annotation> getAnnotationsWithinSpan(Span span, JCas jcas, int annotationType) {

		// System.out.println("Looking for annotations within " + span.getSpanStart() + " -- " +
		// span.getSpanEnd());
		/* Get a reference to the CAS and the CAS ConstraintFactory */
		CAS cas = jcas.getCas();
		ConstraintFactory cf = cas.getConstraintFactory();

		/* Constraints are built from tests and feature-paths */
		/* First build the tests */
		FSIntConstraint gtEqToSpanStart = cf.createIntConstraint();
		gtEqToSpanStart.geq(span.getSpanStart());

		FSIntConstraint ltEqToSpanEnd = cf.createIntConstraint();
		ltEqToSpanEnd.leq(span.getSpanEnd());

		/* Get handles to the features, use the type system */
		TypeSystem ts = cas.getTypeSystem();

		Feature beginFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_BEGIN);
		Feature endFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_END);

		/* Create a feature path for each feature */
		FeaturePath pathToBeginValue = cas.createFeaturePath();
		pathToBeginValue.addFeature(beginFeature);
		FeaturePath pathToEndValue = cas.createFeaturePath();
		pathToEndValue.addFeature(endFeature);

		/*
		 * Connect the tests to the feature paths (s = the span of the trigger annotation, c = the
		 * span of the phrases and tokens to compare)
		 */

		/**
		 * is it within the span
		 * 
		 * <pre>
		 *                           ccccc
		 *                          ssssssss
		 * </pre>
		 */
		FSMatchConstraint testBegin = cf.embedConstraint(pathToBeginValue, gtEqToSpanStart);
		FSMatchConstraint testEnd = cf.embedConstraint(pathToEndValue, ltEqToSpanEnd);

		/* AND the tests for each of the three cases, then OR the AND'ed tests together */
		FSMatchConstraint testBoth = cf.and(testBegin, testEnd);

		/* Create a filtered iterator that uses this constraint */
		Iterator<Annotation> iter = (Iterator<Annotation>) cas.createFilteredIterator(jcas.getJFSIndexRepository()
				.getAnnotationIndex(annotationType).iterator(), testBoth);

		return iter;

	}

	public static Iterator<Annotation> getAnnotationsWithinSpan(Span span, JCas jcas, Type annotationType) {

		// System.out.println("Looking for annotations within " + span.getSpanStart() + " -- " +
		// span.getSpanEnd());

		/* Get a reference to the CAS and the CAS ConstraintFactory */
		CAS cas = jcas.getCas();
		ConstraintFactory cf = cas.getConstraintFactory();

		/* Constraints are built from tests and feature-paths */
		/* First build the tests */
		FSIntConstraint gtEqToSpanStart = cf.createIntConstraint();
		gtEqToSpanStart.geq(span.getSpanStart());

		FSIntConstraint ltEqToSpanEnd = cf.createIntConstraint();
		ltEqToSpanEnd.leq(span.getSpanEnd());

		/* Get handles to the features, use the type system */
		TypeSystem ts = cas.getTypeSystem();

		Feature beginFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_BEGIN);
		Feature endFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_END);

		/* Create a feature path for each feature */
		FeaturePath pathToBeginValue = cas.createFeaturePath();
		pathToBeginValue.addFeature(beginFeature);
		FeaturePath pathToEndValue = cas.createFeaturePath();
		pathToEndValue.addFeature(endFeature);

		/*
		 * Connect the tests to the feature paths (s = the span of the trigger annotation, c = the
		 * span of the phrases and tokens to compare)
		 */

		/**
		 * is it within the span
		 * 
		 * <pre>
		 *                           ccccc
		 *                          ssssssss
		 * </pre>
		 */
		FSMatchConstraint testBegin = cf.embedConstraint(pathToBeginValue, gtEqToSpanStart);
		FSMatchConstraint testEnd = cf.embedConstraint(pathToEndValue, ltEqToSpanEnd);

		/* AND the tests for each of the three cases, then OR the AND'ed tests together */
		FSMatchConstraint testBoth = cf.and(testBegin, testEnd);

		/* Create a filtered iterator that uses this constraint */
		Iterator<Annotation> iter = (Iterator<Annotation>) cas.createFilteredIterator(
				jcas.getAnnotationIndex(annotationType).iterator(), testBoth);

		return iter;

	}

	public static Iterator<Annotation> getOverlappingAnnotations(CCPTextAnnotation ccpTA, JCas jcas, int annotType) {
		Span span = null;
		try {
			span = new Span(ccpTA.getBegin(), ccpTA.getEnd());
		} catch (InvalidSpanException e) {
			e.printStackTrace();
		}

		return getAnnotationsEncompassingSpan(span, jcas, annotType);

	}

	public static Iterator<CCPTextAnnotation> getAnnotationsEncompassingSpan(Span span, JCas jcas) {
		return LegacyCollectionsUtil.checkIterator(getAnnotationsEncompassingSpan(span, jcas, CCPTextAnnotation.type),
				CCPTextAnnotation.class);
	}

	/**
	 * return all annotations that contain the input span
	 * 
	 * @param span
	 * @param jcas
	 * @return
	 */
	public static Iterator<Annotation> getAnnotationsEncompassingSpan(Span span, JCas jcas, int annotType) {

		// System.out.println("Looking for annotations overlapping " + span.getSpanStart() + " -- "
		// +
		// span.getSpanEnd());

		/* Get a reference to the CAS and the CAS ConstraintFactory */
		CAS cas = jcas.getCas();
		ConstraintFactory cf = cas.getConstraintFactory();

		/* Constraints are built from tests and feature-paths */
		/* First build the tests */
		FSIntConstraint ltEqToSpanStart = cf.createIntConstraint();
		ltEqToSpanStart.leq(span.getSpanStart());
		FSIntConstraint gtSpanStart = cf.createIntConstraint();
		gtSpanStart.gt(span.getSpanStart());
		FSIntConstraint gtEqToSpanStart = cf.createIntConstraint();
		gtEqToSpanStart.geq(span.getSpanStart());

		FSIntConstraint gtEqToSpanEnd = cf.createIntConstraint();
		gtEqToSpanEnd.geq(span.getSpanEnd());
		FSIntConstraint ltSpanEnd = cf.createIntConstraint();
		ltSpanEnd.lt(span.getSpanEnd());
		FSIntConstraint ltEqToSpanEnd = cf.createIntConstraint();
		ltEqToSpanEnd.leq(span.getSpanEnd());

		/* Get handles to the features, use the type system */
		TypeSystem ts = cas.getTypeSystem();

		Feature beginFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_BEGIN);
		Feature endFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_END);

		/* Create a feature path for each feature */
		FeaturePath pathToBeginValue = cas.createFeaturePath();
		pathToBeginValue.addFeature(beginFeature);
		FeaturePath pathToEndValue = cas.createFeaturePath();
		pathToEndValue.addFeature(endFeature);

		/*
		 * Connect the tests to the feature paths (s = the span of the trigger annotation, c = the
		 * span of the phrases and tokens to compare)
		 */

		/**
		 * does it overlap the left
		 * 
		 * <pre>
		 *                      cccccccc
		 *                          ssssssss
		 * </pre>
		 */
		FSMatchConstraint testBegin1 = cf.embedConstraint(pathToBeginValue, ltEqToSpanStart);
		FSMatchConstraint testEnd1 = cf.embedConstraint(pathToEndValue, gtSpanStart);
		/**
		 * does it overlap the right
		 * 
		 * <pre>
		 *                        ccccccccc 
		 *                     sssssss
		 * </pre>
		 */
		FSMatchConstraint testBegin2 = cf.embedConstraint(pathToBeginValue, ltSpanEnd);
		FSMatchConstraint testEnd2 = cf.embedConstraint(pathToEndValue, gtEqToSpanEnd);
		/**
		 * is it completely in the middle
		 * 
		 * <pre>
		 *                      cccc 
		 *                    sssssssssss
		 * </pre>
		 */
		FSMatchConstraint testBegin3 = cf.embedConstraint(pathToBeginValue, gtEqToSpanStart);
		FSMatchConstraint testEnd3 = cf.embedConstraint(pathToEndValue, ltEqToSpanEnd);

		/* AND the tests for each of the three cases, then OR the AND'ed tests together */
		FSMatchConstraint testBoth1 = cf.and(testBegin1, testEnd1);
		FSMatchConstraint testBoth2 = cf.and(testBegin2, testEnd2);
		FSMatchConstraint testBoth3 = cf.and(testBegin3, testEnd3);

		FSMatchConstraint testBoth12 = cf.or(testBoth1, testBoth2);
		FSMatchConstraint testBoth123 = cf.or(testBoth12, testBoth3);

		/* Create a filtered iterator that uses this constraint */
		Iterator<Annotation> iter = (Iterator<Annotation>) cas.createFilteredIterator(jcas.getJFSIndexRepository()
				.getAnnotationIndex(annotType).iterator(), testBoth123);

		return iter;

	}

	/**
	 * return annotations in between spans
	 * 
	 * @param span
	 * @param jcas
	 * @return
	 */
	public static Iterator<CCPTextAnnotation> getAnnotationsInBetweenSpans(Span upstreamSpan, Span downstreamSpan,
			JCas jcas) {

		// System.out.println("Looking for annotations in between " + upstreamSpan.getSpanEnd() +
		// " -- " +
		// downstreamSpan.getSpanStart());

		Span betweenSpan = null;
		try {
			betweenSpan = new Span(upstreamSpan.getSpanEnd(), downstreamSpan.getSpanStart());
		} catch (InvalidSpanException e) {
			logger.error("Invalid span detected.. could be because of split span. These are not handled. ["
					+ upstreamSpan.getSpanEnd() + ".." + downstreamSpan.getSpanStart() + "]");
			return null;
			// e.printStackTrace();
		}

		if (betweenSpan != null) {
			return getAnnotationsWithinSpan(betweenSpan, jcas);
		} else {
			return null;
		}

	}

	/**
	 * return all annotations that start with the input startIndex
	 * 
	 * @param startIndex
	 * @param jcas
	 * @return
	 */
	public static Iterator<Annotation> getAnnotationsWithSameStart(int startIndex, JCas jcas) {

		// System.out.println("Looking for annotations starting at " + startIndex);

		/* Get a reference to the CAS and the CAS ConstraintFactory */
		CAS cas = jcas.getCas();
		ConstraintFactory cf = cas.getConstraintFactory();

		/* Constraints are built from tests and feature-paths */
		/* First build the tests */
		FSIntConstraint eqToSpanStart = cf.createIntConstraint();
		eqToSpanStart.eq(startIndex);

		/* Get handles to the features, use the type system */
		TypeSystem ts = cas.getTypeSystem();

		Feature beginFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_BEGIN);

		/* Create a feature path for each feature */
		FeaturePath pathToBeginValue = cas.createFeaturePath();
		pathToBeginValue.addFeature(beginFeature);

		/*
		 * Connect the tests to the feature paths (s = the span of the trigger annotation, c = the
		 * span of the phrases and tokens to compare)
		 */

		/**
		 * does it start at the same index
		 * 
		 * <pre>
		 *                      cccccccc
		 *                      ssssssss
		 * </pre>
		 */
		FSMatchConstraint testStart = cf.embedConstraint(pathToBeginValue, eqToSpanStart);

		/* Create a filtered iterator that uses this constraint */
		Iterator<Annotation> iter = (Iterator<Annotation>) cas.createFilteredIterator(jcas.getJFSIndexRepository()
				.getAnnotationIndex(CCPTextAnnotation.type).iterator(), testStart);

		return iter;

	}

	public static Iterator<Annotation> getPrecedingAnnotations(int startIndex, int ccpAnnotationType, JCas jcas) {

		// System.out.println("Looking for annotations ending before  " + startIndex);

		/* Get a reference to the CAS and the CAS ConstraintFactory */
		CAS cas = jcas.getCas();
		ConstraintFactory cf = cas.getConstraintFactory();

		/* Constraints are built from tests and feature-paths */
		/* First build the tests */
		FSIntConstraint ltSpanStart = cf.createIntConstraint();
		ltSpanStart.lt(startIndex);

		/* Get handles to the features, use the type system */
		TypeSystem ts = cas.getTypeSystem();

		Feature endFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_END);

		/* Create a feature path for each feature */
		FeaturePath pathToEndValue = cas.createFeaturePath();
		pathToEndValue.addFeature(endFeature);

		/*
		 * Connect the tests to the feature paths (s = the span of the trigger annotation, c = the
		 * span of the phrases and tokens to compare)
		 */

		/**
		 * does it start at the same index
		 * 
		 * <pre>
		 *                      cccccccc
		 *                      ssssssss
		 * </pre>
		 */
		FSMatchConstraint testStart = cf.embedConstraint(pathToEndValue, ltSpanStart);

		/* Create a filtered iterator that uses this constraint */
		Iterator<Annotation> iter = (Iterator<Annotation>) cas.createFilteredIterator(jcas.getJFSIndexRepository()
				.getAnnotationIndex(ccpAnnotationType).iterator(), testStart);

		return iter;

	}

	/**
	 * this method resets the span for an annotation.. this includes the Span FSArray
	 * 
	 * @param ccpTA
	 * @param spanStart
	 * @param spanEnd
	 */
	public static void setCCPTextAnnotationSpan(CCPTextAnnotation ccpTA, int spanStart, int spanEnd)
			throws CASException {
		JCas jcas = ccpTA.getCAS().getJCas();

		/* Initialize a new CCPSpan */
		CCPSpan ccpSpan = new CCPSpan(jcas);
		ccpSpan.setSpanStart(spanStart);
		ccpSpan.setSpanEnd(spanEnd);

		/* Add the span to the span array */
		FSArray ccpSpans = new FSArray(jcas, 1);
		ccpSpans.set(0, ccpSpan);

		/* Update the text annotation with the new span information */
		ccpTA.setSpans(ccpSpans);
		ccpTA.setBegin(spanStart);
		ccpTA.setEnd(spanEnd);

		// /* update class mention to point to this text annotation */
		// CCPClassMention ccpCM = ccpTA.getClassMention();
		// if (ccpCM != null) {
		// FSArray ccpTAs = new FSArray(jcas, 1);
		// ccpTAs.set(0, ccpTA);
		// ccpCM.setCcpTextAnnotations(ccpTAs);
		// }

	}

	/**
	 * sets the class mention for the CCPTextAnnotation, and also links the CCPTextAnnotation with
	 * the CCPClassMention
	 * 
	 * @param ccpTA
	 * @param ccpCM
	 * @throws CASException
	 */
	public static void setCCPClassMentionForCCPTextAnnotation(CCPTextAnnotation ccpTA, CCPClassMention ccpCM)
			throws CASException {
		ccpTA.setClassMention(ccpCM);
		ccpCM.setCcpTextAnnotation(ccpTA);
		// addCCPTextAnnotationToCCPClassMention(ccpTA, ccpCM);
	}

	/**
	 * This method provides a sanity check when constructing CCPTextAnnotations, and in particular
	 * the class mention structure from scratch. Slots are checked for appropriate filler, and
	 * logger.error messages are raised if an invalid slot filler is discovered. True is returned if
	 * the CCPTextAnnotation has a valid mention structure, false otherwise.
	 * 
	 * @param ccpTA
	 * @return
	 */
	public static boolean validateCCPTextAnnotation(CCPTextAnnotation ccpTA) {
		boolean isValid = validateCCPClassMention(ccpTA.getClassMention());
		// UIMA_Util uimaUtil = new UIMA_Util();
		if (!isValid) {
			logger.error("Invalid class mention structure detected in CCPTextAnnotation: "
					+ (new WrappedCCPTextAnnotation(ccpTA)).getSingleLineRepresentation());
		}
		logger.debug("Returning " + isValid + " from validateCCPTextAnnotation() end");
		return isValid;
	}

	/**
	 * This method checks each slot mention associated with the input class mention for valid slot
	 * fillers. True is returned if the CCPClassMention has a valid mention structure, false
	 * otherwise.
	 * 
	 * @param ccpTA
	 * @return
	 */
	public static boolean validateCCPClassMention(CCPClassMention ccpCM) {
		boolean isValid = true;

		FSArray shouldBeSlotMentions = ccpCM.getSlotMentions();
		if (shouldBeSlotMentions != null) {
			for (int i = 0; i < shouldBeSlotMentions.size(); i++) {
				Object shouldBeSM = shouldBeSlotMentions.get(i);
				if (shouldBeSM != null) {
					if (shouldBeSM instanceof CCPSlotMention) {
						return validateCCPSlotMention((CCPSlotMention) shouldBeSM);
					} else {
						logger.error("Invalid mention structure detected. Unexpected object found in FSArray holding CCPSlotMentions for the CCPClassMention: \""
								+ ccpCM.getMentionName() + "\" -- " + shouldBeSM.getClass().getName());
						logger.debug("Returning false from validateCCPClassMention() mid1");
						return false;
					}
				}
			}
		}

		logger.debug("Returning " + isValid + " from validateCCPClassMention() end");
		return isValid;

	}

	/**
	 * This method determines the type of slot mention inputted, and calls either
	 * validateCCPComplexSlotMention() or validateCCPNonComplexSlotMention()
	 * 
	 * @param ccpSM
	 * @return
	 */
	public static boolean validateCCPSlotMention(CCPSlotMention ccpSM) {
		if (ccpSM instanceof CCPComplexSlotMention) {
			return validateCCPComplexSlotMention((CCPComplexSlotMention) ccpSM);
		} else if (ccpSM instanceof CCPPrimitiveSlotMention) {
			return validateCCPPrimitiveSlotMention((CCPPrimitiveSlotMention) ccpSM);
		} else {
			logger.error("The superclass CCPSlotMention was found to occupy a slot. Only subclasses of CCPSlotMention are allowed.");
			logger.debug("Returning false from validateCCPSlotMention() end");
			return false;
		}
	}

	/**
	 * This method checks for valid slot fillers of the input CCPComplexSlotMention, i.e. slot
	 * fillers must be valid CCPClassMentions. True is returned if the CCPClassMention has a valid
	 * mention structure, false otherwise.
	 * 
	 * @param ccpTA
	 * @return
	 */
	public static boolean validateCCPComplexSlotMention(CCPComplexSlotMention ccpCSM) {
		boolean isValid = true;

		FSArray slotValues = ccpCSM.getClassMentions();
		if (slotValues != null) {
			for (int i = 0; i < slotValues.size(); i++) {
				Object shouldBeAClassMention = slotValues.get(i);
				if (shouldBeAClassMention != null) {
					if (shouldBeAClassMention instanceof CCPClassMention) {
						isValid = isValid && validateCCPClassMention((CCPClassMention) shouldBeAClassMention);
					} else {
						logger.error("Invalid mention structure discovered. Instead of a CCPClassMention, this slot filler for this CCPComplexSlotMention is a: "
								+ shouldBeAClassMention.getClass().getName());
						logger.debug("Returning false from validateCCPComplexSlotMention()");
						return false;
					}
				}
			}
		}
		logger.debug("Returning " + isValid + " from validateCCPComplexSlotMention() end");
		return isValid;
	}

	/**
	 * This method checks for valid slot fillers of the input CCPNonComplexSlotMention, i.e. slot
	 * fillers must be Strings. This method is not necessary in truth because the StringArray object
	 * of each CCPNonComplexSlotMention will force the slot values to be strings, but is included
	 * for completeness.
	 * 
	 * @param ccpTA
	 * @return
	 */
	public static boolean validateCCPPrimitiveSlotMention(CCPPrimitiveSlotMention ccpNCSM) {
		/*
		 * the CCPPrimitiveSlotMention is forced to hold a certain primitive type, so its mention
		 * structure will always be valid. This method is included only for completeness.
		 */
		return true;
	}

	public static void showCasDebugInfo(JCas jcas, String s) throws CASException {
		System.out.println(">>>====== " + s + "   " + jcas.getViewName());
		Iterator i = jcas.getViewIterator();
		while (i.hasNext()) {
			JCas aCas = (JCas) i.next();
			System.out.println(">>-----" + aCas.getViewName() + " : " + aCas.size());
			AnnotationIndex ai = aCas.getAnnotationIndex();
			Iterator annotationIterator = ai.iterator();
			while (annotationIterator.hasNext()) {
				System.out.println("    " + annotationIterator.next());
			}
			System.out.println("------<<");
		}
		System.out.println("======<<<");
	}

}

/**
 * A local class to hold a span of text along with its document start and end position.
 * 
 * @author Jim Firby
 */
class TextSpan {

	int start = 0;

	int end = 0;

	String text = null;

	TextSpan(int start, int end, String text) {
		this.start = start;
		this.end = end;
		this.text = text;
	}

}
