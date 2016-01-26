package edu.ucdenver.ccp.nlp.doc2txt.pmc;

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

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.doc2txt.XsltConverter;
import edu.ucdenver.ccp.nlp.doc2txt.pmc.PmcDtdClasspathResolver;
import edu.ucdenver.ccp.nlp.doc2txt.pmc.PmcXslLocator;

public class XsltConverterTest {

	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	static final String inputFile = "/14607334.xml";

	@Test
	public void testPmc() {
		InputStream xmlStream = this.getClass().getResourceAsStream(inputFile);
		XsltConverter converter = new XsltConverter(new PmcDtdClasspathResolver());
		String output = converter.convert(xmlStream, PmcXslLocator.getPmcXslStream());
		assertEquals(

				"<?xml version=\"1.0\" encoding=\"UTF-8\"?> <doc><TITLE> PINK1 Protects against Oxidative Stress by Phosphorylating Mitochondrial Chaperone TRAP1 </TITLE>     *    *     <ABSTRACT> <PARAGRAPH> Mutations in the<ITALICS> PTEN induced putative kinase 1 (PINK1) </ITALICS>gene cause an autosomal recessive form of Parkinson disease (PD). So far, no substrates of PINK1 have been reported, and the mechanism by which PINK1 mutations lead to neurodegeneration is unknown. Here we report the identification of TNF receptor-associated protein 1 (TRAP1), a mitochondrial molecular chaperone also known as heat shock protein 75 (Hsp75), as a cellular substrate for PINK1 kinase. PINK1 binds and colocalizes with TRAP1 in the mitochondria and phosphorylates TRAP1 both in vitro and in vivo. We show that PINK1 protects against oxidative-stress-induced cell death by suppressing cytochrome c release from mitochondria, and this protective action of PINK1 depends on its kinase activity to phosphorylate TRAP1. Moreover, we find that the ability of PINK1 to promote TRAP1 phosphorylation and cell survival is impaired by PD-linked PINK1 G309D, L347P, and W437X mutations. Our findings suggest a novel pathway by which PINK1 phosphorylates downstream effector TRAP1 to prevent oxidative-stress-induced apoptosis and implicate the dysregulation of this mitochondrial pathway in PD pathogenesis. </PARAGRAPH>\n"
						+ " </ABSTRACT><ABSTRACT> <TITLE>  </TITLE><PARAGRAPH> Parkinson disease (PD) is characterized by the selective loss of midbrain dopaminergic neurons. Although the cause of PD is unknown, pathological analyses have suggested the involvement of oxidative stress and mitochondrial dysfunction. Recently, an inherited form of early-onset PD has been linked to mutations in both copies of the gene encoding the mitochondrial protein PINK1. Furthermore, increasing evidence indicates that single-copy mutations in PINK1 are a significant risk factor in the development of later-onset PD. Here we show that PINK1 is a protein kinase that phosphorylates the mitochondrial molecular chaperone TRAP1 to promote cell survival. We find that PINK1 normally protects against oxidative-stress-induced cell death by suppressing cytochrome c release from mitochondria. The PINK1 mutations linked to PD impair the ability of PINK1 to phosphorylate TRAP1 and promote cell survival. Our findings reveal a novel anti-apoptotic signaling pathway that is disrupted by mutations in PINK1. We suggest that this pathway has a role in PD pathogenesis and may be a target for therapeutic intervention. </PARAGRAPH>\n"
						+ " </ABSTRACT><ABSTRACT> <PARAGRAPH> Mutations in the gene that codes for PINK1 cause a common form of Parkinson disease. Here the authors show that PINK1 phosphorylates TRAP1, which suppresses apoptotic release of cytochrome c from mitochondria. </PARAGRAPH>\n"
						+ " </ABSTRACT><SECTION> <TITLE> Introduction </TITLE><PARAGRAPH> Parkinson disease (PD) is the second most common neurodegenerative disease, characterized by the selective loss of dopaminergic neurons in the substantia nigra [ 1 ]. The cause of PD, particularly the sporadic disease, is unclear, but it likely involves both genetic and environmental factors. Genetic studies have identified a number of genes associated with familial PD [ 2 ]. Postmortem analyses reveal a deficiency in the mitochondrial complex I function in patients with sporadic PD [ 3 ]. Furthermore, exposure to environmental toxins that inhibit the mitochondrial complex I can lead to PD-like phenotypes in animal models [ 4 ], suggesting the involvement of mitochondrial dysfunction in PD pathogenesis. </PARAGRAPH>\n"
						+ "<PARAGRAPH> Mutations in the<ITALICS> PTEN induced putative kinase 1 (PINK1) </ITALICS>gene were originally discovered in three pedigrees with recessively inherited PD. Two homozygous<ITALICS> PINK1 </ITALICS>mutations were initially identified: a truncating nonsense mutation (W437X) and a G309D missense mutation [ 5 ]. Subsequently, multiple additional types of PD-linked mutations or truncations in<ITALICS> PINK1 </ITALICS>have been reported, making<ITALICS> PINK1 </ITALICS>the second most common causative gene of recessive PD [ 6 , 7 ]. Interestingly, despite autosomal recessive transmission of<ITALICS> PINK1 </ITALICS>-linked early-onset PD, a number of heterozygous mutations affecting only one<ITALICS> PINK1 </ITALICS>allele have been associated with late-onset PD [ 6 – 10 ]. The pathogenic mechanisms by which<ITALICS> PINK1 </ITALICS>mutations lead to neurodegeneration are unknown. </PARAGRAPH>\n"
						+ "<PARAGRAPH> <ITALICS> PINK1 </ITALICS>encodes a 581-amino-acid protein with a predicted N-terminal mitochondrial targeting sequence and a conserved serine/threonine kinase domain [ 5 ]. PINK1 protein has been shown to localize in the mitochondria [ 5 , 11 – 13 ] and exhibit autophosphorylation activity in vitro [ 11 , 12 , 14 ]. The in vivo substrate(s) and biochemical function of PINK1 remain unknown. In cultured mammalian cells, overexpression of wild-type PINK1 protects cells against apoptotic stimuli [ 5 , 15 ], whereas small interfering RNA (siRNA)–mediated depletion of PINK1 increases the susceptibility to apoptotic cell death [ 16 ]. In<ITALICS> Drosophila, </ITALICS>loss of PINK1 leads to mitochondrial defects and degeneration of muscle and dopaminergic neurons [ 17 – 20 ]. Despite ample evidence indicating an essential role of PINK1 in cytoprotection, the mechanism by which PINK1 protects against apoptosis is not understood. </PARAGRAPH>\n"
						+ "<PARAGRAPH> Here, we describe the characterization of mitochondrial serine/threonine kinase PINK1 and report the identification of TNF receptor-associated protein 1 (TRAP1), a mitochondrial molecular chaperone also known as heat shock protein 75 (Hsp75), as a PINK1 substrate. Our results suggest that PINK1 protects against oxidative-stress-induced apoptosis by phosphorylating downstream effector TRAP1, and provide novel insights into the pathogenic mechanisms of PINK1 mutations in causing PD. </PARAGRAPH>\n"
						+ " </SECTION><DEFINITION> <PARAGRAPH> 4′,6-diamidino-2-phenylindole </PARAGRAPH>\n"
						+ " </DEFINITION><DEFINITION> <PARAGRAPH> K219A/D362A/D384A </PARAGRAPH>\n"
						+ " </DEFINITION><DEFINITION> <PARAGRAPH> 3-(4,5-dimethylthiazol-2-yl)-2,5-diphenyltetrazolium bromide </PARAGRAPH>\n"
						+ " </DEFINITION><DEFINITION> <PARAGRAPH> non-targeting </PARAGRAPH>\n"
						+ " </DEFINITION><DEFINITION> <PARAGRAPH> Parkinson disease </PARAGRAPH>\n"
						+ " </DEFINITION><DEFINITION> <PARAGRAPH> PTEN induced putative kinase 1 </PARAGRAPH>\n"
						+ " </DEFINITION><DEFINITION> <PARAGRAPH> standard error of the mean </PARAGRAPH>\n"
						+ " </DEFINITION><DEFINITION> <PARAGRAPH> small interfering RNA </PARAGRAPH>\n"
						+ " </DEFINITION><DEFINITION> <PARAGRAPH> TNF receptor-associated protein 1 </PARAGRAPH>\n"
						+ " </DEFINITION><TITLE> Figures and Tables </TITLE><FIGURE> Interaction of TRAP1 with Wild-Type and Mutant PINK1<PARAGRAPH> (A) Lysates of HeLa cells expressing C-terminally FLAG-tagged wild-type PINK1 (Input) were affinity purified with anti-FLAG M2 affinity gel, and the eluate fractions were analyzed by immunoblotting with anti-FLAG antibody. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (B) Affinity-purified proteins from PINK1-transfected cells and vector control were resolved on SDS-PAGE and detected by Ponceau S staining. Arrows indicate three bands identified by mass spectrometry as TRAP1, PINK1f, and PINK1p. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (C) Specificity of the polyclonal anti-PINK1 antibody. Homogenates (50 μg of protein per lane) from rat brain, liver, untransfected PC12 cells, and transfected PC12 cells expressing wild-type (WT) or mutant PINK1 were analyzed by immunoblotting with anti-PINK1 and anti-actin antibodies. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (D) Endogenous PINK1 interacts with TRAP1. PC12 cell lysates (Input) were immunoprecipitated with anti-TRAP1 antibody, followed by immunoblotting with anti-PINK1 and anti-TRAP1 antibodies. IgG HC, IgG heavy chain. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (E) Lysates from PC12 cells transfected with FLAG-tagged wild-type or mutant PINK1 were immunoprecipitated with anti-FLAG antibody, followed by immunoblotting using anti-TRAP1 and anti-FLAG antibodies. </PARAGRAPH>\n"
						+ " </FIGURE><FIGURE> PINK1 and TRAP1 Colocalize in the Mitochondrial Inner Membrane and the Intermembrane Space<PARAGRAPH> (A) Post-nuclear supernatants of PC12 cells were fractionated on a 5%–15% linear Optiprep gradient, and the fractions were analyzed by immunoblotting for PINK1, TRAP1, TIMM23, and CANX. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (B) Mitochondria (Mito) isolated from PC12 cells were fractionated into matrix, inner mitochondrial membrane (IM), intermembrane space (IMS), and outer mitochondrial membrane (OM) fractions, and analyzed by immunoblotting for PINK1, TRAP1, and markers of mitochondrial subcompartments: HSPD1 (matrix), TIMM23 (IM), cytochrome c (IMS), and VDAC (OM). </PARAGRAPH>\n"
						+ " </FIGURE><FIGURE> Phosphorylation of TRAP1 by Wild-Type and Mutant PINK1<PARAGRAPH> (A) In vitro kinase assays were performed by incubation of purified TRAP1 with [γ32-P] ATP in the absence (control) or presence of FLAG-tagged wild-type (WT) or mutant PINK1 proteins as indicated. Phosphorylated TRAP1 was visualized by autoradiography (top panel). PINK1 and TRAP1 proteins used in the kinase assays were shown by immunoblotting with anti-TRAP1 (middle panel) and anti-FLAG antibodies (bottom panel). </PARAGRAPH>\n"
						+ "<PARAGRAPH> (B) Normalized levels of in vitro TRAP1 phosphorylation by wild-type or mutant PINK1. Data represent mean ± standard error of the mean (SEM) from three independent experiments.*Significantly different from the wild-type PINK1 (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
						+ "<PARAGRAPH> (C) PC12 cells expressing wild-type or mutant PINK1 or vector-transfected controls were treated with 400 μM H2O2as indicated. In vivo phosphorylation of endogenous TRAP1 was determined by immunoprecipitation with anti-TRAP1 antibody followed by immunoblotting using anti-phosphoserine (upper panel) and anti-TRAP1 (lower panel) antibodies. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (D) Normalized levels of in vivo TRAP1 phosphorylation by wild-type or mutant PINK1. Data represent mean ± SEM from three independent experiments.aSignificantly different from the corresponding H2O2-treated vector-transfected controls (<ITALICS> p </ITALICS>&lt; 0.01).bSignificantly different from the wild-type PINK1-transfected cells (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
						+ "<PARAGRAPH> AU, arbitrary units. </PARAGRAPH>\n"
						+ " </FIGURE><FIGURE> Wild-Type PINK1, but Not Kinase-Dead or PD-Linked Mutant PINK1, Protects against Oxidative-Stress-Induced Apoptosis<PARAGRAPH> (A) PC12 cells expressing wild-type (WT) or mutant PINK1 or vector-transfected controls were treated with 400 μM H2O2for 16 h. The extent of cell survival was assessed by using the MTT assay. Data represent mean ± SEM from three independent experiments.aSignificantly different from the vector-transfected controls (<ITALICS> p </ITALICS>&lt; 0.01).bSignificantly different from the wild-type PINK1-transfected cells (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
						+ "<PARAGRAPH> (B) Immunoblot analysis of cytochrome c (Cyt. c) in the cytosol and mitochondria fractions isolated from PC12 cells expressing wild-type or mutant PINK1 after treatment with 400 μM H2O2for the indicated times. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (C) The level of cytochrome c released to the cytosol is normalized to the total level of cytochrome c in each cell sample. Data represent mean ± SEM from three independent experiments.aSignificantly different from the corresponding H2O2-treated vector-transfected controls (<ITALICS> p </ITALICS>&lt; 0.01).bSignificantly different from the wild-type PINK1-transfected cells (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
						+ " </FIGURE><FIGURE> PINK1 Knockdown Reduces TRAP1 Phosphorylation and Protection against Oxidative Stress<PARAGRAPH> (A) PC12 cells were transfected with vehicle (control), NT siRNA, or PINK1-specific siRNAs (PINK1 siRNA-1 and PINK1 siRNA-2). The levels of PINK1 and actin in the cell lysates were analyzed by immunoblotting with anti-PINK1 and anti-actin antibodies. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (B) PC12 cells transfected with the indicated siRNA or vehicle (control) were treated with 400 μM H2O2for the indicated times. In vivo phosphorylation of endogenous TRAP1 was determined by immunoprecipitation with anti-TRAP1 antibody followed by immunoblotting using anti-phosphoserine and anti-TRAP1 antibodies. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (C) Normalized levels of in vivo TRAP1 phosphorylation in the control and siRNA-transfected cells. Data represent mean ± SEM from three independent experiments.*Significantly different from the corresponding H2O2-treated control cells (<ITALICS> p </ITALICS>&lt; 0.01). AU, arbitrary units. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (D) PC12 cells transfected with vehicle (control) or the indicated siRNAs were treated with 400 μM H2O2for 16 h. Cell viability was assessed by using the MTT assay. Data represent mean ± SEM from three independent experiments.*Significantly different from the H2O2-treated control cells (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
						+ "<PARAGRAPH> (E) PC12 cells co-transfected with an expression vector encoding enhanced green fluorescent protein (pEGFP) and vehicle (control) or indicated siRNAs were treated with 400 μM H2O2for 16 h. Transfected cells were shown by the green fluorescence emitted by green fluorescent protein (GFP), and nuclear morphology was visualized by DAPI staining (blue). Arrowheads indicate transfected cells with apoptotic nuclei. Scale bar, 10 μm. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (F) Apoptosis is expressed as the percentage of transfected cells with apoptotic nuclear morphology. Data represent mean ± SEM from three independent experiments.*Significantly different from the H2O2-treated control cells (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
						+ " </FIGURE><FIGURE> PINK1 Depletion Increases Cytochrome c Release from Mitochondria<PARAGRAPH> (A) PC12 cells transfected with the indicated siRNA or vehicle (control) were treated with 400 μM H2O2for the indicated times. The levels of cytochrome c (Cyt. c) and actin in the cytosol were determined by immunoblotting with anti-cytochrome c and anti-actin antibodies. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (B) The level of cytochrome c released to the cytosol is normalized to the level of actin in each cell sample. Data represent mean ± SEM from three independent experiments.*Significantly different from the corresponding H2O2-treated control cells (<ITALICS> p </ITALICS>&lt; 0.01). AU, arbitrary units. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (C) PC12 cells co-transfected with pEGFP and vehicle (control) or indicated siRNAs were treated with 400 μM H2O2for 16 h. Cell morphology was imaged by using phase-contrast microscopy (grey), transfected cells were visualized by the green fluorescence emitted by green fluorescent protein (GFP), and the cellular distribution of cytochrome c was detected by immunostaining with anti-cytochrome c antibody (red). Transfected cells with mitochondrial cytochrome c staining are indicated by arrows, and those with diffuse, cytosolic cytochrome c staining are indicated by arrowheads. Scale bar, 10 μm. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (D) Quantification of the percentage of transfected cells showing cytochrome c release. Data represent mean ± SEM from three independent experiments.*Significantly different from the H2O2-treated control cells (<ITALICS> p </ITALICS>&lt; 0.05). </PARAGRAPH>\n"
						+ " </FIGURE><FIGURE> TRAP1 Depletion Abolishes the Effects of Wild-Type and Mutant PINK1 on Cell Vulnerability to Oxidative Stress<PARAGRAPH> (A) PC12 cells were transfected with vehicle (control), NT siRNA, or TRAP1-specific siRNAs (TRAP1 siRNA-1 and TRAP1 siRNA-2). The levels of TRAP1 and actin in the cell lysates were analyzed by immunoblotting with anti-TRAP1 and anti-actin antibodies. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (B) PC12 cells treated with TRAP1 siRNA-2 or NT siRNA were either untransfected (UT) or transfected with wild-type (WT) or mutant PINK1 as indicated. Vehicle-treated, non-transfected PC12 cells were used as the control. Cells were exposed to 400 μM H2O2for 16 h, and the extent of cell survival was assessed by using the MTT assay. Data represent mean ± SEM from three independent experiments. *,<ITALICS> p </ITALICS>&lt; 0.05; ns, not significant. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (C) PC12 cells co-transfected with pEGFP and vehicle (control) or indicated siRNAs and PINK1 plasmids were treated with 400 μM H2O2for 16 h, and the extent of apoptosis was determined by morphological analysis of DAPI-stained nuclei. The percentage of transfected cells with apoptotic nuclear morphology was quantified. Data represent mean ± SEM from three independent experiments.*,<ITALICS> p </ITALICS>&lt; 0.05; ns, not significant. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (D) The levels of cytochrome c (Cyt. c) and actin in the cytosol fractions from cells described in (B) were determined by immunoblotting with anti-cytochrome c and anti-actin antibodies. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (E) The level of cytochrome c released to the cytosol is normalized to the level of actin in each cell sample. Data represent mean ± SEM from three independent experiments.*,<ITALICS> p </ITALICS>&lt; 0.01; ns, not significant. AU, arbitrary units. </PARAGRAPH>\n"
						+ "<PARAGRAPH> (F) PC12 cells co-transfected with pEGFP and vehicle (control) or indicated siRNAs and PINK1 plasmids were treated with 400 μM H2O2for 16 h, and the cellular distribution of cytochrome c was detected by immunostaining with anti-cytochrome c antibody. The percentage of transfected cells showing cytochrome c release was quantified. Data represent mean ± SEM from three independent experiments.*,<ITALICS> p </ITALICS>&lt; 0.05; ns, not significant. </PARAGRAPH>\n"
						+ " </FIGURE><PARAGRAPH> All authors conceived and designed the experiments. JWP, JAO, and LSC performed the experiments and analyzed the data. LSC contributed reagents/materials/analysis tools. JWP, JAO, and LL wrote the paper. </PARAGRAPH>\n"
						+ "<PARAGRAPH> This work was supported by National Institutes of Health grants NS050650 (LSC) and NS047199 and AG021489 (LL). </PARAGRAPH>\n"
						+ "<PARAGRAPH> The authors have declared that no competing interests exist. </PARAGRAPH>\n"
						+ "</doc> ", output);
	}

}
