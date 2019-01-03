
package edu.ucdenver.ccp.nlp.uima.collections.craft;

/*
 * #%L
 * Colorado Computational Pharmacology's CRAFT-related code module
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

public enum CraftReleaseDocument implements CraftDocument {

	CRAFT_11532192(11532192, "PMC48141", "BMC Genet-2-_-48141.nxml"),
	CRAFT_11597317(11597317, "PMC138691", "Breast Cancer Res-3-5-138691.nxml"),
	CRAFT_11897010(11897010, "PMC88885", "BMC Genomics-3-_-88885.nxml"),
	CRAFT_12079497(12079497, "PMC116589", "BMC Biotechnol-2-_-116589.nxml"),
	CRAFT_12546709(12546709, "PMC149350", "BMC Ophthalmol-3-_-149350.nxml"),
	CRAFT_12585968(12585968, "PMC149366", "BMC Neurosci-4-_-149366.nxml"),
	CRAFT_12925238(12925238, "PMC194730", "BMC Biochem-4-_-194730.nxml"),
	CRAFT_14609438(14609438, "PMC280685", "BMC Struct Biol-3-_-280685.nxml"),
	CRAFT_14611657(14611657, "PMC329117", "Genome Biol-4-11-329117.nxml"),
	CRAFT_14723793(14723793, "PMC340383", "BMC Genomics-5-_-340383.nxml"),
	CRAFT_14737183(14737183, "PMC314463", "PLoS Biol-2-1-314463.nxml"),
	CRAFT_15005800(15005800, "PMC341451", "BMC Dev Biol-4-_-341451.nxml"),
	CRAFT_15040800(15040800, "PMC394351", "BMC Med-2-_-394351.nxml"),
	CRAFT_15061865(15061865, "PMC400732", "BMC Neurosci-5-_-400732.nxml"),
	CRAFT_15207008(15207008, "PMC441373", "BMC Evol Biol-4-_-441373.nxml"),
	CRAFT_15314655(15314655, "PMC509301", "PLoS Biol-2-8-509301.nxml"),
	CRAFT_15314659(15314659, "PMC509305", "PLoS Biol-2-8-509305.nxml"),
	CRAFT_15320950(15320950, "PMC516044", "BMC Med-2-_-516044.nxml"),
	CRAFT_15328533(15328533, "PMC509410", "PLoS Biol-2-10-509410.nxml"),
	CRAFT_15345036(15345036, "PMC549712", "J Biol-3-4-549712.nxml"),
	CRAFT_15492776(15492776, "PMC523229", "PLoS Biol-2-11-523229.nxml"),
	CRAFT_15550985(15550985, "PMC529315", "PLoS Biol-2-12-529315.nxml"),
	CRAFT_15588329(15588329, "PMC539297", "BMC Neurosci-5-_-539297.nxml"),
	CRAFT_15630473(15630473, "PMC539061", "PLoS Biol-3-1-539061.nxml"),
	CRAFT_15676071(15676071, "PMC548520", "BMC Neurosci-6-_-548520.nxml"),
	CRAFT_15760270(15760270, "PMC1064854", "PLoS Biol-3-4-1074790.nxml"),
	CRAFT_15819996(15819996, "PMC1087847", "BMC Neurosci-6-_-1087847.nxml"),
	CRAFT_15836427(15836427, "PMC1084331", "PLoS Biol-3-5-1110896.nxml"),
	CRAFT_15876356(15876356, "PMC1142324", "BMC Neurosci-6-_-1142324.nxml"),
	CRAFT_15917436(15917436, "PMC1140370", "Nucleic Acids Res-33-9-1140370.nxml"),
	CRAFT_15921521(15921521, "PMC1166548", "BMC Genet-6-_-1166548.nxml"),
	CRAFT_15938754(15938754, "PMC1181811", "BMC Genet-6-_-1181811.nxml"),
	CRAFT_16098226(16098226, "PMC1208879", "BMC Genet-6-_-1208879.nxml"),
	CRAFT_16103912(16103912, "PMC1183529", "PLoS Genet-1-1-1183529.nxml"),
	CRAFT_16109169(16109169, "PMC1208873", "BMC Dev Biol-5-_-1208873.nxml"),
	CRAFT_16110338(16110338, "PMC1186732", "PLoS Genet-1-2-1193523.nxml"),
	CRAFT_16121255(16121255, "PMC1189073", "PLoS Genet-1-2-1193529.nxml"),
	CRAFT_16121256(16121256, "PMC1189074", "PLoS Genet-1-2-1193530.nxml"),
	CRAFT_16216087(16216087, "PMC1255741", "PLoS Biol-3-11-1283373.nxml"),
	CRAFT_16221973(16221973, "PMC1253828", "Nucleic Acids Res-33-18-1253828.nxml"),
	CRAFT_16255782(16255782, "PMC1310620", "BMC Cancer-5-_-1310620.nxml"),
	CRAFT_16279840(16279840, "PMC1283364", "PLoS Med-2-12-1322285.nxml"),
	CRAFT_16362077(16362077, "PMC1315279", "PLoS Genet-1-6-1342629.nxml"),
	CRAFT_16433929(16433929, "PMC1382200", "BMC Dev Biol-6-_-1382200.nxml"),
	CRAFT_16462940(16462940, "PMC1359071", "PLoS Genet-2-2-1378123.nxml"),
	CRAFT_16504143(16504143, "PMC1420314", "BMC Neurosci-7-_-1420314.nxml"),
	CRAFT_16504174(16504174, "PMC1420271", "BMC Dev Biol-6-_-1420271.nxml"),
	CRAFT_16507151(16507151, "PMC1526604", "Arthritis Res Ther-8-2-1526604.nxml"),
	CRAFT_16539743(16539743, "PMC1435744", "BMC Dev Biol-6-_-1435744.nxml"),
	CRAFT_16579849(16579849, "PMC1448208", "BMC Med Genet-7-_-1448208.nxml"),
	CRAFT_16628246(16628246, "PMC1440874", "PLoS Genet-2-4-1449899.nxml"),
	CRAFT_16670015(16670015, "PMC1482699", "BMC Genomics-7-_-1482699.nxml"),
	CRAFT_16700629(16700629, "PMC1463023", "PLoS Biol-4-6-1475681.nxml"),
	CRAFT_16870721(16870721, "PMC1540739", "Nucleic Acids Res-34-13-1540739.nxml"),
	CRAFT_17002498(17002498, "PMC1564426", "PLoS Genet-2-9-1584259.nxml"),
	CRAFT_17020410(17020410, "PMC1584416", "PLoS Biol-4-10-1617330.nxml"),
	CRAFT_17022820(17022820, "PMC1617083", "BMC Blood Disord-6-_-1617083.nxml"),
	CRAFT_17069463(17069463, "PMC1626108", "PLoS Genet-2-10-1626108.nxml"),
	CRAFT_17078885(17078885, "PMC1635039", "BMC Dev Biol-6-_-1635039.nxml"),
	CRAFT_17083276(17083276, "PMC1630711", "PLoS Genet-2-11-1657042.nxml"),
	CRAFT_17194222(17194222, "PMC1713256", "PLoS Genet-2-12-1756909.nxml"),
	CRAFT_17244351(17244351, "PMC1860061", "Arthritis Res Ther-9-1-1860061.nxml"),
	CRAFT_17425782(17425782, "PMC1858683", "BMC Biol-5-_-1858683.nxml"),
	CRAFT_17447844(17447844, "PMC1853120", "PLoS Genet-3-4-1857728.nxml"),
	CRAFT_17590087(17590087, "PMC1892049", "PLoS Genet-3-6-1904370.nxml"),
	CRAFT_17608565(17608565, "PMC1914394", "PLoS Biol-5-7-1914394.nxml"),
	CRAFT_17696610(17696610, "PMC1941754", "PLoS Genet-3-8-1959384.nxml");

	private final int pmid;
	private final String pmcId;
	private final String fileName;

	private CraftReleaseDocument(int pmid, String pmcId, String fileName) {
		this.pmid = pmid;
		this.pmcId = pmcId;
		this.fileName = fileName;
	}

	public int pmid() {
		return pmid;
	}

	public String pmcId() {
		return pmcId;
	}

	public String originalNxmlFileName() {
		return fileName;
	}

	public static String getPmcId(int pmid) {
		return valueOf(pmid).pmcId();
	}

	public static CraftReleaseDocument valueOf(int pmid) {
		return CraftReleaseDocument.valueOf("CRAFT_" + pmid);
	}

	public String getXmiFileName() {
		return pmid + ".txt.xmi";
	}

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.craft.CraftDocument#craftAnnotatedFileName()
	 */
	public String craftAnnotatedFileName() {
		return pmid + ".txt";
	}

}
