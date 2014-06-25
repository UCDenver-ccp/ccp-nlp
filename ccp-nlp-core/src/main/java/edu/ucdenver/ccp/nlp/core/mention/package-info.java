/**
 * <p>
 * The architecture of the mention (annotation type) structure was designed to be flexible in its
 * ability to represent virtually any frame-based class. The design mirrors the mention structure
 * used in Knowtator, an annotation tool developed within the Colorado Computational Pharmacology
 * group.
 *
 * In short, this structure is analogous to the classic frame/slot structure introduced by Minsky.
 * The class mention (or concept mention as it is described in the Knowtator documentation) can be
 * thought of as a frame. It represents the semantic type of an annotation. Examples of class
 * mentions include, but are not limited to, things such as entities (protein, cell type, disease,
 * etc.) or more complex relations (e.g. interaction, transport, regulation, etc.). A class mention
 * can have attributes. These attributes are represented as slot mentions (as a frame can have
 * slots). The current structure uses two types of slot mentions. Complex slot mentions are slots
 * that have other class mentions as their fillers, while primitive slot mentions are filled by
 * primitive objects, i.e. Strings, integers, booleans, etc.
 * 
 * <p>
 * To illustrate the mention structure described here, let us use as an example the protein
 * transport frame (shown below) and the example sentence:<br>
 * Src relocated the KDEL receptor from the Golgi apparatus to the endoplasmic reticulum. (PMID:
 * 12975382)
 * 
 * <pre>
 *   protein transport
 *   - transported entity: The protein being transported
 *   - transporter: The protein doing the transporting
 *   - source: The cellular component where the transport event begins
 *   - destination: The cellular component where the transport event ends
 * </pre>
 * 
 * <p>
 * The mention of protein transport in the example sentence above can be represented using the
 * following procedure:
 * <ol>
 * <li>Create an annotation for the text ``Src." Link this CCPTextAnnotation to a CCPClassMention of
 * type protein. Add to the CCPClassMention a CCPIntegerSlotMention of type Entrez gene ID with a
 * single slot value of ``6714."</li>
 * <li>Create an annotation for ``KDEL
 * receptor" with a protein class mention containing an Entrez gene ID slot filled with ``10945"</li>
 * <li>Create an annotation for ``Golgi apparatus" with a class mention of type Golgi Apparatus.</li>
 * <li>Create an annotation for ``endoplasmic reticulum" with a class mention of type Endoplasmic
 * Reticulum.</li>
 * <li>Create the protein transport annotation. This annotation will have four
 * CCPComplexSlotMentions, one for each slot: transported entity, transporter, source, and
 * destination. The fillers for the four complex slot mentions will be the class mentions created
 * earlier.</li>
 * </ol>
 * 
 * <p>
 * The generated protein transport class mention structure for this example is shown below.
 * 
 * <pre>
 *   class mention, name=&quot;protein transport&quot;
 *   - complex slot mention, name=&quot;transported entity&quot;
 *   - class mention, name=\textit{protein} ``KDEL receptor&quot;
 *   - integer slot mention, name=\textit{Entrez gene ID}, value=``6714&quot;
 *   - complex slot mention, name=\textit{transporter}
 *   - class mention, name=\textit{protein} ``Src&quot;
 *   - integer slot mention, name=\textit{Entrez gene ID}, value=``10945&quot;
 *   - complex slot mention, name=\textit{source}
 *   - class mention, name=\textit{Golgi Apparatus} ``Golgi apparatus&quot;
 *   - complex slot mention, name=\textit{destination}
 *   - class mention, name=\textit{Endoplasmic Reticulum} ``endoplasmic reticulum&quot;
 * </pre>
 * 
 * 
 */
package edu.ucdenver.ccp.nlp.core.mention;

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

