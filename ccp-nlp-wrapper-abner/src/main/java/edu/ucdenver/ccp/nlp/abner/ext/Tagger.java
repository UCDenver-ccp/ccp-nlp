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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.umass.cs.mallet.base.fst.CRF4;
import edu.umass.cs.mallet.base.pipe.Pipe;
import edu.umass.cs.mallet.base.pipe.iterator.LineGroupIterator;
import edu.umass.cs.mallet.base.types.Instance;
import edu.umass.cs.mallet.base.types.InstanceList;
import edu.umass.cs.mallet.base.types.Sequence;

/**
 * <p>
 * This is the interface to the CRF that does named entity tagging. It contains methods for taking
 * input text and returning tagged results in a variety of formats.
 * 
 * <p>
 * By default, the all methods in the Tagger class use ABNER's built-in tokenization. A single
 * newline, e.g. <tt>'\n'</tt>, is treated as a space, but two or more will conserve a paragraph
 * break. You may also disable it and use your own pre-tokenized text if you prefer, though tokens
 * must be whitespace-delimited, with newlines separating sentences.
 * 
 * @author Burr Settles <a
 *         href="http://www.cs.wisc.edu/~bsettles">bsettles&#64;&#99;s&#46;&#119;i&#115
 *         ;&#99;&#46;&#101;d&#117;</a>
 * @version 1.5 (March 2005)
 */
public class Tagger {
private static final Logger logger = Logger.getLogger(Tagger.class);
	// constants

	/** The tagger trained on the NLPBA corpus. */
	public static final int NLPBA = 0;
	/** The tagger trained on the BioCreative corpus. */
	public static final int BIOCREATIVE = 1;
	/** Indicates a tagger for some externally-trained model. */
	public static final int EXTERNAL = 2;

	// very important: the CRF itself and its feature pipes!!
	private CRF4 myCRF;
	private Pipe myPipe;
	private boolean doTokenization = true;
	private int myMode;

