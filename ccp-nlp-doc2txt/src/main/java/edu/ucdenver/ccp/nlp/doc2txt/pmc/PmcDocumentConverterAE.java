package edu.ucdenver.ccp.nlp.doc2txt.pmc;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resourceSpecifier.factory.DeploymentDescriptor;
import org.apache.uima.resourceSpecifier.factory.DeploymentDescriptorFactory;
import org.apache.uima.resourceSpecifier.factory.ServiceContext;
import org.apache.uima.resourceSpecifier.factory.UimaASPrimitiveDeploymentDescriptor;
import org.apache.uima.resourceSpecifier.factory.impl.ServiceContextImpl;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.doc2txt.CcpXmlParser;
import edu.ucdenver.ccp.nlp.doc2txt.CcpXmlParser.Annotation;
import edu.ucdenver.ccp.nlp.doc2txt.XsltConverter;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.uima.util.View_Util;

/**
 * Looks for PubMed Central NXML in the XML view and populates the default UIMA
 * view with a plain text extracted from the nxml. Annotations for documents
 * sections, etc. are also added to the CAS.
 *
 */
public class PmcDocumentConverterAE extends JCasAnnotator_ImplBase {

	/* ==== XML encoding configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the XML file
	 * character encoding; default = UTF-8
	 */
	public static final String PARAM_XML_ENCODING = ConfigurationParameterFactory.createConfigurationParameterName(
			PmcDocumentConverterAE.class, "xmlEncoding");

	/**
	 * The character encoding to use when parsing the XML file
	 */
	@ConfigurationParameter(mandatory = false, description = "The character encoding to use when parsing the XML file", defaultValue = "UTF-8")
	private String xmlEncoding;

	/* ==== XML view configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the XML view;
	 * default=XML
	 */
	public static final String PARAM_XML_VIEW_NAME = ConfigurationParameterFactory.createConfigurationParameterName(
			PmcDocumentConverterAE.class, "xmlViewName");

	/**
	 * The name of the CAS View containing the XML to parse
	 */
	@ConfigurationParameter(mandatory = false, description = "The name of the CAS View containing the XML to parse", defaultValue = "XML")
	private String xmlViewName;

	private Logger logger;
	private XsltConverter xslt;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger = context.getLogger();
		xslt = new XsltConverter(new PmcDtdClasspathResolver());
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
			if (View_Util.viewExists(jCas, xmlViewName)) {
				JCas xmlView = View_Util.getView(jCas, xmlViewName);
				String documentId = UIMA_Util.getDocumentID(xmlView);
				InputStream xmlStream = IOUtils.toInputStream(xmlView.getDocumentText(), xmlEncoding);
				String ccpXml = xslt.convert(xmlStream, PmcXslLocator.getPmcXslStream());

				// convert CCP XML to plain text and add annotations for
				// document sections, etc.
				CcpXmlParser parser = new CcpXmlParser();
				String plainText = parser.parse(ccpXml);

				jCas.setDocumentText(plainText);
				UIMA_Util.setDocumentID(jCas, documentId);

				for (CcpXmlParser.Annotation annot : parser.getAnnotations()) {
					importAnnotationIntoCas(annot, jCas);
				}

			} else {
				logger.log(Level.WARNING, "XML View does not exist in CAS. Cannot populate the default "
						+ "view with plain text because expected XML is not present in the CAS.");
			}
		} catch (CASException | IOException | SAXException e) {
			throw new AnalysisEngineProcessException(e);
		}

	}

	private void importAnnotationIntoCas(Annotation annot, JCas jCas) {
		String annotLine = annot.type + "|" + annot.name + "|" + annot.start + "|" + annot.end + "\n";
		System.out.println("ANNOT: " + annotLine);
	}

	public static AnalysisEngineDescription getDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(PmcDocumentConverterAE.class, tsd);
	}

	public static AnalysisEngineDescription getDescription(TypeSystemDescription tsd, CharacterEncoding xmlEncoding,
			String xmlViewName) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(PmcDocumentConverterAE.class, tsd, PARAM_XML_ENCODING,
				xmlEncoding, PARAM_XML_VIEW_NAME, xmlViewName);
	}

	public static DeploymentDescriptor getDeploymentDescriptor(String brokerUrl) throws ResourceInitializationException {
		ServiceContext context = new ServiceContextImpl("nxml2txt", "Converts nxml from the XML view into plain text in the default view.","descriptor","nxml2txtQueue", brokerUrl);
		UimaASPrimitiveDeploymentDescriptor dd = DeploymentDescriptorFactory.createPrimitiveDeploymentDescriptor(context);
//		dd.getProcessErrorHandlingSettings().setThresholdCount(4);
		dd.setScaleup(2);
		return dd;
	}
	
}
