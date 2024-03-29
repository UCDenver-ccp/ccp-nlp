package edu.ucdenver.ccp.nlp.wrapper.conceptmapper;

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

import java.util.List;

import org.apache.uima.conceptMapper.ConceptMapper;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

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
