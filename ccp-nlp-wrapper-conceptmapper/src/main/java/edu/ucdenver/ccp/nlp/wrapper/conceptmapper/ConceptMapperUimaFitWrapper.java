package edu.ucdenver.ccp.nlp.wrapper.conceptmapper;

import java.util.List;

import org.apache.uima.conceptMapper.ConceptMapper;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ExternalResourceDependency;
import org.uimafit.descriptor.ExternalResource;

import org.uimafit.descriptor.ConfigurationParameter;

/* This class is a wrapper around the ConceptMapper used to
   help uimaFIT initialize parameters for CM */
public class ConceptMapperUimaFitWrapper extends ConceptMapper {


	@ConfigurationParameter(name = PARAM_ANNOTATION_NAME, mandatory=true)
	String annotationName;

	@ConfigurationParameter(name = PARAM_ATTRIBUTE_LIST,  mandatory=true)
	List<String> attributeList;

	@ConfigurationParameter(name = PARAM_FEATURE_LIST,    mandatory=true)
	List<String> featureList;

	@ConfigurationParameter(name = PARAM_TOKENANNOTATION, mandatory=true)
    String tokenAnnotation;

	@ConfigurationParameter(name = PARAM_DICT_FILE,       mandatory=true)
	String dictFile;

    //@ConfigurationParameter(name = PARAM_TOKENIZER_DESCRIPTOR)
    @ConfigurationParameter(name = "TokenizerDescriptorPath")
	String tokenizerDescriptorPath; 

    //@ConfigurationParameter(name = PARAM_DATA_BLOCK_FS, mandatory=true)
    @ConfigurationParameter(name = "SpanFeatureStructure", mandatory=true)
	String spanFeatureStructure; 

	@ConfigurationParameter(name = PARAM_ENCLOSINGSPAN,  mandatory=false)
	String enclosingSpanName;

	@ConfigurationParameter(name = PARAM_FINDALLMATCHES,  mandatory=false)
	boolean findAllMatches;

	@ConfigurationParameter(name = PARAM_MATCHEDFEATURE,  mandatory=false)
	String matchedFeature;

	@ConfigurationParameter(name = PARAM_MATCHEDTOKENSFEATURENAME, mandatory=false)
	String matchedTokensFeatureName;

	@ConfigurationParameter(name = PARAM_ORDERINDEPENDENTLOOKUP,   mandatory=false)
	boolean orderIndependentLookup;;

	@ConfigurationParameter(name = PARAM_SEARCHSTRATEGY,  mandatory=false)
	String searchStrategy;

	@ConfigurationParameter(name = PARAM_TOKENCLASSFEATURENAME,    mandatory=false)
	String tokenClassFeatureName;

	@ConfigurationParameter(name = PARAM_TOKENCLASSWRITEBACKFEATURENAMES,  mandatory=false)
	List<String> tokenClassWriteBackFeatureNames; 

	@ConfigurationParameter(name = PARAM_TOKENTEXTFEATURENAME,     mandatory=false)
	String tokenTextFeatureName;

	@ConfigurationParameter(name = PARAM_TOKENTYPEFEATURENAME,     mandatory=false)
	String tokenTypeFeatureName;

}
