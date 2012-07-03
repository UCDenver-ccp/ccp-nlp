/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.shims.annotation;

import java.net.URI;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface AnnotationCreatorExtractor {

	public Annotator getAnnotator(Annotation annotation);

	public static class Annotator {
		private final URI annotatorUri;
		private final String version;
		private final String description;

		/**
		 * @param annotatorUri 
		 * @param version
		 * @param description
		 */
		public Annotator(URI annotatorUri, String version, String description) {
			super();
			this.annotatorUri = annotatorUri;
			this.version = version;
			this.description = description;
		}

		/**
		 * @param annotatorUri 
		 * @param version
		 */
		public Annotator(URI annotatorUri, String version) {
			this(annotatorUri, version, "");
		}

		/**
		 * @return the annotatorUri
		 */
		public URI getAnnotatorUri() {
			return annotatorUri;
		}

		/**
		 * @return the version
		 */
		public String getVersion() {
			return version;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

	}

}
