package edu.ucdenver.ccp.nlp.core.uima.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.apache.uima.analysis_component.AnalysisComponent_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
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
	 * @param aed
	 * @param baseDescriptorDirectory
	 */
	public static void exportXmlDescriptor(Class<? extends AnalysisComponent_ImplBase> cls,
			AnalysisEngineDescription aed, File baseDescriptorDirectory) {
		try {
			aed.doFullValidation();
			File descriptorFile = new File(baseDescriptorDirectory, cls.getName().replaceAll("\\.", File.separator)
					+ ".xml");
			FileUtil.mkdir(descriptorFile.getParentFile());
			aed.toXML(StreamUtil.getEncodingSafeOutputStream(descriptorFile, CharacterEncoding.UTF_8));
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
						if (hasExportXmlDescriptorMethod(cls))
							invokeExportXmlDescriptorMethod(cls, baseDescriptorOutputDirectory, version);
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
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
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
