
package edu.ucdenver.ccp.nlp.uima.collections.craft;

/*
 * #%L
 * Colorado Computational Pharmacology's CRAFT-related code module
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.uima.serialization.xmi.XmiLoaderAE;
import edu.ucdenver.ccp.nlp.uima.serialization.xmi.XmiLoaderAE.XmiFileCompressionType;
import edu.ucdenver.ccp.nlp.uima.serialization.xmi.XmiLoaderAE.XmiPathType;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * This class is abstract b/c the required XMI resources are not included as a
 * dependency for this project (the reasoning is to minimize how much XMI is
 * downloaded by Maven as it can be quite large).
 * 
 * @author Colorado Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public abstract class CraftCollectionReader extends JCasCollectionReader_ImplBase {

	private static final String DESCRIPTION_CRAFT_RELEASE = "Indicates which release of CRAFT to use as the input collection. "
			+ "Options are MAIN (the 67 articles in the initial public release of CRAFT); "
			+ "MAIN_DEV (a subset of 7 articles from the initial release of 67 to be used for training purposes); ";
	public static final String PARAM_CRAFT_RELEASE = "craftRelease";
	@ConfigurationParameter(mandatory = false, description = DESCRIPTION_CRAFT_RELEASE, defaultValue = "RELEASE")
	private CraftRelease craftRelease;

	private static final String DESCRIPTION_XMI_TYPE_SYSTEM = "Indicates which type system to use. Options are CCP and UCOMPARE. "
			+ "The CCP option uses the CCP type system for semantic annotations and the ClearTK type system for treebank annotations. "
			+ "The UCompare option contains only the semantic annotations.";
	public static final String PARAM_XMI_TYPE_SYSTEM = "xmiTypeSystem";
	@ConfigurationParameter(mandatory = true, description = DESCRIPTION_XMI_TYPE_SYSTEM)
	private CraftXmiTypeSystem xmiTypeSystem;

	private static final String DESCRIPTION_CONCEPTS_TO_LOAD = "This parameter allows the user to specify the different types of concept "
			+ "annotations to load into each CAS. It is optional however. If not specified, no annotations will be added to the CAS "
			+ "however the CAS document text will still be set with the appropriate CRAFT full-text document.\n" + "\n"
			+ "Valid values are:\n" + "TEXT_ONLY - to include only the document text (no annotations)\n"
			+ "CHEBI - to include annotations from the Chemical Entities of Biological Interest ontology\n"
			+ "CHEBI_EXT - to include annotations from the Chemical Entities of Biological Interest ontology + extension class annotations\n"
			+ "CL - to include Cell Type Ontology annotations\n" + "EG - to include Entrez Gene annotations\n"
			+ "CL_EXT - to include Cell Type Ontology annotations\n" + "EG - to include Entrez Gene annotations + extension class annotations\n"
			+ "GOCC - to include Gene Ontology Cellular Component annotations\n"
			+ "GOCC_EXT - to include Gene Ontology Cellular Component annotations + extension class annotations\n"
			+ "GOBP - to include Gene Ontology Biological Process annotations\n"
			+ "GOBP_EXT - to include Gene Ontology Biological Process annotations + extension class annotations\n"
			+ "GOMF - to include Gene Ontology Molecular Function annotations\n"
			+ "GOMF_EXT - to include Gene Ontology Molecular Function annotations + extension class annotations\n"
			+ "MOP - to include Molecular Process Ontology annotations\n"
			+ "MOP_EXT - to include Molecular Process Ontology annotations + extension class annotations\n"
			+ "NCBITAXON - to include organism annotations\n" 
			+ "NCBITAXON_EXT - to include organism annotations + extension class annotations\n" 
			+ "PR - to include Protein Ontology annotations\n"
			+ "PR_EXT - to include Protein Ontology annotations + extension class annotations\n"
			+ "SO - to include Sequence Ontology annotations\n"
			+ "SO_EXT - to include Sequence Ontology annotations + extension class annotations\n"
			+ "UBERON - to include UBERON annotations\n"
			+ "UBERON_EXT - to include UBERON annotations + extension class annotations\n"
			+ "TREEBANK - to include treebank annotations using the ClearTK system system (Only available when using the CCP type system)\n"
			+ "TYPO - to include document section and typographic (bold, italics, etc.) annotations\n" + "\n"
			+ "This parameter is mulit-valued, so the user can select one or more annotation categories to include.";

	public static final String PARAM_CONCEPTS_TO_LOAD = "conceptTypesToLoad";
	@ConfigurationParameter(mandatory = false, description = DESCRIPTION_CONCEPTS_TO_LOAD)
	private String[] conceptTypesToLoad;

	/**
	 * Initialized to hold references to each CRAFT document that will be
	 * processed as part of the document collection
	 */
	private CraftDocument[] craftDocuments;

	/**
	 * Keeps track of the collection progress
	 */
	private int documentIndex = 0;

	/**
	 * Initialized to load the appropriate XMI files into the CAS
	 */
	private AnalysisEngine xmiLoaderAe;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		craftDocuments = craftRelease.getDocuments();
		documentIndex = 0;
		Set<CraftConceptType> conceptTypes = new HashSet<CraftConceptType>();
		if (conceptTypesToLoad == null || conceptTypesToLoad.length == 0) {
			conceptTypes.add(CraftConceptType.TEXT_ONLY);
		} else {
			for (String conceptType : conceptTypesToLoad) {
				conceptTypes.add(CraftConceptType.valueOf(conceptType.toUpperCase()));
			}
		}
		xmiLoaderAe = initXmiLoaderAggregate(conceptTypes);
	}

	/**
	 * 
	 * @param conceptTypes
	 * @return an initialized {@link AnalysisEngine} that will load the
	 *         appropriate CRAFT XMI files into the CAS for each document
	 * @throws ResourceInitializationException
	 * 
	 */
	private AnalysisEngine initXmiLoaderAggregate(Set<CraftConceptType> conceptTypes)
			throws ResourceInitializationException {
		List<String> xmiPaths = new ArrayList<String>();
		for (CraftConceptType conceptType : conceptTypes) {
			xmiPaths.add(conceptType.getXmiPath(craftRelease, xmiTypeSystem));
		}
		AnalysisEngineDescription xmiLoaderDesc = XmiLoaderAE.createAnalysisEngineDescription(
				xmiTypeSystem.getTypeSystemDescription(), xmiTypeSystem.getDocumentMetadataExtractorClass(),
				XmiPathType.CLASSPATH, XmiFileCompressionType.GZ, null, xmiPaths.toArray(new String[xmiPaths.size()]));
		return AnalysisEngineFactory.createPrimitive(xmiLoaderDesc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#hasNext()
	 */
	public boolean hasNext() throws IOException, CollectionException {
		if (documentIndex < craftDocuments.length) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.uimafit.component.JCasCollectionReader_ImplBase#getNext(org.apache.
	 * uima.jcas.JCas)
	 */
	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {
		if (!hasNext()) {
			throw new CollectionException(new NoSuchElementException("No documents in collection."));
		}
		try {
			/*
			 * Inserting a document ID so that the XMILoaderAE knows what
			 * document to load
			 */
			DocumentMetadataHandler documentMetaDataExtractor = (DocumentMetadataHandler) ConstructorUtil
					.invokeConstructor(xmiTypeSystem.getDocumentMetadataExtractorClass().getName());
			documentMetaDataExtractor.setDocumentId(jCas, craftDocuments[documentIndex].craftAnnotatedFileName());
			TOP metaDataContainer = documentMetaDataExtractor.getMetaDataContainer(jCas);
			xmiLoaderAe.process(jCas);
			/*
			 * Remove the document metadata container that was originally put
			 * into the empty CAS as there is one in the XMI that will have also
			 * been loaded
			 */
			metaDataContainer.removeFromIndexes();
			metaDataContainer = null;
		} catch (AnalysisEngineProcessException e) {
			throw new CollectionException(e);
		}
		documentIndex++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
	 */
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(documentIndex + 1, craftDocuments.length, Progress.ENTITIES) };
	}

	/**
	 * @param xmiTypeSystem
	 * @param conceptsToLoad
	 * @return an initialized {@link CollectionReaderDescription}
	 * @throws ResourceInitializationException
	 */
	protected static CollectionReaderDescription getDescription(CraftRelease craftRelease,
			CraftXmiTypeSystem xmiTypeSystem, Set<CraftConceptType> conceptsToLoad)
			throws ResourceInitializationException {
		String[] conceptTypes = null;
		if (conceptsToLoad != null) {
			conceptTypes = new String[conceptsToLoad.size()];
			int index = 0;
			for (CraftConceptType type : conceptsToLoad) {
				conceptTypes[index++] = type.name();
			}
		}

		return CollectionReaderFactory.createDescription(CraftCollectionReader.class,
				xmiTypeSystem.getTypeSystemDescription(), PARAM_XMI_TYPE_SYSTEM, xmiTypeSystem.name(),
				PARAM_CONCEPTS_TO_LOAD, conceptTypes, PARAM_CRAFT_RELEASE, craftRelease.name());
	}

}
