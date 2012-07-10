/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.inline;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;

/**
 * Base implementation for {@link InlineTagExtractor} instances. This abstract class provides logic
 * to generate the returned {@link Iterator<InlineTag>}. Extensions of this class must implement the
 * {@link InlineTagExtractor_ImplBase#getInlineTags(Annotation)} method.
 * <p>
 * The annotations used to generate {@link InlineTag} instances are chosen based on the specified
 * annotation type in the constructor.
 * 
 *@author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class InlineTagExtractor_ImplBase implements InlineTagExtractor {

	/**
	 * Specifies the type of annotation to process when extracting {@link InlineTag} instances
	 */
	private final int annotationType;

	/**
	 * To be used by extensions of this class to extracts type and span information (and possibly
	 * other things).
	 */
	private final AnnotationDataExtractor annotationDataExtractor;

	/**
	 * Initializes a new {@link InlineTagExtractor} instance to process annotations identified by
	 * the UIMA annotation type integer.
	 * 
	 * @param annotationType
	 *            Specifies the type of annotation to process when extracting {@link InlineTag}
	 *            instances
	 * @param annotationDataExtractor
	 *            To be used by extensions of this class to extracts type and span information (and
	 *            possibly other things).
	 */
	protected InlineTagExtractor_ImplBase(int annotationType, AnnotationDataExtractor annotationDataExtractor) {
		this.annotationType = annotationType;
		this.annotationDataExtractor = annotationDataExtractor;
	}

	/**
	 * @see edu.uchsc.ccp.uima.ae.util.printer.inline.InlineTagExtractor#getInlineTagIterator(org.apache.uima.jcas.JCas)
	 */
	@Override
	public Iterator<InlineTag> getInlineTagIterator(JCas view) {
		final FSIterator annotationIter = view.getJFSIndexRepository().getAnnotationIndex(annotationType).iterator();
		return new Iterator<InlineTag>() {

			private List<InlineTag> tagsToReturn;

			@Override
			public boolean hasNext() {
				if (tagsToReturn != null && tagsToReturn.size() > 0)
					return true;
				while (annotationIter.hasNext()) {
					Annotation annotation = (Annotation) annotationIter.next();
					tagsToReturn = getInlineTags(annotation);
					if (tagsToReturn.size() > 0)
						return true;
				}
				return false;
			}

			@Override
			public InlineTag next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return tagsToReturn.remove(0);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"This operation not supported for this Iterator implementation.");
			}

		};

	}

	/**
	 * @return the {@link AnnotationDataExtractor} instance being used by this
	 *         {@link InlineTagExtractor} implementation
	 */
	protected AnnotationDataExtractor getAnnotationDataExtractor() {
		return annotationDataExtractor;
	}

	/**
	 * To be implemented by extensions of this base class to provide full {@link InlineTagExtractor}
	 * functionality. This method should return a {@link List<InlineTag>} of {@link InlineTag}
	 * instances corresponding to the input {@link Annotation}.
	 * 
	 * @param annotation
	 *            the returned {@link InlineTag} instances correspond to the input
	 *            {@link Annotation}
	 * @return a {@link List<InlineTag>} containing the {@link InlineTag} instances that correspond
	 *         to the input annotation
	 */
	protected abstract List<InlineTag> getInlineTags(Annotation annotation);

}
