/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.snp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class SnpIdDetectorTest extends DefaultTestCase {

	public static final String DOCUMENT_TEXT = "METHODS: Ten single nucleotide polymorphisms (SNPs; rs1971050, rs1993465, rs13153937, rs10038177, rs11241095, rs10043631, rs10038058, rs10491424, rs17553936, and rs13186912) spanning almost the entire WDR36 gene were selected and their association with eastern Indian POAG patients was evaluated. Our study pool consisted of 323 POAG patients. Of these 116 were patients who had HTG with intraocular perssure (IOP) >21mmHg and 207 were found to be non-HTG patients (presenting IOP<21mmHg). The study also included 303 participants as controls. The polymorphisms were genotyped in both the patients and the controls using the PCR-RFLP method. Moreover, the SNP that showed significant association was validated by DNA sequencing. The haplotypes were obtained using Haploview 4.1 software. The allele and haplotype frequencies were compared between the patient group and the control group using Pearson's χ(2) test.";
	
	
	@Test
	public void testReferenceSnpIdDetection() {
		SnpIdDetector snpIdDetector = new SnpIdDetector();
		List<TextAnnotation> snpIdAnnots = snpIdDetector.getEntitiesFromText(DOCUMENT_TEXT, "1234");
		assertEquals(10, snpIdAnnots.size());
	}

}
