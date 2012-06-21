package edu.ucdenver.ccp.nlp.core.uima.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceCreationSpecifier;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.component.CasCollectionReader_ImplBase;
import org.uimafit.component.CasConsumer_ImplBase;
import org.uimafit.component.CasMultiplier_ImplBase;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.component.JCasConsumer_ImplBase;
import org.uimafit.component.JCasFlowController_ImplBase;
import org.uimafit.component.JCasMultiplier_ImplBase;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.io.StreamUtil;
import edu.ucdenver.ccp.common.string.RegExPatterns;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.common.string.StringUtil;

/**
 * Used during the build process, this class is used to automatically generate UIMA descriptors
 * serialized as XML
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class XmlDescriptorWriter {

	private static final Set<Class<?>> UIMAFIT_COMPONENT_CLASSES = getUimaFitComponentClasses();

	/**
	 * @return a Set containing the available UimaFIT UIMA component class implementations
	 */
	public static Set<Class<?>> getUimaFitComponentClasses() {
		Set<Class<?>> componentClasses = new HashSet<Class<?>>();
		componentClasses.add(CasAnnotator_ImplBase.class);
		componentClasses.add(CasCollectionReader_ImplBase.class);
		componentClasses.add(CasConsumer_ImplBase.class);
		componentClasses.add(CasMultiplier_ImplBase.class);
		componentClasses.add(JCasAnnotator_ImplBase.class);
		componentClasses.add(JCasCollectionReader_ImplBase.class);
		componentClasses.add(JCasConsumer_ImplBase.class);
		componentClasses.add(JCasMultiplier_ImplBase.class);
		componentClasses.add(JCasFlowController_ImplBase.class);
		return componentClasses;
	}

	/**
	 * Analysis components that want to have a descriptor automatically generated during the project
	 * build must have a method with the name specified by this constant
	 */
	public static final String EXPORT_XML_DESCRIPTOR_METHOD_NAME = "exportXmlDescriptor";

	/**
	 * Given a CAS annotator class and an initialized {@link AnalysisEngineDescription} this method
	 * outputs the XML descriptor to file in the specified base descriptor directory.
	 * 
	 * @param cls
	 * @param rcs
	 * @param baseDescriptorDirectory
	 */
	public static void exportXmlDescriptor(Class<?> cls, ResourceCreationSpecifier rcs, File baseDescriptorDirectory) {
		try {
			rcs.doFullValidation();
			File descriptorFile = new File(baseDescriptorDirectory, cls.getName().replaceAll("\\.", File.separator)
					+ ".xml");
			FileUtil.mkdir(descriptorFile.getParentFile());
			rcs.toXML(StreamUtil.getEncodingSafeOutputStream(descriptorFile, CharacterEncoding.UTF_8));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ResourceInitializationException e) {
			throw new RuntimeException("XML Descriptor validation FAILED!", e);
		}
	}

	/**
	 * Given a {@CollectionReader_ImplBase} class and an initialized
	 * {@link CollectionReaderDescription} this method outputs the XML descriptor to file in the
	 * specified base descriptor directory.
	 * 
	 * @param cls
	 * @param crd
	 * @param baseDescriptorDirectory
	 */
	public static void exportXmlDescriptor(Class<? extends CollectionReader_ImplBase> cls,
			CollectionReaderDescription crd, File baseDescriptorDirectory) {
		try {
			crd.doFullValidation();
			File descriptorFile = new File(baseDescriptorDirectory, cls.getName().replaceAll("\\.", File.separator)
					+ ".xml");
			FileUtil.mkdir(descriptorFile.getParentFile());
			crd.toXML(StreamUtil.getEncodingSafeOutputStream(descriptorFile, CharacterEncoding.UTF_8));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ResourceInitializationException e) {
			throw new RuntimeException("XML Descriptor validation FAILED!", e);
		}
	}

	/**
	 * Processes each .java file in the input directories. The directories scanned must be on the
	 * classpath. For each class that has a exportXmlDescriptor(File, String) method, an XML version
	 * of the descriptor is output to the specified target directory
	 * 
	 * @param args
	 *            args[0] source directories (semi-colon delimited) <br>
	 *            args[1] base descriptor output directory<br>
	 *            args[2] version
	 */
	public static void main(String[] args) {
		File baseDescriptorOutputDirectory = new File(args[1]);
		String version = args[2];
		for (String sourceDirectoryStr : args[0].split(StringConstants.SEMICOLON)) {
			File sourceDirectory = new File(sourceDirectoryStr);
			try {
				Iterator<File> srcFileIterator = FileUtil.getFileIterator(sourceDirectory, true, ".java");
				while (srcFileIterator.hasNext()) {
					File srcFile = srcFileIterator.next();
					if (!srcFile.getName().equals("package-info.java")) {
						File relativeSrcFile = FileUtil.getFileRelativeToDirectory(srcFile, sourceDirectory);
						String className = StringUtil.removeSuffix(relativeSrcFile.getAbsolutePath(), ".java");
						className = StringUtil.removePrefix(className, File.separator);
						className = className.replaceAll(RegExPatterns.escapeCharacterForRegEx(File.separator), ".");

						Class<?> cls = Class.forName(className);
						/*
						 * when you have a few minutes for fun... isUimaComponent should return true
						 * if the cls extends one of the uimafit component classes. (there should be
						 * a separate test to look for non-uimafit components - they should cause
						 * the build to fail I think).
						 * 
						 * exportUimaXmlDescriptorFile should look for the @ComponentInfo annotation
						 * to get the vendor and description, then query each
						 * 
						 * @ConfigurationParameter for default values and the configuration
						 * parameter name so that the ConfiguationParamterFactory can be used to
						 * generate the appropriate name.
						 * 
						 * It will then initialize a Description object (depending on the class -
						 * AnalysisEngineDescription for AE's for example) and populate the metadata
						 * with fields from the annotation + the version.
						 * 
						 * The only tricky part will be the Type System needed by the
						 * AnalysisEngineDescription constructor. Perhaps this can be specified by
						 * the annotation? There is a method to create a description without a
						 * TypeSystemDescription, so perhaps the type system can be optional.
						 */
						if (isUimaComponent(cls))
							exportUimaXmlDescriptorFile(cls, baseDescriptorOutputDirectory, version);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (ResourceInitializationException e) {
				throw new RuntimeException(e);
			} 
		}
	}

	/**
	 * @param cls
	 * @param baseDescriptorOutputDirectory
	 * @param version
	 * @throws ResourceInitializationException 
	 */
	private static void exportUimaXmlDescriptorFile(Class<?> cls, File baseDescriptorOutputDirectory, String version) throws ResourceInitializationException {

		ComponentInfo componentInfoAnnotation = cls.getAnnotation(ComponentInfo.class);
		if (componentInfoAnnotation == null)
			throw new RuntimeException(
					"Error during automatic UIMA XML descriptor generation. The following class is missing a @ComponentInfo annotation: "
							+ cls.getName() + " Please add so that the descriptor can be generated.");
		String description = componentInfoAnnotation.description();
		String vendor = componentInfoAnnotation.vendor();
		String typeSystem = componentInfoAnnotation.typeSystem();

		Object[] parameterSettings = getDefaultUimaConfigurationParameterSettings(cls);
		ResourceCreationSpecifier rcs = createDescription(cls, typeSystem, parameterSettings);
		ResourceMetaData metaData = rcs.getMetaData();
		metaData.setName(cls.getSimpleName());
		metaData.setDescription(description);
		metaData.setVendor(vendor);
		metaData.setVersion(version);
		rcs.setMetaData(metaData);
		XmlDescriptorWriter.exportXmlDescriptor(cls, rcs, baseDescriptorOutputDirectory);
	}

	/**
	 * @param cls
	 * @return
	 */
	private static Object[] getDefaultUimaConfigurationParameterSettings(Class<?> cls) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param cls
	 * @param typeSystem
	 * @param parameterSettings
	 * @return
	 * @throws ResourceInitializationException 
	 */
	private static ResourceCreationSpecifier createDescription(Class componentCls, String typeSystem,
			Object[] parameterSettings) throws ResourceInitializationException {
		Class<?> componentSuperCls = getComponentSuperClass(componentCls);
		if (componentSuperCls.equals(JCasAnnotator_ImplBase.class))
			return createAnalysisEngineDescription(componentCls, typeSystem, parameterSettings);
		throw new IllegalArgumentException("Creating descriptor for UIMA components of type " + componentSuperCls + " not yet handled.");

	}

	/**
	 * @param componentCls
	 * @param typeSystem
	 * @param parameterSettings
	 * @return
	 * @throws ResourceInitializationException
	 */
	private static ResourceCreationSpecifier createAnalysisEngineDescription(
			Class<? extends JCasAnnotator_ImplBase> componentCls, String typeSystem, Object[] parameterSettings)
			throws ResourceInitializationException {
		if (typeSystem.isEmpty())
			return AnalysisEngineFactory.createPrimitiveDescription(componentCls, parameterSettings);
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystem);
		return AnalysisEngineFactory.createPrimitiveDescription(componentCls, tsd, parameterSettings);

	}

	/**
	 * Returns the UIMAFIT component implementation class that is the superclass of the input
	 * component class
	 * 
	 * @param componentCls
	 * @return
	 */
	private static Class<?> getComponentSuperClass(Class<?> componentCls) {
		Class<?> superclass = componentCls.getSuperclass();
		while (superclass != null) {
			if (UIMAFIT_COMPONENT_CLASSES.contains(superclass))
				return superclass;
			superclass = superclass.getSuperclass();
		}
		throw new IllegalArgumentException("Input Class expected to be a UIMA component class but is not: "
				+ componentCls);
	}

	/**
	 * @param cls
	 * @return
	 */
	private static boolean isUimaComponent(Class<?> cls) {
		return UIMAFIT_COMPONENT_CLASSES.contains(cls);
	}

	/**
	 * Returns true if the specified class contains the method name reserved for exporting UIMA
	 * descriptors to XML
	 * 
	 * @param cls
	 * @return true if the input class has the exportXmlDescriptor
	 */
	private static boolean hasExportXmlDescriptorMethod(Class<?> cls) {
		for (Method m : cls.getMethods())
			if (m.getName().equals(EXPORT_XML_DESCRIPTOR_METHOD_NAME))
				return true;
		return false;
	}

	/**
	 * Invokes the public static method for the specified class reserved for outputting UIMA
	 * descriptors to XML.
	 * 
	 * @param cls
	 * @param baseDescriptorOutputDirectory
	 * @param version
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static void invokeExportXmlDescriptorMethod(Class<?> cls, File baseDescriptorOutputDirectory, String version)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method method = cls.getMethod(EXPORT_XML_DESCRIPTOR_METHOD_NAME, File.class, String.class);
		method.invoke(null, baseDescriptorOutputDirectory, version);
	}

}
