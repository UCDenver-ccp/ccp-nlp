/* Copyright (C) 2005,2006,2008-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 *
 * The Original Code is UCHSC Utilities.
 *
 * UCHSC Utilities was developed by the Center for Computational Pharmacology
 * (http://compbio.uchsc.edu) at the University of Colorado Health 
 *  Sciences Center School of Medicine with support from the National 
 *  Library of Medicine.  
 *
 *
 * Contributor(s):
 *   William A. Baumgartner <william.baumgartner@uchsc.edu> (Original Author)
 *   Philip V. Ogren
 *   
 * Created: November 2005
 * Dependencies:
 * TODO:    Fill in code for InstanceMention case. This has been left out to speed up 
 *          development time b/c we rarely see it in the MB world.
 *          Augment TextAnnotation to support >1 span assignments
 * Changes:
 *  04/30/06 - wab - the hash keys for storing already extracted annotations and mentions 
 *                   have been altered so that they are much more robust. They now consist 
 *                   of the annotations and slots (in alphabetical order). Before this change 
 *                   was made, annotations with identical spans, but different slot fillers 
 *                   were overwritten due to an imprecise hash key.
 *  02/07/08 - wab - Updated to comply with Knowtator version 1.7.4
 *  03/17/08 - wab - Added functionality to remove/ignore spanless annotations
 *                     
 * Notes: As you read through the source for this class, it will help to understand 
 *        that "TextSourceID" and "DocumentID" are used synonymously.
 *        Also, Whenever I refer to UtilClassMention I am talking about edu.ucdenver.ccp.nlp.core.annotation.ClassMention, 
 *        and whenever I refer to KnowtatorMention I am referring to the corresponding Knowtator class. Same goes for 
 *        UtilTextAnnotation; this refers to edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation
 */
package edu.ucdenver.ccp.wrapper.knowtator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Tree;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.Mention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultComplexSlotMention;
import edu.ucdenver.ccp.wrapper.knowtator.annotation.impl.WrappedKnowtatorAnnotation;
import edu.ucdenver.ccp.wrapper.protege.v3.ProtegeUtil;
import edu.uchsc.ccp.knowtator.AnnotationUtil;
import edu.uchsc.ccp.knowtator.DisplayColors;
import edu.uchsc.ccp.knowtator.InvalidSpanException;
import edu.uchsc.ccp.knowtator.Knowtator;
import edu.uchsc.ccp.knowtator.KnowtatorManager;
import edu.uchsc.ccp.knowtator.KnowtatorProjectUtil;
import edu.uchsc.ccp.knowtator.MentionUtil;
import edu.uchsc.ccp.knowtator.ProjectSettings;
import edu.uchsc.ccp.knowtator.Span;
import edu.uchsc.ccp.knowtator.TextSourceUtil;
import edu.uchsc.ccp.knowtator.textsource.TextSource;
import edu.uchsc.ccp.knowtator.textsource.TextSourceAccessException;
import edu.uchsc.ccp.knowtator.textsource.TextSourceCollection;
import edu.uchsc.ccp.knowtator.textsource.TextSourceIterator;
import edu.uchsc.ccp.knowtator.textsource.files.FileTextSourceCollection;



/**
 * This utility class is geared towards providing an interface between Knowtator and UIMA. Some of
 * the code here has been adapted from edu.uchsc.ccp.knowtator.util.ExportAnnotations. The utilities
 * in this class use edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation as the transferable entity
 * between the Protege and UIMA frameworks. Presumably, anyone in the future would only have to
 * write code to transfer their annotations into TextAnnotation classes in order to interface with
 * Knowtator.<br>
 * <br>
 * Note: spanless annotations are ignored <br>
 * 
 * @author William A. Baumgartner, Jr.
 * @author Philip V. Ogren
 * 
 */
public class KnowtatorUtil {

	private static Logger logger = Logger.getLogger(KnowtatorUtil.class);

	// private static final boolean DEBUG = false;

	private Project project;

	private KnowledgeBase kb;

	private KnowtatorProjectUtil kpu;

	private AnnotationUtil annotationUtil;

	private TextSourceUtil textSourceUtil;

	private MentionUtil mentionUtil;

	private DisplayColors displayColors;

	private ProtegeUtil pu;

	private final String projectFileName;

	private static final String KNOWTATOR_WIDGET_CLASS_NAME = Knowtator.class.getName();

	// private List<SimpleInstance> mentions;

	/*
	 * A hash with documentID/List<annotation> key/value pairs. This will allow annotations to be
	 * looked up by Document ID.
	 */
	private Map<String, List<SimpleInstance>> textSourceName2AnnotationsMap;

	private Map<String, SimpleInstance> textSourceName2InstanceMap;

	/**
	 * Stores a mapping from annotation set id to the set name. Extracted annotation sets will be
	 * assigned arbitrary ids.
	 */
	private Map<String, Integer> annotationSetName2IDMap;

	/**
	 * Stores a mapping from annotator name to id. Extracted annotators are assigned an arbitrary
	 * id.
	 */
	private Map<String, Integer> annotatorName2IDMap;

	/**
	 * This slot holds a String containing Traversal ID - Mention ID pairings
	 */
	private final String UNIQUE_ID_SLOT_NAME = "uniqueID";
	/**
	 * This slot hold a long indicating the mention ID;
	 */
	private static final String MENTION_ID_SLOT_NAME = "mentionID";

	// /**
	// * Constructs a new KnowtatorUtils class from a Knowtator Project. The primary use of this
	// class
	// * is to interface between Knowtator's underlying data structure and
	// * edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation.
	// *
	// * @param project
	// * a Knowtator Project class
	// * @throws Exception
	// */
	// public Knowtator_Util(Project project) throws Exception {
	// initializeKnowtatorUtils(project);
	// }

	/**
	 * Constructs a new KnowtatorUtils class from a file containing a Knowtator Project. The primary
	 * use of this class is to interface between Knowtator's underlying data structure and
	 * edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation.
	 * 
	 * @param projectFileName
	 *            the path to a file containing a Knowtator Project
	 * @throws Exception
	 */
	public KnowtatorUtil(String projectFileName) {
		this.projectFileName = projectFileName;
		System.err.println("Opening project: " + projectFileName);
		project = ProtegeUtil.openProject(projectFileName);
		if (project != null) {
			initializeKnowtatorUtils(project, projectFileName);
		} else {
			System.out.println("Error opening project.");
			System.exit(0);
		}
	}

	public KnowtatorUtil(String projectFileName, File textSourcesDirectory, Charset charset) throws IOException {
		this.projectFileName = projectFileName;
		assert textSourcesDirectory.exists() : String.format("Text sources directory does not exist: %s",
				textSourcesDirectory.getAbsolutePath());
		logger.info(String.format("Opening project: %s and assigning text sources directory: %s", projectFileName,
				textSourcesDirectory.getAbsolutePath()));
		project = ProtegeUtil.openProject(projectFileName);
		if (project != null) {
			initializeKnowtatorUtils(project, projectFileName, textSourcesDirectory, charset);
		} else {
			logger.error(String.format("Error opening Protege project: %s", projectFileName));
			System.exit(0);
		}
	}

	public void reload() {
		logger.info("Reloading Knowtator Project");
		project = ProtegeUtil.openProject(projectFileName);
		if (project != null) {
			initializeKnowtatorUtils(project, projectFileName);
		} else {
			logger.error("Error reloading project.");
			System.exit(0);
		}
	}

	/*
	 * Initialize a KnowtatorUtils
	 */
	private void initializeKnowtatorUtils(Project project, String projectFileName) {
		initializeKnowtatorUtilities(project, projectFileName);
		Collection<SimpleInstance> knowtatorAnnotations = getKnowtatorAnnotationInstances();
		populateTextSourceName2AnnotationsMap(knowtatorAnnotations);
		addMentionIteratorIdSlotToProject();
		addMentionIdSlotToProject();
		// createUniqueIdentifiersForKnowtatorAnnotations(annotations);
		populateTextSourceName2InstanceMap();
	}

	/*
	 * Initialize a KnowtatorUtils
	 */
	private void initializeKnowtatorUtils(Project project, String projectFileName, File textSourcesDirectory,
			Charset charset) throws IOException {
		initializeKnowtatorUtilities(project, projectFileName);
		setDirectoryForFileTextSourceCollection(textSourcesDirectory, charset);
		Collection<SimpleInstance> knowtatorAnnotations = getKnowtatorAnnotationInstances();
		populateTextSourceName2AnnotationsMap(knowtatorAnnotations);
		addMentionIteratorIdSlotToProject();
		addMentionIdSlotToProject();
		// createUniqueIdentifiersForKnowtatorAnnotations(annotations);
		populateTextSourceName2InstanceMap();
	}

	private void initializeKnowtatorUtilities(Project project, String projectFileName) {
		this.pu = new ProtegeUtil(project, projectFileName);
		this.kb = project.getKnowledgeBase();
		this.kpu = new KnowtatorProjectUtil(kb);
		KnowtatorManager km = new KnowtatorManager(kpu);

		this.annotationUtil = km.getAnnotationUtil();
		this.mentionUtil = km.getMentionUtil();
		this.displayColors = km.getDisplayColors();
		this.textSourceUtil = km.getTextSourceUtil();
		this.textSourceUtil.init();

		this.annotationSetName2IDMap = new HashMap<String, Integer>();
		this.annotatorName2IDMap = new HashMap<String, Integer>();
	}

