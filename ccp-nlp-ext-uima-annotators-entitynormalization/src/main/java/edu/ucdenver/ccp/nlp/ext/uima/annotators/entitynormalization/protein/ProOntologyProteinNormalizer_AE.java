/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitynormalization.protein;

import java.io.File;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdResolver;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.AccessMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.BerkeleyDbDictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.BerkeleyDbGeneNameEntry;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.GeneNameRegularizer;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.GeneTaggerMentionFilter;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.HomologeneDictionaryBuilder;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.protein.ProteinOntologyDictionaryBuilder;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio.GeneIdAnnotationDecorator;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio.impl.CcpGeneIdAnnotationDecorator;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ProOntologyProteinNormalizer_AE extends JCasAnnotator_ImplBase {

	/* ==== dictionary directory configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_HOMOLOGENE_DICTIONARY_DIRECTORY = ConfigurationParameterFactory
			.createConfigurationParameterName(ProOntologyProteinNormalizer_AE.class, "dictionaryDirectory");

	/**
	 * The name of the TokenAttributeExtractor implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "directory containing the Homologene dictionary")
	private File dictionaryDirectory;

	/* ==== DocumentMetaDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(ProOntologyProteinNormalizer_AE.class,
					"documentMetadataExtractorClassName");

	/**
	 * The name of the TokenAttributeExtractor implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentMetaDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor")
	private String documentMetadataExtractorClassName;

	/**
	 * this {@link DocumentMetaDataExtractor} will be initialized based on the class name specified
	 * by the documentMetadataExtractorClassName parameter
	 */
	private DocumentMetaDataExtractor documentMetaDataExtractor;

	/* ==== AnnotationDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the annotation data extractor
	 * implementation to use
	 */
	public static final String PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(ProOntologyProteinNormalizer_AE.class,
					"annotationDataExtractorClassName");

	/**
	 * The name of the {@link AnnotationDataExtractor} implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the AnnotationDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationDataExtractor")
	private String annotationDataExtractorClassName;

	/**
	 * this {@link AnnotationDataExtractor} will be initialized based on the class name specified by
	 * the annotationDataExtractorClassName parameter
	 */
	private AnnotationDataExtractor annotationDataExtractor;

	/* ==== DocumentMetaDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_GENE_ID_ANNOTATION_DECORATOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(ProOntologyProteinNormalizer_AE.class,
					"geneAnnotationDecoratorClassName");

	/**
	 * The name of the TokenAttributeExtractor implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the GeneAnnotationDecorator implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio.impl.CcpGeneIdAnnotationDecorator")
	private String geneAnnotationDecoratorClassName;

	private CcpGeneIdAnnotationDecorator d;
	/**
	 * this {@link DocumentMetaDataExtractor} will be initialized based on the class name specified
	 * by the documentMetadataExtractorClassName parameter
	 */
	private GeneIdAnnotationDecorator geneIdAnnotationDecorator;

	/* ==== other member variables below ==== */

	private Dictionary<BerkeleyDbGeneNameEntry> dictionary;

	private GeneNameRegularizer geneNameRegularizer;

	private static Logger logger;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger = context.getLogger();
		dictionary = new BerkeleyDbDictionary<BerkeleyDbGeneNameEntry>(dictionaryDirectory,
				ProteinOntologyDictionaryBuilder.STORE_NAME, ProteinOntologyDictionaryBuilder.ENTRY_INDEX_ACCESSOR_CLASS,
				ProteinOntologyDictionaryBuilder.DICTIONARY_ENTRY_CLASS);
		dictionary.open(AccessMode.READONLY);

		try {
			geneNameRegularizer = ProteinOntologyDictionaryBuilder.ENTRY_REGULARIZER_CLASS.newInstance();
		} catch (InstantiationException e) {
			throw new ResourceInitializationException(e);
		} catch (IllegalAccessException e) {
			throw new ResourceInitializationException(e);
		}

		documentMetaDataExtractor = (DocumentMetaDataExtractor) ConstructorUtil
				.invokeConstructor(documentMetadataExtractorClassName);
		annotationDataExtractor = (AnnotationDataExtractor) ConstructorUtil
				.invokeConstructor(annotationDataExtractorClassName);
		geneIdAnnotationDecorator = (GeneIdAnnotationDecorator) ConstructorUtil
				.invokeConstructor(geneAnnotationDecoratorClassName);
	}

	/**
	 * Closes the dictionary
	 */
	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		dictionary.shutdown();
	}

	/**
	 * Processes all protein annotations in the CAS by searching the dictionary for each protein
	 * annotation's covered text. For each match made, the Homologene Group ID or Entrez Gene ID is
	 * added to the protein annotation using the GeneIdAnnotationDecorator implementation.
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex().iterator(); annotIter
				.hasNext();) {
			Annotation annot = annotIter.next();
			String type = annotationDataExtractor.getAnnotationType(annot);
			if (type != null && type.equals(ClassMentionTypes.PROTEIN)) {
				String proteinText = annot.getCoveredText();
				proteinText = GeneTaggerMentionFilter.filterMention(proteinText);
				if (!proteinText.isEmpty()) {
					Set<BerkeleyDbGeneNameEntry> hits = dictionary.searchAliases(geneNameRegularizer, proteinText);
//					logger.log(Level.INFO,"Hits for '" + proteinText + "': " + hits.size() + " -- " + hits.toString());
					for (BerkeleyDbGeneNameEntry entry : hits) {
						// in this case the id will either be a Homologene Group Id or an Entrez
						// Gene Id
						DataSourceIdentifier<?> id = DataSourceIdResolver.resolveId(entry.getDictionaryKey());
						if (id == null)
							logger.log(Level.WARNING,"Unable to resolve ID: " + entry.getDictionaryKey() + " Please update the DataSourceIdResolver to handle this condition.");
						else
							geneIdAnnotationDecorator.addAnnotationAttribute(annot, id);
					}
				}
			}
		}
	}

	/**
	 * Returns an initialized {@link AnalysisEngineDescription}
	 * 
	 * @param tsd
	 * @param documentMetaDatExtractorClass
	 * @param annotationDataExtractorClass
	 * @param geneIdAnnotationDecoratorClass
	 * @param dictionaryDirectory
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			Class<? extends DocumentMetaDataExtractor> documentMetaDatExtractorClass,
			Class<? extends AnnotationDataExtractor> annotationDataExtractorClass,
			Class<? extends GeneIdAnnotationDecorator> geneIdAnnotationDecoratorClass, File dictionaryDirectory)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(ProOntologyProteinNormalizer_AE.class, tsd,
				PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS, documentMetaDatExtractorClass.getName(),
				PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS, annotationDataExtractorClass.getName(),
				PARAM_GENE_ID_ANNOTATION_DECORATOR_CLASS, geneIdAnnotationDecoratorClass,
				dictionaryDirectory.getName(), PARAM_HOMOLOGENE_DICTIONARY_DIRECTORY,
				dictionaryDirectory.getAbsolutePath());
	}

	/**
	 * Returns an {@link AnalysisEngineDescription} intialized with default values (CCP-specific)
	 * 
	 * @param tsd
	 * @param dictionaryDirectory
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			File dictionaryDirectory) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(ProOntologyProteinNormalizer_AE.class, tsd,
				PARAM_HOMOLOGENE_DICTIONARY_DIRECTORY, dictionaryDirectory.getAbsolutePath());
	}
}
