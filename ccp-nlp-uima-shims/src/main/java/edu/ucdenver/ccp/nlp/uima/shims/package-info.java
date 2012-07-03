/**
 * Contains interfaces and base classes for facilitating development of type-system-independent UIMA components. 
 * There are currently two general types of interfaces, one useful for extracting meta information from the {@link JCas} 
 * and the other type useful for extracting information from {@link Annotation} instances.
 * <p>
 * About the package name: A shim is a thin often tapered piece of material (as wood, metal, or stone) used to fill in 
 * space between things (as for support, leveling, or adjustment of fit) [http://www.merriam-webster.com/dictionary/shim]. 
 * Implementations of the interfaces and base classes in this package are meant to fit UIMA components to type systems in a 
 * simple, relatively easy to construct manner.  
 * <p>
 * Components that utilize these interfaces can remain largely ignorant of the UIMA type system(s) involved. For each type 
 * system, a shim is implemented, e.g. {@link CcpDocumentMetaDataExtractor}, to link components to that particular type system. 
 * It is recommended that UIMA components that use these shim interfaces allow the specific shim implementation to be specified 
 * as a runtime component parameter and then dynamically construct a new instance (@see InlinePrinter). For this reason, it is 
 * also recommended (and in some cases required) for shim implementations to use a default (or no-argument) constructor. 
 * 
 */
package edu.ucdenver.ccp.nlp.uima.shims;

