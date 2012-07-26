/**
 * Medline XML format comes in two flavors depending on the source. Files downloaded from NLM as part of the Medline lease use the following preamble: 
 * <pre>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <!DOCTYPE MedlineCitationSet PUBLIC "-//NLM//DTD Medline Citation, 1st January, 2011//EN"
 *                                    "http://www.nlm.nih.gov/databases/dtd/nlmmedlinecitationset_110101.dtd">
 * <MedlineCitationSet>
 * <MedlineCitation Owner="NLM" Status="MEDLINE"> 
 * </pre>
 * 
 * while files downloaded from a PubMed search in the XML format use:
 * <pre>
 * <!DOCTYPE PubmedArticleSet PUBLIC "-//NLM//DTD PubMedArticle, 1st January 2011//EN" "http://www.ncbi.nlm.nih.gov/corehtml/query/DTD/pubmed_110101.dtd">
 * <PubmedArticleSet>
 * 
 * <PubmedArticle>
 * <MedlineCitation Owner="NLM" Status="In-Process">
 * </pre>
 * 
 * If dealing with the "medline" format - use {@link edu.ucdenver.ccp.nlp.ext.uima.collections.file.medline.MedlineXmlFileCollectionReader} 
 * If dealing with the "pubmed" format - use {@link edu.ucdenver.ccp.nlp.ext.uima.collections.file.medline.PubmedXmlFileCollectionReader} 
 */
package edu.ucdenver.ccp.nlp.uima.collections.file.medline;