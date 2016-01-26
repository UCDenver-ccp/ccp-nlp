package edu.ucdenver.ccp.nlp.core.annotation;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.Assert;

import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class TestTextAnnotationCreator {

	/**
	 * This method produces a list of TextAnnotation objects to be used for testing code that
	 * processes TextAnnotations. A total of six annotations are created.
	 * 
	 * <pre>
	 *          ======================= Annotation: -11 =======================
	 *              Annotator: -30|Test Annotator|#1|CCP
	 *              --- AnnotationSets: -20|Test Set #1|This is a test annnotation set.
	 *              --- Span: 50 - 53  
	 *              --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *              --- Covered Text: nucl
	 *              -CLASS MENTION: nucleus &quot;nucl&quot;
	 *              =================================================================================
	 *              ======================= Annotation: -10 =======================
	 *              Annotator: -30|Test Annotator|#1|CCP
	 *              --- AnnotationSets: -21|Test Set #2|This is another test annotation set.
	 *              -20|Test Set #1|This is a test annnotation set.
	 *              --- Span: 
	 *              --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *              --- Covered Text: gated..transport
	 *              -CLASS MENTION: gated nuclear transport &quot;gated..transport&quot;
	 *              -    COMPLEX SLOT MENTION: transport origin
	 *              -        CLASS MENTION: nucleus &quot;nucl&quot;
	 *              -    COMPLEX SLOT MENTION: transport location
	 *              -        CLASS MENTION: nucleus &quot;nucl&quot;
	 *              -    COMPLEX SLOT MENTION: transport participants
	 *              -        CLASS MENTION: protein &quot;E2F-4&quot;
	 *              -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999  
	 *              -    COMPLEX SLOT MENTION: transported entities
	 *              -        CLASS MENTION: protein &quot;E2F-4&quot;
	 *              -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999  
	 *              =================================================================================
	 *              ======================= Annotation: -12 =======================
	 *              Annotator: -31|Test Annotator|#2|CCP
	 *              --- AnnotationSets: -20|Test Set #1|This is a test annnotation set.
	 *              --- Span: 65 - 70  
	 *              --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *              --- Covered Text: E2F-4
	 *              -CLASS MENTION: protein &quot;E2F-4&quot;
	 *              -    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999  
	 *              =================================================================================
	 *              ======================= Annotation: -17 =======================
	 *              Annotator: -30|Test Annotator|#1|CCP
	 *              --- AnnotationSets: -20|Test Set #1|This is a test annnotation set.
	 *              --- Span: 111 - 120  
	 *              --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *              --- Covered Text: HER-23
	 *              -CLASS MENTION: protein &quot;HER-23&quot;
	 *              -    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 13579  
	 *              =================================================================================
	 *              ======================= Annotation: -16 =======================
	 *              Annotator: -30|Test Annotator|#1|CCP
	 *              --- AnnotationSets: -20|Test Set #1|This is a test annnotation set.
	 *              --- Span: 0 - 10  
	 *              --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *              --- Covered Text: activation
	 *              -CLASS MENTION: activation &quot;activation&quot;
	 *              -    COMPLEX SLOT MENTION: activated process
	 *              -        CLASS MENTION: gated nuclear transport &quot;gated..transport&quot;
	 *              -            COMPLEX SLOT MENTION: transport origin
	 *              -                CLASS MENTION: nucleus &quot;nucl&quot;
	 *              -            COMPLEX SLOT MENTION: transport location
	 *              -                CLASS MENTION: nucleus &quot;nucl&quot;
	 *              -            COMPLEX SLOT MENTION: transport participants
	 *              -                CLASS MENTION: protein &quot;E2F-4&quot;
	 *              -                    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999  
	 *              -            COMPLEX SLOT MENTION: transported entities
	 *              -                CLASS MENTION: protein &quot;E2F-4&quot;
	 *              -                    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999  
	 *              -    COMPLEX SLOT MENTION: activating entity
	 *              -        CLASS MENTION: protein &quot;HER-23&quot;
	 *              -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 13579  
	 *              =================================================================================
	 *              ======================= Annotation: -19 =======================
	 *              Annotator: -30|Test Annotator|#1|CCP
	 *              --- AnnotationSets: -20|Test Set #1|This is a test annnotation set.
	 *              --- Span: 134 - 156  
	 *              --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *              --- Covered Text: protein kinase C
	 *              -CLASS MENTION: protein &quot;protein kinase C&quot;
	 *              -    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 975  
	 *              =================================================================================
	 *              ======================= Annotation: -18 =======================
	 *              Annotator: -30|Test Annotator|#1|CCP
	 *              --- AnnotationSets: -20|Test Set #1|This is a test annnotation set.
	 *              --- Span: 0 - 10  
	 *              --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *              --- Covered Text: activation
	 *              -CLASS MENTION: regulation &quot;activation&quot;
	 *              -    COMPLEX SLOT MENTION: regulated process
	 *              -        CLASS MENTION: activation &quot;activation&quot;
	 *              -            COMPLEX SLOT MENTION: activated process
	 *              -                CLASS MENTION: gated nuclear transport &quot;gated..transport&quot;
	 *              -                    COMPLEX SLOT MENTION: transport origin
	 *              -                        CLASS MENTION: nucleus &quot;nucl&quot;
	 *              -                    COMPLEX SLOT MENTION: transport location
	 *              -                        CLASS MENTION: nucleus &quot;nucl&quot;
	 *              -                    COMPLEX SLOT MENTION: transport participants
	 *              -                        CLASS MENTION: protein &quot;E2F-4&quot;
	 *              -                            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999  
	 *              -                    COMPLEX SLOT MENTION: transported entities
	 *              -                        CLASS MENTION: protein &quot;E2F-4&quot;
	 *              -                            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999  
	 *              -            COMPLEX SLOT MENTION: activating entity
	 *              -                CLASS MENTION: protein &quot;HER-23&quot;
	 *              -                    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 13579  
	 *              -    COMPLEX SLOT MENTION: regulating entity
	 *              -        CLASS MENTION: protein &quot;protein kinase C&quot;
	 *              -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 975  
	 *              =================================================================================
	 *              ======================= Annotation: -13 =======================
	 *              Annotator: -31|Test Annotator|#2|CCP
	 *              --- AnnotationSets: -21|Test Set #2|This is another test annotation set.
	 *              --- Span: 
	 *              --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *              --- Covered Text: EBV LMP1
	 *              -CLASS MENTION: protein &quot;EBV LMP1&quot;
	 *              -    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 888888  
	 *              =================================================================================
	 *              ======================= Annotation: -14 =======================
	 *              Annotator: -31|Test Annotator|#2|CCP
	 *              --- AnnotationSets: -21|Test Set #2|This is another test annotation set.
	 *              --- Span: 
	 *              --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *              --- Covered Text: p16INK4
	 *              -CLASS MENTION: protein &quot;p16INK4&quot;
	 *              -    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 7777  
	 *              -    SLOT MENTION: processed text with SLOT VALUE(s):   
	 *              =================================================================================
	 *              ======================= Annotation: -15 =======================
	 *              Annotator: -31|Test Annotator|#2|CCP
	 *              --- AnnotationSets: -21|Test Set #2|This is another test annotation set.
	 *              --- Span: 
	 *              --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *              --- Covered Text: E2f-4
	 *              -CLASS MENTION: protein &quot;E2f-4&quot;
	 *              -    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 22222  
	 *              -    SLOT MENTION: processed text with SLOT VALUE(s):   
	 *              =================================================================================
	 * 
	 * </pre>
	 * 
	 * @return
	 */

	public static List<Annotator> getTestAnnotators() {
		Annotator annotator1 = new Annotator(new Integer(-30), "Test Annotator", "#1", "CCP");
		Annotator annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");

		List<Annotator> testAnnotators = new ArrayList<Annotator>();
		testAnnotators.add(annotator1);
		testAnnotators.add(annotator2);

		return testAnnotators;
	}

	public static List<AnnotationSet> getTestAnnotationSets() {
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1",
				"This is a test annnotation set.");
		AnnotationSet annotationSet2 = new AnnotationSet(new Integer(-21), "Test Set #2",
				"This is another test annotation set.");

		List<AnnotationSet> testAnnotationSets = new ArrayList<AnnotationSet>();
		testAnnotationSets.add(annotationSet1);
		testAnnotationSets.add(annotationSet2);

		return testAnnotationSets;
	}

	public static List<TextAnnotation> getTestTextAnnotations() throws Exception {
		List<TextAnnotation> returnArray = new ArrayList<TextAnnotation>();

		/* Initialize test annotators */
		Annotator annotator1 = new Annotator(new Integer(-30), "Test Annotator", "#1", "CCP");
		Annotator annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");

		/* Initialize test annotation sets */
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1",
				"This is a test annnotation set.");
		AnnotationSet annotationSet2 = new AnnotationSet(new Integer(-21), "Test Set #2",
				"This is another test annotation set.");

		/* This annotation has a split span: 45-49 and 53-61 */
		ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span> spanList = new ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span>();
		edu.ucdenver.ccp.nlp.core.annotation.Span span1;
		edu.ucdenver.ccp.nlp.core.annotation.Span span2;
		try {
			span1 = new edu.ucdenver.ccp.nlp.core.annotation.Span(45, 49);
			span2 = new edu.ucdenver.ccp.nlp.core.annotation.Span(53, 61);
			spanList.add(span1);
			spanList.add(span2);
		} catch (edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException e) {
			e.printStackTrace();
		}

		/* Create the "gated nuclear transport" annotation */
		TextAnnotation ta = new DefaultTextAnnotation(spanList);

		/* annotationID = -10 */
		ta.setAnnotationID(-10);

		/* coverted text = gated (45-49), transport (53-61) */
		ta.setCoveredText("gated..transport");

		/* this annotation belongs to two annotation sets */
		Set<AnnotationSet> annotationSetList = new HashSet<AnnotationSet>();
		annotationSetList.add(annotationSet1);
		annotationSetList.add(annotationSet2);
		ta.setAnnotationSets(annotationSetList);

		/* this annotation was created by annotator1 */
		ta.setAnnotator(annotator1);

		/* the documentcollection = -1 */
		ta.setDocumentCollectionID(-1);
		ta.setDocumentID("1234");
		ta.setDocumentSectionID(0);

		/* Initialize the transport mention */
		ClassMention cm = new DefaultClassMention("gated nuclear transport");

		ta.setClassMention(cm);

		/* The transport mention will have 4 filled slots */
		/* transport origin */
		/* transport location */
		/* transport participants */
		/* transported entities */

		/* ----- transport origin ----- */
		ComplexSlotMention transportOriginMention = new DefaultComplexSlotMention("transport origin");

		/* create a "nucleus" annotation to fill the transport origin slot */
		/* first create the nucleus class mention */
		ClassMention nucleusMention = new DefaultClassMention("nucleus");

		/* create the nucleus annotation */
		TextAnnotation nucleusAnnotation = new DefaultTextAnnotation(50, 53, "nucl", annotator1, annotationSet1, -11,
				-1, "1234", 0, nucleusMention);

		/* add the nucleus mention as the slot filler for the transport origin */
		transportOriginMention.addClassMention(nucleusMention);

		/* add the transport origin slot to the transport mention */
		cm.addComplexSlotMention(transportOriginMention);

		/* ----- transport location ----- */
		ComplexSlotMention transportLocationMention = new DefaultComplexSlotMention("transport location");

		/* add the already created nucleus mention as the slot filler for the transport location */
		transportLocationMention.addClassMention(nucleusMention);

		/* add the transport location slot to the transport mention */
		cm.addComplexSlotMention(transportLocationMention);

		/* ----- transport participants ----- */
		ComplexSlotMention transportParticipantsMention = new DefaultComplexSlotMention("transport participants");

		/* create a protein mention and annotation to fill the transport participants slot */
		ClassMention e2f4ProteinMention = new DefaultClassMention("protein");

		/* create a slot for the Entrez ID for this protein mention */
		IntegerSlotMention entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(9999999);
		e2f4ProteinMention.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		TextAnnotation e2f4Annotation = new DefaultTextAnnotation(65, 70, "E2F-4", annotator2, annotationSet1, -12, -1,
				"1234", 0, e2f4ProteinMention);

		/* fill the transport participants slot with the e2f4 protein mention */
		transportParticipantsMention.addClassMention(e2f4ProteinMention);

		/* add the transport participants slot to the transport mention */
		cm.addComplexSlotMention(transportParticipantsMention);

		/* ----- transported entities ----- */
		ComplexSlotMention transportedEntitiesMention = new DefaultComplexSlotMention("transported entities");

		/* add the already created e2f4 protein mention to the transported entities slot */
		transportedEntitiesMention.addClassMention(e2f4ProteinMention);

		/* add the transported entities slot to the transport mention */
		cm.addComplexSlotMention(transportedEntitiesMention);

		/*
		 * Add the nucleus annotation, transport annotation, and e2f4 protein annotation to the
		 * return array
		 */
		returnArray.add(nucleusAnnotation);
		returnArray.add(ta);
		returnArray.add(e2f4Annotation);

		/* create a activation of transport annotation */
		ClassMention activationMention = new DefaultClassMention("activation");
		ComplexSlotMention activatedProcess = new DefaultComplexSlotMention("activated process");
		activatedProcess.addClassMention(cm);
		activationMention.addComplexSlotMention(activatedProcess);

		/* create a protein responsible for the activation */
		ClassMention activatingProteinMention = new DefaultClassMention("protein");
		IntegerSlotMention activatingEntrezID = new DefaultIntegerSlotMention("entrez_gene_id");
		activatingEntrezID.addSlotValue(13579);
		activatingProteinMention.addPrimitiveSlotMention(activatingEntrezID);
		ComplexSlotMention activatingEntity = new DefaultComplexSlotMention("activating entity");
		activatingEntity.addClassMention(activatingProteinMention);
		activationMention.addComplexSlotMention(activatingEntity);
		TextAnnotation activatingProteinAnnotation = new DefaultTextAnnotation(111, 120, "HER-23", annotator1,
				annotationSet1, -17, -1, "1234", 0, activatingProteinMention);
		returnArray.add(activatingProteinAnnotation);

		TextAnnotation activationOfTransport = new DefaultTextAnnotation(0, 10, "activation", annotator1,
				annotationSet1, -16, -1, "1234", 0, activationMention);
		returnArray.add(activationOfTransport);

		/* create a regulation of activation of transport annotation */
		ClassMention regulationOfActivationMention = new DefaultClassMention("regulation");
		ComplexSlotMention regulatedProcess = new DefaultComplexSlotMention("regulated process");
		regulatedProcess.addClassMention(activationMention);
		regulationOfActivationMention.addComplexSlotMention(regulatedProcess);
		TextAnnotation regulatedActivationOfTransport = new DefaultTextAnnotation(0, 10, "activation", annotator1,
				annotationSet1, -18, -1, "1234", 0, regulationOfActivationMention);

		/* create a protein responsible for the regulation */
		ClassMention regulatingProteinMention = new DefaultClassMention("protein");
		IntegerSlotMention regulatingEntrezID = new DefaultIntegerSlotMention("entrez_gene_id");
		regulatingEntrezID.addSlotValue(975);
		regulatingProteinMention.addPrimitiveSlotMention(regulatingEntrezID);
		ComplexSlotMention regulatingEntity = new DefaultComplexSlotMention("regulating entity");
		regulatingEntity.addClassMention(regulatingProteinMention);
		regulationOfActivationMention.addComplexSlotMention(regulatingEntity);
		TextAnnotation regulatingProteinAnnotation = new DefaultTextAnnotation(134, 156, "protein kinase C",
				annotator1, annotationSet1, -19, -1, "1234", 0, regulatingProteinMention);
		returnArray.add(regulatingProteinAnnotation);

		returnArray.add(regulatedActivationOfTransport);

		/* create another protein annotation: EBV LMP1 */
		/* ******************************************** */

		spanList = new ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span>();
		try {
			span1 = new edu.ucdenver.ccp.nlp.core.annotation.Span(0, 8);
			spanList.add(span1);
		} catch (edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException e1) {
			e1.printStackTrace();
		}

		ta = new DefaultTextAnnotation(spanList);
		ta.setAnnotationID(-13);
		ta.setCoveredText("EBV LMP1");

		annotationSetList = new HashSet<AnnotationSet>();
		annotationSetList.add(annotationSet2);
		ta.setAnnotationSets(annotationSetList);

		ta.setAnnotator(annotator2);
		ta.setDocumentCollectionID(-1);
		ta.setDocumentID("1234");
		ta.setDocumentSectionID(0);

		cm = new DefaultClassMention("protein");

		entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(888888);
		cm.addPrimitiveSlotMention(entrezIDSlotMention);

		ta.setClassMention(cm);

		returnArray.add(ta);

		/* create another protein annotation: p16INK4 */
		/* ******************************************** */

		spanList = new ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span>();
		try {
			span1 = new edu.ucdenver.ccp.nlp.core.annotation.Span(16, 23);
			spanList.add(span1);
		} catch (edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException e) {
			e.printStackTrace();
		}
		ta = new DefaultTextAnnotation(spanList);
		ta.setAnnotationID(-14);

		ta.setCoveredText("p16INK4");

		annotationSetList = new HashSet<AnnotationSet>();
		annotationSetList.add(annotationSet2);
		ta.setAnnotationSets(annotationSetList);

		ta.setAnnotator(annotator2);
		ta.setDocumentCollectionID(-1);
		ta.setDocumentID("1234");
		ta.setDocumentSectionID(0);

		cm = new DefaultClassMention("protein");

		entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(7777);
		cm.addPrimitiveSlotMention(entrezIDSlotMention);

		StringSlotMention processedTextSM = new DefaultStringSlotMention(SlotMentionType.PROCESSED_TEXT_SLOT.typeName());
		processedTextSM.addSlotValue("");
		cm.addPrimitiveSlotMention(processedTextSM);

		ta.setClassMention(cm);

		returnArray.add(ta);

		/* create another protein annotation: E2f-4 */
		/* ******************************************** */

		spanList = new ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span>();
		try {
			span1 = new edu.ucdenver.ccp.nlp.core.annotation.Span(63, 68);
			spanList.add(span1);
		} catch (edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException e) {
			e.printStackTrace();
		}
		ta = new DefaultTextAnnotation(spanList);
		ta.setAnnotationID(-15);

		ta.setCoveredText("E2f-4");

		annotationSetList = new HashSet<AnnotationSet>();
		annotationSetList.add(annotationSet2);
		ta.setAnnotationSets(annotationSetList);

		ta.setAnnotator(annotator2);
		ta.setDocumentCollectionID(-1);
		ta.setDocumentID("1234");
		ta.setDocumentSectionID(0);

		cm = new DefaultClassMention("protein");

		entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(22222);
		cm.addPrimitiveSlotMention(entrezIDSlotMention);

		processedTextSM = new DefaultStringSlotMention(SlotMentionType.PROCESSED_TEXT_SLOT.typeName());
		processedTextSM.addSlotValue("");
		cm.addPrimitiveSlotMention(processedTextSM);

		ta.setClassMention(cm);

		returnArray.add(ta);

		return returnArray;
	}

	/**
	 * Returns an annotation that matches Annotation #18 (above, Regulation of Activation of
	 * Transport) exactly.
	 * 
	 * <pre>
	 *       MATCHES 18 EXACTLY ###################################
	 *           ======================= Annotation: -18 =======================
	 *           Annotator: -30|Test Annotator|#1|CCP
	 *           --- AnnotationSets: -20|Test Set #1|This is a test annnotation set.
	 *           --- Span: 0 - 10  
	 *           --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *           --- Covered Text: activation
	 *           -CLASS MENTION: regulation &quot;activation&quot;
	 *           -    COMPLEX SLOT MENTION: regulated process
	 *           -        CLASS MENTION: activation &quot;activation&quot;
	 *           -            COMPLEX SLOT MENTION: activated process
	 *           -                CLASS MENTION: gated nuclear transport &quot;gated..transport&quot;
	 *           -                    COMPLEX SLOT MENTION: transport origin
	 *           -                        CLASS MENTION: nucleus &quot;nucl&quot;
	 *           -                    COMPLEX SLOT MENTION: transport location
	 *           -                        CLASS MENTION: nucleus &quot;nucl&quot;
	 *           -                    COMPLEX SLOT MENTION: transport participants
	 *           -                        CLASS MENTION: protein &quot;E2F-4&quot;
	 *           -                            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999  
	 *           -                    COMPLEX SLOT MENTION: transported entities
	 *           -                        CLASS MENTION: protein &quot;E2F-4&quot;
	 *           -                            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999  
	 *           -            COMPLEX SLOT MENTION: activating entity
	 *           -                CLASS MENTION: protein &quot;HER-23&quot;
	 *           -                    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 13579  
	 *           -    COMPLEX SLOT MENTION: regulating entity
	 *           -        CLASS MENTION: protein &quot;protein kinase C&quot;
	 *           -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 975  
	 *           =================================================================================
	 * </pre>
	 * 
	 * @return
	 * @throws Exception
	 */
	public static TextAnnotation getAnnotationToMatch18Exactly() throws Exception {
		/* Initialize test annotators */
		Annotator annotator1 = new Annotator(new Integer(-30), "Test Annotator", "#1", "CCP");
		Annotator annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");

		/* Initialize test annotation sets */
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1",
				"This is a test annnotation set.");
		AnnotationSet annotationSet2 = new AnnotationSet(new Integer(-21), "Test Set #2",
				"This is another test annotation set.");

		/* This annotation has a split span: 45-49 and 53-61 */
		ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span> spanList = new ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span>();
		edu.ucdenver.ccp.nlp.core.annotation.Span span1;
		edu.ucdenver.ccp.nlp.core.annotation.Span span2;
		try {
			span1 = new edu.ucdenver.ccp.nlp.core.annotation.Span(45, 49);
			span2 = new edu.ucdenver.ccp.nlp.core.annotation.Span(53, 61);
			spanList.add(span1);
			spanList.add(span2);
		} catch (edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException e) {
			e.printStackTrace();
		}
		/* Create the "gated nuclear transport" annotation */
		TextAnnotation ta = new DefaultTextAnnotation(spanList);

		/* annotationID = -10 */
		ta.setAnnotationID(-10);

		/* coverted text = gated (45-49), transport (53-61) */
		ta.setCoveredText("gated..transport");

		/* this annotation belongs to two annotation sets */
		Set<AnnotationSet> annotationSetList = new HashSet<AnnotationSet>();
		annotationSetList.add(annotationSet1);
		annotationSetList.add(annotationSet2);
		ta.setAnnotationSets(annotationSetList);

		/* this annotation was created by annotator1 */
		ta.setAnnotator(annotator1);

		/* the documentcollection = -1 */
		ta.setDocumentCollectionID(-1);
		ta.setDocumentID("1234");
		ta.setDocumentSectionID(0);

		/* Initialize the transport mention */
		ClassMention cm = new DefaultClassMention("gated nuclear transport");

		ta.setClassMention(cm);

		/* The transport mention will have 4 filled slots */
		/* transport origin */
		/* transport location */
		/* transport participants */
		/* transported entities */

		/* ----- transport origin ----- */
		ComplexSlotMention transportOriginMention = new DefaultComplexSlotMention("transport origin");

		/* create a "nucleus" annotation to fill the transport origin slot */
		/* first create the nucleus class mention */
		ClassMention nucleusMention = new DefaultClassMention("nucleus");

		/* create the nucleus annotation */
		TextAnnotation nucleusAnnotation = new DefaultTextAnnotation(50, 53, "nucl", annotator1, annotationSet1, -11,
				-1, "1234", 0, nucleusMention);

		/* add the nucleus mention as the slot filler for the transport origin */
		transportOriginMention.addClassMention(nucleusMention);

		/* add the transport origin slot to the transport mention */
		cm.addComplexSlotMention(transportOriginMention);

		/* ----- transport location ----- */
		ComplexSlotMention transportLocationMention = new DefaultComplexSlotMention("transport location");

		/* add the already created nucleus mention as the slot filler for the transport location */
		transportLocationMention.addClassMention(nucleusMention);

		/* add the transport location slot to the transport mention */
		cm.addComplexSlotMention(transportLocationMention);

		/* ----- transport participants ----- */
		ComplexSlotMention transportParticipantsMention = new DefaultComplexSlotMention("transport participants");

		/* create a protein mention and annotation to fill the transport participants slot */
		ClassMention e2f4ProteinMention = new DefaultClassMention("protein");

		/* create a slot for the Entrez ID for this protein mention */
		IntegerSlotMention entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(9999999);
		e2f4ProteinMention.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		TextAnnotation e2f4Annotation = new DefaultTextAnnotation(65, 70, "E2F-4", annotator2, annotationSet1, -12, -1,
				"1234", 0, e2f4ProteinMention);

		/* fill the transport participants slot with the e2f4 protein mention */
		transportParticipantsMention.addClassMention(e2f4ProteinMention);

		/* add the transport participants slot to the transport mention */
		cm.addComplexSlotMention(transportParticipantsMention);

		/* ----- transported entities ----- */
		ComplexSlotMention transportedEntitiesMention = new DefaultComplexSlotMention("transported entities");

		/* add the already created e2f4 protein mention to the transported entities slot */
		transportedEntitiesMention.addClassMention(e2f4ProteinMention);

		/* add the transported entities slot to the transport mention */
		cm.addComplexSlotMention(transportedEntitiesMention);

		/* create a activation of transport annotation */
		ClassMention activationMention = new DefaultClassMention("activation");
		ComplexSlotMention activatedProcess = new DefaultComplexSlotMention("activated process");
		activatedProcess.addClassMention(cm);
		activationMention.addComplexSlotMention(activatedProcess);

		/* create a protein responsible for the activation */
		ClassMention activatingProteinMention = new DefaultClassMention("protein");
		IntegerSlotMention activatingEntrezID = new DefaultIntegerSlotMention("entrez_gene_id");
		activatingEntrezID.addSlotValue(13579);
		activatingProteinMention.addPrimitiveSlotMention(activatingEntrezID);
		ComplexSlotMention activatingEntity = new DefaultComplexSlotMention("activating entity");
		activatingEntity.addClassMention(activatingProteinMention);
		activationMention.addComplexSlotMention(activatingEntity);
		TextAnnotation activatingProteinAnnotation = new DefaultTextAnnotation(111, 120, "HER-23", annotator1,
				annotationSet1, -17, -1, "1234", 0, activatingProteinMention);

		TextAnnotation activationOfTransport = new DefaultTextAnnotation(0, 10, "activation", annotator1,
				annotationSet1, -16, -1, "1234", 0, activationMention);

		/* create a regulation of activation of transport annotation */
		ClassMention regulationOfActivationMention = new DefaultClassMention("regulation");
		ComplexSlotMention regulatedProcess = new DefaultComplexSlotMention("regulated process");
		regulatedProcess.addClassMention(activationMention);
		regulationOfActivationMention.addComplexSlotMention(regulatedProcess);
		TextAnnotation regulatedActivationOfTransport = new DefaultTextAnnotation(0, 10, "activation", annotator1,
				annotationSet1, -18, -1, "1234", 0, regulationOfActivationMention);

		/* create a protein responsible for the regulation */
		ClassMention regulatingProteinMention = new DefaultClassMention("protein");
		IntegerSlotMention regulatingEntrezID = new DefaultIntegerSlotMention("entrez_gene_id");
		regulatingEntrezID.addSlotValue(975);
		regulatingProteinMention.addPrimitiveSlotMention(regulatingEntrezID);
		ComplexSlotMention regulatingEntity = new DefaultComplexSlotMention("regulating entity");
		regulatingEntity.addClassMention(regulatingProteinMention);
		regulationOfActivationMention.addComplexSlotMention(regulatingEntity);
		TextAnnotation regulatingProteinAnnotation = new DefaultTextAnnotation(134, 156, "protein kinase C",
				annotator1, annotationSet1, -19, -1, "1234", 0, regulatingProteinMention);

		return regulatedActivationOfTransport;
	}

	/**
	 * Return an annotation that matches Annotation #18 at levels 0,1, and 2, but have different
	 * transported entities (entrez id differs), so fails the exact match at level 3<br>
	 * All spans are the same. The only difference is in the entrez Id for E2F-4, the transported
	 * entity and participant
	 * 
	 * <pre>
	 *      MATCHES 18 THRU LEVEL 3 ###################################
	 *          ======================= Annotation: -18 =======================
	 *          Annotator: -30|Test Annotator|#1|CCP
	 *          --- AnnotationSets: -20|Test Set #1|This is a test annnotation set.
	 *          --- Span: 0 - 10  
	 *          --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *          --- Covered Text: activation
	 *          -CLASS MENTION: regulation &quot;activation&quot;
	 *          -    COMPLEX SLOT MENTION: regulated process
	 *          -        CLASS MENTION: activation &quot;activation&quot;
	 *          -            COMPLEX SLOT MENTION: activated process
	 *          -                CLASS MENTION: gated nuclear transport &quot;gated..transport&quot;
	 *          -                    COMPLEX SLOT MENTION: transport origin
	 *          -                        CLASS MENTION: nucleus &quot;nucl&quot;
	 *          -                    COMPLEX SLOT MENTION: transport location
	 *          -                        CLASS MENTION: nucleus &quot;nucl&quot;
	 *          -                    COMPLEX SLOT MENTION: transport participants
	 *          -                        CLASS MENTION: protein &quot;E2F-4&quot;
	 *          -                            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): this is a different entrez id  
	 *          -                    COMPLEX SLOT MENTION: transported entities
	 *          -                        CLASS MENTION: protein &quot;E2F-4&quot;
	 *          -                            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): this is a different entrez id  
	 *          -            COMPLEX SLOT MENTION: activating entity
	 *          -                CLASS MENTION: protein &quot;HER-23&quot;
	 *          -                    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 13579  
	 *          -    COMPLEX SLOT MENTION: regulating entity
	 *          -        CLASS MENTION: protein &quot;protein kinase C&quot;
	 *          -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 975  
	 *          =================================================================================
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public static TextAnnotation getAnnotationToMatch18ThruLevel3() throws Exception {
		/* Initialize test annotators */
		Annotator annotator1 = new Annotator(new Integer(-30), "Test Annotator", "#1", "CCP");
		Annotator annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");

		/* Initialize test annotation sets */
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1",
				"This is a test annnotation set.");
		AnnotationSet annotationSet2 = new AnnotationSet(new Integer(-21), "Test Set #2",
				"This is another test annotation set.");

		/* This annotation has a split span: 45-49 and 53-61 */
		ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span> spanList = new ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span>();
		edu.ucdenver.ccp.nlp.core.annotation.Span span1;
		edu.ucdenver.ccp.nlp.core.annotation.Span span2;
		try {
			span1 = new edu.ucdenver.ccp.nlp.core.annotation.Span(45, 49);
			span2 = new edu.ucdenver.ccp.nlp.core.annotation.Span(53, 61);
			spanList.add(span1);
			spanList.add(span2);
		} catch (edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException e) {
			e.printStackTrace();
		}
		/* Create the "gated nuclear transport" annotation */
		TextAnnotation ta = new DefaultTextAnnotation(spanList);

		/* annotationID = -10 */
		ta.setAnnotationID(-10);

		/* coverted text = gated (45-49), transport (53-61) */
		ta.setCoveredText("gated..transport");

		/* this annotation belongs to two annotation sets */
		Set<AnnotationSet> annotationSetList = new HashSet<AnnotationSet>();
		annotationSetList.add(annotationSet1);
		annotationSetList.add(annotationSet2);
		ta.setAnnotationSets(annotationSetList);

		/* this annotation was created by annotator1 */
		ta.setAnnotator(annotator1);

		/* the documentcollection = -1 */
		ta.setDocumentCollectionID(-1);
		ta.setDocumentID("1234");
		ta.setDocumentSectionID(0);

		/* Initialize the transport mention */
		ClassMention cm = new DefaultClassMention("gated nuclear transport");

		ta.setClassMention(cm);

		/* The transport mention will have 4 filled slots */
		/* transport origin */
		/* transport location */
		/* transport participants */
		/* transported entities */

		/* ----- transport origin ----- */
		ComplexSlotMention transportOriginMention = new DefaultComplexSlotMention("transport origin");

		/* create a "nucleus" annotation to fill the transport origin slot */
		/* first create the nucleus class mention */
		ClassMention nucleusMention = new DefaultClassMention("nucleus");

		/* create the nucleus annotation */
		TextAnnotation nucleusAnnotation = new DefaultTextAnnotation(50, 53, "nucl", annotator1, annotationSet1, -11,
				-1, "1234", 0, nucleusMention);

		/* add the nucleus mention as the slot filler for the transport origin */
		transportOriginMention.addClassMention(nucleusMention);

		/* add the transport origin slot to the transport mention */
		cm.addComplexSlotMention(transportOriginMention);

		/* ----- transport location ----- */
		ComplexSlotMention transportLocationMention = new DefaultComplexSlotMention("transport location");

		/* add the already created nucleus mention as the slot filler for the transport location */
		transportLocationMention.addClassMention(nucleusMention);

		/* add the transport location slot to the transport mention */
		cm.addComplexSlotMention(transportLocationMention);

		/* ----- transport participants ----- */
		ComplexSlotMention transportParticipantsMention = new DefaultComplexSlotMention("transport participants");

		/* create a protein mention and annotation to fill the transport participants slot */
		ClassMention e2f4ProteinMention = new DefaultClassMention("protein");

		/* create a slot for the Entrez ID for this protein mention */
		IntegerSlotMention entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(-1);
		e2f4ProteinMention.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		TextAnnotation e2f4Annotation = new DefaultTextAnnotation(65, 70, "E2F-4", annotator2, annotationSet1, -12, -1,
				"1234", 0, e2f4ProteinMention);

		/* fill the transport participants slot with the e2f4 protein mention */
		transportParticipantsMention.addClassMention(e2f4ProteinMention);

		/* add the transport participants slot to the transport mention */
		cm.addComplexSlotMention(transportParticipantsMention);

		/* ----- transported entities ----- */
		ComplexSlotMention transportedEntitiesMention = new DefaultComplexSlotMention("transported entities");

		/* add the already created e2f4 protein mention to the transported entities slot */
		transportedEntitiesMention.addClassMention(e2f4ProteinMention);

		/* add the transported entities slot to the transport mention */
		cm.addComplexSlotMention(transportedEntitiesMention);

		/* create a activation of transport annotation */
		ClassMention activationMention = new DefaultClassMention("activation");
		ComplexSlotMention activatedProcess = new DefaultComplexSlotMention("activated process");
		activatedProcess.addClassMention(cm);
		activationMention.addComplexSlotMention(activatedProcess);

		/* create a protein responsible for the activation */
		ClassMention activatingProteinMention = new DefaultClassMention("protein");
		IntegerSlotMention activatingEntrezID = new DefaultIntegerSlotMention("entrez_gene_id");
		activatingEntrezID.addSlotValue(13579);
		activatingProteinMention.addPrimitiveSlotMention(activatingEntrezID);
		ComplexSlotMention activatingEntity = new DefaultComplexSlotMention("activating entity");
		activatingEntity.addClassMention(activatingProteinMention);
		activationMention.addComplexSlotMention(activatingEntity);
		TextAnnotation activatingProteinAnnotation = new DefaultTextAnnotation(111, 120, "HER-23", annotator1,
				annotationSet1, -17, -1, "1234", 0, activatingProteinMention);

		TextAnnotation activationOfTransport = new DefaultTextAnnotation(0, 10, "activation", annotator1,
				annotationSet1, -16, -1, "1234", 0, activationMention);

		/* create a regulation of activation of transport annotation */
		ClassMention regulationOfActivationMention = new DefaultClassMention("regulation");
		ComplexSlotMention regulatedProcess = new DefaultComplexSlotMention("regulated process");
		regulatedProcess.addClassMention(activationMention);
		regulationOfActivationMention.addComplexSlotMention(regulatedProcess);
		TextAnnotation regulatedActivationOfTransport = new DefaultTextAnnotation(0, 10, "activation", annotator1,
				annotationSet1, -18, -1, "1234", 0, regulationOfActivationMention);

		/* create a protein responsible for the regulation */
		ClassMention regulatingProteinMention = new DefaultClassMention("protein");
		IntegerSlotMention regulatingEntrezID = new DefaultIntegerSlotMention("entrez_gene_id");
		regulatingEntrezID.addSlotValue(975);
		regulatingProteinMention.addPrimitiveSlotMention(regulatingEntrezID);
		ComplexSlotMention regulatingEntity = new DefaultComplexSlotMention("regulating entity");
		regulatingEntity.addClassMention(regulatingProteinMention);
		regulationOfActivationMention.addComplexSlotMention(regulatingEntity);
		TextAnnotation regulatingProteinAnnotation = new DefaultTextAnnotation(134, 156, "protein kinase C",
				annotator1, annotationSet1, -19, -1, "1234", 0, regulatingProteinMention);

		return regulatedActivationOfTransport;
	}

	/**
	 * Returns an annotation that matches Annotation #18 (above, Regulation of Activation of
	 * Transport) exactly in regards to mention, but contains an overlapping annotation for the
	 * "activating entity" HER-23 and for the "nucleus". <br>
	 * The HER-23 annotation has the same right boundary, but a different left boundary (one that is
	 * to the left of the previous)<br>
	 * The nucleus annotation has the same left boundary, but a different right boundary (one that
	 * is to the right of the previous)<br>
	 * 
	 * @return
	 * @throws Exception
	 */
	public static TextAnnotation getAnnotationToMatch18ExactlyButHasOverlappingSpans() throws Exception {
		/* Initialize test annotators */
		Annotator annotator1 = new Annotator(new Integer(-30), "Test Annotator", "#1", "CCP");
		Annotator annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");

		/* Initialize test annotation sets */
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1",
				"This is a test annnotation set.");
		AnnotationSet annotationSet2 = new AnnotationSet(new Integer(-21), "Test Set #2",
				"This is another test annotation set.");

		/* This annotation has a split span: 45-49 and 53-61 */
		ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span> spanList = new ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span>();
		edu.ucdenver.ccp.nlp.core.annotation.Span span1;
		edu.ucdenver.ccp.nlp.core.annotation.Span span2;
		try {
			span1 = new edu.ucdenver.ccp.nlp.core.annotation.Span(45, 49);
			span2 = new edu.ucdenver.ccp.nlp.core.annotation.Span(53, 61);
			spanList.add(span1);
			spanList.add(span2);
		} catch (edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException e) {
			e.printStackTrace();
		}
		/* Create the "gated nuclear transport" annotation */
		TextAnnotation ta = new DefaultTextAnnotation(spanList);

		/* annotationID = -10 */
		ta.setAnnotationID(-10);

		/* coverted text = gated (45-49), transport (53-61) */
		ta.setCoveredText("gated..transport");

		/* this annotation belongs to two annotation sets */
		Set<AnnotationSet> annotationSetList = new HashSet<AnnotationSet>();
		annotationSetList.add(annotationSet1);
		annotationSetList.add(annotationSet2);
		ta.setAnnotationSets(annotationSetList);

		/* this annotation was created by annotator1 */
		ta.setAnnotator(annotator1);

		/* the documentcollection = -1 */
		ta.setDocumentCollectionID(-1);
		ta.setDocumentID("1234");
		ta.setDocumentSectionID(0);

		/* Initialize the transport mention */
		ClassMention cm = new DefaultClassMention("gated nuclear transport");

		ta.setClassMention(cm);

		/* The transport mention will have 4 filled slots */
		/* transport origin */
		/* transport location */
		/* transport participants */
		/* transported entities */

		/* ----- transport origin ----- */
		ComplexSlotMention transportOriginMention = new DefaultComplexSlotMention("transport origin");

		/* create a "nucleus" annotation to fill the transport origin slot */
		/* first create the nucleus class mention */
		ClassMention nucleusMention = new DefaultClassMention("nucleus");

		/* create the nucleus annotation */
		TextAnnotation nucleusAnnotation = new DefaultTextAnnotation(50, 56, "nucl", annotator1, annotationSet1, -11,
				-1, "1234", 0, nucleusMention);

		/* add the nucleus mention as the slot filler for the transport origin */
		transportOriginMention.addClassMention(nucleusMention);

		/* add the transport origin slot to the transport mention */
		cm.addComplexSlotMention(transportOriginMention);

		/* ----- transport location ----- */
		ComplexSlotMention transportLocationMention = new DefaultComplexSlotMention("transport location");

		/* add the already created nucleus mention as the slot filler for the transport location */
		transportLocationMention.addClassMention(nucleusMention);

		/* add the transport location slot to the transport mention */
		cm.addComplexSlotMention(transportLocationMention);

		/* ----- transport participants ----- */
		ComplexSlotMention transportParticipantsMention = new DefaultComplexSlotMention("transport participants");

		/* create a protein mention and annotation to fill the transport participants slot */
		ClassMention e2f4ProteinMention = new DefaultClassMention("protein");

		/* create a slot for the Entrez ID for this protein mention */
		IntegerSlotMention entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(9999999);
		e2f4ProteinMention.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		TextAnnotation e2f4Annotation = new DefaultTextAnnotation(65, 70, "E2F-4", annotator2, annotationSet1, -12, -1,
				"1234", 0, e2f4ProteinMention);

		/* fill the transport participants slot with the e2f4 protein mention */
		transportParticipantsMention.addClassMention(e2f4ProteinMention);

		/* add the transport participants slot to the transport mention */
		cm.addComplexSlotMention(transportParticipantsMention);

		/* ----- transported entities ----- */
		ComplexSlotMention transportedEntitiesMention = new DefaultComplexSlotMention("transported entities");

		/* add the already created e2f4 protein mention to the transported entities slot */
		transportedEntitiesMention.addClassMention(e2f4ProteinMention);

		/* add the transported entities slot to the transport mention */
		cm.addComplexSlotMention(transportedEntitiesMention);

		/* create a activation of transport annotation */
		ClassMention activationMention = new DefaultClassMention("activation");
		ComplexSlotMention activatedProcess = new DefaultComplexSlotMention("activated process");
		activatedProcess.addClassMention(cm);
		activationMention.addComplexSlotMention(activatedProcess);

		/* create a protein responsible for the activation */
		ClassMention activatingProteinMention = new DefaultClassMention("protein");
		IntegerSlotMention activatingEntrezID = new DefaultIntegerSlotMention("entrez_gene_id");
		activatingEntrezID.addSlotValue(13579);
		activatingProteinMention.addPrimitiveSlotMention(activatingEntrezID);
		ComplexSlotMention activatingEntity = new DefaultComplexSlotMention("activating entity");
		activatingEntity.addClassMention(activatingProteinMention);
		activationMention.addComplexSlotMention(activatingEntity);
		TextAnnotation activatingProteinAnnotation = new DefaultTextAnnotation(109, 120, "HER-23", annotator1,
				annotationSet1, -17, -1, "1234", 0, activatingProteinMention);

		TextAnnotation activationOfTransport = new DefaultTextAnnotation(0, 10, "activation", annotator1,
				annotationSet1, -16, -1, "1234", 0, activationMention);

		/* create a regulation of activation of transport annotation */
		ClassMention regulationOfActivationMention = new DefaultClassMention("regulation");
		ComplexSlotMention regulatedProcess = new DefaultComplexSlotMention("regulated process");
		regulatedProcess.addClassMention(activationMention);
		regulationOfActivationMention.addComplexSlotMention(regulatedProcess);
		TextAnnotation regulatedActivationOfTransport = new DefaultTextAnnotation(0, 10, "activation", annotator1,
				annotationSet1, -18, -1, "1234", 0, regulationOfActivationMention);

		/* create a protein responsible for the regulation */
		ClassMention regulatingProteinMention = new DefaultClassMention("protein");
		IntegerSlotMention regulatingEntrezID = new DefaultIntegerSlotMention("entrez_gene_id");
		regulatingEntrezID.addSlotValue(975);
		regulatingProteinMention.addPrimitiveSlotMention(regulatingEntrezID);
		ComplexSlotMention regulatingEntity = new DefaultComplexSlotMention("regulating entity");
		regulatingEntity.addClassMention(regulatingProteinMention);
		regulationOfActivationMention.addComplexSlotMention(regulatingEntity);
		TextAnnotation regulatingProteinAnnotation = new DefaultTextAnnotation(134, 156, "protein kinase C",
				annotator1, annotationSet1, -19, -1, "1234", 0, regulatingProteinMention);

		return regulatedActivationOfTransport;
	}

	/**
	 * Returns an annotation that matches Annotation #18 (above, Regulation of Activation of
	 * Transport) exactly. This annotation is the regulation of activation of nothing.
	 * 
	 * <pre>
	 *    MATCHES 18 THRU LEVEL 1 ###################################
	 *        ======================= Annotation: -18 =======================
	 *        Annotator: -30|Test Annotator|#1|CCP
	 *        --- AnnotationSets: -20|Test Set #1|This is a test annnotation set.
	 *        --- Span: 0 - 10  
	 *        --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *        --- Covered Text: activation
	 *        -CLASS MENTION: regulation &quot;activation&quot;
	 *        -    COMPLEX SLOT MENTION: regulated process
	 *        -        CLASS MENTION: activation &quot;activation&quot;
	 *        -    COMPLEX SLOT MENTION: regulating entity
	 *        -        CLASS MENTION: protein &quot;protein kinase C&quot;
	 *        -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 975  
	 *        =================================================================================
	 * </pre>
	 * 
	 * @return
	 * @throws Exception
	 */
	public static TextAnnotation getAnnotationToMatch18ThruLevel1Only() throws Exception {
		/* Initialize test annotators */
		Annotator annotator1 = new Annotator(new Integer(-30), "Test Annotator", "#1", "CCP");

		/* Initialize test annotation sets */
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1",
				"This is a test annnotation set.");

		// /* Create the "gated nuclear transport" annotation */
		// TextAnnotation ta = new DefaultTextAnnotation();

		/* create a activation of transport annotation */
		ClassMention activationMention = new DefaultClassMention("activation");
		TextAnnotation activationOfTransport = new DefaultTextAnnotation(0, 10, "activation", annotator1,
				annotationSet1, -16, -1, "1234", 0, activationMention);

		/* create a regulation of activation of transport annotation */
		ClassMention regulationOfActivationMention = new DefaultClassMention("regulation");
		ComplexSlotMention regulatedProcess = new DefaultComplexSlotMention("regulated process");
		regulatedProcess.addClassMention(activationMention);
		regulationOfActivationMention.addComplexSlotMention(regulatedProcess);
		TextAnnotation regulatedActivationOfTransport = new DefaultTextAnnotation(0, 10, "activation", annotator1,
				annotationSet1, -18, -1, "1234", 0, regulationOfActivationMention);

		/* create a protein responsible for the regulation */
		ClassMention regulatingProteinMention = new DefaultClassMention("protein");
		IntegerSlotMention regulatingEntrezID = new DefaultIntegerSlotMention("entrez_gene_id");
		regulatingEntrezID.addSlotValue(975);
		regulatingProteinMention.addPrimitiveSlotMention(regulatingEntrezID);
		ComplexSlotMention regulatingEntity = new DefaultComplexSlotMention("regulating entity");
		regulatingEntity.addClassMention(regulatingProteinMention);
		regulationOfActivationMention.addComplexSlotMention(regulatingEntity);
		TextAnnotation regulatingProteinAnnotation = new DefaultTextAnnotation(134, 156, "protein kinase C",
				annotator1, annotationSet1, -19, -1, "1234", 0, regulatingProteinMention);

		return regulatedActivationOfTransport;
	}

	/**
	 * Exact match to Annotation 10, transport, but missing "transported entities" slot
	 * 
	 * <pre>
	 *  MATCHES 10 BUT MISSING TRANSPORTED ENTITIES SLOT ###################################
	 *      ======================= Annotation: -10 =======================
	 *      Annotator: -30|Test Annotator|#1|CCP
	 *      --- AnnotationSets: -21|Test Set #2|This is another test annotation set.
	 *      -20|Test Set #1|This is a test annnotation set.
	 *      --- Span: 
	 *      --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *      --- Covered Text: gated..transport
	 *      -CLASS MENTION: gated nuclear transport &quot;gated..transport&quot;
	 *      -    COMPLEX SLOT MENTION: transport origin
	 *      -        CLASS MENTION: nucleus &quot;nucl&quot;
	 *      -    COMPLEX SLOT MENTION: transport location
	 *      -        CLASS MENTION: nucleus &quot;nucl&quot;
	 *      -    COMPLEX SLOT MENTION: transport participants
	 *      -        CLASS MENTION: protein &quot;E2F-4&quot;
	 *      -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999  
	 *      =================================================================================
	 * </pre>
	 * 
	 * @return
	 * @throws Exception
	 */
	public static TextAnnotation getAnnotationToMatch10ButMissingASlot() throws Exception {
		/* Initialize test annotators */
		Annotator annotator1 = new Annotator(new Integer(-30), "Test Annotator", "#1", "CCP");
		Annotator annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");

		/* Initialize test annotation sets */
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1",
				"This is a test annnotation set.");
		AnnotationSet annotationSet2 = new AnnotationSet(new Integer(-21), "Test Set #2",
				"This is another test annotation set.");

		/* This annotation has a split span: 45-49 and 53-61 */
		ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span> spanList = new ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span>();
		edu.ucdenver.ccp.nlp.core.annotation.Span span1;
		edu.ucdenver.ccp.nlp.core.annotation.Span span2;
		try {
			span1 = new edu.ucdenver.ccp.nlp.core.annotation.Span(45, 49);
			span2 = new edu.ucdenver.ccp.nlp.core.annotation.Span(53, 61);
			spanList.add(span1);
			spanList.add(span2);
		} catch (edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException e) {
			e.printStackTrace();
		}
		/* Create the "gated nuclear transport" annotation */
		TextAnnotation ta = new DefaultTextAnnotation(spanList);

		/* annotationID = -10 */
		ta.setAnnotationID(-10);

		/* coverted text = gated (45-49), transport (53-61) */
		ta.setCoveredText("gated..transport");

		/* this annotation belongs to two annotation sets */
		Set<AnnotationSet> annotationSetList = new HashSet<AnnotationSet>();
		annotationSetList.add(annotationSet1);
		annotationSetList.add(annotationSet2);
		ta.setAnnotationSets(annotationSetList);

		/* this annotation was created by annotator1 */
		ta.setAnnotator(annotator1);

		/* the documentcollection = -1 */
		ta.setDocumentCollectionID(-1);
		ta.setDocumentID("1234");
		ta.setDocumentSectionID(0);

		/* Initialize the transport mention */
		ClassMention cm = new DefaultClassMention("gated nuclear transport");

		ta.setClassMention(cm);

		/* The transport mention will have 4 filled slots */
		/* transport origin */
		/* transport location */
		/* transport participants */
		/* transported entities */

		/* ----- transport origin ----- */
		ComplexSlotMention transportOriginMention = new DefaultComplexSlotMention("transport origin");

		/* create a "nucleus" annotation to fill the transport origin slot */
		/* first create the nucleus class mention */
		ClassMention nucleusMention = new DefaultClassMention("nucleus");

		/* create the nucleus annotation */
		TextAnnotation nucleusAnnotation = new DefaultTextAnnotation(50, 53, "nucl", annotator1, annotationSet1, -11,
				-1, "1234", 0, nucleusMention);

		/* add the nucleus mention as the slot filler for the transport origin */
		transportOriginMention.addClassMention(nucleusMention);

		/* add the transport origin slot to the transport mention */
		cm.addComplexSlotMention(transportOriginMention);

		/* ----- transport location ----- */
		ComplexSlotMention transportLocationMention = new DefaultComplexSlotMention("transport location");

		/* add the already created nucleus mention as the slot filler for the transport location */
		transportLocationMention.addClassMention(nucleusMention);

		/* add the transport location slot to the transport mention */
		cm.addComplexSlotMention(transportLocationMention);

		/* ----- transport participants ----- */
		ComplexSlotMention transportParticipantsMention = new DefaultComplexSlotMention("transport participants");

		/* create a protein mention and annotation to fill the transport participants slot */
		ClassMention e2f4ProteinMention = new DefaultClassMention("protein");

		/* create a slot for the Entrez ID for this protein mention */
		IntegerSlotMention entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(9999999);
		e2f4ProteinMention.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		TextAnnotation e2f4Annotation = new DefaultTextAnnotation(65, 70, "E2F-4", annotator2, annotationSet1, -12, -1,
				"1234", 0, e2f4ProteinMention);

		/* fill the transport participants slot with the e2f4 protein mention */
		transportParticipantsMention.addClassMention(e2f4ProteinMention);

		/* add the transport participants slot to the transport mention */
		cm.addComplexSlotMention(transportParticipantsMention);

		/* ----- transported entities ----- */
		ComplexSlotMention transportedEntitiesMention = new DefaultComplexSlotMention("transported entities");

		/* add the already created e2f4 protein mention to the transported entities slot */
		transportedEntitiesMention.addClassMention(e2f4ProteinMention);
		return ta;
	}

	/**
	 * Return the nucleus annotation with an overlapping span. Left side has moved to the left;
	 * right side remains the same
	 * 
	 * @return
	 */
	public static TextAnnotation getAnnotationToMatch11WithOverlappingSpan() {
		/* Initialize test annotators */
		Annotator annotator1 = new Annotator(new Integer(-30), "Test Annotator", "#1", "CCP");

		/* Initialize test annotation sets */
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1",
				"This is a test annnotation set.");
		ClassMention nucleusMention = new DefaultClassMention("nucleus");

		/* create the nucleus annotation */
		TextAnnotation nucleusAnnotation = new DefaultTextAnnotation(45, 53, "nucl", annotator1, annotationSet1, -11,
				-1, "1234", 0, nucleusMention);

		return nucleusAnnotation;
	}

	/**
	 * This method returns the list of annotations mapped to their annotation id's
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<Integer, TextAnnotation> getID2TextAnnotationMap() throws Exception {
		List<TextAnnotation> taList = TestTextAnnotationCreator.getTestTextAnnotations();
		Map<Integer, TextAnnotation> id2annotationMap = new HashMap<Integer, TextAnnotation>();
		for (TextAnnotation ta : taList) {
			id2annotationMap.put(ta.getAnnotationID(), ta);
		}
		return id2annotationMap;
	}

	@Test
	public void dummyTestMethod() throws Exception {
		// dummy test to allow this class to have a Test.java suffix, and thus not be included in
		// the code coverage
		// computations
		Assert.assertTrue(true);
	}

}