	// //////////////////////////////////////////////////////////////
	private void initialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// load the CRF into memory and get ready to go...
		myCRF = (CRF4) ois.readObject();
		myPipe = myCRF.getInputPipe();
	}

	/**
	 * Basic Constructor: Loads the "NLPBA" model by default.
	 */
	public Tagger() {
		this(NLPBA);
	}

	/**
	 * Advanced constructor: Specify either "NLPBA" or "BioCreative" model.
	 */
	public Tagger(int mode) {
		myMode = mode;
		URL model = null;// Tagger.class.getResource("resources/nlpba.crf");
		if (mode == BIOCREATIVE) {
			System.err.println("Loading BioCreative tagging module...");
			model = Tagger.class.getResource("resources/biocreative.crf");
		} else {
			System.err.println("Loading default NLPBA tagging module...");
			model = Tagger.class.getResource("resources/nlpba.crf");
		}
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(model.openStream());
			initialize(ois);
			ois.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * External constructor: Load a trained CRF specified by the external model file.
	 */
	public Tagger(File f) {
		try {
			System.err.println("Loading external tagging module from '" + f.getPath() + "'...");
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			initialize(ois);
			ois.close();
			myMode = EXTERNAL;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	// //////////////////////////////////////////////////////////////
	/**
	 * <p>
	 * Turn on/off ABNER's built-in tokenization (default is <tt>true</tt>).
	 */
	public void setTokenization(boolean t) {
		doTokenization = t;
	}

	/**
	 * <p>
	 * Return the tagger's current tokenization setting.
	 */
	public boolean getTokenization() {
		return doTokenization;
	}

	/**
	 * <p>
	 * Return the tagger's mode (NLPBA, BIOCREATIVE, or EXTERNAL)
	 */
	public int getMode() {
		return myMode;
	}

	// //////////////////////////////////////////////////////////////
	/**
	 * <p>
	 * Take raw text apply ABNER's built-in tokenization on it.
	 */
	public String tokenize(String s) {
		StringBuffer sb = new StringBuffer();
		Scanner scanner = new Scanner(new StringReader(s));
		// Scanner scanner = new Scanner(StreamUtil.getEncodingSafeInputStream(new
		// ByteArrayInputStream(s.getBytes()), CharacterEncoding.UTF_8));
		String t;
		try {
			while ((t = scanner.nextToken()) != null) {
				sb.append(t + " ");
				if (t.toString().matches("[?!\\.]"))
					sb.append("\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// System.out.println("Tokenize returning: " + sb.toString());
		return sb.toString();
	}

	// //////////////////////////////////////////////////////////////
	/**
	 * <p>
	 * Take an input string (if tokenization is turned on, this string will be tokenized as well)
	 * and return a {@link Vector} of 2D {@link String} arrays, where sentence tokens are
	 * <i>words</i> stored in <tt>result[0][...]</tt> and tags are stored in <tt>result[1][...]</tt>.
	 */
	public Vector getWords(String text) {
		Vector myList = new Vector();
		Vector tagged = doTheTagging(text);
		for (int i = 0; i < tagged.size(); i++) {
			myList.add((String[][]) tagged.get(i));
		}
		return myList;
	}

	// //////////////////////////////////////////////////////////////
	/**
	 * <p>
	 * Take an input string (if tokenization is turned on, this string will be tokenized as well)
	 * and return a {@link Vector} of 2D {@link String} arrays, where sentence tokens are
	 * <i>segments</i> (not individual words). In other words, words comprising protein names are
	 * grouped together, words in non-entity phrases are grouped together, as well, etc.
	 */
	public Vector getSegments(String text) {
		Vector myList = new Vector();
		Vector tagged = doTheTagging(text);
		// cycle through all the sentences
		for (int i = 0; i < tagged.size(); i++) {
			String sent[][] = (String[][]) tagged.get(i);
			// we need to be sure that this isn't a blank line.
			if (sent[0].length > 0) {
				Vector tmpSegs = new Vector();
				Vector tmpTags = new Vector();
				// cycle through words and build the segments
				StringBuffer tmpSeg = new StringBuffer(sent[0][0]);
				String tmpTag = sent[1][0].replaceAll("[BI]-", "");
				for (int j = 1; j < sent[0].length; j++) {
					// if we're starting a new segment, store the
					// seg-in-progress and start the new one...
					if (!sent[1][j].replaceAll("[BI]-", "").equals(tmpTag)) {
						tmpSegs.add(tmpSeg.toString());
						tmpTags.add(tmpTag);
						tmpSeg = new StringBuffer(sent[0][j]);
						tmpTag = sent[1][j].replaceAll("[BI]-", "");
					}
					// if same segment, just tack on this word...
					else
						tmpSeg.append(" " + sent[0][j]);
				}
				tmpSegs.add(tmpSeg.toString());
				tmpTags.add(tmpTag);
				// done. load it up!
				String[][] val = new String[2][tmpSegs.size()];
				for (int j = 0; j < val[0].length; j++) {
					val[0][j] = (String) tmpSegs.get(j);
					val[1][j] = (String) tmpTags.get(j);
				}
				myList.add(val);
			}
			// if it's a blank line... move along...
			else {
				myList.add(new String[2][0]);
			}
		}
		return myList;
	}

	// //////////////////////////////////////////////////////////////
	/**
	 * <p>
	 * Similar to getSegments, but returns all segments in the entire document that correspond to
	 * entities (e.g. "DNA," "protein," etc.). Segment <i>text</i> is stored in
	 * <tt>result[0][...]</tt> and entity tags (minus "B-" and "I-" prefixes) are stored in
	 * <tt>result[1][...]</tt>.
	 */
	public String[][] getEntities(String text) {
		String[][] result;
		Vector tmpSegs = new Vector();
		Vector tmpTags = new Vector();
		Vector tagged;
		try {
			 tagged = doTheTagging(text);
		} catch (IndexOutOfBoundsException e) {
			logger.warn("ABNER failed on text: " + text);
			tagged = new Vector();
			/*
			 * @formatter:off Caused by: java.lang.ArrayIndexOutOfBoundsException: 4 at
			 * edu.ucdenver.ccp.nlp.abner.ext.Tagger.doTheTagging(Tagger.java:464) at
			 * edu.ucdenver.ccp.nlp.abner.ext.Tagger.getEntities(Tagger.java:254) at
			 * edu.ucdenver.ccp.nlp.wrapper.abner.Abner_Util.getEntities(Abner_Util.java:225) at
			 * edu.
			 * ucdenver.ccp.nlp.wrapper.abner.Abner_Util.getEntitiesFromText(Abner_Util.java:370) at
			 * edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.EntityTagger_AE.process(
			 * EntityTagger_AE.java:77) at
			 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase.
			 * process(JCasAnnotator_ImplBase.java:48) at
			 * org.apache.uima.analysis_engine.impl.PrimitiveAnalysisEngine_impl
			 * .callAnalysisComponentProcess(PrimitiveAn... @formatter:on
			 */
		}
		// cycle through all the sentences
		for (int i = 0; i < tagged.size(); i++) {
			String sent[][] = (String[][]) tagged.get(i);
			// we need to be sure that this isn't a blank line.
			if (sent[0].length > 0) {
				// cycle through words and build the segments
				StringBuffer tmpSeg = new StringBuffer(sent[0][0]);
				String tmpTag = sent[1][0].replaceAll("[BI]-", "");
				for (int j = 1; j < sent[0].length; j++) {
					// if we're starting a new segment, store the
					// seg-in-progress and start the new one...
					if (!sent[1][j].replaceAll("[BI]-", "").equals(tmpTag)) { //
						if (!tmpTag.equals("O")) {
							tmpSegs.add(tmpSeg.toString());
							tmpTags.add(tmpTag);
						}
						tmpSeg = new StringBuffer(sent[0][j]);
						tmpTag = sent[1][j].replaceAll("[BI]-", "");
					}
					// if same segment, just tack on this word...
					else
						tmpSeg.append(" " + sent[0][j]);
				}
				if (!tmpTag.equals("O")) {
					tmpSegs.add(tmpSeg.toString());
					tmpTags.add(tmpTag);
				}
			}
		}
		// done. load it up!
		result = new String[2][tmpSegs.size()];
		for (int j = 0; j < result[0].length; j++) {
			result[0][j] = (String) tmpSegs.get(j);
			result[1][j] = (String) tmpTags.get(j);
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////
	/**
	 * <p>
	 * Returns only segments corresponding to the entity provided in the <tt>tag</tt> argument (do
	 * not us "B-" or "I-" prefixes).
	 */
	public String[] getEntities(String text, String tag) {
		String[] result;
		Vector tmpSegs = new Vector();
		Vector tmpTags = new Vector();
		Vector tagged = doTheTagging(text);
		// cycle through all the sentences
		for (int i = 0; i < tagged.size(); i++) {
			String sent[][] = (String[][]) tagged.get(i);
			// we need to be sure that this isn't a blank line.
			if (sent[0].length > 0) {
				// cycle through words and build the segments
				StringBuffer tmpSeg = new StringBuffer(sent[0][0]);
				String tmpTag = sent[1][0].replaceAll("[BI]-", "");
				for (int j = 1; j < sent[0].length; j++) {
					// if we're starting a new segment, store the
					// seg-in-progress and start the new one...
					if (!sent[1][j].replaceAll("[BI]-", "").equals(tmpTag)) {
						if (tmpTag.equals(tag)) {
							tmpSegs.add(tmpSeg.toString());
							tmpTags.add(tmpTag);
						}
						tmpSeg = new StringBuffer(sent[0][j]);
						tmpTag = sent[1][j].replaceAll("[BI]-", "");
					}
					// if same segment, just tack on this word...
					else
						tmpSeg.append(" " + sent[0][j]);
				}
				if (tmpTag.equals(tag)) {
					tmpSegs.add(tmpSeg.toString());
					tmpTags.add(tmpTag);
				}
			}
		}
		// done. load it up!
		result = new String[tmpSegs.size()];
		for (int j = 0; j < result.length; j++) {
			result[j] = (String) tmpSegs.get(j);
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////
	/**
	 * <p>
	 * Takes input text and returns a string of annotated text in the ABNER training format:
	 * 
	 * <pre>
	 *        IL-2|B-DNA  gene|I-DNA  expression|O  and|O  NF-kappa|B-PROTEIN  B|I-PROTEIN  activation|O  ...
	 * </pre>
	 * 
	 * Words and tags are "|" (vertical pipe) delimited, and sentences are separated with newlines.
	 */
	public String tagABNER(String text) {
		StringBuffer tmp = new StringBuffer();
		// first, do the annotations
		Vector tagged = doTheTagging(text);
		for (int i = 0; i < tagged.size(); i++) {
			String sent[][] = (String[][]) tagged.get(i);
			for (int j = 0; j < sent[0].length; j++) {
				tmp.append(sent[0][j] + "|");
				tmp.append(sent[1][j] + "  ");
			}
			if (sent[0].length > 0)
				tmp.append("\n");
		}
		return tmp.toString();
	}

	// //////////////////////////////////////////////////////////////
	/**
	 * <p>
	 * Takes input text and returns a string of annotated text in CoNLL-style "IOB" format:
	 * 
	 * <pre>
	 *        IL-2    B-DNA
	 *        gene    I-DNA
	 *        expression      O
	 *        and     O
	 *        NF-kappa        B-PROTEIN
	 *        B       I-PROTEIN
	 *        activation      O
	 *        ...
	 * </pre>
	 * 
	 * Words and tags are tab-delimited, and sentences are separated by blank lines.
	 */
	public String tagIOB(String text) {
		StringBuffer tmp = new StringBuffer();
		// first, do the annotations
		Vector tagged = doTheTagging(text);
		String tag = "";
		for (int i = 0; i < tagged.size(); i++) {
			String sent[][] = (String[][]) tagged.get(i);
			for (int j = 0; j < sent[0].length; j++) {
				tmp.append(sent[0][j] + "\t");
				tmp.append(sent[1][j] + "\n");
			}
			if (sent[0].length > 0)
				tmp.append("\n");
		}
		return tmp.toString();
	}

	// //////////////////////////////////////////////////////////////
	/**
	 * <p>
	 * Takes input text and returns a string of annotated text in a generic SGML-style format:
	 * 
	 * <pre>
	 *        &lt;DNA&gt; IL-2 gene &lt;/DNA&gt; expression and &lt;PROTEIN&gt; NF-kappa B &lt;/PROTEIN&gt; activation...
	 * </pre>
	 * 
	 * Words remain tokenized, and sentences are separated by newlines.
	 */
	public String tagSGML(String text) {
		StringBuffer tmp = new StringBuffer();
		Vector segs = getSegments(text);
		for (int i = 0; i < segs.size(); i++) {
			// Sentence s = (Sentence)segs.get(i);
			String[][] s = (String[][]) segs.get(i);
			for (int j = 0; j < s[0].length; j++) {
				if (s[1][j].equals("O"))
					tmp.append(s[0][j] + " ");
				else
					tmp.append("<" + s[1][j] + "> " + s[0][j] + " </" + s[1][j] + "> ");
			}
			tmp.append("\n");
		}
		return tmp.toString();
	}

	// //////////////////////////////////////////////////////////////
	// THIS FUNCTION ACTUALLY DOES THE TAGGING ITSELF
	private Vector doTheTagging(String text) {
		Vector result = new Vector();
		// try {
		// define the instance feature pipe...
		InstanceList data = new InstanceList(myPipe);

		// System.out.println("before tok");
		// tokenize if appropriate, otherwise don't...
		if (doTokenization) {
			data.add(new LineGroupIterator(new StringReader(tokenize(text)), Pattern.compile("^.*$"), false));
		} else {
			data.add(new LineGroupIterator(new StringReader(text), Pattern.compile("^.*$"), false));
		}

		// System.out.println("done tok");

		// cycle through sentences, tag each one, store up the
		for (int i = 0; i < data.size(); i++) {
			// nab the sentence, set up the input, and
			Instance instance = data.getInstance(i);
			Sequence input = (Sequence) instance.getData();

			// get the predicted labeling...
			Sequence predOutput = myCRF.viterbiPath(input).output();
			assert (input.size() == predOutput.size());

			String[][] tokens = new String[2][];
			tokens[0] = ((String) instance.getSource().toString()).split("[ \t]+");
			tokens[1] = new String[tokens[0].length];
			if (tokens[0].length > 0) {
				for (int j = 0; j < predOutput.size(); j++)
					tokens[1][j] = predOutput.get(j).toString();
			}
			result.add(tokens);
		}
		// done return the results.
		return result;
	}

}
