/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.conceptMapper.ConceptMapper;
import org.apache.uima.conceptMapper.support.dictionaryResource.DictionaryResource_impl;
import org.apache.uima.conceptMapper.support.stemmer.Stemmer;
import org.apache.uima.conceptMapper.support.tokens.TokenFilter;
import org.apache.uima.conceptMapper.support.tokens.TokenNormalizer;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.pear.util.FileUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.ExternalResourceBinding;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.NameValuePair;
import org.apache.uima.resource.ExternalResourceDependency;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.ExternalResourceBinding;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.xml.sax.SAXException;

import org.apache.uima.resource.metadata.OperationalProperties;

import uima.tt.TokenAnnotation;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.TokenNormalizerConfigParam.CaseMatchParamValue;

/* (non javadoc) 

 Two issues (among others) effect the design of this class and prevent it
 from being a pure uimaFIT implementation. They force the use of xml descriptor
 files:

 - The nuances of creating descriptors for either ConceptMapper or OffsetTokenizer
 from uimaFIT without the use of xml descriptor files haven't been solved, so
 XML descriptor files are used to create initial versions of the descriptor structures.
 The problem starts with the fact that neither inherit from uimaFIT's 
 JCasAnnotator_ImplBase class, and may extend to the fact that they don't 
 inherit from UIMA's JCasAnnotator_ImplBase either, rather TextAnnotator (1.x not 2.x).

 - ConceptMapper requires a path to an xml descriptor file for the tokenizer.
 We haven't found a way to pass a descriptor structure into ConceptMapper in its
 place. To work around this, we create a descriptor by reading an xml file, 
 modify it, write a new descriptor file to disk and pass that to ConceptMapper.

 In some cases, the xml SAX code used by UIMA conflicts with other versions. If both
 are on the classpath and this code is executed first, it is what gets loaded. Code
 elsewhere that requires a different version won't necessarily work. A work-around
 is to avoid the xml descriptor file rewrite business by calling the the version of
 buildConceptMapperDescription that takes a path instead of an in-memory descriptor 
 structure.

 The strategy to start with descriptors built by UIMA by reading descriptor XML files,
 extends to the point of modifying structures within the descriptor rather than
 creating them.
 */

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ConceptMapperFactory {

	/**
	 * This enum defines the parameters available to the ConceptMapper. Comments were copied from
	 * the sample descriptor file that comes as part of the ConceptMapper distribution. A couple of
	 * notes: Not all parameter names are available as public static constants in the
	 * {@link ConceptMapper} class.
	 * 
	 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	private enum CmConfigParam implements UimaConfigParam {

		/**
		 * Name of the annotation type created by this TAE, must match the typeSystemDescription
		 * entry </description> <type>String</type> <multiValued>false</multiValued>
		 * <mandatory>true</mandatory>
		 */
		RESULTING_ANNOTATION_NAME(ConceptMapper.PARAM_ANNOTATION_NAME),
		/**
		 * Name of the feature in the resultingAnnotation to contain the span that encloses it (i.e.
		 * its sentence) </description> <type>String</type> <multiValued>false</multiValued>
		 * <mandatory>false</mandatory>
		 */
		RESULTING_ENCLOSING_SPAN_NAME(ConceptMapper.PARAM_ENCLOSINGSPAN),

		/**
		 * List of attribute names for XML dictionary entry record - must correspond to FeatureList
		 * </description> <type>String</type> <multiValued>true</multiValued>
		 * <mandatory>true</mandatory>
		 */
		ATTRIBUTE_LIST(ConceptMapper.PARAM_ATTRIBUTE_LIST),
		/**
		 * * List of feature names for CAS annotation - must correspond to AttributeList
		 * </description> <type>String</type> <multiValued>true</multiValued>
		 * <mandatory>true</mandatory>
		 */
		FEATURE_LIST(ConceptMapper.PARAM_FEATURE_LIST),
		/**
		 * * <multiValued>false</multiValued> <mandatory>true</mandatory>
		 */
		TOKEN_ANNOTATION(ConceptMapper.PARAM_TOKENANNOTATION),
		/**
		 * * Name of feature used when doing lookups against IncludedTokenClasses and
		 * ExcludedTokenClasses </description> <type>String</type> <multiValued>false</multiValued>
		 * <mandatory>false</mandatory>
		 */
		TOKEN_CLASS_FEATURE_NAME(ConceptMapper.PARAM_TOKENCLASSFEATURENAME),
		/**
		 * <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		TOKEN_TEXT_FEATURE_NAME(ConceptMapper.PARAM_TOKENTEXTFEATURENAME),
		/**
		 * Type of annotation which corresponds to spans of data for processing (e.g. a Sentence)
		 * </description> <type>String</type> <multiValued>false</multiValued>
		 * <mandatory>true</mandatory>
		 */
		SPAN_FEATURE_STRUCTURE("SpanFeatureStructure"),
		/**
		 * True if should ignore element order during lookup (i.e., "top box" would equal
		 * "box top"). Default is False. </description> <type>Boolean</type>
		 * <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		ORDER_INDEPENDENT_LOOKUP(ConceptMapper.PARAM_ORDERINDEPENDENTLOOKUP),
		/**
		 * Name of feature used when doing lookups against IncludedTokenTypes and ExcludedTokenTypes
		 * </description> <type>String</type> <multiValued>false</multiValued>
		 * <mandatory>false</mandatory>
		 */
		TOKEN_TYPE_FEATURE_NAME(ConceptMapper.PARAM_TOKENTYPEFEATURENAME),

		/**
		 * names of features that should be written back to a token, such as a POS tag
		 * </description> <type>String</type> <multiValued>true</multiValued>
		 * <mandatory>false</mandatory>
		 */
		TOKEN_CLASS_WRITE_BACK_FEATURE_NAMES(ConceptMapper.PARAM_TOKENCLASSWRITEBACKFEATURENAMES),
		/**
		 * <type>String</type> <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		RESULTING_ANNOTATION_MATCHED_TEXT_FEATURE(ConceptMapper.PARAM_MATCHEDFEATURE),
		/**
		 * Can be either "SkipAnyMatch", "SkipAnyMatchAllowOverlap" or
		 * "ContiguousMatch"&#13;&#13;ContiguousMatch: longest match of contiguous tokens within
		 * enclosing span(taking into account included/excluded items). DEFAULT strategy
		 * &#13;SkipAnyMatch: longest match of not-necessarily contiguous tokens within enclosing
		 * span (taking into account included/excluded items). Subsequent lookups begin in span
		 * after complete match. IMPLIES order-independent lookup &#13;SkipAnyMatchAllowOverlap:
		 * longest match of not-necessarily contiguous tokens within enclosing span (taking into
		 * account included/excluded items). Subsequent lookups begin in span after next token.
		 * IMPLIES order-independent lookup </description> <type>String</type>
		 * <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		SEARCH_STRATEGY(ConceptMapper.PARAM_SEARCHSTRATEGY),
		/**
		 * <type>Boolean</type> <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		FIND_ALL_MATCHES(ConceptMapper.PARAM_FINDALLMATCHES),
		/**
		 * <type>String</type> <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		MATCHED_TOKENS_FEATURE_NAME(ConceptMapper.PARAM_MATCHEDTOKENSFEATURENAME),
		/**
		 * <type>String</type> <multiValued>false</multiValued> <mandatory>true</mandatory>
		 */
		TOKENIZER_DESCRIPTOR_PATH("TokenizerDescriptorPath"),

		DICTIONARY_FILE(ConceptMapper.PARAM_DICT_FILE);

		private final String paramName;

		private CmConfigParam(String paramName) {
			this.paramName = paramName;
		}

		@Override
		public String paramName() {
			return paramName;
		}
	}

	public enum SearchStrategyParamValue implements UimaConfigParamValue<String> {
		CONTIGUOUS_MATCH(ConceptMapper.PARAMVALUE_CONTIGUOUSMATCH),
		SKIP_ANY_MATCH(ConceptMapper.PARAMVALUE_SKIPANYMATCH),
		SKIP_ANY_MATCH_ALLOW_OVERLAP(ConceptMapper.PARAMVALUE_SKIPANYMATCHALLOWOVERLAP);

		private final String paramValue;

		private SearchStrategyParamValue(String paramValue) {
			this.paramValue = paramValue;
		}

		@Override
		public String paramValue() {
			return paramValue;
		}
	}

	public enum TokenNormalizerConfigParam implements UimaConfigParam {

		/**
		 * this parameter specifies the case folding mode: ignoreall - fold everything to lowercase
		 * for matching insensitive - fold only tokens with initial caps to lowercase digitfold -
		 * fold all (and only) tokens with a digit sensitive - perform no case folding
		 * <type>String</type> <multiValued>false</multiValued> <mandatory>true</mandatory>
		 */
		CASE_MATCH(TokenNormalizer.PARAM_CASE_MATCH),
		/**
		 * Name of stemmer class to use before matching. MUST have a zero-parameter constructor! If
		 * not specified, no stemming will be performed. <type>String</type>
		 * <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		STEMMER_CLASS_NAME(TokenNormalizer.PARAM_STEMMER_CLASS),
		/**
		 * Configuration parameter key/label for the stemmer dictionary, passed into the stemmer's
		 * initialization method
		 */
		STEMMER_DICTIONARY(TokenNormalizer.PARAM_STEMMER_DICT),
		/**
		 * replace instances of "," with the token "and" defaults to false <type>Boolean</type>
		 * <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		REPLACE_COMMA_WITH_AND("ReplaceCommaWithAND");

		public enum CaseMatchParamValue implements UimaConfigParamValue<String> {
			CASE_INSENSITIVE("insensitive"),
			CASE_FOLD_DIGITS("digitfold"),
			CASE_IGNORE("ignoreall"),
			CASE_SENSITIVE("sensitive");

			private final String paramValue;

			private CaseMatchParamValue(String paramValue) {
				this.paramValue = paramValue;
			}

			@Override
			public String paramValue() {
				return paramValue;
			}
		}

		private final String paramName;

		private TokenNormalizerConfigParam(String paramName) {
			this.paramName = paramName;
		}

		@Override
		public String paramName() {
			return paramName;
		}

	}

	public enum TokenFilterConfigParam implements UimaConfigParam {

		/**
		 * * Type of tokens to include in lookups (if not supplied, then all types are included
		 * except those specifically mentioned in ExcludedTokenTypes) </description>
		 * <type>Integer</type> <multiValued>true</multiValued> <mandatory>false</mandatory>
		 */
		INCLUDED_TOKEN_TYPES(TokenFilter.PARAM_INCLUDEDTOKENTYPES),
		/**
		 * * <type>Integer</type> <multiValued>true</multiValued> <mandatory>false</mandatory>
		 */
		EXCLUDED_TOKEN_TYPES(TokenFilter.PARAM_EXCLUDEDTOKENTYPES),
		/**
		 * * Class of tokens to exclude from lookups (if not supplied, then all classes are excluded
		 * except those specifically mentioned in IncludedTokenClasses, unless IncludedTokenClasses
		 * is not supplied, in which case none are excluded) </description> <type>String</type>
		 * <multiValued>true</multiValued> <mandatory>false</mandatory>
		 */
		EXCLUDED_TOKEN_CLASSES(TokenFilter.PARAM_EXCLUDEDTOKENCLASSES),
		/**
		 * Class of tokens to include in lookups (if not supplied, then all classes are included
		 * except those specifically mentioned in ExcludedTokenClasses) </description>
		 * <type>String</type> <multiValued>true</multiValued> <mandatory>false</mandatory>
		 */
		INCLUDED_TOKEN_CLASSES(TokenFilter.PARAM_INCLUDEDTOKENCLASSES),
		/**
		 * <type>String</type> <multiValued>true</multiValued> <mandatory>false</mandatory>
		 */
		STOP_WORDS(TokenFilter.PARAM_STOPWORDS);

		private final String paramName;

		private TokenFilterConfigParam(String paramName) {
			this.paramName = paramName;
		}

		@Override
		public String paramName() {
			return paramName;
		}
	}

	public enum DictionaryResourceConfigParam implements UimaConfigParam {
		/**
		 * <type>Boolean</type> <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		PRINT_DICTIONARY(DictionaryResource_impl.PARAM_DUMPDICT),
		/**
		 * <type>String</type> <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		LANGUAGE_ID("LanguageID");

		private final String paramName;

		private DictionaryResourceConfigParam(String paramName) {
			this.paramName = paramName;
		}

		@Override
		public String paramName() {
			return paramName;
		}
	}

	private static final String CONCEPT_MAPPER_DESCRIPTOR_PATH = "analysis_engine.primitive.ConceptMapperOffsetTokenizer";

	public static TypeSystemDescription getDefaultConceptMapperTypeSystem() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem",
				"edu.ucdenver.ccp.nlp.wrapper.conceptmapper.TypeSystem",
				"edu.ucdenver.ccp.nlp.wrapper.conceptmapper.TestTypeSystem", "analysis_engine.primitive.DictTerm",
				"org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation");
	}

	/**
	 * Returns an {@link AnalysisEngineDescription} initialized using the input configuration data.
	 * The base of the description is loaded from the ConceptMapperOffsetTokenizer.xml descriptor
	 * file that is part of the ConceptMapper distribution. Parameter settings in that file are
	 * overridden by those set in the input configuration data.
	 * 
	 * @param tsd
	 * @param configurationData
	 * @return
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static AnalysisEngineDescription buildConceptMapperDescription(TypeSystemDescription tsd,
			Object[] configurationData) throws UIMAException, IOException {
		AnalysisEngineDescription description = AnalysisEngineFactory.createAnalysisEngineDescription(
				CONCEPT_MAPPER_DESCRIPTOR_PATH, configurationData);
		TypeSystemDescription cmTypeSystem = description.getAnalysisEngineMetaData().getTypeSystem();

		/*
		 * The ConceptMapper Descriptor defines the uima.tt.TokenAnnotation type so we extract it
		 * and add it to the input type system
		 */
		TypeDescription tokenAnnotationTypeDesc = cmTypeSystem.getType("uima.tt.TokenAnnotation");
		List<TypeDescription> types = new ArrayList<TypeDescription>(Arrays.asList(tsd.getTypes()));
		types.add(tokenAnnotationTypeDesc);

		TypeSystemDescription tsdToUse = TypeSystemDescriptionFactory.createTypeSystemDescription();
		tsdToUse.setTypes(types.toArray(new TypeDescription[types.size()]));
		description.getAnalysisEngineMetaData().setTypeSystem(tsdToUse);
		return description;
	}

	/**
	 * @param attributeList
	 * @param dictionaryFile
	 * @param featureList
	 * @param findAllMatches
	 * @param matchedTokensFeatureName
	 * @param orderIndependentLookup
	 * @param resultingAnnotationMatchedTextFeature
	 * @param resultingAnnotationClass
	 * @param resultingEnclosingSpanName
	 * @param searchStrategyParamValue
	 * @param spanFeatureStructureClass
	 *            commonly edu.ucdenver.ccp.nlp.ext.uima.types.Sentence
	 * @param tokenAnnotationClass
	 * @param tokenClassFeatureName
	 * @param tokenClassWriteBackFeatureNames
	 * @param tokenTextFeatureName
	 * @param tokenTypeFeatureName
	 * @param tokenizerDescriptorPath
	 * @param replaceCommaWithAnd
	 * @param stemmerClass
	 *            is optional, leave null if none desired
	 * @param stemmerDictionaryFile
	 * @param excludedTokenClasses
	 * @param excludedTokenTypes
	 * @param includedTokenClasses
	 * @param includedTokenTypes
	 * @param stopwords
	 * @param languageId
	 * @param printDictionary
	 * @param caseMatch
	 * @return
	 */
	public static Object[] buildConfigurationData(String[] attributeList, File dictionaryFile, String[] featureList,
			boolean findAllMatches, String matchedTokensFeatureName, boolean orderIndependentLookup,
			String resultingAnnotationMatchedTextFeature, Class<? extends Annotation> resultingAnnotationClass,
			String resultingEnclosingSpanName, SearchStrategyParamValue searchStrategyParamValue,
			Class<? extends Annotation> spanFeatureStructureClass, Class<? extends Annotation> tokenAnnotationClass,
			String tokenClassFeatureName, String[] tokenClassWriteBackFeatureNames, String tokenTextFeatureName,
			String tokenTypeFeatureName, File tokenizerDescriptorPath, CaseMatchParamValue caseMatchParamValue,
			boolean replaceCommaWithAnd, Class<? extends Stemmer> stemmerClass, File stemmerDictionaryFile,
			String[] excludedTokenClasses, Integer[] excludedTokenTypes, String[] includedTokenClasses,
			Integer[] includedTokenTypes, String[] stopwords, String languageId, boolean printDictionary) {
		/* @formatter:off */
		Object[] configData = new Object[] {
				CmConfigParam.ATTRIBUTE_LIST.paramName(), attributeList,
				CmConfigParam.DICTIONARY_FILE.paramName(),dictionaryFile.getAbsolutePath(),
				CmConfigParam.FEATURE_LIST.paramName(),featureList,
				CmConfigParam.FIND_ALL_MATCHES.paramName(),findAllMatches,
				CmConfigParam.MATCHED_TOKENS_FEATURE_NAME.paramName(),matchedTokensFeatureName,
				CmConfigParam.ORDER_INDEPENDENT_LOOKUP.paramName(),orderIndependentLookup,
				CmConfigParam.RESULTING_ANNOTATION_MATCHED_TEXT_FEATURE.paramName(),resultingAnnotationMatchedTextFeature,
				CmConfigParam.RESULTING_ANNOTATION_NAME.paramName(),resultingAnnotationClass.getName(),
				CmConfigParam.RESULTING_ENCLOSING_SPAN_NAME.paramName(),resultingEnclosingSpanName,
				CmConfigParam.SEARCH_STRATEGY.paramName(),searchStrategyParamValue.paramValue(),
				CmConfigParam.SPAN_FEATURE_STRUCTURE.paramName(),spanFeatureStructureClass.getName(),
				CmConfigParam.TOKEN_ANNOTATION.paramName(),tokenAnnotationClass.getName(),
				CmConfigParam.TOKEN_CLASS_WRITE_BACK_FEATURE_NAMES.paramName(),tokenClassWriteBackFeatureNames,
				CmConfigParam.TOKENIZER_DESCRIPTOR_PATH.paramName(),tokenizerDescriptorPath.getAbsolutePath(),
				TokenNormalizerConfigParam.CASE_MATCH.paramName(), caseMatchParamValue.paramValue(),
				TokenNormalizerConfigParam.REPLACE_COMMA_WITH_AND.paramName(), replaceCommaWithAnd,
				TokenFilterConfigParam.EXCLUDED_TOKEN_CLASSES.paramName(), excludedTokenClasses,
				TokenFilterConfigParam.EXCLUDED_TOKEN_TYPES.paramName(), excludedTokenTypes,
				TokenFilterConfigParam.INCLUDED_TOKEN_CLASSES.paramName(), includedTokenClasses,
				TokenFilterConfigParam.INCLUDED_TOKEN_TYPES.paramName(), includedTokenTypes,
				TokenFilterConfigParam.STOP_WORDS.paramName(), stopwords,
				DictionaryResourceConfigParam.LANGUAGE_ID.paramName(), languageId,
				DictionaryResourceConfigParam.PRINT_DICTIONARY.paramName(), printDictionary };
		/* @formatter:on */

		// add optional stemmer parameters when present
		if (stemmerClass != null) {
			configData = Arrays.copyOf(configData, configData.length + 2);
			configData[configData.length - 2] = TokenNormalizerConfigParam.STEMMER_CLASS_NAME.paramName();
			configData[configData.length - 1] = stemmerClass.getName();
			if (stemmerDictionaryFile != null) {
				configData = Arrays.copyOf(configData, configData.length + 2);
				configData[configData.length - 2] = TokenNormalizerConfigParam.STEMMER_DICTIONARY.paramName();
				configData[configData.length - 1] = stemmerDictionaryFile.getAbsolutePath();
			}
		}

		if (tokenClassFeatureName != null) {
			configData = Arrays.copyOf(configData, configData.length + 2);
			configData[configData.length - 2] = CmConfigParam.TOKEN_CLASS_FEATURE_NAME.paramName();
			configData[configData.length - 1] = tokenClassFeatureName;
		}

		if (tokenTextFeatureName != null) {
			configData = Arrays.copyOf(configData, configData.length + 2);
			configData[configData.length - 2] = CmConfigParam.TOKEN_TEXT_FEATURE_NAME.paramName();
			configData[configData.length - 1] = tokenTextFeatureName;
		}

		if (tokenTypeFeatureName != null) {
			configData = Arrays.copyOf(configData, configData.length + 2);
			configData[configData.length - 2] = CmConfigParam.TOKEN_TYPE_FEATURE_NAME.paramName();
			configData[configData.length - 1] = tokenTypeFeatureName;
		}

		return configData;
	}

	/**
	 * @param tsd
	 * @param dictionaryFile
	 * @param caseMatchParamValue
	 * @param searchStrategyParamValue
	 * @param stemmerClass
	 *            is optional, leave null if none desired
	 * @param stopwords
	 * @param orderIndependentLookup
	 * @param findAllMatches
	 * @param replaceCommaWithAnd
	 * @param spanFeatureStructureClass
	 *            commonly edu.ucdenver.ccp.nlp.ext.uima.types.Sentence
	 * @param tokenizerDescription
	 * @return
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static AnalysisEngineDescription buildConceptMapperDescription(TypeSystemDescription tsd,
			File dictionaryFile, CaseMatchParamValue caseMatchParamValue,
			SearchStrategyParamValue searchStrategyParamValue, Class<? extends Stemmer> stemmerClass,
			String[] stopwords, boolean orderIndependentLookup, boolean findAllMatches, boolean replaceCommaWithAnd,
			Class<? extends Annotation> spanFeatureStructureClass, AnalysisEngineDescription tokenizerDescription)
			throws UIMAException, IOException {

		String[] attributeList = new String[] { "canonical", "id" };
		String[] featureList = new String[] { "DictCanon", "ID" };
		String matchedTokensFeatureName = "matchedTokens";
		String resultingAnnotationMatchedTextFeature = "matchedText";
		Class<? extends Annotation> resultingAnnotationClass = OntologyTerm.class;
		String resultingEnclosingSpanName = "enclosingSpan";
		Class<? extends Annotation> tokenAnnotationClass = TokenAnnotation.class;
		String tokenClassFeatureName = null;
		String[] tokenClassWriteBackFeatureNames = new String[0];
		String tokenTextFeatureName = null;
		String tokenTypeFeatureName = null;
		File stemmerDictionaryFile = null;
		String[] excludedTokenClasses = new String[0];
		Integer[] excludedTokenTypes = new Integer[0];
		String[] includedTokenClasses = new String[0];
		Integer[] includedTokenTypes = new Integer[0];
		String languageId = "en";
		boolean printDictionary = false;

		File tokenizerDescriptorPath = FileUtil.createTempFile("tokenizer-desc", "xml");
		System.out.println(tokenizerDescription.toString());

		BufferedWriter writer = FileWriterUtil.initBufferedWriter(tokenizerDescriptorPath, CharacterEncoding.UTF_8,
				WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
		try {
			tokenizerDescription.toXML(writer);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
		writer.close();

		// UIMA_Util.outputDescriptorToFile(tokenizerDescription, tokenizerDescriptorPath);
		System.out.println("tokenizer desc: " + tokenizerDescriptorPath.getAbsolutePath());

		Object[] configurationData = ConceptMapperFactory.buildConfigurationData(attributeList, dictionaryFile,
				featureList, findAllMatches, matchedTokensFeatureName, orderIndependentLookup,
				resultingAnnotationMatchedTextFeature, resultingAnnotationClass, resultingEnclosingSpanName,
				searchStrategyParamValue, spanFeatureStructureClass, tokenAnnotationClass, tokenClassFeatureName,
				tokenClassWriteBackFeatureNames, tokenTextFeatureName, tokenTypeFeatureName, tokenizerDescriptorPath,
				caseMatchParamValue, replaceCommaWithAnd, stemmerClass, stemmerDictionaryFile, excludedTokenClasses,
				excludedTokenTypes, includedTokenClasses, includedTokenTypes, stopwords, languageId, printDictionary);

		AnalysisEngineDescription description = ConceptMapperFactory.buildConceptMapperDescription(tsd,
				configurationData);

		ExternalResourceDescription[] externalResources = description.getResourceManagerConfiguration()
				.getExternalResources();
		// System.out.println("# resources = " + externalResources.length);
		ExternalResourceDescription dictionaryFileResourceDesc = externalResources[0];
		// System.out.println(dictionaryFileResourceDesc.getResourceSpecifier().getAttributeValue("fileUrl"));
		dictionaryFileResourceDesc.getResourceSpecifier()
				.setAttributeValue("fileUrl", dictionaryFile.getAbsolutePath());
		description.getResourceManagerConfiguration().setExternalResources(
				new ExternalResourceDescription[] { dictionaryFileResourceDesc });

		return description;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * This function will evolve to creating the ConceptMapper descriptor rather than reading a file
	 * and modifying the created descriptor.
	 * 
	 * It will likely not solve the problem of having to provide an xml descriptor for the tokenizer
	 * to ConceptMapper.
	 */

	public static AnalysisEngineDescription buildConceptMapperDescription(TypeSystemDescription tsd,
			File dictionaryFile, CaseMatchParamValue caseMatchParamValue,
			SearchStrategyParamValue searchStrategyParamValue, Class<? extends Stemmer> stemmerClass,
			String[] stopwords, boolean orderIndependentLookup, boolean findAllMatches, boolean replaceCommaWithAnd,
			Class<? extends Annotation> spanFeatureStructureClass, File tokenizerDescriptorPath)

	throws UIMAException, IOException {

		// boilerplace ConceptMapper config for the type systems
		String[] attributeList = new String[] { "canonical", "id" };
		String[] featureList = new String[] { "DictCanon", "ID" };
		String matchedTokensFeatureName = "matchedTokens";
		String resultingAnnotationMatchedTextFeature = "matchedText";
		Class<? extends Annotation> resultingAnnotationClass = OntologyTerm.class;
		String resultingEnclosingSpanName = "enclosingSpan";
		Class<? extends Annotation> tokenAnnotationClass = TokenAnnotation.class;
		String tokenClassFeatureName = null;
		String[] tokenClassWriteBackFeatureNames = new String[0];
		String tokenTextFeatureName = null;
		String tokenTypeFeatureName = null;
		File stemmerDictionaryFile = null;
		String[] excludedTokenClasses = new String[0];
		Integer[] excludedTokenTypes = new Integer[0];
		String[] includedTokenClasses = new String[0];
		Integer[] includedTokenTypes = new Integer[0];
		String languageId = "en";
		boolean printDictionary = false;

		Object[] configurationData = ConceptMapperFactory.buildConfigurationData(attributeList, dictionaryFile,
				featureList, findAllMatches, matchedTokensFeatureName, orderIndependentLookup,
				resultingAnnotationMatchedTextFeature, resultingAnnotationClass, resultingEnclosingSpanName,
				searchStrategyParamValue, spanFeatureStructureClass, tokenAnnotationClass, tokenClassFeatureName,
				tokenClassWriteBackFeatureNames, tokenTextFeatureName, tokenTypeFeatureName, tokenizerDescriptorPath,
				caseMatchParamValue, replaceCommaWithAnd, stemmerClass, stemmerDictionaryFile, excludedTokenClasses,
				excludedTokenTypes, includedTokenClasses, includedTokenTypes, stopwords, languageId, printDictionary);

		AnalysisEngineDescription aeDescription = AnalysisEngineFactory.createPrimitiveDescription(
				(Class) ConceptMapperUimaFitWrapper.class, tsd, configurationData);

		// Set Up and Bind External Resources for dictionaryFile
		final String DEPENDENCY_KEY = "DictionaryFile";
		final String RESOURCE_KEY = "DictionaryFileName";

		// Create a ResourceDependency on the key: DictionaryFile, and interface:DictionaryResource
		// This particular method also sets the dependency on the descriptor
		ExternalResourceFactory.createDependency(aeDescription, DEPENDENCY_KEY,
				org.apache.uima.conceptMapper.support.dictionaryResource.DictionaryResource.class);

		// Create a Resource on the key: DictionaryFileName, and class DictionaryResource_impl
		ExternalResourceDescription resourceDescription = null;
		{
			resourceDescription = ExternalResourceFactory.createExternalResourceDescription(
					org.apache.uima.conceptMapper.support.dictionaryResource.DictionaryResource_impl.class, "file:///"
							+ dictionaryFile.getAbsolutePath());
		}

		// Bind the two, tie that binding into the CM's descriptor
		// WARNING: the dependency needs to exist on the descriptor beforehand or this will
		// *quietly* fail
		ExternalResourceFactory.bindResource(aeDescription, DEPENDENCY_KEY, resourceDescription);

		if (false) {
			AnalysisEngineMetaData aemd = aeDescription.getAnalysisEngineMetaData();
			System.out.println(aemd.getName());
			ConfigurationParameterSettings cps = aemd.getConfigurationParameterSettings();
			System.out.println("config parameters --------------------------");
			for (NameValuePair nvp : cps.getParameterSettings()) {
				System.out.println(nvp.getName() + ", " + nvp.getValue());
			}
			System.out.println("--------------------------");

			System.out.println("deps --------------------------");
			ExternalResourceDependency[] deps = aeDescription.getExternalResourceDependencies();
			for (ExternalResourceDependency dep : deps) {
				System.out.println("key:" + dep.getKey());
				System.out.println("iface:" + dep.getInterfaceName());

			}
			System.out.println("--------------------------");

			if (aeDescription.getResourceManagerConfiguration() != null) {
				ExternalResourceBinding bindings[] = aeDescription.getResourceManagerConfiguration()
						.getExternalResourceBindings();
				System.out.println("external bindings --------------------------");
				for (ExternalResourceBinding erb : bindings) {
					System.out.println("binding key:" + erb.getKey());
					System.out.println("resource name :" + erb.getResourceName());
				}
				System.out.println("--------------------------");
			} else {
				System.out.println(" no external resource binding manager");
			}
		}

		return aeDescription;
	}
}
