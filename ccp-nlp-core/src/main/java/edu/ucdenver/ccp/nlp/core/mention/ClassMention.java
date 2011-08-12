package edu.ucdenver.ccp.nlp.core.mention;


import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.mention.comparison.IdenticalMentionComparator;

/**
 * <p>
 * The architecture of the mention (annotation type) structure was designed to be flexible in its ability to represent
 * virtually any frame-based class. The design mirrors the mention structure used in Knowtator, an annotation tool
 * developed within the Center for Computational Pharmacology. In short, this structure is analogous to the classic
 * frame/slot structure introduced by Minsky. The class mention (or concept mention as it is described in the Knowtator
 * documentation) can be thought of as a frame. It represents the semantic type of an annotation. Examples of class
 * mentions include, but are not limited to, things such as entities (protein, cell type, disease, etc.) or more complex
 * relations (e.g. interaction, transport, regulation, etc.). A class mention can have attributes. These attributes are
 * represented as slot mentions (as a frame can have slots). The current structure uses two types of slot mentions.
 * Complex slot mentions are slots that have other class mentions as their fillers, while non-complex slot mentions are
 * filled by object, which are typically just <code>Strings</code>.
 * 
 * <p>
 * To illustrate the mention structure described here, let us use as an example the protein transport frame (shown
 * below) and the example sentence:<br>
 * Src relocated the KDEL receptor from the Golgi apparatus to the endoplasmic reticulum. (PMID: 12975382)
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
 * The mention of protein transport in the example sentence above can be represented using the following procedure:
 * <ol>
 * <li>Create an annotation for the text ``Src." Link this CCPTextAnnotation to a CCPClassMention of type protein. Add
 * to the CCPClassMention a CCPNonComplexSlotMention of type Entrez gene ID with a single slot value of ``6714."</li>
 * <li>Create an annotation for ``KDEL
 * receptor" with a protein class mention containing an Entrez gene ID slot filled with ``10945"</li>
 * <li>Create an annotation for ``Golgi apparatus" with a class mention of type Golgi Apparatus.</li>
 * <li>Create an annotation for ``endoplasmic reticulum" with a class mention of type Endoplasmic Reticulum.</li>
 * <li>Create the protein transport annotation. This annotation will have four CCPComplexSlotMentions, one for each
 * slot: transported entity, transporter, source, and destination. The fillers for the four complex slot mentions will
 * be the class mentions created earlier.</li>
 * </ol>
 * 
 * <p>
 * The generated protein transport class mention structure for this example is shown below.
 * 
 * <pre>
 *   class mention, name=&quot;protein transport&quot;
 *   - complex slot mention, name=&quot;transported entity&quot;
 *   - class mention, name=\textit{protein} ``KDEL receptor&quot;
 *   - non-complex slot mention, name=\textit{Entrez gene ID}, value=``6714&quot;
 *   - complex slot mention, name=\textit{transporter}
 *   - class mention, name=\textit{protein} ``Src&quot;
 *   - non-complex slot mention, name=\textit{Entrez gene ID}, value=``10945&quot;
 *   - complex slot mention, name=\textit{source}
 *   - class mention, name=\textit{Golgi Apparatus} ``Golgi apparatus&quot;
 *   - complex slot mention, name=\textit{destination}
 *   - class mention, name=\textit{Endoplasmic Reticulum} ``endoplasmic reticulum&quot;
 * </pre>
 * 
 * <p>
 * The CCPClassMention is the root of a flexible class structure that can store virtually any frame-based representation
 * of a particular class. Common class mention types include, but are not limited to, such things as entities (protein,
 * cell type, cell line, disease, tissue, etc.) and frames (interaction, transport, regulation, etc.).
 * <p>
 * A class mention optionally has slot mentions which represent attributes of that class. There are two types of slot
 * mentions, complex and non-complex. The difference between complex and non-complex slot mentions is simply the type of
 * filler (or slot value) for each. Complex slot mentions are filled with a class mention, whereas non-complex slot
 * mentions are filled by simple Strings.
 * <p>
 * Just as CCPTextAnnotations are linked to a CCPClassMention, it is sometimes useful to be able to follow a
 * CCPClassMention back to its corresponding CCPTextAnnotation, therefore, this FSArray contains links to the
 * CCPTextAnotation(s) for this class. [NOTE: The use of an array is probably not needed here as there is typically only
 * one occupant in the FSArray. This may be addressed in a future release.]
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public abstract class ClassMention extends Mention implements IClassMention {

	private static Logger logger = Logger.getLogger(ClassMention.class);

//	protected TextAnnotation textAnnotation;

//	public ClassMention(String mentionName, IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {// , TextAnnotation linkedAnnotation) {
//		super(mentionName, traversalTracker, wrappedObjectPlusGlobalVars);
//		// setTextAnnotationMemberVariable(linkedAnnotation);
//	}

	public ClassMention(IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {// , TextAnnotation linkedAnnotation) {
		super(traversalTracker, wrappedObjectPlusGlobalVars);
		// setTextAnnotationMemberVariable(linkedAnnotation);
	}

	
//	protected void initializeMention() {
//		// primitiveSlotMentionsMap = new HashMap<String, PrimitiveSlotMention>();
//		// complexSlotMentionsMap = new HashMap<String, ComplexSlotMention>();
//	}

	// @Override
	// protected void initializeFromWrappedMention(Object wrappedObject) throws Exception {
	// classSpecificInitialization(wrappedObject);
	// // fillSlotMentionMaps();
	//
	// }

//	@Override
//	public void setMentionID(long mentionID) {
//		if (hasWrappedMention) {
//			setWrappedObjectMentionID(mentionID);
//		}
//		super.setMentionID(mentionID);
//	}

//	protected abstract void setWrappedObjectMentionID(long mentionID);

	// protected abstract void classSpecificInitialization(Object wrappedObject) throws Exception;

	// protected abstract void setTextAnnotationMemberVariable(TextAnnotation linkedAnnotation);

//	public TextAnnotation getTextAnnotation() {
//		return textAnnotation;
//	}

//	public void setTextAnnotation(TextAnnotation textAnnotation, boolean linkToWrappedMention) {
//		this.textAnnotation = textAnnotation;
//		if (hasWrappedMention && linkToWrappedMention) {
//			setWrappedMentionTextAnnotation(textAnnotation);
//		}
//	}
//
//	protected abstract void setWrappedMentionTextAnnotation(TextAnnotation textAnnotation);

	//
	// public abstract Collection<ComplexSlotMention> getComplexSlotMentions();
	//
	// public abstract void setComplexSlotMentions(Map<String, ComplexSlotMention> complexSlotMentionsMap);
	//
	// protected abstract void setWrappedMentionComplexSlotMentions(Map<String, ComplexSlotMention>
	// complexSlotMentionsMap);
	//
	// public abstract Collection<PrimitiveSlotMention> getPrimitiveSlotMentions();
	//
	// public abstract void setSlotMentions(Map<String, PrimitiveSlotMention> slotMentionsMap);
	//
	// protected abstract void setWrappedMentionPrimitiveSlotMentions(Map<String, PrimitiveSlotMention>
	// slotMentionsMap);
	//
	// public abstract void addComplexSlotMention(ComplexSlotMention csm);
	//
	// protected abstract void addComplexSlotMentionToWrappedMention(ComplexSlotMention csm) throws Exception;
	//
	// public abstract void addSlotMention(PrimitiveSlotMention sm);
	//
	// protected abstract void addPrimitiveSlotMentionToWrappedMention(PrimitiveSlotMention sm) throws Exception;

	// /**
	// * Returns the unique names of all slotmentions
	// *
	// * @return
	// */
	// public abstract Collection<String> getSlotMentionNames();
	//
	// public abstract PrimitiveSlotMention getSlotMentionByName(String slotMentionName);
	//
	// public abstract ComplexSlotMention getComplexSlotMentionByName(String complexSlotMentionName);
	//
	// protected abstract ComplexSlotMention createComplexSlotMention(String slotMentionName);
	//
	// protected abstract PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, Object slotValue);

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		String returnStr = getIndentString(indentLevel) + "CLASS MENTION: " + getMentionName();
		if (showReferencingAnnotationInfo) {
			returnStr += (" " + getReferencingAnnotationString());
		}
		return returnStr;
	}

	/**
	 * Returns a compact representation of the referencing annotation: coveredText [spanStart..spanEnd]
	 * 
	 * @return
	 */
	private String getReferencingAnnotationString() {
		String annotationString = "";
		String spanStr = "";
		if (this.getTextAnnotation() != null) {
			annotationString = this.getTextAnnotation().getCoveredText();
			spanStr = this.getTextAnnotation().getAggregateSpan().toString();
		}

		return "\"" + annotationString + "\"\t" + spanStr;
	}

	// public String getHashKey() {
	// String hashKey = "";
	// try {
	// hashKey = textAnnotation.getHashKey();
	// } catch (Exception e) {
	// logger
	// .error("ClassMention is missing referential TextAnnotation, therefore proper hashkey cannot be produced... ClassMention: "
	// + this.toString());
	// e.printStackTrace();
	// }
	//
	// return hashKey;
	// }
    
    @Override
    public int hashCode() {
    	return getSingleLineRepresentation().hashCode();
    }

	/**
	 * The default compareTo uses the IdenticalClassMentionComparator()
	 */
	public int compareTo(Mention m) {
		if (m instanceof ClassMention) {
			ClassMention cmToCompare = (ClassMention) m;
			IdenticalMentionComparator icmc = new IdenticalMentionComparator();
			return icmc.compare(this, cmToCompare);
		} else {
			logger.warn("Unexpected object when comparing to ClassMention: object = " + m.getClass().getName());
			return -1;
		}

	}

	/**
	 * The default equals() method uses the IdenticalClassMentionComparator.
	 */
	@Override
	public boolean equals(Object classMentionToEquate) {
		if (!(classMentionToEquate instanceof ClassMention)) {
			throw new ClassCastException("A ClassMention object expected.");
		} else {
			ClassMention cm = (ClassMention) classMentionToEquate;

			IdenticalMentionComparator icmc = new IdenticalMentionComparator();

			if (icmc.compare(this, cm) == 0) {
				return true;
			} else {
				return false;
			}
		}
	}

}