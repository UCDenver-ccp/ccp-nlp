/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.lingpipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.exception.InitializationException;
import edu.ucdenver.ccp.nlp.core.interfaces.ISentenceDetector;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class LingPipeSentenceDetector implements ISentenceDetector {

	private TokenizerFactory TOKENIZER_FACTORY = new IndoEuropeanTokenizerFactory();

	private final SentenceModel SENTENCE_MODEL = new MedlineSentenceModel();

	private final SentenceChunker SENTENCE_CHUNKER = new SentenceChunker(TOKENIZER_FACTORY, SENTENCE_MODEL);

	private final Annotator annotator = new Annotator(new Integer(22), "", "LingPipe", "Alias-i");

	private final AnnotationSet annotationSet = new AnnotationSet();

	public List<TextAnnotation> getSentencesFromText(String inputText, String documentID) {
		return findSentences(0, inputText, documentID);
	}

	@Override
	public List<TextAnnotation> getSentencesFromText(String inputText) {
		return findSentences(0, inputText, "-1");
	}

	@Override
	public List<TextAnnotation> getSentencesFromText(int charOffset, String inputText) {
		return findSentences(charOffset, inputText, "-1");
	}

	/**
	 * Use SentenceModel to find sentence boundaries in text LingPipe relevant code used by this
	 * method was taken from:
	 * http://alias-i.com/lingpipe/demos/tutorial/sentences/src/SentenceChunkerDemo.java
	 * 
	 * @param inputText
	 */
	private List<TextAnnotation> findSentences(int charOffset, String inputText, String documentID) {
		/* this method will return a list of TextAnnotations; one per sentence */
		List<TextAnnotation> annotations = new ArrayList<TextAnnotation>();

		Chunking chunking = SENTENCE_CHUNKER.chunk(inputText.toCharArray(), 0, inputText.length());
		Set sentences = chunking.chunkSet();
		if (sentences.size() < 1) {
			if (inputText.trim().length() > 0) {
				System.out.println("WARNING -- LingPipe_Util: No sentence chunks found for input text: \"" + inputText
						+ "\"");
			}
			return annotations;
		}
		for (Iterator it = sentences.iterator(); it.hasNext();) {
			Chunk sentence = (Chunk) it.next();
			int start = sentence.start();
			int end = sentence.end();

			TextAnnotation ta = new DefaultTextAnnotation(start + charOffset, end + charOffset);
			ta.setAnnotator(annotator);
			ta.setDocumentID(documentID);
			ta.setCoveredText(inputText.substring(start, end));

			ta.addAnnotationSet(annotationSet);

			ClassMention cm = new DefaultClassMention(ClassMentionTypes.SENTENCE);
			try {
				ta.setClassMention(cm);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			annotations.add(ta);
		}

		/* if the sentence has a line break in it, then split the sentence on the line break */
		List<TextAnnotation> annotationsToRemove = new ArrayList<TextAnnotation>();
		List<TextAnnotation> annotationsToAdd = new ArrayList<TextAnnotation>();
		for (TextAnnotation sentenceAnnot : annotations) {
			if (sentenceAnnot.getCoveredText().contains("\n")) {
				String[] splitSentence = sentenceAnnot.getCoveredText().split("\\n");
				int spanStart = sentenceAnnot.getAnnotationSpanStart();
				for (String sent : splitSentence) {

					TextAnnotation ta = new DefaultTextAnnotation(spanStart, spanStart + sent.length());
					ta.setAnnotator(annotator);
					ta.setDocumentID(documentID);
					ta.setCoveredText(sent);
					// ta.setAnnotationSpanEnd(spanStart + sent.length());
					// ta.setAnnotationSpanStart(spanStart);
					ta.addAnnotationSet(annotationSet);
					ClassMention cm = new DefaultClassMention(ClassMentionTypes.SENTENCE);
					try {
						ta.setClassMention(cm);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					spanStart = spanStart + sent.length() + 1;

					annotationsToAdd.add(ta);
				}
				/* remove the original that has now been split */
				annotationsToRemove.add(sentenceAnnot);
			}

		}

		for (TextAnnotation ta : annotationsToRemove) {
			annotations.remove(ta);
		}

		for (TextAnnotation ta : annotationsToAdd) {
			annotations.add(ta);
		}

		return annotations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.interfaces.ITagger#initialize(int, java.lang.String[])
	 */
	@Override
	public void initialize(int taggerType, String[] args) throws InitializationException {
		// TODO Auto-generated method stub

	}

}
