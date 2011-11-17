/* ****************************************************************

Copyright (C) 2004 Burr Settles, University of Wisconsin-Madison,
Dept. of Computer Sciences and Dept. of Biostatistics and Medical
Informatics.

This file is part of the "ABNER (A Biomedical Named Entity
Recognizer)" system. It requires Java 1.4. This software is
provided "as is," and the author makes no representations or
warranties, express or implied. For details, see the "README" file
included in this distribution.

This software is provided under the terms of the Common Public
License, v1.0, as published by http://www.opensource.org. For more
information, see the "LICENSE" file included in this distribution.

**************************************************************** */

package edu.ucdenver.ccp.nlp.abner.ext;

import java.io.File;
import java.io.FileReader;
import java.util.regex.Pattern;

import edu.umass.cs.mallet.base.fst.CRF4;
import edu.umass.cs.mallet.base.fst.MultiSegmentationEvaluator;
import edu.umass.cs.mallet.base.pipe.Pipe;
import edu.umass.cs.mallet.base.pipe.SerialPipes;
import edu.umass.cs.mallet.base.pipe.TokenSequence2FeatureVectorSequence;
import edu.umass.cs.mallet.base.pipe.iterator.LineGroupIterator;
import edu.umass.cs.mallet.base.pipe.tsf.OffsetConjunctions;
import edu.umass.cs.mallet.base.pipe.tsf.RegexMatches;
import edu.umass.cs.mallet.base.pipe.tsf.TokenTextCharPrefix;
import edu.umass.cs.mallet.base.pipe.tsf.TokenTextCharSuffix;
import edu.umass.cs.mallet.base.types.InstanceList;

/**
   <p>The Trainer class will train a CRF to extract entities from a
   customized dataset. The input file must be tokenized with one
   sentence per line, with a "|" (vertical pipe) separating a
   word/token from its label. The first token of an entity name should
   have a label beginning with "B-", all other entity token labels
   should begin with "I-", and non-entity tokens should be labeled
   with "O":

   <pre>
   IL-2|B-DNA gene|I-DNA expression|O and|O NF-kappa|B-PROTEIN B|I-PROTEIN activation|O ...
   </pre>

   @author Burr Settles <a href="http://www.cs.wisc.edu/~bsettles">bsettles&#64;&#99;s&#46;&#119;i&#115;&#99;&#46;&#101;d&#117;</a> 
   @version 1.5 (March 2005)
*/
public class Trainer {
    int numEvaluations = 0;
    static int iterationsBetweenEvals = 16;

    private static String CAPS = "[A-Z��������������]";
    private static String LOW = "[a-z��������������]";
    private static String CAPSNUM = "[A-Z��������������0-9]";
    private static String ALPHA = "[A-Z��������������a-z��������������]";
    private static String ALPHANUM = "[A-Z��������������a-z��������������0-9]";
    private static String PUNCTUATION = "[,\\.;:?!()]";
    private static String QUOTE = "[\"`']";
    private static String GREEK = "(alpha|beta|gamma|delta|epsilon|zeta|eta|theta|iota|kappa|lambda|mu|nu|xi|omicron|pi|rho|sigma|tau|upsilon|phi|chi|psi|omega)";


    /**
       <p>Takes input <tt>trainFile</tt> (format described above), and
       saves a trained linear-chain CRF on the data using ABNER's
       default feature set in the corresponding output
       <tt>modelFile</tt>.

       <p><i>Warning: training will take several hours, perhaps even
       days to complete depending on corpus size and number of entity
       tags.</i>
    */
    public void train (String trainFile, String modelFile) {
	train(trainFile, modelFile, null);
    }