	public static void removeKnowtatorPprjAbsolutePathIfPresent(File protegeProjectFile) {
		File tmpFile = new File(protegeProjectFile.getAbsolutePath() + ".tmp");
		Pattern includedProjectLinePattern = Pattern.compile("^\\s*\\(included_projects \"file:.*?knowtator\\.pprj");
		Matcher m;
		BufferedReader br = null;
		PrintStream tmpPS = null;
		try {
			tmpPS = new PrintStream(tmpFile);
			String line;
			br = new BufferedReader(new FileReader(protegeProjectFile));
			while ((line = br.readLine()) != null) {
				m = includedProjectLinePattern.matcher(line);
				if (m.find()) {
					logger.info(String.format("Found knowtator.pprj absolute path:%s Replacing with relative path.",
							line));
					line = "(included_projects \"knowtator.pprj\")";
				}
				tmpPS.println(line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (tmpPS != null) {
				tmpPS.close();
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		moveFile(tmpFile, protegeProjectFile);
	}

	private static void moveFile(File srcFile, File destFile) {
		BufferedReader br = null;
		PrintStream ps = null;
		try {
			ps = new PrintStream(destFile);
			String line;
			br = new BufferedReader(new FileReader(srcFile));
			while ((line = br.readLine()) != null) {
				ps.println(line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		srcFile.delete();
	}

	public static void addKnowtatorPprjAsIncludedProject(File protegeProjectFile, File knowtatorPprjFile) {
		logger.info(String.format(
				"%s -- Adding knowtator.pprj as a project to include. (Mimicking the Manage Included Projects menu)",
				protegeProjectFile.getName()));
		ProtegeUtil pu = new ProtegeUtil(protegeProjectFile.getAbsolutePath());
		Project project = pu.getProject();
		Tree<URI> uriTree = project.getProjectTree();
		uriTree = removeCurrentKnowtatorPprjFileIfPresent(uriTree);
		uriTree.addChild(uriTree.getRoot(), knowtatorPprjFile.toURI());
		Set<URI> projectsToInclude = uriTree.getChildren(uriTree.getRoot());
		pu.getProject().setDirectIncludedProjectURIs(projectsToInclude);
		pu.saveAndClose();
	}

	/**
	 * Occasionally the knowtator.pprj file gets saved to an absolute path. This causes issues, so
	 * here we will remove any knowtator.pprj file present in the uri tree so that it can be
	 * replaced with a different path.
	 * 
	 * @param uriTree
	 * @return
	 */
	private static Tree<URI> removeCurrentKnowtatorPprjFileIfPresent(Tree<URI> uriTree) {
		Set<URI> children = uriTree.getChildren(uriTree.getRoot());
		for (URI uri : children) {
			if (uri.getPath().contains("knowtator.pprj")) {
				uriTree.removeChild(uriTree.getRoot(), uri);
			}
		}
		return uriTree;
	}

	/**
	 * Takes a simple Protege project and configures it to be a knowtator project
	 * 
	 * @param protegeProjectFile
	 * @param knowtatorPprjFile
	 * @throws IOException
	 */
	public static void convertProtegeProjectIntoKnowtatorProject(File protegeProjectFile, File knowtatorPprjFile,
			File textSourcesDirectory) throws IOException {
		logger.info(String.format("Configuring Protege Project for Knowtator: %s", protegeProjectFile.getName()));
		addKnowtatorPprjAsIncludedProject(protegeProjectFile, knowtatorPprjFile);
		// ProtegeUtil pu = new ProtegeUtil(protegeProjectFile.getAbsolutePath());
		// Project project = pu.getProject();
		//		
		// logger.info(String.format("%s -- Adding knowtator.pprj as a project to include. (Mimicking the Manage Included Projects menu)",
		// protegeProjectFile.getName()));
		// Tree<URI> uriTree = project.getProjectTree();
		// uriTree.addChild(uriTree.getRoot(), knowtatorPprjFile.toURI());
		// Set<URI> projectsToInclude = uriTree.getChildren(uriTree.getRoot());
		// // ProjectManager.getProjectManager().setCurrentProject(project);
		// // assert ProjectManager.getProjectManager() != null;
		// // assert ProjectManager.getProjectManager().getCurrentProject() != null;
		// // assert ProjectManager.getProjectManager().getCurrentProject().getProjectURI() != null;
		// // assert pu.getProject().getProjectURI() != null;
		// // assert
		// ProjectManager.getProjectManager().getCurrentProject().getProjectURI().equals(pu.getProject().getProjectURI())
		// : "Project URIs must be equal.";
		// //
		// ProjectManager.getProjectManager().changeIncludedProjectURIsRequest(projectsToInclude);
		// pu.getProject().setDirectIncludedProjectURIs(projectsToInclude);
		// pu.saveAndReload();

		// logger.info(String.format("%s -- Making the Knowtator tab viewable. (Mimicking the Configure menu) Widget Name=%s",
		// protegeProjectFile.getName(), KNOWTATOR_WIDGET_CLASS_NAME));
		// // WidgetDescriptor knowtatorWidget =
		// project.getTabWidgetDescriptor(KNOWTATOR_WIDGET_CLASS_NAME);
		// // assert knowtatorWidget != null :
		// String.format("Knowtator widget is null. Class Name = %s", KNOWTATOR_WIDGET_CLASS_NAME);
		// Collection<WidgetDescriptor> widgetDescriptors = project.getTabWidgetDescriptors();
		// Collection<Instance> instances = new ArrayList<Instance>();
		// for (WidgetDescriptor wd : widgetDescriptors) {
		// if (wd.getWidgetClassName().equals(KNOWTATOR_WIDGET_CLASS_NAME)) {
		// wd.setVisible(true);
		// wd.setIncluded(true);
		// }
		// logger.info(String.format("WD: name: %s -- widgetClassName: %s isViewable: %s isIncluded: %s toString: %s"
		// , wd.getName(), wd.getWidgetClassName(), Boolean.toString(wd.isVisible()),
		// Boolean.toString(wd.isIncluded()), wd.toString()));
		// instances.add(wd.getInstance());
		// }
		//		
		// ModelUtilities.setOwnSlotValues(project.getProjectInstance(), "tabs", instances);
		// ModelUtilities.setOwnSlotValues(project.getProjectInstance(),
		// "customized_instance_widgets", instances);
		//		
		//		
		// // Collection errors = new ArrayList();
		// // assert WidgetUtilities.isSuitableTab(KNOWTATOR_WIDGET_CLASS_NAME, project, errors);
		// //// knowtatorWidget.setVisible(true);
		// project.setTabWidgetDescriptorOrder(widgetDescriptors);
		// // widgetDescriptors = project.getTabWidgetDescriptors();
		// // logger.info("Project widgets - knowtator viz should be true...");
		// for (WidgetDescriptor wd : widgetDescriptors) {
		// logger.info(String.format("WD: name: %s -- widgetClassName: %s isViewable: %s isIncluded: %s toString: %s"
		// , wd.getName(), wd.getWidgetClassName(), Boolean.toString(wd.isVisible()),
		// Boolean.toString(wd.isIncluded()), wd.toString()));
		// }
		// pu.saveAndClose();
		// // pu.close();

		logger.info(String.format("%s -- Setting the Knowtator text source collection to a directory: %s",
				protegeProjectFile.getName(), textSourcesDirectory.getAbsolutePath()));
		KnowtatorUtil ku = new KnowtatorUtil(protegeProjectFile.getAbsolutePath(), textSourcesDirectory, Charset
				.forName("UTF-8"));
		// ku.setDirectoryForFileTextSourceCollection(textSourcesDirectory,
		// Charset.forName("UTF-8"));

		// widgetDescriptors = ku.project.getTabWidgetDescriptors();
		// logger.info("Project from KU widgets - knowtator viz should be true...");
		// for (WidgetDescriptor wd : widgetDescriptors) {
		// logger.info(String.format("WD: name: %s -- widgetClassName: %s isViewable: %s isIncluded: %s toString: %s"
		// , wd.getName(), wd.getWidgetClassName(), Boolean.toString(wd.isVisible()),
		// Boolean.toString(wd.isIncluded()), wd.toString()));
		// }

		ku.saveProject();
		ku.close();

	}

	/**
	 * Returns the collection of SimpleInstances representing annotations in this Knowtator project
	 * 
	 * @return
	 */
	private Collection<SimpleInstance> getKnowtatorInstances(Cls cls) {
		Collection<Instance> instances = kb.getInstances(cls);
		Collection<SimpleInstance> simpleInstances = new ArrayList<SimpleInstance>();
		for (Instance instance : instances) {
			SimpleInstance si = (SimpleInstance) instance;
			simpleInstances.add(si);
		}
		return simpleInstances;
	}

	private Collection<SimpleInstance> getKnowtatorAnnotationInstances() {
		return getKnowtatorInstances(kpu.getAnnotationCls());
	}

	// private Collection<SimpleInstance> getKnowtatorMentionInstances() {
	// return getKnowtatorInstances(kpu.getMentionCls());
	// }

	private void addMentionIteratorIdSlotToProject() {
		Slot slot = pu.createSlotForClass(UNIQUE_ID_SLOT_NAME, kpu.getMentionCls().getName());
		pu.assignSlotToClass(slot, kpu.getAnnotationCls());
	}

	private void addMentionIdSlotToProject() {
		Slot slot = pu.createSlotForClass(MENTION_ID_SLOT_NAME, kpu.getMentionCls().getName());
		slot.setAllowsMultipleValues(false);
		// slot.setMaximumCardinality(1);
		slot.setValueType(ValueType.STRING);
		/*
		 * This will store a long, but since there is no long ValueType we will use String. Using
		 * integer would present a possible overflow issue
		 */
		pu.assignSlotToClass(slot, kpu.getAnnotationCls());
	}

	private void removeMentionIteratorIdSlotFromProject() {
		if (kb.getSlot(UNIQUE_ID_SLOT_NAME) != null) {
			pu.removeSlotFromClass(UNIQUE_ID_SLOT_NAME, kpu.getMentionCls().getName());
			pu.removeSlotFromClass(UNIQUE_ID_SLOT_NAME, kpu.getAnnotationCls().getName());
			pu.removeSlot(UNIQUE_ID_SLOT_NAME);
		}
	}

	private void removeMentionIdSlotFromProject() {
		if (kb.getSlot(MENTION_ID_SLOT_NAME) != null) {
			pu.removeSlotFromClass(MENTION_ID_SLOT_NAME, kpu.getMentionCls().getName());
			pu.removeSlotFromClass(MENTION_ID_SLOT_NAME, kpu.getAnnotationCls().getName());
			pu.removeSlot(MENTION_ID_SLOT_NAME);
		}
	}

	// private void
	// createUniqueIdentifiersForKnowtatorAnnotationsOrMentions(Collection<SimpleInstance> mentions)
	// {
	// int id = 0;
	// Slot idSlot = kb.getSlot(UNIQUE_ID_SLOT_NAME);
	// for (SimpleInstance mention : mentions) {
	// mention.setDirectOwnSlotValue(idSlot, id++);
	// }
	// }
	//
	// private Collection<SimpleInstance> getClassMentionsForAnnotations(Collection<SimpleInstance>
	// annotations) {
	// Collection<SimpleInstance> mentions = new ArrayList<SimpleInstance>();
	// for (SimpleInstance annotation : annotations) {
	// SimpleInstance knowtatorMention = annotationUtil.getMention(annotation);
	// mentions.add(knowtatorMention);
	// }
	// return mentions;
	// }

	private Slot getTraversalIDPairingSlot() {
		return kb.getSlot(UNIQUE_ID_SLOT_NAME);
	}

	private Slot getMentionIDSlot() {
		return kb.getSlot(MENTION_ID_SLOT_NAME);
	}

	public long getMentionID(SimpleInstance mention) {
		return Long.parseLong((String) mention.getDirectOwnSlotValue(getMentionIDSlot()));
	}

	public void setMentionID(SimpleInstance mention, long mentionID) {
		mention.setDirectOwnSlotValue(getMentionIDSlot(), Long.toString(mentionID));
	}

	// public String getMentionName(SimpleInstance mention) {
	// return mentionUtil.getMentionCls(mention).getName();
	// }
	//	
	// public void setMentionName(SimpleInstance mention, String mentionName) {
	// mentionUtil.setMentionCls(mention, kb.getCls(mentionName));
	// }

	/**
	 * Returns the mention iterator id slot value for the input mention
	 * 
	 * @param mention
	 * @return
	 */
	public Collection<TraversalIDPairing> getTraversalIDPairings(SimpleInstance mention) {
		if (mention == null) {
			logger.info("Traversal Pairing slot is null: " + (mention == null));
			logger.info("Traversal ID Pairing slot is null: " + (getTraversalIDPairingSlot() == null));
		}
		// logger.debug("Traversal Pairing slot values are null: " +
		// (mention.getDirectOwnSlotValues(getTraversalIDPairingSlot()) == null));
		Collection<String> slotValues = mention.getDirectOwnSlotValues(getTraversalIDPairingSlot());
		Collection<TraversalIDPairing> traversalIdPairings = new ArrayList<TraversalIDPairing>();
		if (traversalIdPairings != null) {
			for (String slotValue : slotValues) {
				traversalIdPairings.add(new TraversalIDPairing(slotValue));
			}
		}
		return traversalIdPairings;
	}

	/**
	 * Sets the traversal ID/ mention ID pairs for this particular mention
	 * 
	 * @param traversalIDPairings
	 * @param mention
	 */
	public void setTraversalIDPairings(Collection<TraversalIDPairing> traversalIDPairings, SimpleInstance mention) {
		Collection<String> slotValues = new ArrayList<String>();
		for (TraversalIDPairing traversalIDPairing : traversalIDPairings) {
			slotValues.add(traversalIDPairing.toString());
		}
		mention.setDirectOwnSlotValues(getTraversalIDPairingSlot(), slotValues);
	}

	/**
	 * Removes the input traversal ID
	 * 
	 * @param traversalID
	 * @param mention
	 */
	public void removeTraversalID(UUID traversalID, SimpleInstance mention) {
		Collection<TraversalIDPairing> traversalIDPairings = getTraversalIDPairings(mention);
		Collection<String> slotValues = new ArrayList<String>();
		for (TraversalIDPairing traversalIDPairing : traversalIDPairings) {
			if (!traversalIDPairing.getTraversalID().equals( traversalID)) {
				slotValues.add(traversalIDPairing.toString());
			}
		}
		mention.setDirectOwnSlotValues(getTraversalIDPairingSlot(), slotValues);
	}

	public UUID getMentionIDForTraversal(SimpleInstance mention, UUID traversalID) {
		Collection<TraversalIDPairing> traversalIDPairings = getTraversalIDPairings(mention);
		for (TraversalIDPairing traversalIDPairing : traversalIDPairings) {
			if (traversalIDPairing.getTraversalID().equals(traversalID)) {
				return traversalIDPairing.getMentionID();
			}
		}
		return null;
	}

	/**
	 * Adds a traversal ID - mention Id pairing to the input mention
	 * 
	 * @param mentionID
	 * @param traversalID
	 * @param mention
	 */
	public void setMentionIDForTraversal(UUID mentionID, UUID traversalID, SimpleInstance mention) {
		removeTraversalID(traversalID, mention);
		Collection<TraversalIDPairing> traversalIDPairings = getTraversalIDPairings(mention);
		traversalIDPairings.add(new TraversalIDPairing(traversalID, mentionID));
		setTraversalIDPairings(traversalIDPairings, mention);
	}

	public class TraversalIDPairing {
		private final UUID traversalID;
		private final UUID mentionID;

		public TraversalIDPairing(UUID traversalID, UUID mentionID) {
			super();
			this.traversalID = traversalID;
			this.mentionID = mentionID;
		}

		public TraversalIDPairing(String stringRep) {
			String[] toks = stringRep.split("\\t");
			if (toks.length == 2) {
				traversalID = UUID.fromString(toks[0]);
				mentionID = UUID.fromString(toks[1]);
			} else {
				throw new Error("TraversalIDPairing string is incorrectly formatted: " + stringRep);
			}
		}

		public UUID getTraversalID() {
			return traversalID;
		}

		public UUID getMentionID() {
			return mentionID;
		}

		@Override
		public String toString() {
			return traversalID.toString() + "\t" + mentionID.toString();
		}
	}

	/**
	 * Returns the number of annotations within the knowtator project
	 * 
	 * @return
	 */
	public int getNumberOfAnnotationsInProject() {
		return kb.getInstances(kpu.getAnnotationCls()).size();
	}

	public int getNumberOfClassMentionsInProject() {
		return kb.getInstances(kpu.getClassMentionCls()).size();
	}

	private void populateTextSourceName2InstanceMap() {
		textSourceName2InstanceMap = new HashMap<String, SimpleInstance>();

		TextSourceCollection tsc = textSourceUtil.getCurrentTextSourceCollection();
		TextSourceIterator textSourceIterator = null;
		try {
			textSourceIterator = tsc.iterator();
		} catch (NullPointerException npe) {
			logger
					.error("Null Pointer Exception caught while trying to get text source names. This is typically the result of a Knowtator project that is not linked to a text source. Please open up the Knowtator project of interest and make sure you save it after assigning it a text source, then try again.");
		}
		while (textSourceIterator.hasNext()) {
			TextSource ts = null;
			try {
				ts = textSourceIterator.next();
			} catch (TextSourceAccessException e) {
				throw new RuntimeException(e);
			}
			SimpleInstance tsi = textSourceUtil.getTextSourceInstance(ts, false);
			if (tsi == null) {
				textSourceName2InstanceMap.put(ts.getName(), textSourceUtil.getTextSourceInstance(ts, true));
			} else {
				textSourceName2InstanceMap.put(ts.getName(), tsi);
			}
		}

		/* Check to see if any of the text sources are null */
		Collection<String> keyset = textSourceName2InstanceMap.keySet();
		for (String key : keyset) {
			Instance ts = textSourceName2InstanceMap.get(key);
			if (ts == null) {
				System.err.println("Found null TS after initialization for document: " + key);
			}
		}
	}

	/**
	 * Populates a hash with documentID/List<annotation> key/value pairs. This will allow
	 * annotations to be looked up by Document ID.
	 * 
	 * @param annotations
	 */
	private void populateTextSourceName2AnnotationsMap(Collection<SimpleInstance> annotations) {
		textSourceName2AnnotationsMap = new HashMap<String, List<SimpleInstance>>();
		for (SimpleInstance annotation : annotations) {
			updateTextSourceName2AnnotationsMap(annotation);
		}
	}

	/**
	 * Adds new annotations to the annotation2TextID hash
	 * 
	 *@param annotation
	 */
	private void updateTextSourceName2AnnotationsMap(SimpleInstance annotation) {
		String textSourceName = getTextSourceNameFromKnowtatorAnnotation(annotation);
		/*
		 * Sometimes an annotation might not have an associated text source. This is a result of
		 * annotations being leftover from a previous knowtator project, so we ignore those
		 * annotations
		 */
		if (textSourceName != null) {
			if (!(textSourceName2AnnotationsMap.containsKey(textSourceName))) {
				List<SimpleInstance> annotations = new ArrayList<SimpleInstance>();
				annotations.add(annotation);
				textSourceName2AnnotationsMap.put(textSourceName, annotations);
			} else {
				textSourceName2AnnotationsMap.get(textSourceName).add(annotation);
			}
		}
	}

	public Collection<TextAnnotation> getTextAnnotationsFromKnowtatorDocument(String documentID,
			boolean ignoreSpanlessAnnotations) {
		Collection<TextAnnotation> annotationsForDocument = new ArrayList<TextAnnotation>();
		for (SimpleInstance knowtatorAnnotation : getKnowtatorAnnotationsForDocument(documentID)) {
			if (!ignoreSpanlessAnnotations
					|| (ignoreSpanlessAnnotations && annotationUtil.getSpans(knowtatorAnnotation).size() > 0)) {
				annotationsForDocument.add(new WrappedKnowtatorAnnotation(knowtatorAnnotation, this));
			}
		}
		return annotationsForDocument;
	}

	// /**
	// * This method retrieves annotations from a Knowtator project in the form of a generalized
	// * utility representation of text annotations using the TextAnnotation class.
	// *
	// * @param documentID
	// * @return
	// */
	// public List<DefaultTextAnnotation> getTextAnnotationsFromKnowtatorDocument(String documentID)
	// {
	// addMentionIteratorIdSlotToProject();
	//
	// List<DefaultTextAnnotation> textAnnotationsToReturn = new ArrayList<DefaultTextAnnotation>();
	//
	// List<SimpleInstance> annotations = getKnowtatorAnnotationsForDocument(documentID);
	// assignUniqueIdentifiersToAnnotationsAndClassMentions(annotations);
	//
	// HashMap<Integer, DefaultTextAnnotation> alreadyExtractedAnnotations = new HashMap<Integer,
	// DefaultTextAnnotation>();
	// HashMap<Integer, DefaultClassMention> alreadyExtractedMentions = new HashMap<Integer,
	// DefaultClassMention>();
	//
	// for (SimpleInstance annotation : annotations) {
	// try {
	// DefaultTextAnnotation ta = extractTextAnnotation(annotation, alreadyExtractedAnnotations,
	// alreadyExtractedMentions, null);
	// textAnnotationsToReturn.add(ta);
	// } catch (StackOverflowError soe) {
	// System.err.println("STACK OVERFLOW WHILE DOING: " + documentID);
	// System.err
	// .println("This is probably due to a circular reference within the project. At this point circular references are not handled by this code. If it is a slot that contains circular references, e.g. \"is equivalent to\" then place the slot name in the 'slotsToIgnoreList' in Knowtator_Util, otherwise remove the circular reference manually.");
	// soe.printStackTrace();
	// } catch (InvalidInputException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// removeMentionIteratorIdSlotFromProject();
	// return textAnnotationsToReturn;
	// }
	//
	// /**
	// * Adds a slot to the simple instances (annotations and class mentions) and assigns a unique
	// * Integer id (unique for the document)
	// *
	// * @param annotations
	// */
	// private void assignUniqueIdentifiersToAnnotationsAndClassMentions(Collection<SimpleInstance>
	// annotations) {
	// createUniqueIdentifiersForKnowtatorAnnotationsOrMentions(annotations);
	// createUniqueIdentifiersForKnowtatorAnnotationsOrMentions(getClassMentionsForAnnotations(annotations));
	// }
	//
	// /**
	// * Processes a Knowtator annotation and converts it into a TextAnnotation which is stored in
	// the
	// * alreadyCreatedAnnnotations HashMap.
	// *
	// * This method replaces addAnnotation.
	// *
	// *
	// * @param alreadyCreatedAnnotations
	// * @param alreadyCreatedMentions
	// * @param annotation
	// * @param mention
	// * @throws Exception
	// * @throws InvalidInputException
	// */
	// private DefaultTextAnnotation extractTextAnnotation(SimpleInstance annotation,
	// HashMap<Integer, DefaultTextAnnotation> alreadyExtractedAnnotations,
	// HashMap<Integer, DefaultClassMention> alreadyExtractedMentions, DefaultClassMention
	// classMention)
	// throws InvalidInputException, Exception {
	//
	// logger.debug("Checking to see if this annotation has been extracted already: "
	// + annotationPrintString(annotation));
	// if (!alreadyExtractedAnnotations.containsKey(getUniqueID(annotation))) {
	// logger.debug("The annotation has not yet been extracted: " +
	// annotationPrintString(annotation));
	//
	// DefaultTextAnnotation ta = new DefaultTextAnnotation();
	// ta.setAnnotationSets(getUtilAnnotationSetsFromKnowtatorAnnotation(annotation));
	// ta.setAnnotator(getUtilAnnotatorFromKnowtatorAnnotation(annotation));
	// ta.setSpans(getUtilSpanList(annotation));
	// ta.setCoveredText(annotationUtil.getText(annotation));
	// ta.setDocumentID(getTextSourceNameFromKnowtatorAnnotation(annotation));
	// ta.setDocumentCollectionID(-1);
	// ta.setDocumentSectionID(-1);
	// ta.setClassMention(getUtilClassMention(annotation, classMention, alreadyExtractedAnnotations,
	// alreadyExtractedMentions));
	//
	// alreadyExtractedAnnotations.put(getUniqueID(annotation), ta);
	//
	// return ta;
	// } else {
	// return alreadyExtractedAnnotations.get(getUniqueID(annotation));
	// }
	// }

	public String getCoveredText(SimpleInstance annotation) {
		return annotationUtil.getText(annotation);
	}

	// /**
	// * if mention is null, then create a new Util ClassMention from the associated Knowtator
	// * mention, else, use the inputed mention. this prevents infinite loops from occurring when
	// * creating an annotation from a ClassMention that was found in a complexSlotMention.
	// *
	// * @param annotation
	// * @param classMention
	// * @return
	// * @throws Exception
	// */
	// private DefaultClassMention getUtilClassMention(SimpleInstance annotation,
	// DefaultClassMention classMention,
	// HashMap<Integer, DefaultTextAnnotation> alreadyExtractedAnnotations,
	// HashMap<Integer, DefaultClassMention> alreadyExtractedMentions) throws Exception {
	// if (classMention == null) {
	// SimpleInstance knowtatorMention = annotationUtil.getMention(annotation);
	// return (DefaultClassMention) extractUtilMention(knowtatorMention,
	// alreadyExtractedAnnotations,
	// alreadyExtractedMentions);
	// } else {
	// return classMention;
	// }
	// }

	public List<edu.ucdenver.ccp.nlp.core.annotation.Span> getUtilSpanList(SimpleInstance annotation) {
		return convertKnowatorSpanList2UtilSpanList(annotationUtil.getSpans(annotation));
	}

	// /**
	// * Recursively processes a Knowtator mention and converts it to a Util Mention.
	// *
	// * This method replaces addMention
	// *
	// * @param knowtatorMention
	// * @param alreadyExtractedAnnotations
	// * @param alreadyExtractedMentions
	// * @return
	// * @throws Exception
	// */
	// private Mention extractUtilMention(SimpleInstance knowtatorMention,
	// HashMap<Integer, DefaultTextAnnotation> alreadyExtractedAnnotations,
	// HashMap<Integer, DefaultClassMention> alreadyExtractedMentions) throws Exception {
	// logger.debug("Extracting mention: " + mentionUtil.getMentionCls(knowtatorMention));
	//
	// Mention returnMention;
	// if (mentionUtil.isClassMention(knowtatorMention)) {
	// returnMention = processClassMention(knowtatorMention, alreadyExtractedAnnotations,
	// alreadyExtractedMentions);
	// } else if (mentionUtil.isComplexSlotMention(knowtatorMention)) {
	// returnMention = processComplexSlotMention(knowtatorMention, alreadyExtractedAnnotations,
	// alreadyExtractedMentions);
	// } else if (mentionUtil.isSlotMention(knowtatorMention)) {
	// returnMention = processSlotMention(knowtatorMention);
	// } else {
	// logger.error("The mention you are trying to create is an instance of: "
	// + mentionUtil.getMentionCls(knowtatorMention).getName()
	// + "\nCurrently, only ClassMentions, ComplexSlotMentions, and SlotMentions can be added.");
	// returnMention = null;
	// }
	//
	// return returnMention;
	// }
	//
	// /**
	// * Converts a knowtator class mention into a util class mention
	// *
	// * @param knowtatorMention
	// * @param alreadyExtractedAnnotations
	// * @param alreadyExtractedMentions
	// * @return
	// * @throws Exception
	// */
	// private DefaultClassMention processClassMention(SimpleInstance knowtatorMention,
	// HashMap<Integer, DefaultTextAnnotation> alreadyExtractedAnnotations,
	// HashMap<Integer, DefaultClassMention> alreadyExtractedMentions) throws Exception {
	//
	// DefaultClassMention returnCM;
	//
	// if (!alreadyExtractedMentions.containsKey(getUniqueID(knowtatorMention))) {
	// Cls classMention = mentionUtil.getMentionCls(knowtatorMention);
	// returnCM = new DefaultClassMention(classMention.getName());
	// alreadyExtractedMentions.put(getUniqueID(knowtatorMention), returnCM);
	//
	// Collection<SimpleInstance> slotMentions = (Collection<SimpleInstance>) knowtatorMention
	// .getOwnSlotValues(kpu.getSlotMentionSlot());
	// for (SimpleInstance slotMention : slotMentions) {
	// Mention mention = extractUtilMention(slotMention, alreadyExtractedAnnotations,
	// alreadyExtractedMentions);
	// if (mention instanceof DefaultComplexSlotMention) {
	// returnCM.addComplexSlotMention((DefaultComplexSlotMention) mention);
	// } else if (mention instanceof StringSlotMention) {
	// returnCM.addPrimitiveSlotMention((StringSlotMention) mention);
	// } else if (mention instanceof IntegerSlotMention) {
	// returnCM.addPrimitiveSlotMention((IntegerSlotMention) mention);
	// // } else if (mention instanceof DoubleSlotMention) {
	// // returnCM.addSlotMention((DoubleSlotMention) mention);
	// } else if (mention instanceof FloatSlotMention) {
	// returnCM.addPrimitiveSlotMention((FloatSlotMention) mention);
	// } else if (mention instanceof BooleanSlotMention) {
	// returnCM.addPrimitiveSlotMention((BooleanSlotMention) mention);
	// } else {
	// logger.error("While adding slot mention to class mention. Unknown slot mention type: "
	// + mention.getClass().getName());
	// }
	// }
	//
	// SimpleInstance annotationForThisMention = mentionUtil.getMentionAnnotation(knowtatorMention);
	// extractTextAnnotation(annotationForThisMention, alreadyExtractedAnnotations,
	// alreadyExtractedMentions,
	// returnCM);
	// } else {
	// returnCM = alreadyExtractedMentions.get(getUniqueID(knowtatorMention));
	// }
	// return returnCM;
	// }

	public String getClassMentionName(SimpleInstance knowtatorMention) {
		return mentionUtil.getMentionCls(knowtatorMention).getName();
	}

	public void setClassMentionName(SimpleInstance knowtatorMention, String mentionName) {
		mentionUtil.setMentionCls(knowtatorMention, kb.getCls(mentionName));
	}

	public String getSlotMentionName(SimpleInstance knowtatorSlotMention) {
		Slot slot = mentionUtil.getSlotMentionSlot(knowtatorSlotMention);
		return slot.getName();
	}

	public void setSlotMentionName(SimpleInstance knowtatorSlotMention, String mentionName) {
		throw new UnsupportedOperationException("Setting the slot mention name is not supported.. looks complicated.");
	}

	public Collection getSlotValues(SimpleInstance slotMention) {
		// System.err.println("kpu is null: " + (kpu==null));
		// System.err.println("kpu.getMentionSlotValueSlot() is null: " +
		// (kpu.getMentionSlotValueSlot() == null));
		// System.err.println("slotMention is null: " + (slotMention == null));
		// System.err.println("slot values are null: " +
		// (slotMention.getOwnSlotValues(kpu.getMentionSlotValueSlot()) == null));
		// System.err.println("RETURNING SLOT VALUES: " +
		// slotMention.getOwnSlotValues(kpu.getMentionSlotValueSlot()).size());
		return slotMention.getOwnSlotValues(kpu.getMentionSlotValueSlot());
	}

	public void setSlotValues(SimpleInstance slotMention, Collection<Object> slotValues) {
		mentionUtil.setSlotMentionValues(slotMention, Collections.list(Collections.enumeration(slotValues)));
	}

	public void addSlotValue(SimpleInstance slotMention, Object slotValue) {
		mentionUtil.addValueToSlotMention(slotMention, slotValue);
	}

	// /**
	// * Converts a knowtator complex slot mention into a util complex slot mention
	// *
	// * @param knowtatorMention
	// * @param alreadyExtractedAnnotations
	// * @param alreadyExtractedMentions
	// * @return
	// * @throws Exception
	// */
	// private DefaultComplexSlotMention processComplexSlotMention(SimpleInstance knowtatorMention,
	// HashMap<Integer, DefaultTextAnnotation> alreadyExtractedAnnotations,
	// HashMap<Integer, DefaultClassMention> alreadyExtractedMentions) throws Exception {
	//
	// Slot slot = mentionUtil.getSlotMentionSlot(knowtatorMention);
	// DefaultComplexSlotMention returnCSM = new DefaultComplexSlotMention(slot.getName());
	//
	// Collection slotValues = knowtatorMention.getOwnSlotValues(kpu.getMentionSlotValueSlot());
	// if (slotValues != null && slotValues.size() > 0) {
	// /*
	// * look at the first value to check to make sure the slot value is a mention and not
	// * some primitive
	// */
	// Object value = CollectionUtilities.getFirstItem(slotValues);
	// if (value instanceof SimpleInstance) {
	// for (Object slotValue : slotValues) {
	// SimpleInstance slotValueInstance = (SimpleInstance) slotValue;
	// DefaultClassMention cm = (DefaultClassMention) extractUtilMention(slotValueInstance,
	// alreadyExtractedAnnotations, alreadyExtractedMentions);
	// returnCSM.addClassMention(cm);
	// }
	// } else {
	// logger.error("SHOULD NOT BE HERE...  primitive value in slot of a Complex Slot Mention.");
	// }
	// }
	// return returnCSM;
	// }
	//
	// /**
	// * Converts a knowtator slot mention into a util primitive slot mention
	// *
	// * @param knowtatorMention
	// * @return
	// * @throws InvalidInputException
	// */
	// private PrimitiveSlotMention processSlotMention(SimpleInstance knowtatorMention) throws
	// InvalidInputException {
	// Slot slot = mentionUtil.getSlotMentionSlot(knowtatorMention);
	//
	// Collection<Object> slotValues =
	// knowtatorMention.getOwnSlotValues(kpu.getMentionSlotValueSlot());
	// if (slotValues != null && slotValues.size() > 0) {
	// return DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(slot.getName(),
	// slotValues);
	// }
	// return null;
	// }

	/**
	 * Returns a set of util annotation sets for the knowtator annotation
	 * 
	 * @param annotation
	 * @return
	 */
	public Set<AnnotationSet> getUtilAnnotationSetsFromKnowtatorAnnotation(SimpleInstance annotation) {
		Set<AnnotationSet> utilAnnotationSets = new HashSet<AnnotationSet>();
		Collection<SimpleInstance> knowtatorAnnotationSets = (Collection<SimpleInstance>) annotation
				.getOwnSlotValues(kpu.getSetSlot());
		for (SimpleInstance setInstance : knowtatorAnnotationSets) {
			String setName = (String) setInstance.getDirectOwnSlotValue(kpu.getSetNameSlot());
			String setDescription = (String) setInstance.getDirectOwnSlotValue(kpu.getSetDescriptionSlot());

			Integer setID;
			if (annotationSetName2IDMap.containsKey(setName)) {
				setID = annotationSetName2IDMap.get(setName);
			} else {
				setID = annotationSetName2IDMap.size() + 1;
				annotationSetName2IDMap.put(setName, setID);
			}
			AnnotationSet annotationSet = new AnnotationSet(setID, setName, setDescription);

			utilAnnotationSets.add(annotationSet);
		}
		return utilAnnotationSets;
	}

	/**
	 * Returns the equivalent Util Annotator object for the knowtator annotation
	 * 
	 * @param annotation
	 * @return
	 */
	public Annotator getUtilAnnotatorFromKnowtatorAnnotation(SimpleInstance annotation) {
		SimpleInstance annotator = annotationUtil.getAnnotator(annotation);

		String firstName;
		String lastName;
		String affiliation;
		if (annotator == null) {
			firstName = "Default";
			lastName = "Annotator";
			affiliation = "Unknown";
		} else {
			firstName = (String) annotator.getDirectOwnSlotValue(kb
					.getSlot(KnowtatorProjectUtil.ANNOTATOR_FIRST_NAME_SLOT_NAME));
			lastName = (String) annotator.getDirectOwnSlotValue(kb
					.getSlot(KnowtatorProjectUtil.ANNOTATOR_LAST_NAME_SLOT_NAME));
			affiliation = (String) annotator.getDirectOwnSlotValue(kb
					.getSlot(KnowtatorProjectUtil.ANNOTATOR_AFFILIATION_SLOT_NAME));
		}
		Integer annotatorID;
		String key = firstName + " " + lastName + " " + affiliation;
		if (annotatorName2IDMap.containsKey(key)) {
			annotatorID = annotatorName2IDMap.get(key);
		} else {
			annotatorID = annotatorName2IDMap.size() + 1;
			annotatorName2IDMap.put(key, annotatorID);
		}
		Annotator utilAnnotator = new Annotator(annotatorID, firstName, lastName, affiliation);

		return utilAnnotator;
	}

	/**
	 * Returns the textsource name for a given annotation
	 * 
	 * @param annotation
	 */
	public String getTextSourceNameFromKnowtatorAnnotation(SimpleInstance annotation) {
		SimpleInstance textSourceInstance = annotationUtil.getTextSource(annotation);
		/*
		 * for some reason, there are cases where the textSource does not get assigned to an
		 * annotation.. making it useless. If that is the case, we return null.
		 */
		if (textSourceInstance != null) {
			String textSourceName = textSourceInstance.getName();
			return textSourceName;
		} else {
			return null;
		}
	}

	/**
	 * This method facilitates the addition of a List of TextAnnotations to a Knowtator project.
	 * 
	 * 
	 * @param textAnnotations
	 */
	public void addTextAnnotationsToKnowtatorProject(List<TextAnnotation> textAnnotations) {

		/* Set text annotation IDs */
		int annotationID = 0;
		long mentionID = 0;
		for (TextAnnotation ta : textAnnotations) {
			ta.setAnnotationID(annotationID++);
			ta.getClassMention().setMentionID(mentionID++);
			assert (ta.getClassMention().getMentionID() != -1L) : "Mention ID should not be -1 here.";
		}

		/* All class mentions should now have a non-negative-one mention id, check for that here */
		for (TextAnnotation ta : textAnnotations) {
			Collection<ComplexSlotMention> complexSlotMentions = ta.getClassMention().getComplexSlotMentions();
			for (ComplexSlotMention csm : complexSlotMentions) {
				Collection<ClassMention> classMentions = csm.getClassMentions();
				for (ClassMention cm : classMentions) {
					assert cm.getMentionID() != -1L : "Error in annotation/mention connectivity. There is a mention that is not attached to a text annotation?";
				}
			}
		}

		HashMap<Integer, String> alreadyCreatedAnnotations = new HashMap<Integer, String>();
		HashMap<Long, SimpleInstance> alreadyCreatedMentions = new HashMap<Long, SimpleInstance>();

		for (TextAnnotation ta : textAnnotations) {
			createKnowtatorAnnotation(ta, alreadyCreatedAnnotations, alreadyCreatedMentions, null);
		}

		// save the Knowtator project
		saveProject();
		// ProtegeUtil.saveProject(project);
	}

	/**
	 * 
	 * @param ta
	 * @param alreadyCreatedAnnotations
	 * @param alreadyCreatedMentions
	 * @param mention
	 *            if this is null, a new mention is created from ta.getClassMention, otherwise, this
	 *            inputed mention is used for creating the Knowtator annotation
	 */
	private void createKnowtatorAnnotation(TextAnnotation ta, HashMap<Integer, String> alreadyCreatedAnnotations,
			HashMap<Long, SimpleInstance> alreadyCreatedMentions, SimpleInstance mention) {

		// check to see if the text annotation has already been added
		if (!alreadyCreatedAnnotations.containsKey(ta.getAnnotationID())) {

			// add key to alreadyAddedAnnotations
			alreadyCreatedAnnotations.put(ta.getAnnotationID(), "");

			// for this TextAnnotation, check to see if each associated
			// annotationset is already in the Knowtator project (Hash)
			Collection<SimpleInstance> annotationSets = validateAnnotationSets(ta);

			// check to see if the annotator is already in the Knowtator project
			// get the Annotator first name, last name, and affiliation
			SimpleInstance annotatorInstance = validateAnnotator(ta);

			// create Span list - must convert utilSpans to knowtatorSpans
			// logger.info(String.format("Inserting annotation into knowtator with spans: %s",
			// edu.ucdenver.ccp.nlp.core.annotation.Span.toString(ta.getSpans())));
			List<Span> knowtatorSpans = convertUtilSpanList2KnowtatorSpanList(ta.getSpans());
			
			
				

			// get TextSource instance
			SimpleInstance textSourceInstance = this.getTextSourceInstanceFromKnowtatorProject(ta.getDocumentID());

			if (mention == null) {
				ClassMention cm = ta.getClassMention();
				assert (cm.getMentionID() != -1L) : "Mention ID should not be -1 here.";
				mention = createKnowtatorMention(cm, alreadyCreatedAnnotations, alreadyCreatedMentions);
			}

			// finally, create a new Knowtator annotation
			try {
				if (textSourceInstance == null) {
					System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
					System.err.println("ERROR... detected null text source. Expected text source for document ID: "
							+ ta.getDocumentID());

					textSourceInstance = this.getTextSourceInstanceFromKnowtatorProject(ta.getDocumentID());
					if (textSourceInstance == null) {
						System.err.println("Tried again and got NULL ");
					} else {
						System.err.println("Tried again and got: " + textSourceInstance.getName());
					}
					System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				}

				SimpleInstance knowtatorAnnotation = annotationUtil.createAnnotation(mention, annotatorInstance,
						knowtatorSpans, null, textSourceInstance, annotationSets);
				
				String comment = ta.getAnnotationComment();
				if (comment != null) {
					setAnnotationComment(knowtatorAnnotation, comment);
					assert getAnnotationComment(knowtatorAnnotation).equals(comment);
				}

				assert (getKnowtatorClassMentionFromAnnotation(knowtatorAnnotation) != null) : "Each annotation must have an associated class mention";
				// logger.info(String.format("Creating knowtator annotation %s of type '%s'", (new
				// WrappedKnowtatorAnnotation(knowtatorAnnotation,this)).getAggregateSpan().toString(),getKnowtatorClassMentionFromAnnotation(knowtatorAnnotation).getName()
				// ));
				// add the new annotation to the annotations2TextID Hash
				updateTextSourceName2AnnotationsMap(knowtatorAnnotation);
			} catch (TextSourceAccessException tsae) {
				throw new RuntimeException(tsae);
			}
		} else {
			// do nothing, this annotation has already been created in the
			// Knowtator project
		}

	}

	/**
	 * Deletes all annotations associated with the input text source
	 * 
	 * @param textSourceName
	 */
	public void deleteAnnotationsForTextSource(String textSourceName) {
		Collection<SimpleInstance> annotationsForTextSource = getKnowtatorAnnotationsForDocument(textSourceName);
		System.err.println("Removing " + annotationsForTextSource.size() + " annotations");
		for (SimpleInstance annotationInstance : annotationsForTextSource) {
			deleteAnnotation(annotationInstance);
		}
	}

	public void deleteAnnotation(SimpleInstance annotationInstance) {
		annotationUtil.deleteMention(annotationInstance);
		kb.deleteInstance(annotationInstance);
	}

	public Collection<SimpleInstance> getComplexSlotMentions(SimpleInstance classMention) {
		return mentionUtil.getComplexSlotMentions(classMention);
	}

	private List<SimpleInstance> getSimpleSlotMentions(SimpleInstance mention) {
		List<SimpleInstance> returnValues = new ArrayList<SimpleInstance>();
		if (mentionUtil.isClassMention(mention) || mentionUtil.isInstanceMention(mention)) {
			List<SimpleInstance> allSlotMentions = mentionUtil.getSlotMentions(mention);
			for (SimpleInstance slotMention : allSlotMentions) {
				if (mentionUtil.isSimpleSlotMention(slotMention)) {
					returnValues.add(slotMention);
				}
			}
		}
		return returnValues;
	}

	public Collection<SimpleInstance> getPrimitiveSlotMentions(SimpleInstance classMention) {
		return getSimpleSlotMentions(classMention);
	}

	public SimpleInstance getKnowtatorAnnotationFromClassMention(SimpleInstance classMention) {
		return mentionUtil.getMentionAnnotation(classMention);
	}

	public void setKnowtatorAnnotationForClassMention(SimpleInstance classMention, SimpleInstance annotation) {
		Collection<SimpleInstance> annotations = new ArrayList<SimpleInstance>();
		annotations.add(annotation);
		mentionUtil.setMentionAnnotations(classMention, annotations);
	}

	public SimpleInstance getKnowtatorClassMentionFromAnnotation(SimpleInstance annotation) {
		return (SimpleInstance) annotation.getOwnSlotValue(kpu.getAnnotatedMentionSlot());
	}

	public SimpleInstance createKnowtatorClassMention(String mentionName) {
		SimpleInstance newMention = mentionUtil.createMention(kb.getCls(mentionName));
		return newMention;
	}

	private SimpleInstance createKnowtatorMention(Mention mentionToAdd,
			HashMap<Integer, String> alreadyCreatedAnnotations, HashMap<Long, SimpleInstance> alreadyCreatedMentions) {

		SimpleInstance returnMention;

		if (mentionToAdd instanceof ClassMention) {
			returnMention = processClassMention((ClassMention) mentionToAdd, alreadyCreatedAnnotations,
					alreadyCreatedMentions);
		} else if (mentionToAdd instanceof ComplexSlotMention) {
			returnMention = processComplexSlotMention((ComplexSlotMention) mentionToAdd, alreadyCreatedAnnotations,
					alreadyCreatedMentions);
		} else if (mentionToAdd instanceof PrimitiveSlotMention) {
			returnMention = processSlotMention((PrimitiveSlotMention) mentionToAdd);
		} else {
			logger.error("The mention you are trying to create is an instance of: " + mentionToAdd.getClass().getName()
					+ "\nCurrently, only ClassMentions, ComplexSlotMentions, and SlotMentions can be added.");
			returnMention = null;
		}

		return returnMention;
	}

	private SimpleInstance processClassMention(ClassMention classMention,
			HashMap<Integer, String> alreadyCreatedAnnotations, HashMap<Long, SimpleInstance> alreadyCreatedMentions) {

		SimpleInstance knowtatorMention;
		// logger.info(String.format("processing cm of type %s and span %s with id %d",classMention.getMentionName(),
		// classMention.getTextAnnotation().getAggregateSpan().toString(),
		// classMention.getMentionID()));
		assert (classMention.getMentionID() != -1L) : "Mention ID should not be -1 here.";
		// check to see if this mention has already been created
		if (alreadyCreatedMentions.containsKey(classMention.getMentionID())) {
			knowtatorMention = alreadyCreatedMentions.get(classMention.getMentionID());
		} else {
			// create a new Knowtator mention
			assert kb.getCls(classMention.getMentionName()) != null : String.format(
					"Knowledgebase is missing required class: %s", classMention.getMentionName());
			int mentionCount = getNumberOfClassMentionsInProject();
			knowtatorMention = mentionUtil.createMention(kb.getCls(classMention.getMentionName()));
			assert (getNumberOfClassMentionsInProject() == mentionCount + 1) : String
					.format("Created a mention but the number of mentions in project did not increase.");
			assert (mentionUtil.getMentionCls(knowtatorMention).getName().equals(classMention.getMentionName())) : String
					.format("Expected new mention to be class '%s' but instead was class '%s'", classMention
							.getMentionName(), mentionUtil.getMentionCls(knowtatorMention).getName());

			alreadyCreatedMentions.put(classMention.getMentionID(), knowtatorMention);

			Collection<ComplexSlotMention> complexSlotMentions = classMention.getComplexSlotMentions();
			for (ComplexSlotMention csm : complexSlotMentions) {
				SimpleInstance complexSlotMention = createKnowtatorMention(csm, alreadyCreatedAnnotations,
						alreadyCreatedMentions);
				mentionUtil.addSlotMention(knowtatorMention, complexSlotMention);
			}

			Collection<PrimitiveSlotMention> slotMentions = null;
			try {
				slotMentions = classMention.getPrimitiveSlotMentions();
			} catch (KnowledgeRepresentationWrapperException e) {
				throw new RuntimeException(e);
			}
			for (PrimitiveSlotMention sm : slotMentions) {
				SimpleInstance slotMention = createKnowtatorMention(sm, alreadyCreatedAnnotations,
						alreadyCreatedMentions);
				mentionUtil.addSlotMention(knowtatorMention, slotMention);
			}

			createKnowtatorAnnotation(classMention.getTextAnnotation(), alreadyCreatedAnnotations,
					alreadyCreatedMentions, knowtatorMention);
		}
		return knowtatorMention;
	}

	public void addSlotMentionToClassMention(SimpleInstance classMention, SimpleInstance slotMention) {
		mentionUtil.addSlotMention(classMention, slotMention);
	}

	public Cls getClassMentionCls() {
		// System.err.println("ClassmentionCls==null: " + (kpu.getClassMentionCls() == null));
		return kpu.getClassMentionCls();
	}

	public Cls getSlotMentionCls() {
		return kpu.getSlotMentionCls();
	}

	public boolean isComplexSlotMention(SimpleInstance mention) {
		return mentionUtil.isComplexSlotMention(mention);
	}

	public boolean isPrimitiveSlotMention(SimpleInstance mention) {
		return mentionUtil.isSimpleSlotMention(mention);
	}

	/**
	 * Converts a util complex slot mention to a knowtator complex slot mention
	 * 
	 * @param complexSlotMention
	 * @param alreadyCreatedAnnotations
	 * @param alreadyCreatedMentions
	 * @return
	 */
	private SimpleInstance processComplexSlotMention(ComplexSlotMention complexSlotMention,
			HashMap<Integer, String> alreadyCreatedAnnotations, HashMap<Long, SimpleInstance> alreadyCreatedMentions) {

		SimpleInstance knowtatorMention = mentionUtil
				.createSlotMention(kb.getSlot(complexSlotMention.getMentionName()));
		Collection<ClassMention> classMentions = complexSlotMention.getClassMentions();
		for (ClassMention cm : classMentions) {
			// logger.info(String.format("complex slot mention (%s) filler type %s with id %d",complexSlotMention.getMentionName(),
			// cm.getMentionName(), cm.getMentionID()));
			SimpleInstance classMention = createKnowtatorMention(cm, alreadyCreatedAnnotations, alreadyCreatedMentions);
			mentionUtil.addValueToSlotMention(knowtatorMention, classMention);
		}
		return knowtatorMention;
	}

	public SimpleInstance createKnowtatorSlotMention(String mentionName) {
		return mentionUtil.createSlotMention(kb.getSlot(mentionName));
	}

	public SimpleInstance createKnowtatorPrimitiveSlotMention(String mentionName, Object slotValue) {
		SimpleInstance slotMention = createKnowtatorSlotMention(mentionName);
		mentionUtil.addValueToSlotMention(slotMention, slotValue);
		return slotMention;
	}

	/**
	 * Converts a util primitive slot mention into a knowtator slot mention
	 * 
	 * @param slotMention
	 * @return
	 */
	private SimpleInstance processSlotMention(PrimitiveSlotMention slotMention) {

		SimpleInstance knowtatorMention = null;
		try {
			knowtatorMention = mentionUtil.createSlotMention(kb.getSlot(slotMention.getMentionName()));
		} catch (NullPointerException npe) {
			logger
					.error("Error while processing a slot mention. This is most likely caused by the slot mention not existing in the Knowtator project. Please check that: \""
							+ slotMention.getMentionName()
							+ "\" is part of the Knowtator project you are working with.");
			throw new RuntimeException(npe);
		}

		ArrayList<Object> slotValues = (ArrayList<Object>) slotMention.getSlotValues();
		for (Object value : slotValues) {
			mentionUtil.addValueToSlotMention(knowtatorMention, value);
		}

		return knowtatorMention;
	}

	private SimpleInstance validateAnnotator(TextAnnotation ta) {
		Annotator annotator = ta.getAnnotator();
		return validateAnnotator(annotator);
	}

	/**
	 * This method checks if the annotator associated with the given TextAnnotation is part of the
	 * Knowtator project. If the annotator is not found in the Knowtator project, it is added to the
	 * Knowtator project.
	 * 
	 * @param ta
	 */
	private SimpleInstance validateAnnotator(Annotator annotator) {
		String annotatorFirstName;
		String annotatorLastName;
		String annotatorAffiliation;
		if (annotator != null) {
			// annotator = annotatorValidator.getAnnotatorWithID(new Integer(ta.getAnnotatorID()));
			annotatorFirstName = annotator.getFirstName();
			annotatorLastName = annotator.getLastName();
			annotatorAffiliation = annotator.getAffiliation();
		} else {
			annotatorFirstName = "Default";
			annotatorLastName = "Default";
			annotatorAffiliation = "Unknown";
		}

		// check to see if the Annotator is already in the Knowtator project
		SimpleInstance annotatorInstance = getAnnotatorInstanceFromKnowtatorProject(annotatorFirstName,
				annotatorLastName, annotatorAffiliation);
		// if it is not already in the Knowtator project, then we should add
		// it to the project
		if (annotatorInstance == null) {
			logger.info("Annotator not found in Knowtator project... adding " + annotatorFirstName + " "
					+ annotatorLastName + ", " + annotatorAffiliation);
			this.createAnnotatorInKnowtatorProject(annotatorFirstName, annotatorLastName, annotatorAffiliation);
			annotatorInstance = getAnnotatorInstanceFromKnowtatorProject(annotatorFirstName, annotatorLastName,
					annotatorAffiliation);
		}

		return annotatorInstance;
	}

	public void setSpans(SimpleInstance annotation, List<edu.ucdenver.ccp.nlp.core.annotation.Span> spans) {
		List<Span> knowtatorSpans = convertUtilSpanList2KnowtatorSpanList(spans);
		try {
			annotationUtil.setSpans(annotation, knowtatorSpans, null);
		} catch (TextSourceAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void offsetSpans(SimpleInstance annotation, int offset) {
		List<Span> spans = annotationUtil.getSpans(annotation);
		List<Span> updatedSpans = new ArrayList<Span>();
		for (Span span : spans) {
			updatedSpans.add(new Span(span.getStart() + offset, span.getEnd() + offset));
		}
		try {
			annotationUtil.setSpans(annotation, updatedSpans, null);
		} catch (TextSourceAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void setAnnotationSpanEnd(SimpleInstance annotation, int spanEnd) {
		sortSpans(annotation);
		List<Span> spans = annotationUtil.getSpans(annotation);
		Span trailingSpan = spans.get(spans.size() - 1);
		if (spanEnd > trailingSpan.getStart()) {
			Span updatedEndSpan = new Span(trailingSpan.getStart(), spanEnd);
			spans.remove(spans.size() - 1);
			spans.add(updatedEndSpan);
		} else {
			throw new InvalidSpanException(
					"Updating span end has caused an invalid span. The desired span end offset is to the left of the start of the trailing (last) span for this annotation.");
		}
	}

	public void setAnnotationSpanStart(SimpleInstance annotation, int spanStart) {
		sortSpans(annotation);
		List<Span> spans = annotationUtil.getSpans(annotation);
		Span leadingSpan = spans.get(0);
		if (spanStart < leadingSpan.getEnd()) {
			Span updatedStartSpan = new Span(spanStart, leadingSpan.getEnd());
			spans.remove(0);
			spans.add(0, updatedStartSpan);
		} else {
			throw new InvalidSpanException(
					"Updating span end has caused an invalid span. The desired span end offset is to the left of the start of the trailing (last) span for this annotation.");
		}
	}

	public void sortSpans(SimpleInstance annotation) {
		List<Span> spans = annotationUtil.getSpans(annotation);
		Collections.sort(spans, KNOWTATOR_SPAN_BY_OFFSET());
	}

	private static Comparator<Span> KNOWTATOR_SPAN_BY_OFFSET() {
		return new Comparator<Span>() {
			public int compare(Span span1, Span span2) {
				return span1.compareTo(span2);
			}
		};
	}

	public void setAnnotator(SimpleInstance annotation, Annotator annotator) {
		SimpleInstance annotationInstance = validateAnnotator(annotator);
		annotation.setOwnSlotValue(kpu.getAnnotationAnnotatorSlot(), annotationInstance);
	}

	public void setAnnotationSets(SimpleInstance annotation, Set<AnnotationSet> annotationSets) {
		Collection<SimpleInstance> annotationSetInstances = validateAnnotationSets(annotationSets);
		annotation.setOwnSlotValues(kpu.getSetSlot(), annotationSetInstances);
	}

	public void addAnnotationSet(SimpleInstance annotation, AnnotationSet annotationSet) {
		SimpleInstance annotationSetInstance = validateAnnotationSet(annotationSet);
		annotation.addOwnSlotValue(kpu.getSetSlot(), annotationSetInstance);
	}

	private Collection<SimpleInstance> validateAnnotationSets(TextAnnotation ta) {
		return validateAnnotationSets(ta.getAnnotationSets());
	}

	/**
	 * This method checks if the annotation sets associated with a given TextAnnotation are part of
	 * the Knowtator project. If the annotation sets are not found, they are added to the Knowtator
	 * project.
	 * 
	 * @param ta
	 */
	private Collection<SimpleInstance> validateAnnotationSets(Set<AnnotationSet> annotationSets) {
		Collection<SimpleInstance> annotationSetInstances = new ArrayList<SimpleInstance>();
		for (AnnotationSet annotationSet : annotationSets) {
			// add the annotation set instance to the collection
			annotationSetInstances.add(validateAnnotationSet(annotationSet));
		}
		return annotationSetInstances;
	}

	private SimpleInstance validateAnnotationSet(AnnotationSet annotationSet) {
		String annotationSetName = null;
		String annotationSetDescription = null;
		if (annotationSet != null) {
			annotationSetName = annotationSet.getAnnotationSetName();
			annotationSetDescription = annotationSet.getAnnotationSetDescription();
		} else {
			annotationSetName = "Default Set";
			annotationSetDescription = "Default Set";
		}

		// check to see if Annotation Set is already in the Knowtator
		// project
		SimpleInstance annotationSetInstance = getAnnotationSetInstanceFromKnowtatorProject(annotationSetName);
		// if it is not already in the Knowtator project, then we should
		// add it to the project
		if (annotationSetInstance == null) {
			logger.info("Annotation set not found in Knowtator project... adding " + annotationSetName);
			this.createAnnotationSetInKnowtatorProject(annotationSetName, annotationSetDescription);
			annotationSetInstance = getAnnotationSetInstanceFromKnowtatorProject(annotationSetName);
		}
		return annotationSetInstance;
	}

	/**
	 * Returns a TextSource instance given an input String specifying the TextSource name, which is
	 * typically the same as the document unique ID.
	 */
	public SimpleInstance getTextSourceInstanceFromKnowtatorProject(String textSourceName) {
		if (textSourceName2InstanceMap.containsKey(textSourceName)) {
			return textSourceName2InstanceMap.get(textSourceName);
		} else {
			return null;
		}
	}

	public TextSourceUtil getTextSourceUtil() {
		return textSourceUtil;
	}

	/**
	 * Returns a list of text source names for this project
	 * 
	 * @return
	 */
	public List<String> getTextSourceNames() {
		List<String> textSourceNames = new ArrayList<String>();
		TextSourceCollection tsc = textSourceUtil.getCurrentTextSourceCollection();
		TextSourceIterator tsIter = tsc.iterator();
		while (tsIter.hasNext()) {
			try {
				TextSource ts = tsIter.next();
				String textSourceName = ts.getName();
				textSourceNames.add(textSourceName);
			} catch (TextSourceAccessException e) {
				throw new RuntimeException(e);
			}
		}

		return textSourceNames;
	}

	/**
	 * Enables the user to add a FileTextSource to the collection on the fly
	 * 
	 */
	public void addFileTextSourceToCollection(File fileToAdd) {
		TextSourceCollection tsc = textSourceUtil.getCurrentTextSourceCollection();
		if (tsc instanceof FileTextSourceCollection) {
			FileTextSourceCollection ftsc = (FileTextSourceCollection) tsc;
			ftsc.addTextSourceToCollection(fileToAdd);
			/* update the textSourceName2InstanceHash */
			populateTextSourceName2InstanceMap();
		} else {
			logger
					.warn("Attempting to add a new TextSource on the fly to a non-FileTextSourceCollection. This is not allowed.");
		}
	}

	/**
	 * Returns the text source directory from a Knowtator project if that project is using the
	 * FileTextSourceCollection, null otherwise.
	 * 
	 * @return
	 */
	public File getDirectoryForFileTextSourceCollection() {
		TextSourceCollection tsc = textSourceUtil.getCurrentTextSourceCollection();
		if (tsc instanceof FileTextSourceCollection) {
			FileTextSourceCollection ftsc = (FileTextSourceCollection) tsc;
			return ftsc.getDirectory();
		} else {
			logger
					.warn("Attempting to retrieve the working directory from a non-FileTextSourceCollection. This is not allowed.");
			return null;
		}
	}

	public void setDirectoryForFileTextSourceCollection(File directory, Charset charSet) throws IOException {
		FileTextSourceCollection tsc = new FileTextSourceCollection(directory, charSet);
		textSourceUtil.setCurrentTextSourceCollection(tsc);
	}

	/**
	 * This method returns an AnnotationSet (Consensus Set) instance given an input String
	 * specifying the name of the annotation set.
	 * 
	 * @param instanceName
	 * @return
	 */
	private SimpleInstance getAnnotationSetInstanceFromKnowtatorProject(String instanceName) {
		Cls c = kb.getCls(KnowtatorProjectUtil.SET_CLS_NAME);
		Iterator iter = c.getDirectInstances().iterator();
		while (iter.hasNext()) {
			SimpleInstance instance = (SimpleInstance) iter.next();
			String setName = (String) instance.getDirectOwnSlotValue(kpu.getSetNameSlot());
			if (setName.equalsIgnoreCase(instanceName)) {
				return instance;
			}
		}
		return null;
	}

	/**
	 * This method creates an annotation set with the given name in the Knowtator project
	 * 
	 * @param consensusSetName
	 * @param setDescription
	 */
	private void createAnnotationSetInKnowtatorProject(String consensusSetName, String setDescription) {
		SimpleInstance consensusSet = kb.createSimpleInstance(null, null, CollectionUtilities.createCollection(kpu
				.getSetCls()), true);
		consensusSet.setDirectOwnSlotValue(kpu.getSetNameSlot(), consensusSetName);
		consensusSet.setDirectOwnSlotValue(kpu.getSetDescriptionSlot(), setDescription);
	}

	/**
	 * This method creates an annotator with the given name and affiliation in the Knowtator project
	 * 
	 * @param annotatorFirstName
	 * @param annotatorLastName
	 * @param annotatorAffiliation
	 */
	private void createAnnotatorInKnowtatorProject(String annotatorFirstName, String annotatorLastName,
			String annotatorAffiliation) {
		SimpleInstance annotator = kb.createSimpleInstance(null, null, CollectionUtilities.createCollection(kpu
				.getHumanAnnotatorCls()), true);
		annotator.setDirectOwnSlotValue(kb.getSlot(KnowtatorProjectUtil.ANNOTATOR_FIRST_NAME_SLOT_NAME),
				annotatorFirstName);
		annotator.setDirectOwnSlotValue(kb.getSlot(KnowtatorProjectUtil.ANNOTATOR_LAST_NAME_SLOT_NAME),
				annotatorLastName);
		annotator.setDirectOwnSlotValue(kb.getSlot(KnowtatorProjectUtil.ANNOTATOR_AFFILIATION_SLOT_NAME),
				annotatorAffiliation);
	}

	/**
	 * This method retrieves an instance of an Annotator from a Knowtator project given the name and
	 * affiliation.
	 * 
	 * @param firstName
	 * @param lastName
	 * @param affiliation
	 * @return
	 */
	private SimpleInstance getAnnotatorInstanceFromKnowtatorProject(String firstName, String lastName,
			String affiliation) {
		if (firstName == null) {
			firstName = "";
		}
		if (lastName == null) {
			lastName = "";
		}
		if (affiliation == null) {
			affiliation = "";
		}
		Cls c = kb.getCls(KnowtatorProjectUtil.HUMAN_ANNOTATOR_CLS_NAME);
		Iterator iter = c.getDirectInstances().iterator();
		while (iter.hasNext()) {
			SimpleInstance instance = (SimpleInstance) iter.next();
			String fn = (String) instance.getDirectOwnSlotValue(kb
					.getSlot(KnowtatorProjectUtil.ANNOTATOR_FIRST_NAME_SLOT_NAME));
			String ln = (String) instance.getDirectOwnSlotValue(kb
					.getSlot(KnowtatorProjectUtil.ANNOTATOR_LAST_NAME_SLOT_NAME));
			String af = (String) instance.getDirectOwnSlotValue(kb
					.getSlot(KnowtatorProjectUtil.ANNOTATOR_AFFILIATION_SLOT_NAME));

			if (fn == null) {
				fn = "";
			}
			if (ln == null) {
				ln = "";
			}
			if (af == null) {
				af = "";
			}
			if (fn.equalsIgnoreCase(firstName) & ln.equalsIgnoreCase(lastName) & af.equalsIgnoreCase(affiliation)) {
				return instance;
			}
		}
		return null;
	}

	/**
	 * Returns a List of the annotations belonging to a give documentID
	 * 
	 * @param documentID
	 */
	private List<SimpleInstance> getKnowtatorAnnotationsForDocument(String documentID) {
		ArrayList<SimpleInstance> annotationsList = new ArrayList<SimpleInstance>();
		if (textSourceName2AnnotationsMap.containsKey(documentID)) {
			annotationsList = (ArrayList<SimpleInstance>) textSourceName2AnnotationsMap.get(documentID);
		}
		return annotationsList;
	}

	/**
	 * creates a util span list from a knowtator span list
	 * 
	 * @param knowtatorSpanList
	 * @return
	 */
	private List<edu.ucdenver.ccp.nlp.core.annotation.Span> convertKnowatorSpanList2UtilSpanList(
			List<edu.uchsc.ccp.knowtator.Span> knowtatorSpanList) {
		ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span> utilSpanList = new ArrayList<edu.ucdenver.ccp.nlp.core.annotation.Span>();
		for (edu.uchsc.ccp.knowtator.Span span : knowtatorSpanList) {
			utilSpanList.add(convertKnowtatorSpan2UtilSpan(span));
		}
		return utilSpanList;
	}

	/**
	 * creates a knowtator span list from a util span list
	 * 
	 * @param utilSpanList
	 * @return
	 */
	private List<edu.uchsc.ccp.knowtator.Span> convertUtilSpanList2KnowtatorSpanList(
			List<edu.ucdenver.ccp.nlp.core.annotation.Span> utilSpanList) {
		ArrayList<Span> knowtatorSpans = new ArrayList<Span>();
		for (edu.ucdenver.ccp.nlp.core.annotation.Span utilSpan : utilSpanList) {
			knowtatorSpans.add(convertUtilSpan2KnowtatorSpan(utilSpan));
		}
		return knowtatorSpans;
	}

	/**
	 * Converts a single knowtator span to a single util span
	 * 
	 * @param knowtatorSpan
	 * @return
	 * @throws InvalidSpanException
	 */
	private edu.ucdenver.ccp.nlp.core.annotation.Span convertKnowtatorSpan2UtilSpan(
			edu.uchsc.ccp.knowtator.Span knowtatorSpan) throws InvalidSpanException {
		edu.ucdenver.ccp.nlp.core.annotation.Span utilSpan;
		utilSpan = new edu.ucdenver.ccp.nlp.core.annotation.Span(knowtatorSpan.getStart(), knowtatorSpan.getEnd());
		return utilSpan;
	}

	/**
	 * converts a single util span to a new knowtator span
	 * 
	 * @param utilSpan
	 * @return
	 * @throws InvalidSpanException
	 */
	private edu.uchsc.ccp.knowtator.Span convertUtilSpan2KnowtatorSpan(edu.ucdenver.ccp.nlp.core.annotation.Span utilSpan)
			throws InvalidSpanException {
		edu.uchsc.ccp.knowtator.Span knowtatorSpan = new edu.uchsc.ccp.knowtator.Span(utilSpan.getSpanStart(), utilSpan
				.getSpanEnd());
		return knowtatorSpan;
	}

	// /**
	// * Print to the display the annotations contained in the Knowtator Project belonging to a
	// given
	// * document ID. This method is useful for testing/debugging.
	// *
	// * @param documentID
	// * the document ID for the document of interest
	// */
	// public void printAnnotationsForDocument(String documentID) {
	//
	// List<SimpleInstance> annotations = getKnowtatorAnnotationsForDocument(documentID);
	//
	// for (SimpleInstance annotation : annotations) {
	// printAnnotation(annotation, true, 0);
	// }
	//
	// }

	// /*
	// * Prints the contents of a Knowtator annotation SimpleInstance to the screen
	// */
	// private void printAnnotation(SimpleInstance annotation, boolean printMentions, int
	// indentLevel) {
	// String indentSpace = "";
	// for (int i = 0; i < indentLevel * 4; i++) {
	// indentSpace += " ";
	// }
	//
	// /*********************************************************************************************************************************************
	// * Get the Annotation-specific information: span, annotator, date, color, text source,
	// * annotation set(s), etc.
	// ********************************************************************************************************************************************/
	// // get the annotator
	// SimpleInstance annotator = annotationUtil.getAnnotator(annotation);
	// String annotatorID = annotator.getName();
	// String annotatorName = annotator.getBrowserText();
	//
	// // get the comment
	// String comment = annotationUtil.getComment(annotation);
	//
	// // get the date
	// String dateString = annotationUtil.getCreationDate(annotation);
	//
	// // get the color
	// // Color annotationColor = displayColors.getColor(annotation);
	//
	// // get the covered text
	// String spannedText = annotationUtil.getText(annotation);
	//
	// // get the text source
	// SimpleInstance textSourceInstance = annotationUtil.getTextSource(annotation);
	// String textSourceID = textSourceInstance.getName();
	//
	// // get the annotation sets that this annotation belongs to
	// Collection<SimpleInstance> annotationSets = (Collection<SimpleInstance>)
	// annotation.getOwnSlotValues(kpu
	// .getSetSlot());
	//
	// // get the spans of text associated with this annotation
	// Collection<Span> spans = null;
	// try {
	// spans = (Collection<Span>) annotationUtil.getSpans(annotation);
	// } catch (InvalidSpanException e) {
	// e.printStackTrace();
	// }
	//
	// /*********************************************************************************************************************************************
	// * Get the Mention-specific information:
	// ********************************************************************************************************************************************/
	// // get the mention for this annotation
	// SimpleInstance mention = annotationUtil.getMention(annotation);
	//
	// // get the slots for this mention
	// Collection<SimpleInstance> slotMentions = (Collection<SimpleInstance>)
	// mention.getOwnSlotValues(kpu
	// .getSlotMentionSlot());
	//
	// /*********************************************************************************************************************************************
	// * Print Annotation-specific information
	// ********************************************************************************************************************************************/
	// System.out.println(indentSpace + " ---------------------");
	// System.out.println(indentSpace + "TEXT ID: " + textSourceID + "\tAnnotator: " + annotatorName
	// + " ("
	// + annotatorID + ") " + "\tDate: " + dateString);
	// for (Iterator iter = spans.iterator(); iter.hasNext();) {
	// Span span = (Span) iter.next();
	// System.out.print(indentSpace + span.getStart() + " -- " + span.getEnd() + " | ");
	// }
	// System.out.print("Covered Text: " + spannedText + "\tComment: " + comment + "\tSets: {");
	// for (SimpleInstance setInstance : annotationSets) {
	// String setName = setInstance.getName();
	// System.out.print(setName + "; ");
	// }
	// System.out.println("}");
	//
	// /*********************************************************************************************************************************************
	// * Print Mention-specific information
	// ********************************************************************************************************************************************/
	// // System.out.println("Printing first mention...");
	// if (printMentions) {
	// printMention(mention, 0);
	// }
	// // System.out.println("Finished printing first mention...");
	// // for (SimpleInstance ment : slotMentions) {
	// // System.out.print(" with slot: "); printMention(ment);
	// // }
	// System.out.println(indentSpace + " ---------------------");
	// }
	//
	// private void printMention(SimpleInstance mention, int indentLevel) {
	// String indentSpace = "";
	// for (int i = 0; i < indentLevel * 4; i++) {
	// indentSpace += " ";
	// }
	//
	// Collection<Span> spans = null;
	// try {
	// spans = (Collection<Span>) annotationUtil.getSpans(mention);
	// } catch (InvalidSpanException e) {
	// e.printStackTrace();
	// }
	// // List l = mentionUtil.getMentionAnnotations(mention);
	// // System.out.println("Mention Annotations Count = " + l.size());
	// if (mentionUtil.isClassMention(mention)) {
	// Cls classMention = mentionUtil.getMentionCls(mention);
	// System.out.println(indentSpace + "CLASS MENTION: " + classMention.getName());
	//
	// SimpleInstance annotationForThisMention = mentionUtil.getMentionAnnotation(mention);
	// System.out.println(indentSpace +
	// "+++++++++++++++++ CORRESPONDING TEXT ANNOTATIONS FOR MENTION: "
	// + classMention.getName() + " +++++++++++++++++++++");
	// printAnnotation(annotationForThisMention, false, indentLevel);
	// System.out.println(indentSpace
	// + "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	//
	// // for (Iterator iter = spans.iterator(); iter.hasNext();) {
	// // Span span = (Span) iter.next();
	// // System.out.print(" | " + span.getStart() + " -- " +
	// // span.getEnd());
	// // }
	// Collection<SimpleInstance> slotMentions = (Collection<SimpleInstance>)
	// mention.getOwnSlotValues(kpu
	// .getSlotMentionSlot());
	// indentLevel++;
	// for (SimpleInstance slotMention : slotMentions) {
	// // exportMention(slotMention, exportedMentions);
	// printMention(slotMention, indentLevel);
	// }
	// } else if (mentionUtil.isInstanceMention(mention)) {
	// SimpleInstance instance = mentionUtil.getMentionInstance(mention);
	// System.out.println(indentSpace + "INSTANCE MENTION: " + instance.getName());
	//
	// SimpleInstance annotationForThisMention = mentionUtil.getMentionAnnotation(mention);
	// System.out.println(indentSpace +
	// "+++++++++++++++++ CORRESPONDING TEXT ANNOTATIONS FOR MENTION: "
	// + instance.getName() + " +++++++++++++++++++++");
	// printAnnotation(annotationForThisMention, false, indentLevel);
	// System.out.println(indentSpace
	// + "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	//
	// Collection<SimpleInstance> slotMentions = (Collection<SimpleInstance>)
	// mention.getOwnSlotValues(kpu
	// .getSlotMentionSlot());
	// indentLevel++;
	// for (SimpleInstance slotMention : slotMentions) {
	// // exportMention(slotMention, exportedMentions);
	// printMention(slotMention, indentLevel);
	// }
	// } else if (mentionUtil.isComplexSlotMention(mention)) {
	// Slot slot = mentionUtil.getSlotMentionSlot(mention);
	// System.out.println(indentSpace + "COMPLEX SLOT MENTION: " + slot.getName());
	//
	// // Collection<SimpleInstance> annotationsForThisMention =
	// // mentionUtil.getMentionAnnotations(mention);
	// // System.out.println(indentSpace + "+++++++++++++++++ CORRESPONDING
	// // TEXT ANNOTATIONS FOR MENTION: " + slot.getName() + "
	// // +++++++++++++++++++++");
	// // for (SimpleInstance annot : annotationsForThisMention) {
	// // printAnnotation(annot, false, indentLevel);
	// // }
	// // System.out.println(indentSpace +
	// // "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	//
	// Collection slotValues = mention.getOwnSlotValues(kpu.getMentionSlotValueSlot());
	//
	// if (slotValues != null && slotValues.size() > 0) {
	// Object value = CollectionUtilities.getFirstItem(slotValues);
	// if (value instanceof SimpleInstance) {
	// indentLevel++;
	// for (Object slotValue : slotValues) {
	// SimpleInstance slotValueInstance = (SimpleInstance) slotValue;
	// // exportMention(slotValueInstance, exportedMentions);
	// printMention(slotValueInstance, indentLevel);
	// }
	// } else {
	// // value is some primitive such as int, String, boolean
	// System.out.println(indentSpace + "    SLOT VALUE: " + value);
	// }
	// }
	//
	// // Collection<SimpleInstance> slotMentions =
	// // mentionUtil.getComplexMentionSlotValues(mention);
	// // if (DEBUG) {
	// // System.out.println("There are " + slotMentions.size() + " slot
	// // values for Complex Slot Mention: " + slot.getName());
	// // Collection<Slot> subslots = slot.getSubslots();
	// // Collection<Slot> superslots = slot.getSuperslots();
	// // System.out.println("There are " + subslots.size() + " subSlots");
	// // for (Slot s : subslots) {
	// // System.out.println(" subslot name: ");
	// // }
	// // System.out.println("There are " + superslots.size() + "
	// // superSlots");
	// // for (Slot s : superslots) {
	// // System.out.println(" superslot name: ");
	// // }
	// // }
	// // for (SimpleInstance ment : slotMentions) {
	// // System.out.print(" with slot: "); printMention(ment);
	// // }
	// } else if (mentionUtil.isSlotMention(mention)) {
	// Slot slot = mentionUtil.getSlotMentionSlot(mention);
	// System.out.println(indentSpace + "SLOT MENTION: " + slot.getName());
	//
	// // Collection<SimpleInstance> annotationsForThisMention =
	// // mentionUtil.getMentionAnnotations(mention);
	// // System.out.println(indentSpace + "+++++++++++++++++ CORRESPONDING
	// // TEXT ANNOTATIONS FOR MENTION: " + slot.getName() + "
	// // +++++++++++++++++++++");
	// // for (SimpleInstance annot : annotationsForThisMention) {
	// // printAnnotation(annot, false, indentLevel);
	// // }
	// // System.out.println(indentSpace +
	// // "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	//
	// Collection slotValues = mention.getOwnSlotValues(kpu.getMentionSlotValueSlot());
	//
	// if (slotValues != null && slotValues.size() > 0) {
	// Object value = CollectionUtilities.getFirstItem(slotValues);
	// if (value instanceof SimpleInstance) {
	// indentLevel++;
	// for (Object slotValue : slotValues) {
	// SimpleInstance slotValueInstance = (SimpleInstance) slotValue;
	// // exportMention(slotValueInstance, exportedMentions);
	// printMention(slotValueInstance, indentLevel);
	// }
	// } else {
	//
	// // value is some primitive such as int, String, boolean
	// System.out.println(indentSpace + "    SLOT VALUE: " + value);
	// }
	// }
	// }
	//
	// // Collection<SimpleInstance> annotations =
	// // mentionUtil.getMentionAnnotations(mention);
	// // for (SimpleInstance annot : annotations) {
	// // printAnnotation(annot);
	// // }
	// }

	// private void displayErrors(Collection errors) {
	// Iterator i = errors.iterator();
	// while (i.hasNext()) {
	// System.out.println("Error: " + i.next());
	// }
	// }

	public void saveProject() {
		removeMentionIteratorIdSlotFromProject();
		removeMentionIdSlotFromProject();
		ProtegeUtil.saveProject(project);
	}

	/**
	 * returns a frame with all slots, but not filled in
	 * 
	 * @param className
	 * @return
	 */
	public ClassMention getFullFrame(String className) {
		DefaultClassMention cm = null;
		Cls cls = kb.getCls(className);
		Instance instance = kb.createInstance(null, cls);
		if (cls != null) {
			cm = new DefaultClassMention(cls.getName());
			Collection<Slot> slots = instance.getOwnSlots(); // cls.getDirectTemplateSlots();
			for (Object slot : slots) {
				String slotName = ((Slot) slot).getName();
				if (!slotName.startsWith(":")) {
					DefaultComplexSlotMention csm = new DefaultComplexSlotMention(slotName);
					cm.addComplexSlotMention(csm);
				}
			}
		}

		return cm;
	}

	/*
	 * This method cycles through each annotation and removes any part of it that does not have a
	 * span.
	 */
	private List<TextAnnotation> removeSpanlessAnnotations(List<TextAnnotation> annotationList) {

		System.err.println("AnnotationCount BEFORE: " + annotationList.size());

		List<TextAnnotation> annotationsToReturn = new ArrayList<TextAnnotation>();

		for (TextAnnotation ta : annotationList) {
			/*
			 * look to see if it has an invalid span.. if it does, remove it. If there are no other
			 * spans left, then remove the annotation altogether
			 */
			List<edu.ucdenver.ccp.nlp.core.annotation.Span> spans = ta.getSpans();
			int numSpans = spans.size();
			for (edu.ucdenver.ccp.nlp.core.annotation.Span span : spans) {
				if (!span.isValid()) {
					numSpans--;
					System.err.println("REMOVED INVALID SPAN");
				}
			}
			/*
			 * every time there is an invalid span, we decrement numSpans. If there are only invalid
			 * spans, then numSpans=0 at the end of the loop above. If there is at least one valid
			 * span remaining, then we keep the annotation, otherwise we discard it.
			 */
			if (numSpans > 0) {
				annotationsToReturn.add(ta);
			}
		}

		System.err.println("AnnotationCount AFTER: " + annotationsToReturn.size());

		return annotationsToReturn;

	}

	public void setComplexSlotMentions(SimpleInstance knowtatorCM,
			Collection<SimpleInstance> complexSlotMentionInstances) {
		Collection<SimpleInstance> slotMentionsToAdd = getPrimitiveSlotMentions(knowtatorCM);
		removeSlotMentions(knowtatorCM);
		slotMentionsToAdd.addAll(complexSlotMentionInstances);
		for (SimpleInstance smInstance : slotMentionsToAdd) {
			addSlotMentionToClassMention(knowtatorCM, smInstance);
		}
	}

	public void setPrimitiveSlotMentions(SimpleInstance knowtatorCM,
			Collection<SimpleInstance> primitiveSlotMentionInstances) {
		Collection<SimpleInstance> slotMentionsToAdd = getComplexSlotMentions(knowtatorCM);
		removeSlotMentions(knowtatorCM);
		slotMentionsToAdd.addAll(primitiveSlotMentionInstances);
		for (SimpleInstance smInstance : slotMentionsToAdd) {
			addSlotMentionToClassMention(knowtatorCM, smInstance);
		}
	}

	private void removeSlotMentions(SimpleInstance classMention) {
		Collection<SimpleInstance> slots = classMention.getOwnSlotValues(kpu.getSlotMentionSlot());
		for (SimpleInstance slot : slots) {
			classMention.removeOwnSlotValue(kpu.getSlotMentionSlot(), slot);
		}
	}

	public boolean isBooleanSlotMention(SimpleInstance knowtatorMention) {
		return mentionUtil.isBooleanSlotMention(knowtatorMention);
	}

	public boolean isStringSlotMention(SimpleInstance knowtatorMention) {
		return mentionUtil.isStringSlotMention(knowtatorMention);
	}

	public boolean isIntegerSlotMention(SimpleInstance knowtatorMention) {
		return mentionUtil.isIntegerSlotMention(knowtatorMention);
	}

	public boolean isFloatSlotMention(SimpleInstance knowtatorMention) {
		return mentionUtil.isFloatSlotMention(knowtatorMention);
	}

	public void setCoveredText(SimpleInstance annotation, String coveredText) {
		annotationUtil.setText(annotation, coveredText);
	}

	public void close() {
		logger.info(String.format("Closing KnowtatorUtil for project: %s", projectFileName));
		pu.close();
		if (project != null) {
			project.dispose();
		}
		kb = null;
		kpu = null;
		annotationUtil = null;
		textSourceUtil = null;
		mentionUtil = null;
		displayColors = null;
	}

	public void addColorMapping(String clsName, String colorName) {
		Cls cls = kb.getCls(clsName);
		if (cls != null) {
			SimpleInstance colorInstance = getColorInstance(colorName);
			if (colorInstance != null) {
				addColorMapping(cls, colorInstance);
			} else {
				logger.warn(String.format(
						"Cannot create color assignment for color '%s'. Color does not exist in project.", colorName));
			}
		} else {
			logger.warn(String.format(
					"Cannot create color assignment for class '%s'. Class does not exist in project.", clsName));
		}
	}

	public SimpleInstance getColorInstance(String colorName) {
		Collection<Instance> colorInstances = kb.getInstances(kpu.getDisplayColorCls());
		for (Instance colorInstance : colorInstances) {
			String colorInstanceName = (String) colorInstance.getDirectOwnSlotValue(kpu.getDisplayColorNameSlot());
			if (colorName.equalsIgnoreCase(colorInstanceName)) {
				return (SimpleInstance) colorInstance;
			}
		}
		return null;
	}

	public void addColorMapping(Cls cls, SimpleInstance colorInstance) {
		// int colorAssignmentCount = kb.getInstanceCount(kpu.getColorAssignmentCls());
		displayColors.addAssignment(cls, colorInstance);
		// SimpleInstance colorAssignment = kb.createSimpleInstance(null,null,
		// CollectionUtilities.createCollection(kpu.getColorAssignmentCls()), true);
		// colorAssignment.setOwnSlotValue(kpu.getColorClassSlot(), cls);
		// colorAssignment.setOwnSlotValue(kpu.getDisplayColorSlot(), colorInstance);
		SimpleInstance activeConfiguration = ProjectSettings.getActiveConfiguration(project);
		activeConfiguration.setDirectOwnSlotValues(kpu.getColorAssignmentsSlot(), kb.getInstances(kpu
				.getColorAssignmentCls()));
	}

	public void checkConfiguration() {
		logger.info("CHECKING CONFIGURATION");
		SimpleInstance activeConfiguration = ProjectSettings.getActiveConfiguration(project);
		logger.info(String.format("Active configuration name: %s #slots: %d #references: %d", activeConfiguration
				.getName(), activeConfiguration.getOwnSlots().size(), activeConfiguration.getReferences().size()));
		Collection<Slot> slots = activeConfiguration.getOwnSlots();
		for (Slot slot : slots) {
			Collection<Object> slotValues = activeConfiguration.getOwnSlotValues(slot);
			String slotValuesStr = "";
			if (slotValues != null) {
				slotValuesStr = slotValues.toString();
			}
			logger.info(String.format("Slot name: %s -- values: %s", slot.getName(), slotValuesStr));
		}

	}

	public void removeAnnotationsWithNullClassMentions() {
		Collection<SimpleInstance> annotationsToDelete = new ArrayList<SimpleInstance>();
		for (SimpleInstance annotation : getKnowtatorAnnotationInstances()) {
			SimpleInstance mention = getKnowtatorClassMentionFromAnnotation(annotation);
			if (mention == null) {
				annotationsToDelete.add(annotation);
			}
		}

		for (SimpleInstance annotationToDelete : annotationsToDelete) {
			String documentID = getTextSourceNameFromKnowtatorAnnotation(annotationToDelete);
			List<edu.ucdenver.ccp.nlp.core.annotation.Span> spans = getUtilSpanList(annotationToDelete);
			String coveredText = getCoveredText(annotationToDelete);
			logger.warn(String.format("Deleting annotation with null mention [%s] [%d..%d] '%s'", documentID, spans
					.get(0).getSpanStart(), spans.get(spans.size() - 1).getSpanEnd(), coveredText));
			deleteAnnotation(annotationToDelete);
		}
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		KnowtatorUtil ku = new KnowtatorUtil(
				"/Users/bill/Documents/eclipse-workspace/CRAFT/craft-combined/merged-knowtator-projects/craft-combined-11-14.pprj");
		ku.checkConfiguration();

	}

	// /**
	// * This method creates a filter. UNTESTED... provided by Philip
	// *
	// * @param consensusSetName
	// * @param consensusSet
	// * @param annotators
	// * @param teamAnnotator
	// */
	// public void createFilterInKnowtatorProject(String consensusSetName,
	// Object consensusSet, Collection annotators,
	// Object teamAnnotator) {
	// SimpleInstance consensusFilter = kb.createSimpleInstance(null, null,
	// CollectionUtilities.createCollection(kpu
	// .getConsensusFilterCls()), true);
	// consensusFilter.setDirectOwnSlotValue(kpu.getFilterNameSlot(),
	// consensusSetName + " filter");
	// consensusFilter.setDirectOwnSlotValue(kpu.getFilterSetSlot(),
	// consensusSet);
	// consensusFilter.setDirectOwnSlotValues(kpu.getFilterAnnotatorSlot(),
	// annotators);
	// consensusFilter.addOwnSlotValue(kpu.getFilterAnnotatorSlot(),
	// teamAnnotator);
	// }

	/**
	 * Sets the annotation comment for the input annotation
	 */
	public void setAnnotationComment(SimpleInstance knowtatorAnnotation, String comment) {
		annotationUtil.setComment(knowtatorAnnotation, comment);
		assert comment.equals(getAnnotationComment(knowtatorAnnotation));
	}

	/**
	 * Returns the annotation comment for the input annotation
	 * 
	 * @param knowtatorAnnotation
	 * @return
	 */
	public String getAnnotationComment(SimpleInstance knowtatorAnnotation) {
		return annotationUtil.getComment(knowtatorAnnotation);
	}
}
