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

/*
 * #%L
 * Colorado Computational Pharmacology's common module
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