    /**
       <p>Identical to the other train routine, but the set of tags
       (e.g. "PROTEIN", "DNA", etc.) allows the model to periodically
       output progress in terms of precision/recall/f1 during
       training. <i>Note: do not use "B-" or "I-" prefixes.</i>
    */
    public void train (String trainFile, String modelFile, String[] tags) {

	try {

	    // stuff we'll need.
	    Pipe p;
	    CRF4 crf;
	    p = new SerialPipes (new Pipe[] {
		new Input2TokenSequence (),

		new RegexMatches ("INITCAPS", Pattern.compile ("[A-Z].*")),
		new RegexMatches ("INITCAPSALPHA", Pattern.compile ("[A-Z][a-z].*")),
		new RegexMatches ("ALLCAPS", Pattern.compile ("[A-Z]+")),
		new RegexMatches ("CAPSMIX", Pattern.compile ("[A-Za-z]+")),
		new RegexMatches ("HASDIGIT", Pattern.compile (".*[0-9].*")),
		new RegexMatches ("SINGLEDIGIT", Pattern.compile ("[0-9]")),
		new RegexMatches ("DOUBLEDIGIT", Pattern.compile ("[0-9][0-9]")),
		new RegexMatches ("NATURALNUMBER", Pattern.compile ("[0-9]+")),
		new RegexMatches ("REALNUMBER", Pattern.compile ("[-0-9]+[.,]+[0-9.,]+")),
		new RegexMatches ("HASDASH", Pattern.compile (".*-.*")),
		new RegexMatches ("INITDASH", Pattern.compile ("-.*")),
		new RegexMatches ("ENDDASH", Pattern.compile (".*-")),

		new TokenTextCharPrefix ("PREFIX=", 3),
		new TokenTextCharPrefix ("PREFIX=", 4),
		new TokenTextCharSuffix ("SUFFIX=", 3),
		new TokenTextCharSuffix ("SUFFIX=", 4),

		new OffsetConjunctions (new int[][] {{-1}, {1}}),

		new RegexMatches ("ALPHANUMERIC", Pattern.compile (".*[A-Za-z].*[0-9].*")),
		new RegexMatches ("ALPHANUMERIC", Pattern.compile (".*[0-9].*[A-Za-z].*")),

		new RegexMatches ("ROMAN", Pattern.compile ("[IVXDLCM]+")),
		new RegexMatches ("HASROMAN", Pattern.compile (".*\\b[IVXDLCM]+\\b.*")),
		new RegexMatches ("GREEK", Pattern.compile (GREEK)),
		new RegexMatches ("HASGREEK", Pattern.compile (".*\\b"+GREEK+"\\b.*")),

		new RegexMatches ("PUNCTUATION", Pattern.compile ("[,.;:?!-+]")),

		//new PrintTokenSequenceFeatures(), // for debugging
		new TokenSequence2FeatureVectorSequence (true, true),

	    });

	    // init model w/info
	    crf = new CRF4 (p, null);

	    // read in the traing set files
	    System.out.println("Reading '"+trainFile+"' file...");
	    InstanceList trainingData = new InstanceList (p);
	    trainingData.add (new LineGroupIterator (new FileReader (new File (trainFile)), 
						     Pattern.compile("^.*$"), false));

	    System.out.println ("Doing the deed...");
	    System.out.println ("Number of features = "+p.getDataAlphabet().size());
	    System.out.println ("Training on "+trainingData.size()+" training instances...");

	    // properly set up the state transition graph...
	    crf.addStatesForLabelsConnectedAsIn (trainingData);

	    // if a tagset was specified, train with an evaluator...
	    if (tags != null) {
		String[] bTags = new String[tags.length];
		String[] iTags = new String[tags.length];
		for (int i=0; i<tags.length; i++) {
		    bTags[i] = "B-"+tags[i];
		    iTags[i] = "I-"+tags[i];
		}
		MultiSegmentationEvaluator eval =
		    new MultiSegmentationEvaluator (bTags, iTags, false);
		crf.train (trainingData, (InstanceList)null, (InstanceList)null, 
			   eval, 99999, 10, new double[] {.2, .5, .8});
	    } 

	    // otherwise, just get going...
	    else {
		crf.train (trainingData, (InstanceList)null, (InstanceList)null,
			   (MultiSegmentationEvaluator)null, 99999,
			   10, new double[] {.2, .5, .8});
	    }

	    // now save the model!
	    crf.write(new File(modelFile));

	} catch (Exception e) {
	    System.err.println(e);
	}

    }
}
