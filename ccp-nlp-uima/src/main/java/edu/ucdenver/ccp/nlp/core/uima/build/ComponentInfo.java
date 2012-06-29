/**
 * 
 */
package edu.ucdenver.ccp.nlp.core.uima.build;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation details some meta information pertaining to the particular UIMA component class
 * that is annotated. This information will be extracted and used to populate fields in
 * auto-generated UIMA XML descriptor files.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentInfo {
	/**
	 * @return the vendor for the UIMA component annotated by this annotation
	 */
	String vendor();

	/**
	 * @return the description of the UIMA component annotated by this annotation
	 */
	String description();

	/**
	 * 
	 * @return a dotted reference to the type system used by this component, e.g.
	 *         "edu.ucdenver.ccp.nlp.core.uima.TypeSystem". An empty {@link String} indicates a
	 *         type-system-independent component.
	 */
	String typeSystem() default "";

}
