/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitynormalization.protein;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.fileparsers.obo.OboUtil;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio.impl.CcpGeneIdAnnotationDecorator;

/**
 * If there are multiple PRO terms for a single protein, this AE looks to see if they have a
 * parent-child relationship. If they do, then the child is kept and the parent is discarded.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ProTermDisambiguatorAE extends JCasAnnotator_ImplBase {

	public static final String PARAM_PRO_OBO_FILE = ConfigurationParameterFactory.createConfigurationParameterName(
			ProTermDisambiguatorAE.class, "proOboFile");

	@ConfigurationParameter(mandatory = true, description = "pro.obo file to use")
	private File proOboFile;

	private OboUtil oboUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			oboUtil = new OboUtil(proOboFile, CharacterEncoding.ISO_8859_1);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas); annotIter.hasNext();) {
			CCPTextAnnotation ccpTa = annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (mentionName.equals("protein")) {
				WrappedCCPTextAnnotation wrappedTa = new WrappedCCPTextAnnotation(ccpTa);
				PrimitiveSlotMention<String> prIdSlot = wrappedTa.getClassMention().getPrimitiveSlotMentionByName(
						CcpGeneIdAnnotationDecorator.PRO_ID_SLOT_NAME);
				if (prIdSlot != null && prIdSlot.getSlotValues().size() > 1) {
					Collection<String> disambiguatedSlotValues = disambiguatePrIds(prIdSlot.getSlotValues());
//					System.out.println("ProTermDisamb: slotValuesBefore: " + prIdSlot.getSlotValues() + " slotValuesAfter: " + disambiguatedSlotValues);
					prIdSlot.setSlotValues(disambiguatedSlotValues);
				}
			}
		}

	}

	/**
	 * TODO finish this then test protein exchanger then see if annotation-to-rdf passes (there
	 * should be no more protein annotations in the cas)
	 * 
	 * @param slotValues
	 * @return
	 */
	private Collection<String> disambiguatePrIds(Collection<String> slotValues) {
		Collection<String> filteredSlotValues = new HashSet<String>();

		for (String sv1 : slotValues) {
			for (String sv2 : slotValues) {
				if (!sv1.equals(sv2)) {
					if (oboUtil.isDescendent(sv1, sv2))
						filteredSlotValues.add(sv1);
					else if (oboUtil.isDescendent(sv2, sv1))
						filteredSlotValues.add(sv2);
					else {
						filteredSlotValues.add(sv1);
						filteredSlotValues.add(sv2);
					}
				}
			}
		}

		return filteredSlotValues;
	}

}
