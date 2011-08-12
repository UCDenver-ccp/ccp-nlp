package edu.ucdenver.ccp.rdf.craft;

import java.net.URI;
import java.net.URISyntaxException;

import edu.ucdenver.ccp.identifier.publication.PubMedCentralID;
import edu.ucdenver.ccp.identifier.publication.PubMedID;
import edu.ucdenver.ccp.rdf.pmc.PmcUriUtil;
import edu.ucdenver.ccp.rdf.pubmed.PubMedUriUtil;

public enum CraftDocument {

CRAFT_11319941(new PubMedID(11319941),new PubMedCentralID("PMC31432"),"BMC Neurosci-2-_-31432.nxml"),
CRAFT_11532192(new PubMedID(11532192),new PubMedCentralID("PMC48141"),"BMC Genet-2-_-48141.nxml"),
CRAFT_11597317(new PubMedID(11597317),new PubMedCentralID("PMC138691"),"Breast Cancer Res-3-5-138691.nxml"),
CRAFT_11604102(new PubMedID(11604102),new PubMedCentralID("PMC57980"),"BMC Genomics-2-_-57980.nxml"),
CRAFT_11897010(new PubMedID(11897010),new PubMedCentralID("PMC88885"),"BMC Genomics-3-_-88885.nxml"),
CRAFT_12079497(new PubMedID(12079497),new PubMedCentralID("PMC116589"),"BMC Biotechnol-2-_-116589.nxml"),
CRAFT_12546709(new PubMedID(12546709),new PubMedCentralID("PMC149350"),"BMC Ophthalmol-3-_-149350.nxml"),
CRAFT_12585968(new PubMedID(12585968),new PubMedCentralID("PMC149366"),"BMC Neurosci-4-_-149366.nxml"),
CRAFT_12925238(new PubMedID(12925238),new PubMedCentralID("PMC194730"),"BMC Biochem-4-_-194730.nxml"),
CRAFT_14609438(new PubMedID(14609438),new PubMedCentralID("PMC280685"),"BMC Struct Biol-3-_-280685.nxml"),
CRAFT_14611657(new PubMedID(14611657),new PubMedCentralID("PMC329117"),"Genome Biol-4-11-329117.nxml"),
CRAFT_14624252(new PubMedID(14624252),new PubMedCentralID("PMC261889"),"PLoS Biol-1-2-261889.nxml"),
CRAFT_14675480(new PubMedID(14675480),new PubMedCentralID("PMC324396"),"BMC Dev Biol-3-_-324396.nxml"),
CRAFT_14691534(new PubMedID(14691534),new PubMedCentralID("PMC270016"),"PLoS Biol-1-3-270016.nxml"),
CRAFT_14723793(new PubMedID(14723793),new PubMedCentralID("PMC340383"),"BMC Genomics-5-_-340383.nxml"),
CRAFT_14737183(new PubMedID(14737183),new PubMedCentralID("PMC314463"),"PLoS Biol-2-1-314463.nxml"),
CRAFT_15005800(new PubMedID(15005800),new PubMedCentralID("PMC341451"),"BMC Dev Biol-4-_-341451.nxml"),
CRAFT_15018652(new PubMedID(15018652),new PubMedCentralID("PMC362866"),"BMC Dev Biol-4-_-362866.nxml"),
CRAFT_15040800(new PubMedID(15040800),new PubMedCentralID("PMC394351"),"BMC Med-2-_-394351.nxml"),
CRAFT_15061865(new PubMedID(15061865),new PubMedCentralID("PMC400732"),"BMC Neurosci-5-_-400732.nxml"),
CRAFT_15070402(new PubMedID(15070402),new PubMedCentralID("PMC385223"),"BMC Cell Biol-5-_-385223.nxml"),
CRAFT_15207008(new PubMedID(15207008),new PubMedCentralID("PMC441373"),"BMC Evol Biol-4-_-441373.nxml"),
CRAFT_15238161(new PubMedID(15238161),new PubMedCentralID("PMC471547"),"BMC Dev Biol-4-_-471547.nxml"),
CRAFT_15314655(new PubMedID(15314655),new PubMedCentralID("PMC509301"),"PLoS Biol-2-8-509301.nxml"),
CRAFT_15314659(new PubMedID(15314659),new PubMedCentralID("PMC509305"),"PLoS Biol-2-8-509305.nxml"),
CRAFT_15320950(new PubMedID(15320950),new PubMedCentralID("PMC516044"),"BMC Med-2-_-516044.nxml"),
CRAFT_15328533(new PubMedID(15328533),new PubMedCentralID("PMC509410"),"PLoS Biol-2-10-509410.nxml"),
CRAFT_15328538(new PubMedID(15328538),new PubMedCentralID("PMC514537"),"PLoS Biol-2-10-514537.nxml"),
CRAFT_15345036(new PubMedID(15345036),new PubMedCentralID("PMC549712"),"J Biol-3-4-549712.nxml"),
CRAFT_15492776(new PubMedID(15492776),new PubMedCentralID("PMC523229"),"PLoS Biol-2-11-523229.nxml"),
CRAFT_15550985(new PubMedID(15550985),new PubMedCentralID("PMC529315"),"PLoS Biol-2-12-529315.nxml"),
CRAFT_15560850(new PubMedID(15560850),new PubMedCentralID("PMC539250"),"BMC Cancer-4-_-539250.nxml"),
CRAFT_15588329(new PubMedID(15588329),new PubMedCentralID("PMC539297"),"BMC Neurosci-5-_-539297.nxml"),
CRAFT_15615595(new PubMedID(15615595),new PubMedCentralID("PMC545075"),"BMC Dev Biol-4-_-545075.nxml"),
CRAFT_15619330(new PubMedID(15619330),new PubMedCentralID("PMC544401"),"BMC Biotechnol-4-_-544401.nxml"),
CRAFT_15630473(new PubMedID(15630473),new PubMedCentralID("PMC539061"),"PLoS Biol-3-1-539061.nxml"),
CRAFT_15676071(new PubMedID(15676071),new PubMedCentralID("PMC548520"),"BMC Neurosci-6-_-548520.nxml"),
CRAFT_15760270(new PubMedID(15760270),new PubMedCentralID("PMC1064854"),"PLoS Biol-3-4-1074790.nxml"),
CRAFT_15784609(new PubMedID(15784609),new PubMedCentralID("PMC1069131"),"Nucleic Acids Res-33-5-1069131.nxml"),
CRAFT_15819996(new PubMedID(15819996),new PubMedCentralID("PMC1087847"),"BMC Neurosci-6-_-1087847.nxml"),
CRAFT_15836427(new PubMedID(15836427),new PubMedCentralID("PMC1084331"),"PLoS Biol-3-5-1110896.nxml"),
CRAFT_15850489(new PubMedID(15850489),new PubMedCentralID("PMC1097738"),"BMC Neurosci-6-_-1097738.nxml"),
CRAFT_15876356(new PubMedID(15876356),new PubMedCentralID("PMC1142324"),"BMC Neurosci-6-_-1142324.nxml"),
CRAFT_15882093(new PubMedID(15882093),new PubMedCentralID("PMC1110909"),"PLoS Biol-3-6-1149479.nxml"),
CRAFT_15917436(new PubMedID(15917436),new PubMedCentralID("PMC1140370"),"Nucleic Acids Res-33-9-1140370.nxml"),
CRAFT_15921521(new PubMedID(15921521),new PubMedCentralID("PMC1166548"),"BMC Genet-6-_-1166548.nxml"),
CRAFT_15938754(new PubMedID(15938754),new PubMedCentralID("PMC1181811"),"BMC Genet-6-_-1181811.nxml"),
CRAFT_16026622(new PubMedID(16026622),new PubMedCentralID("PMC1180855"),"Respir Res-6-1-1180855.nxml"),
CRAFT_16027110(new PubMedID(16027110),new PubMedCentralID("PMC1175460"),"Nucleic Acids Res-33-12-1175460.nxml"),
CRAFT_16098226(new PubMedID(16098226),new PubMedCentralID("PMC1208879"),"BMC Genet-6-_-1208879.nxml"),
CRAFT_16103912(new PubMedID(16103912),new PubMedCentralID("PMC1183529"),"PLoS Genet-1-1-1183529.nxml"),
CRAFT_16109169(new PubMedID(16109169),new PubMedCentralID("PMC1208873"),"BMC Dev Biol-5-_-1208873.nxml"),
CRAFT_16110338(new PubMedID(16110338),new PubMedCentralID("PMC1186732"),"PLoS Genet-1-2-1193523.nxml"),
CRAFT_16121255(new PubMedID(16121255),new PubMedCentralID("PMC1189073"),"PLoS Genet-1-2-1193529.nxml"),
CRAFT_16121256(new PubMedID(16121256),new PubMedCentralID("PMC1189074"),"PLoS Genet-1-2-1193530.nxml"),
CRAFT_16216087(new PubMedID(16216087),new PubMedCentralID("PMC1255741"),"PLoS Biol-3-11-1283373.nxml"),
CRAFT_16221973(new PubMedID(16221973),new PubMedCentralID("PMC1253828"),"Nucleic Acids Res-33-18-1253828.nxml"),
CRAFT_16255782(new PubMedID(16255782),new PubMedCentralID("PMC1310620"),"BMC Cancer-5-_-1310620.nxml"),
CRAFT_16279840(new PubMedID(16279840),new PubMedCentralID("PMC1283364"),"PLoS Med-2-12-1322285.nxml"),
CRAFT_16362077(new PubMedID(16362077),new PubMedCentralID("PMC1315279"),"PLoS Genet-1-6-1342629.nxml"),
CRAFT_16410827(new PubMedID(16410827),new PubMedCentralID("PMC1326221"),"PLoS Genet-2-1-1353279.nxml"),
CRAFT_16433929(new PubMedID(16433929),new PubMedCentralID("PMC1382200"),"BMC Dev Biol-6-_-1382200.nxml"),
CRAFT_16462940(new PubMedID(16462940),new PubMedCentralID("PMC1359071"),"PLoS Genet-2-2-1378123.nxml"),
CRAFT_16504143(new PubMedID(16504143),new PubMedCentralID("PMC1420314"),"BMC Neurosci-7-_-1420314.nxml"),
CRAFT_16504174(new PubMedID(16504174),new PubMedCentralID("PMC1420271"),"BMC Dev Biol-6-_-1420271.nxml"),
CRAFT_16507151(new PubMedID(16507151),new PubMedCentralID("PMC1526604"),"Arthritis Res Ther-8-2-1526604.nxml"),
CRAFT_16517939(new PubMedID(16517939),new PubMedCentralID("PMC1390687"),"Nucleic Acids Res-34-5-1390687.nxml"),
CRAFT_16539743(new PubMedID(16539743),new PubMedCentralID("PMC1435744"),"BMC Dev Biol-6-_-1435744.nxml"),
CRAFT_16579849(new PubMedID(16579849),new PubMedCentralID("PMC1448208"),"BMC Med Genet-7-_-1448208.nxml"),
CRAFT_16611361(new PubMedID(16611361),new PubMedCentralID("PMC1481595"),"BMC Dev Biol-6-_-1481595.nxml"),
CRAFT_16628246(new PubMedID(16628246),new PubMedCentralID("PMC1440874"),"PLoS Genet-2-4-1449899.nxml"),
CRAFT_16670015(new PubMedID(16670015),new PubMedCentralID("PMC1482699"),"BMC Genomics-7-_-1482699.nxml"),
CRAFT_16700629(new PubMedID(16700629),new PubMedCentralID("PMC1463023"),"PLoS Biol-4-6-1475681.nxml"),
CRAFT_16787536(new PubMedID(16787536),new PubMedCentralID("PMC1533814"),"BMC Dev Biol-6-_-1533814.nxml"),
CRAFT_16800892(new PubMedID(16800892),new PubMedCentralID("PMC1557845"),"BMC Biotechnol-6-_-1557845.nxml"),
CRAFT_16870721(new PubMedID(16870721),new PubMedCentralID("PMC1540739"),"Nucleic Acids Res-34-13-1540739.nxml"),
CRAFT_16968134(new PubMedID(16968134),new PubMedCentralID("PMC1563491"),"PLoS Biol-4-10-1617324.nxml"),
CRAFT_17002498(new PubMedID(17002498),new PubMedCentralID("PMC1564426"),"PLoS Genet-2-9-1584259.nxml"),
CRAFT_17020410(new PubMedID(17020410),new PubMedCentralID("PMC1584416"),"PLoS Biol-4-10-1617330.nxml"),
CRAFT_17022820(new PubMedID(17022820),new PubMedCentralID("PMC1617083"),"BMC Blood Disord-6-_-1617083.nxml"),
CRAFT_17029558(new PubMedID(17029558),new PubMedCentralID("PMC1592239"),"PLoS Genet-2-10-1630446.nxml"),
CRAFT_17069463(new PubMedID(17069463),new PubMedCentralID("PMC1626108"),"PLoS Genet-2-10-1626108.nxml"),
CRAFT_17078885(new PubMedID(17078885),new PubMedCentralID("PMC1635039"),"BMC Dev Biol-6-_-1635039.nxml"),
CRAFT_17083276(new PubMedID(17083276),new PubMedCentralID("PMC1630711"),"PLoS Genet-2-11-1657042.nxml"),
CRAFT_17194222(new PubMedID(17194222),new PubMedCentralID("PMC1713256"),"PLoS Genet-2-12-1756909.nxml"),
CRAFT_17201918(new PubMedID(17201918),new PubMedCentralID("PMC1851382"),"Breast Cancer Res-9-1-1851382.nxml"),
CRAFT_17206865(new PubMedID(17206865),new PubMedCentralID("PMC1761047"),"PLoS Genet-3-1-1781487.nxml"),
CRAFT_17244351(new PubMedID(17244351),new PubMedCentralID("PMC1860061"),"Arthritis Res Ther-9-1-1860061.nxml"),
CRAFT_17425782(new PubMedID(17425782),new PubMedCentralID("PMC1858683"),"BMC Biol-5-_-1858683.nxml"),
CRAFT_17447844(new PubMedID(17447844),new PubMedCentralID("PMC1853120"),"PLoS Genet-3-4-1857728.nxml"),
CRAFT_17465682(new PubMedID(17465682),new PubMedCentralID("PMC1857730"),"PLoS Genet-3-4-1857730.nxml"),
CRAFT_17503968(new PubMedID(17503968),new PubMedCentralID("PMC1868042"),"PLoS Biol-5-6-1892821.nxml"),
CRAFT_17565376(new PubMedID(17565376),new PubMedCentralID("PMC1885825"),"PLoS ONE-2-6-1885825.nxml"),
CRAFT_17590087(new PubMedID(17590087),new PubMedCentralID("PMC1892049"),"PLoS Genet-3-6-1904370.nxml"),
CRAFT_17608565(new PubMedID(17608565),new PubMedCentralID("PMC1914394"),"PLoS Biol-5-7-1914394.nxml"),
CRAFT_17677002(new PubMedID(17677002),new PubMedCentralID("PMC1934399"),"PLoS Genet-3-7-1934399.nxml"),
CRAFT_17696610(new PubMedID(17696610),new PubMedCentralID("PMC1941754"),"PLoS Genet-3-8-1959384.nxml");

private final PubMedID pmid;
private final PubMedCentralID pmcId;
private final String fileName;



private CraftDocument(PubMedID pmid, PubMedCentralID pmcId, String fileName) {
	this.pmid = pmid;
	this.pmcId = pmcId;
	this.fileName = fileName;
}

public PubMedID pmid() {
	return pmid;
}

public PubMedCentralID pmcId() {
	return pmcId;
}

public String fileName() {
	return fileName;
}

public URI pubMedUri() {
	try {
		return PubMedUriUtil.createPubMedUri(pmid.toString());
	} catch (URISyntaxException e) {
		throw new IllegalStateException(e);
	}
}

public URI pubMedCentralUri() {
	try {
		return PmcUriUtil.createPmcUri(pmcId.toString());
	} catch (URISyntaxException e) {
		throw new IllegalStateException(e);
	}
}

public static PubMedCentralID getPmcId(PubMedID pmid) {
	return valueOf(pmid).pmcId();
}

public static CraftDocument valueOf(PubMedID pmid) {
	return CraftDocument.valueOf("CRAFT_" + pmid.toString());
}
	
}
