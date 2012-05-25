/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
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

package edu.ucdenver.ccp.wrapper.protege.v3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.storage.clips.ClipsKnowledgeBaseFactory;

public class ProtegeUtil {
	private static Logger logger = Logger.getLogger(ProtegeUtil.class);

	private static final String METACLS_LEVEL_OBO_RELATIONSHIP_CLS_NAME = "metaclass-level OBO relationship";
	private static final String CLS_LEVEL_OBO_RELATIONSHIP_CLS_NAME = "class-level OBO relationship";
	private static final String OBO_TERM_META_CLS = "OBO term";
	private static final String KNOWTATOR_SUPPORT_CLASS = "knowtator support class";
	private static final String OBO_TERM_SLOT_COMMENT = "comment";
	private static final String OBO_TERM_SLOT_DEFINITION = "definition";
	private static final String OBO_TERM_SLOT_NAME = "name";
	private static final String OBO_TERM_SLOT_ALTERNATE_IDS = "alternate IDs";
	private static final String OBO_TERM_SLOT_BROAD_SYNONYMS = "broad synonyms";
	private static final String OBO_TERM_SLOT_EXACT_SYNONYMS = "exact synonyms";
	private static final String OBO_TERM_SLOT_NARROW_SYNONYMS = "narrow synonyms";
	private static final String OBO_TERM_SLOT_REFERENCES_OF_KNOWN_TYPE = "references of known type";
	private static final String OBO_TERM_SLOT_REFERENCES_OF_UNKNOWN_TYPE = "references of unknown type";
	private static final String OBO_TERM_SLOT_RELATED_SYNONYMS = "related synonyms";
	private static final String OBO_TERM_SLOT_SYNONYMS = "synonyms";
	private static final String OBO_TERM_SLOT_TERM_SUBSETS = "term subsets";
	public static final String PART_OF_SLOT = "part_of";
	private static final String GO_RELATIONSHIP = "GO relationship";
	static final String DIRECT_DOMAIN_SLOT_NAME = ":DIRECT-DOMAIN";
	static final String ALLOWED_CLASSES_FOR_SLOT = ":SLOT-CONSTRAINTS";
	private static final String STANDARD_SLOT = ":STANDARD-SLOT";

	private static final String HAS_CORRESPONDING_CONCEPTS_SLOT = "has corresponding concepts";
	private static final String HAS_CORRESPONDING_CONCEPT_SLOT = "has corresponding concept";

	private static final String EG_ROOT_REGION_CLS_NAME = "Entrez Gene sequence";
	private static final String GENE_OR_TRANSCRIPT_OR_POLYPEPTIDE_OR_MACROMOLECULAR_COMPLEX_CLS_NAME = "gene or transcript or polypeptide or macromolecular complex";
	private static final String CDNA_CLS_NAME = "cDNA";
	private static final String PROMOTER_CLS_NAME = "promoter";

	private static final String HAS_ENTREZ_GENE_ID_SLOT_NAME = "has Entrez Gene ID";

	private static final String ORGANISM_CLS_NAME = "organism";

	private static final String COMMON_NAME_SLOT_NAME = "common name";

	private static final String TAXON_ABIGUITY_SLOT_NAME = "taxon ambiguity";

	private static final String TAXONOMY_ID_SLOT_NAME = "taxonomy ID";

	private static final String MACROMOLECULAR_COMPLEX_OR_PROTEIN_AMBIGUITY_SLOT = "macromolecular complex or protein ambiguity";

	private static final String TAXONOMIC_RANK_CLS_NAME = "taxonomic_rank";

	private static final String TYPOGRAPHY_CLS_NAME = "typography";

	private static final String SECTION_CLS_NAME = "section";

	private static final String SECTION_NAME_SLOT_NAME = "section name";

	private Project project;
	private final String projectFileName;

	public String getProjectFileName() {
		return projectFileName;
	}

	public ProtegeUtil(File protegeProjectFile) {
		this(protegeProjectFile.getAbsolutePath());
	}

	public ProtegeUtil(String projectFileName) {
		project = openProject(projectFileName);
		this.projectFileName = projectFileName;
	}

	public ProtegeUtil(Project project, String projectFileName) {
		this.project = project;
		this.projectFileName = projectFileName;
	}

	public void reload() {
		this.project = openProject(projectFileName);
	}

	public Project getProject() {
		return project;
	}

	/**
	 * Open an existing Protege project with the input file name.
	 * 
	 * @param projectFileName
	 * @return
	 */
	public static Project openProject(String projectFileName) {
		Collection errors = new ArrayList();
		Project project = new Project(projectFileName, errors);
		if (errors.size() == 0) {
			return project;
		} else {
			System.err.println("Project: " + projectFileName + " not found. Please check the file name and try again.");
			displayErrors(errors);
			return project;
		}
	}

	/*
	 * display errors
	 * 
	 * @param errors
	 */
	private static void displayErrors(Collection errors) {
		Iterator i = errors.iterator();
		while (i.hasNext()) {
			logger.error("Error: " + i.next());
		}
	}

	public void saveAndReload() {
		saveProject(this.project);
		reload();
	}

	public void saveAndClose() {
		saveProject(this.project);
		close();
	}

	/**
	 * Returns the number of Cls's represented in this Protege project
	 * 
	 * @return
	 */
	public int getClsCount() {
		// logger.info("CLASSES: " + getClsNames(project.getKnowledgeBase().getClses()).toString());
		return project.getKnowledgeBase().getClsCount();
	}

	/**
	 * Save an open Protege project. Filename must already have been assigned.
	 * 
	 * @param project
	 */
	public static void saveProject(Project project) {
		Collection errors = new ArrayList();
		project.save(errors);
		displayErrors(errors);

	}

	/**
	 * Save a Protege project using the given file name
	 * 
	 * @param project
	 * @param projectFileName
	 */
	public static void saveProject(Project project, String projectFileName) {
		project.setProjectFilePath(projectFileName);
		saveProject(project);
	}

	/**
	 * Create a new Protege project with the given KnowledgeBaseFactory
	 * 
	 * @param factory
	 * @return
	 */
	public static Project createNewProject(KnowledgeBaseFactory factory) {
		Collection errors = new ArrayList();
		Project project = Project.createNewProject(factory, errors);
		if (errors.size() == 0) {
			return project;
		} else {
			displayErrors(errors);
			return null;
		}
	}

	/**
	 * Create a new Clips Protege project. This is the default Frames-based project type.
	 * 
	 * @return
	 */
	public static Project createNewClipsProject() {
		return createNewProject(new ClipsKnowledgeBaseFactory());
	}

	/**
	 * Creates a new Clips project for the given project file name and returns a Protege_Util for
	 * that project
	 * 
	 * @param projectFileName
	 * @return
	 */
	public static ProtegeUtil createNewProject(String projectFileName) {
		Project newProject = ProtegeUtil.createNewClipsProject();
		String newProjectFileName = projectFileName;
		if (!newProjectFileName.endsWith(".pprj")) {
			newProjectFileName += ".pprj";
		}
		ProtegeUtil.saveProject(newProject, newProjectFileName);
		return new ProtegeUtil(newProject, newProjectFileName);
	}

	/**
	 * Delete .pprj, .pins, .pont files for a give project
	 * 
	 * @param projectFileName
	 */
	public static void deleteProject(String projectFileName) {
		logger.info(String.format("Deleting Protege project: ", projectFileName));
		String projectPrefix = projectFileName.substring(0, projectFileName.lastIndexOf("."));

		File f = new File(projectPrefix + ".pprj");
		if (!f.delete()) {
			logger.error("Unable to delete " + projectPrefix + ".pprj");
		}

		f = new File(projectPrefix + ".pins");
		if (!f.delete()) {
			logger.error("Unable to delete " + projectPrefix + ".pins");
		}

		f = new File(projectPrefix + ".pont");
		if (!f.delete()) {
			logger.error("Unable to delete " + projectPrefix + ".pont");
		}

	}

	// public void saveAs(String directory, String projectFileName) {
	// logger.info("Saving project as: " + projectFileName);
	// project.setProjectFilePath(directory + File.separatorChar + projectFileName);
	// String projectNamePrefix = projectFileName;
	// if (projectFileName.endsWith(".pprj")) {
	// projectNamePrefix = projectFileName.substring(0, projectFileName.indexOf(".pprj"));
	// }
	// ClipsKnowledgeBaseFactory.setSourceFiles(project.getSources(), projectNamePrefix + ".pont",
	// projectNamePrefix
	// + ".pins");
	// saveProject(project);
	// }

	public static void copyProject(Project project, File copiedProjectPprjFile) {
		logger.info(String.format("Copying project to file: %s", copiedProjectPprjFile.getAbsolutePath()));
		project.setProjectFilePath(copiedProjectPprjFile.getAbsolutePath());
		String projectNamePrefix = copiedProjectPprjFile.getName();
		if (projectNamePrefix.endsWith(".pprj")) {
			projectNamePrefix = projectNamePrefix.substring(0, projectNamePrefix.indexOf(".pprj"));
		}
		ClipsKnowledgeBaseFactory.setSourceFiles(project.getSources(), projectNamePrefix + ".pont", projectNamePrefix
				+ ".pins");
		saveProject(project);
	}

	public static void saveProjectAs(Project project, String directory, String projectFileName) {
		System.err.println("Saving project as: " + projectFileName);
		project.setProjectFilePath(directory + File.separatorChar + projectFileName);
		String projectNamePrefix = projectFileName;
		if (projectFileName.endsWith(".pprj")) {
			projectNamePrefix = projectFileName.substring(0, projectFileName.indexOf(".pprj"));
		}
		ClipsKnowledgeBaseFactory.setSourceFiles(project.getSources(), projectNamePrefix + ".pont", projectNamePrefix
				+ ".pins");
		saveProject(project);
	}

	/**
	 * Given a Protege Knowledgebase and the name of a class in the Knowledgebase, return all paths
	 * from that class to the root node. Each path is returned in a list, where each member of the
	 * list is a superclass along the path. Multiple paths may be returned as it is possible for an
	 * ontology term to have multiple parents.
	 * 
	 * @param kb
	 * @param className
	 * @return
	 */
	public List<List<String>> getPathsToRoot(String className) {
		logger.debug("Finding path to root from class: " + className);
		KnowledgeBase kb = project.getKnowledgeBase();
		List<List<String>> pathsToRoot = new ArrayList<List<String>>();
		List<String> pathToRoot = new ArrayList<String>();
		pathToRoot.add(className);
		pathsToRoot.add(pathToRoot);

		getPathToRoot(kb, className, pathsToRoot);

		return pathsToRoot;
	}

	/**
	 * Returns an Iterator over the Cls objects in the current knowledgebase. If rootClses is null,
	 * then all classes are returned. If rootClses is filled, then those classes and any subclasses
	 * are returned.
	 * 
	 * @param rootClses
	 * @return
	 */
	public Iterator<Cls> getClasses(Set<String> rootClses) {
		KnowledgeBase kb = project.getKnowledgeBase();
		Collection<Cls> classes = null;
		if (rootClses == null) {
			int clsCount = kb.getClsCount();
			logger.debug("Returning " + clsCount + " classes from protege project: " + projectFileName + " -- "
					+ getClsNames(kb.getClses()).toString());
			classes = kb.getClses();
		} else {
			classes = new ArrayList<Cls>();
			Set<String> classNames = new HashSet<String>();
			for (String rootClsName : rootClses) {
				Cls rootCls = kb.getCls(rootClsName);
				if (rootCls != null) {
					Collection<Cls> subClasses = rootCls.getSubclasses();
					classNames.addAll(getClsNames(subClasses));
					classNames.add(rootCls.getName());
				} else {
					logger.warn("Detected null root class: " + rootClsName);
				}
			}
			for (String clsName : classNames) {
				classes.add(kb.getCls(clsName));
			}
		}
		return Collections.list(Collections.enumeration(classes)).iterator();
	}

	public Iterator<Cls> getMetaClasses() {
		KnowledgeBase kb = project.getKnowledgeBase();
		int clsCount = kb.getClsCount();
		logger.debug("Returning " + clsCount + " classes from protege project: " + projectFileName);
		Collection<Cls> classes = kb.getClses();
		Collection<Cls> metaClasses = new ArrayList<Cls>();
		for (Cls cls : classes) {
			if (cls.isMetaCls()) {
				metaClasses.add(cls);
			}
		}
		return Collections.list(Collections.enumeration(metaClasses)).iterator();
	}

	// private static boolean isKnowtatorCls(KnowledgeBase kb, Cls cls) {
	// if (cls ==null) {
	// return false;
	// }
	// if (cls.getName().equals(KNOWTATOR_SUPPORT_CLASS)) {
	// return true;
	// }
	// Collection<Cls> superclasses = cls.getDirectSuperclasses();
	// Collection<String> superClassNames= getClsNames(superclasses);
	// return superClassNames.contains(KNOWTATOR_SUPPORT_CLASS);
	// }

	/**
	 * Copies all classes from one Protege project to another
	 * 
	 * @param fromProjectName
	 * @param toProjectName
	 */
	public static void copyClasses(String fromProjectName, String toProjectName) {
		ProtegeUtil fromPU = new ProtegeUtil(fromProjectName);
		ProtegeUtil toPU = new ProtegeUtil(toProjectName);

		copyClses(fromPU, toPU, fromPU.getMetaClasses(), null);
		copyMetaSlots(fromPU.getKnowledgeBase(), toPU.getKnowledgeBase());
		copyClses(fromPU, toPU, fromPU.getClasses(null), null);

		toPU.saveAndReload();
		toPU = new ProtegeUtil(toProjectName);

		fillOboTermSlotsForCopiedClasses(fromPU, toPU, null);
		removeKnowtatorClasses(toPU);
		// createPartOfSlot(fromPU, toPU);

		toPU.saveAndClose();
	}

	// private static void createPartOfSlot(Protege_Util fromPU, Protege_Util toPU) {
	// Slot partOfSlot = null;
	// if (!slotAlreadyExists(toPU.getKnowledgeBase(), PART_OF_SLOT)) {
	// Cls goRelationshipCls = toPU.getKnowledgeBase().getCls(GO_RELATIONSHIP);
	// partOfSlot = toPU.getKnowledgeBase().createSlot(PART_OF_SLOT, goRelationshipCls);
	// partOfSlot.setAllowsMultipleValues(true);
	// partOfSlot.setValueType(ValueType.INSTANCE);
	// } else {
	// partOfSlot = toPU.getKnowledgeBase().getSlot(PART_OF_SLOT);
	// }
	//
	// Slot fromPartOfSlot = fromPU.getKnowledgeBase().getSlot(PART_OF_SLOT);
	// Collection<Cls> allowedClasses = fromPartOfSlot.getAllowedClses();
	// Collection<Cls> domains = fromPartOfSlot.getDomain();
	//
	// Collection<Cls> allowedClassesInToKB = partOfSlot.getAllowedClses();
	// for (Cls allowedClass : allowedClasses) {
	// Cls allowedClassInToKB = toPU.getKnowledgeBase().getCls(allowedClass.getName());
	// allowedClassesInToKB.add(allowedClassInToKB);
	// }
	// partOfSlot.setAllowedClses(allowedClassesInToKB);
	//
	// Collection<Cls> domainsInToKB = partOfSlot.getDomain();
	// for (Cls domain : domains) {
	// Cls domainInToKB = toPU.getKnowledgeBase().getCls(domain.getName());
	// domainsInToKB.add(domainInToKB);
	// }
	// partOfSlot.setOwnSlotValues(toPU.getKnowledgeBase().getSlot(DIRECT_DOMAIN_SLOT_NAME),
	// domainsInToKB);
	//
	// }

	/**
	 * Removes the "knowtator support class" and all subclasses from the knowledgebase if it exists
	 * 
	 * @param toPU
	 */
	private static void removeKnowtatorClasses(ProtegeUtil toPU) {
		if (hasKnowtatorSupportCls(toPU.getKnowledgeBase())) {
			toPU.getKnowledgeBase().deleteCls(toPU.getKnowledgeBase().getCls(KNOWTATOR_SUPPORT_CLASS));
		}
	}

	/**
	 * Returns true if there is an OBO term class in the input knowledgebase
	 * 
	 * @param kb
	 * @return
	 */
	private static boolean hasOboTermCls(KnowledgeBase kb) {
		Cls oboTermMetaCls = kb.getCls(OBO_TERM_META_CLS);
		return oboTermMetaCls != null;
	}

	/**
	 * Returns true if there is a Knowtator support class in the input knowledgebase
	 * 
	 * @param kb
	 * @return
	 */
	private static boolean hasKnowtatorSupportCls(KnowledgeBase kb) {
		Cls knowtatorSupportCls = kb.getCls(KNOWTATOR_SUPPORT_CLASS);
		return knowtatorSupportCls != null;
	}

	private static void fillOboTermSlotsForCopiedClasses(ProtegeUtil fromPU, ProtegeUtil toPU, Set<String> rootClsNames) {
		if (hasOboTermCls(fromPU.getKnowledgeBase())) {
			Iterator<Cls> fromClassIter = fromPU.getClasses(rootClsNames);
			int count = 0;
			while (fromClassIter.hasNext()) {
				Cls fromCls = fromClassIter.next();
				if (!fromCls.isMetaCls()) {
					if (count++ % 1000 == 0) {
						logger.debug("Slot filling Progress: " + count);
					}
					Cls toCls = toPU.getKnowledgeBase().getCls(fromCls.getName());
					fillSingleAttributeSlots(fromCls, toCls, fromPU.getKnowledgeBase(), toPU.getKnowledgeBase());
					fillMultipleAttributeSlots(fromCls, toCls, fromPU.getKnowledgeBase(), toPU.getKnowledgeBase());
				}
			}
		}

	}

	private static void fillSingleAttributeSlots(Cls fromCls, Cls toCls, KnowledgeBase fromKB, KnowledgeBase toKB) {
		Object comment = fromCls.getOwnSlotValue(fromKB.getSlot(OBO_TERM_SLOT_COMMENT));
		if (comment != null) {
			toCls.setOwnSlotValue(toKB.getSlot(OBO_TERM_SLOT_COMMENT), comment);
		}
		Object definition = fromCls.getOwnSlotValue(fromKB.getSlot(OBO_TERM_SLOT_DEFINITION));
		if (definition != null) {
			toCls.setOwnSlotValue(toKB.getSlot(OBO_TERM_SLOT_DEFINITION), definition);
		}
		Object name = fromCls.getOwnSlotValue(fromKB.getSlot(OBO_TERM_SLOT_NAME));
		if (name != null) {
			toCls.setOwnSlotValue(toKB.getSlot(OBO_TERM_SLOT_NAME), name);
		}
	}

	private static void fillMultipleAttributeSlots(Cls fromCls, Cls toCls, KnowledgeBase fromKB, KnowledgeBase toKB) {
		String[] slotNames = new String[] { OBO_TERM_SLOT_ALTERNATE_IDS, OBO_TERM_SLOT_BROAD_SYNONYMS,
				OBO_TERM_SLOT_EXACT_SYNONYMS, OBO_TERM_SLOT_NARROW_SYNONYMS, OBO_TERM_SLOT_REFERENCES_OF_KNOWN_TYPE,
				OBO_TERM_SLOT_REFERENCES_OF_UNKNOWN_TYPE, OBO_TERM_SLOT_RELATED_SYNONYMS, OBO_TERM_SLOT_SYNONYMS,
				OBO_TERM_SLOT_TERM_SUBSETS };
		for (String slotName : slotNames) {
			Collection values = fromCls.getOwnSlotValues(fromKB.getSlot(slotName));
			if (values != null) {
				toCls.setOwnSlotValues(toKB.getSlot(slotName), values);
			}
		}

	}

	/**
	 * Copies the metaclasses from one protege project to another
	 * 
	 * @param fromPU
	 * @param toPU
	 */
	private static void copyClses(ProtegeUtil fromPU, ProtegeUtil toPU, Iterator<Cls> classesToCopyIter,
			Set<String> rootClsNames) {
		// Iterator<Cls> clsesToInsertIter = fromPU.getMetaClasses();
		int count = 0;
		while (classesToCopyIter.hasNext()) {
			if (count++ % 1000 == 0) {
				logger.info("Copy Progress: " + count);
			}
			Cls clsToInsert = classesToCopyIter.next();
			// if (rootClassIDs == null || setContainsSuperclass(rootClassIDs, clsToInsert,
			// fromPU.getKnowledgeBase())) {
			// logger.info(String.format("About to copy cls: %s",clsToInsert.getName()));
			toPU.copyClassFromKB(fromPU.getKnowledgeBase(), clsToInsert.getName(), rootClsNames);
			// }
		}

	}

	// private static void copyInstancesOfMetaClses(Protege_Util fromPU, Protege_Util toPU) {
	// Iterator<Cls> clsesToInsertIter = fromPU.getMetaClasses();
	// int count = 0;
	// while (clsesToInsertIter.hasNext()) {
	//
	// Cls clsToInsert = clsesToInsertIter.next();
	// Collection<Instance> instancesToInsert = clsToInsert.getInstances();
	// Iterator<Instance> instanceToInsertIter = instancesToInsert.iterator();
	// while (instanceToInsertIter.hasNext()) {
	// if (count++ % 1000 == 0) {
	// logger.info("Copy Progress: " + count);
	// }
	// Instance instance = instanceToInsertIter.next();
	// toPU.copyInstanceFromKB(fromPU.getKnowledgeBase(), instance);
	// }
	// }
	// }
	//

	/**
	 * Returns true if the input Cls object is an descendant of any of the root class IDs.
	 */
	private static boolean setContainsSuperclass(Set<String> rootClassIDs, Cls cls, KnowledgeBase kb) {
		for (String rootClsName : rootClassIDs) {
			Cls rootCls = kb.getCls(rootClsName);
			if (rootCls != null) {
				if (cls.getSuperclasses().contains(rootCls)) {
					return true;
				}
			}
		}
		return false;
	}

	private static void copyMetaSlots(KnowledgeBase fromKB, KnowledgeBase toKB) {
		Cls oldOboTermMetaCls = fromKB.getCls(OBO_TERM_META_CLS);
		Cls oboTermMetaCls = toKB.getCls(OBO_TERM_META_CLS);
		Cls metaClassLevelOboRelationshipCls = toKB.getCls(METACLS_LEVEL_OBO_RELATIONSHIP_CLS_NAME);

		if (oboTermMetaCls != null) {
			Collection<Slot> templateSlots = oldOboTermMetaCls.getTemplateSlots();
			for (Slot templateSlot : templateSlots) {
				if (!slotAlreadyExists(toKB, templateSlot.getName())) {
					logger.info(String.format("Copying metaclass-level OBO slot: %s", templateSlot.getName()));
					Slot newTemplateSlot = toKB.createSlot(templateSlot.getName(), metaClassLevelOboRelationshipCls);
					// logger.error("TEMPLATE SLOT NAME: " + templateSlot.getName());
					// logger.error("TEMPLATE SLOT VALUE TYPE: " + templateSlot.getValueType());
					// logger.error("TEMPLATE SLOT ALLOWS MULTIPLE VALUES: " +
					// templateSlot.getAllowsMultipleValues());
					newTemplateSlot.setValueType(templateSlot.getValueType());
					newTemplateSlot.setAllowsMultipleValues(templateSlot.getAllowsMultipleValues());
					oboTermMetaCls.addDirectTemplateSlot(newTemplateSlot);
				}
			}
		}
	}

	private static boolean slotAlreadyExists(KnowledgeBase kb, String slotName) {
		Slot slot = kb.getSlot(slotName);
		return slot != null;
	}

	private void copySlots(ProtegeUtil fromPU, ProtegeUtil toPU, String superSlotClsName,
			String mandatedToSlotSuperClass) {
		copySlots(fromPU.getKnowledgeBase(), toPU.getKnowledgeBase(), superSlotClsName, mandatedToSlotSuperClass);
	}

	private void copySlots(KnowledgeBase fromKB, KnowledgeBase toKB, String superSlotClsName,
			String mandatedToSlotSuperClass) {
		Cls superSlotCls = fromKB.getCls(superSlotClsName);
		if (superSlotCls != null) {
			for (Instance slotInstance : superSlotCls.getInstances()) {
				if (slotInstance instanceof Slot) {
					Slot slotToCopy = (Slot) slotInstance;
					// logger.info("Checking to transfer slot: " + slotToCopy);
					// if (!slotToCopy.isSystem()) {
					// logger.info("Slot is not a system slot: " + slotToCopy.getName());
					// Collection<Cls> allowedClses = slotToCopy.getAllowedClses();
					// logger.info("allowed classes size: " + allowedClses.size() +
					// " acceptable class names: " + classNamesInToKB.toString());
					// if (allowedClses.size() > 0) {
					// if
					// (classNamesInToKB.contains(Collections.list(Collections.enumeration(allowedClses)).get(0)))
					// {
					// String slotName = slotToCopy.getName();
					// if (!slotAlreadyExists(toKB, slotName)) {
					copySlot(slotToCopy, fromKB, toKB, mandatedToSlotSuperClass);
					// }
					// }
					// }
				}
			}
		} else {
			logger.warn(String.format("Failed in attempt to import slot instances of null super cls: %s",
					superSlotClsName));
		}
	}

	/**
	 * Create a copy of the fromSlot in the toKB. Copies meta data as well.
	 * 
	 * @param fromSlot
	 * @param toKB
	 * @param mandatedToSlotSuperClass
	 *            - all slots in the copied project will be moved directly under the mandated super
	 *            class. This is a fix to avoid having a single slot be instances of multiple meta
	 *            classes, e.g. part_of would need to be an instance of GO Relationship, CHEBI
	 *            relationship, etc.
	 */
	private void copySlot(Slot fromSlot, KnowledgeBase fromKB, KnowledgeBase toKB, String mandatedToSlotSuperClass) {
		Slot toSlot = null;
		if (!slotAlreadyExists(toKB, fromSlot.getName())) {
			toSlot = toKB.createSlot(fromSlot.getName(), toKB.getCls(mandatedToSlotSuperClass));
			toSlot.setAllowsMultipleValues(fromSlot.getAllowsMultipleValues());
			toSlot.setMaximumCardinality(fromSlot.getMaximumCardinality());
			toSlot.setMaximumValue(fromSlot.getMaximumValue());
			toSlot.setMinimumCardinality(fromSlot.getMinimumCardinality());
			toSlot.setMinimumValue(fromSlot.getMinimumValue());
			toSlot.setValueType(fromSlot.getValueType());
		} else {
			toSlot = toKB.getSlot(fromSlot.getName());
		}

		Collection<Cls> allowedClses = fromSlot.getAllowedClses(); // fromSlot.getDirectOwnSlotValues(fromKB.getSlot(ALLOWED_CLASSES_FOR_SLOT));
		Collection<Cls> domain = fromSlot.getDirectOwnSlotValues(fromKB.getSlot(DIRECT_DOMAIN_SLOT_NAME));// getDomain();
		logger.debug("Creating slot: " + toSlot.getName() + " with allowed clses: "
				+ getClsNames(allowedClses).toString() + " and domain: " + getClsNames(domain).toString());

		Collection<Cls> domainsForToSlot = new ArrayList<Cls>(toSlot.getDirectOwnSlotValues(toKB
				.getSlot(DIRECT_DOMAIN_SLOT_NAME)));
		domainsForToSlot.addAll(getClses(toKB, getClsNames(domain)));
		toSlot.setDirectOwnSlotValues(toKB.getSlot(DIRECT_DOMAIN_SLOT_NAME), domainsForToSlot);

		Collection<Cls> allowedClsesForToSlot = new ArrayList<Cls>(toSlot.getDirectOwnSlotValues(toKB
				.getSlot(ALLOWED_CLASSES_FOR_SLOT)));
		allowedClsesForToSlot.addAll(getClses(toKB, getClsNames(allowedClses)));
		toSlot.setDirectOwnSlotValues(toKB.getSlot(ALLOWED_CLASSES_FOR_SLOT), allowedClsesForToSlot);

		System.err.println("BAM: " + toSlot.toString() + " -- domain: "
				+ getClsNames(toSlot.getDirectOwnSlotValues(toKB.getSlot(DIRECT_DOMAIN_SLOT_NAME))).toString()
				+ " -- allowed classes: "
				+ getClsNames(toSlot.getDirectOwnSlotValues(toKB.getSlot(ALLOWED_CLASSES_FOR_SLOT))).toString());
	}

	private Collection<Cls> getClses(KnowledgeBase kb, Set<String> clsNames) {
		logger.debug("Classes in kb: " + getClsNames(kb.getClses()).toString());
		Collection<Cls> clses = new ArrayList<Cls>();
		for (String clsName : clsNames) {
			logger.debug("Looking for class: " + clsName);
			if (hasCls(kb, clsName)) {
				logger.debug("Has class: " + clsName);
				clses.add(kb.getCls(clsName));
			}
		}
		return clses;
	}

	private boolean hasCls(KnowledgeBase kb, String clsName) {
		return (kb.getCls(clsName) != null);
	}

	public KnowledgeBase getKnowledgeBase() {
		return project.getKnowledgeBase();
	}

	// private static void copyClses(Protege_Util fromPU, Protege_Util toPU) {
	// Iterator<Cls> clsesToInsertIter = fromPU.getClasses();
	// int count = 0;
	// while (clsesToInsertIter.hasNext()) {
	// if (count++ % 1000 == 0) {
	// logger.info("Copy Progress: " + count);
	// }
	// Cls clsToInsert = clsesToInsertIter.next();
	// Set<String> alreadyCreatedClasses = new HashSet<String>();
	//
	// toPU.createOntologyClass(clsToInsert.getName(), fromPU.getKnowledgeBase(), pathsToRoot,
	// superClsNames, alreadyCreatedClasses);
	// }
	// }
	/**
	 * Creates
	 * 
	 * @param clsNameToCreate
	 * @param superClassName
	 * @param alreadyCreatedClasses
	 */
	public void createOntologyClass(String clsNameToCreate, KnowledgeBase oldKB, List<List<String>> pathsToRoot,
			Collection<String> superClsNames, Set<String> alreadyCreatedClasses) {

		KnowledgeBase kb = project.getKnowledgeBase();
		for (List<String> pathToRoot : pathsToRoot) {
			// logger.error("PATH TO ROOT: " + pathToRoot.toString());
			for (String clsNameToInsert : pathToRoot) {
				// logger.error("INSERTING: " + clsNameToInsert);
				// for (int i = pathToRoot.size()-1; i > -1; i--) {
				// String clsNameToInsert = pathToRoot.get(i);
				if (alreadyCreatedClasses.contains(clsNameToInsert)) {
					continue;
				}
				if (clsNameToInsert == null) {
					logger.warn("Encountered null class name");
					continue;
				}
				// if (clsNameToInsert.startsWith("knowtator")) {
				// logger.info("Skipping copy of knowtator class: " + clsNameToInsert);
				// continue;
				// }
				if (clsAlreadyExists(clsNameToInsert)) {
					if (!alreadyCreatedClasses.contains(clsNameToInsert)) {
						if (clsNameToInsert.charAt(0) != ':') {
							logger.error("Conflict encountered when creating ontology class (" + clsNameToInsert
									+ "). Class already exists in project.");
						}
					}
				} else {
					Cls oldCls = oldKB.getCls(clsNameToInsert);
					Cls metaCls = oldCls.getDirectType();
					// logger.error("Direct type: " + metaCls.getName());
					if (superClsNames == null || superClsNames.size() == 0) {
						kb.createCls(clsNameToInsert, kb.getRootClses(), metaCls);
					} else {
						List<Cls> superClsList = new ArrayList<Cls>();
						boolean foundNullSuperCls = false;
						for (String superClsName : superClsNames) {
							Cls superCls = kb.getCls(superClsName);
							if (superCls == null) {
								logger.error("Found null superclass: " + superClsName + " for class: "
										+ clsNameToInsert);
								foundNullSuperCls = true;
							}
							superClsList.add(superCls);
						}
						if (foundNullSuperCls) {
							kb.createCls(clsNameToInsert, kb.getRootClses(), metaCls);
						} else {
							kb.createCls(clsNameToInsert, superClsList, metaCls);
						}
					}
					alreadyCreatedClasses.add(clsNameToInsert);
				}
			}
		}

	}

	public Slot createSlotForClass(String slotName, String className) {
		KnowledgeBase kb = project.getKnowledgeBase();
		Cls clsToAssignSlot = kb.getCls(className);
		Slot slot = kb.createSlot(slotName, null);
		clsToAssignSlot.addDirectTemplateSlot(slot);
		return slot;
	}

	public void assignSlotToClass(Slot slot, Cls cls) {
		cls.addDirectTemplateSlot(slot);
	}

	public void removeSlotFromClass(String slotName, String className) {
		KnowledgeBase kb = project.getKnowledgeBase();
		Cls clsToAssignSlot = kb.getCls(className);
		Slot slot = kb.getSlot(slotName);
		clsToAssignSlot.removeDirectTemplateSlot(slot);
	}

	public void removeSlot(String slotName) {
		KnowledgeBase kb = project.getKnowledgeBase();
		kb.deleteSlot(kb.getSlot(slotName));
	}

	public Collection<Slot> getSlotsForCls(String className) {
		KnowledgeBase kb = project.getKnowledgeBase();
		Cls cls = kb.getCls(className);
		Instance instance = kb.createInstance(null, cls);
		Collection<Slot> slots = instance.getOwnSlots();
		return slots;
	}

	public List<String> getAllSubClasses(String parentClassName) {

		List<String> subClassNames = new ArrayList<String>();

		Cls cls = project.getKnowledgeBase().getCls(parentClassName);
		Collection<Cls> subClasses = cls.getSubclasses();

		for (Cls subClass : subClasses) {
			subClassNames.add(subClass.getName());
		}

		return subClassNames;
	}

	/**
	 * Ignores slots that return true for isSystem()
	 * 
	 * @param className
	 * @return
	 */
	public Collection<Slot> getUserSlotsForCls(String className) {
		Collection<Slot> slots = getSlotsForCls(className);
		Collection<Slot> userSlots = new ArrayList<Slot>();
		for (Slot slot : slots) {
			if (!slot.isSystem()) {
				userSlots.add(slot);
			}
		}
		return userSlots;
	}

	// /**
	// * Returns a list of Cls objects
	// *
	// * @param superClsNames
	// * @param kb
	// * @return
	// */
	// private List<Cls> createClsList(List<String> clsNames, KnowledgeBase kb) {
	// List<Cls> clsList = new ArrayList<Cls>();
	// for (String clsName : clsNames) {
	// clsList.add(kb.getCls(clsName));
	// }
	// return clsList;
	// }

	/**
	 * Returns a collection of Cls names given the input collection of Cls objects
	 * 
	 * @param clses
	 * @return
	 */
	public static Set<String> getClsNames(Collection<Cls> clses) {
		Set<String> clsNames = new HashSet<String>();
		for (Cls cls : clses) {
			clsNames.add(cls.getName());
		}
		return clsNames;
	}

	/**
	 * Returns true if the class is already in the KB
	 * 
	 * @param clsName
	 * @return
	 */
	boolean clsAlreadyExists(String clsName) {
		// if (clsName.charAt(0) == ':') {
		// clsName = clsName.replace(":", "");
		// }
		Cls cls = project.getKnowledgeBase().getCls(clsName);
		logger.debug("Checking for class existence: " + clsName + "  Returning: " + (cls != null));
		return cls != null;
	}

	public void createOntologyClass(String classToCreate, String superClass) {
		KnowledgeBase kb = project.getKnowledgeBase();
		if (superClass == null) {
			kb.createCls(classToCreate, kb.getRootClses());
		} else {
			Cls protein = kb.getCls(superClass);
			List<Cls> clsList = new ArrayList<Cls>();
			clsList.add(protein);
			kb.createCls(classToCreate, clsList);
		}
	}

	/**
	 * This function is used recursively to get the paths to root for a particular ontology term
	 * 
	 * @param kb
	 * @param className
	 * @param waitingLists
	 */
	private void getPathToRoot(KnowledgeBase kb, String className, List<List<String>> waitingLists) {
		logger.debug("Getting paths to root for cls: " + className);
		Cls cls = kb.getCls(className);
		if (cls != null) {
			Collection<Cls> superClasses = cls.getDirectSuperclasses();
			List<Cls> superClassList = Collections.list(Collections.enumeration(superClasses));
			String superClassNames = "";
			for (Cls superClass : superClassList) {
				superClassNames += (" " + superClass.getName());
			}
			logger.debug("classname: " + className + " -- # superclasses: " + superClasses.size() + " -- "
					+ superClassNames);

			/*
			 * for each superclass (if there are 2 or more), we need to take any lists with
			 * className on top and clone them
			 */

			/* simple case, there is only one superclass */
			if (superClassList.size() == 1) {
				Cls superClass = superClassList.get(0);
				if (superClass.isRoot()) {
					/* We've reached the root class so we are done */
				} else {
					/* This is the only superclass, so add it to any list with className on the top */
					for (List<String> waitingList : waitingLists) {
						if (waitingList.get(0).equalsIgnoreCase(className)) {
							waitingList.add(0, superClass.getName());
						}
					}
					/* Now get the path to root from the new term */
					getPathToRoot(kb, superClass.getName(), waitingLists);
				}
			} else {
				/* There are more than one superclass for this term */

				/*
				 * Get the lists that have className at the top -- these are the lists we will need
				 * to clone
				 */
				List<List<String>> listsToClone = new ArrayList<List<String>>();
				for (List<String> waitingList : waitingLists) {
					if (waitingList.get(0).equalsIgnoreCase(className)) {
						listsToClone.add(waitingList);
					}
				}

				/*
				 * for all by the first superclass, clone the listsToClone, add the superclass name
				 * to the top, and add the lists to the waiting lists
				 */
				for (int i = 1; i < superClassList.size(); i++) {
					String superClassName = superClassList.get(i).getName();
					for (List<String> listToClone : listsToClone) {
						List<String> clonedList = new ArrayList<String>(listToClone);
						clonedList.add(0, superClassName);
						waitingLists.add(clonedList);
					}
					/* Follow the paths... */
					getPathToRoot(kb, superClassName, waitingLists);
				}

				/* Now do for the first superclass name just like we did for a single superclass */
				if (superClassList.size() > 0) {
					Cls superClass = superClassList.get(0);
					for (List<String> waitingList : waitingLists) {
						if (waitingList.get(0).equalsIgnoreCase(className)) {
							waitingList.add(0, superClass.getName());
						}
					}
					/* Now get the path to root from the new term */
					getPathToRoot(kb, superClass.getName(), waitingLists);
				}
			}

		} else {
			// the cls is null, therefore it is not found in the ontology, so return null
			waitingLists = null;
		}
	}

	/**
	 * Copies the input class (indicated by the class name) from the input KB to this KB. If root
	 * class names are specified, then only classes up to and including the root classes are
	 * included. If root classes is null, then all classes up to the root of the ontology are used.
	 * 
	 * @param fromKB
	 * @param clsName
	 * @param rootClsNames
	 */
	public void copyClassFromKB(KnowledgeBase fromKB, String clsName, Set<String> rootClsNames) {
		// if (clsAlreadyExists(clsName)) {
		// return;
		// }
		/* get parent Cls objects, if they don't exist in this KB then create them */
		Set<String> parentClsNames = getParentClsNames(fromKB, fromKB.getCls(clsName));
		for (String parentClsName : parentClsNames) {
			if (!clsAlreadyExists(parentClsName)) {
				copyClassFromKB(fromKB, parentClsName, rootClsNames);
			}
			if (rootClsNames != null && rootClsNames.contains(parentClsName)) {
				break;
			}
		}

		/* create class */
		KnowledgeBase kb = project.getKnowledgeBase();

		Collection<Cls> parentClses = new ArrayList<Cls>();
		for (String parentClsName : parentClsNames) {
			parentClses.add(kb.getCls(parentClsName));
		}

		// logger.info(String.format("Copying class: %s (already exists=%s)",clsName,Boolean.toString(kb.getCls(clsName)!=null)));
		if (kb.getCls(clsName) == null) {
			Cls oldCls = fromKB.getCls(clsName);
			Cls metaCls = oldCls.getDirectType();
			// logger.error("Direct type: " + metaCls.getName());
			if (parentClses.size() == 0) {
				kb.createCls(clsName, kb.getRootClses(), metaCls);
			} else {
				kb.createCls(clsName, parentClses, metaCls);
			}
		} else {
			// logger.info(String.format("Cls already exists: %s",clsName));
			/* The class already exists - so update the parent clses instead */
			Cls cls = kb.getCls(clsName);
			Collection<Cls> superClasses = new ArrayList<Cls>(cls.getDirectSuperclasses());
			Set<String> superClsNames = getClsNames(superClasses);
			for (Cls parentCls : parentClses) {
				if (!superClsNames.contains(parentCls.getName())) {
					// logger.info(String.format("Adding superclass: %s",parentCls.getName()));
					kb.addDirectSuperclass(cls, parentCls);
				}
			}

		}
	}

	/**
	 * Returns the Set<String> containing the names of the parent Cls objects for the input child
	 * Cls object
	 * 
	 * @param fromKB
	 * @param childCls
	 * @return
	 */
	private Set<String> getParentClsNames(KnowledgeBase fromKB, Cls childCls) {
		if (childCls.isRoot()) {
			return new HashSet<String>();
		}
		Collection<Cls> parentClses = childCls.getDirectSuperclasses();
		return getClsNames(parentClses);
	}

	public static void main(String[] args) {

		// // String TEST_PROTEGE_PROJECT_FILE =
		// // "data/test/edu.uchsc.ccp.util.nlp.tool.external.protege/testProject.pprj";
		// //
		// //
		// // Protege_Util protegeUtil = new Protege_Util(TEST_PROTEGE_PROJECT_FILE);
		// // // List<List<String>> pathsToRoot = protegeUtil.getPathsToRoot("class-3-0-0");
		// // List<List<String>> pathsToRoot =
		// // protegeUtil.getPathsToRoot("class-3-0-0-0-0-MI-subclass");
		// //
		// // for (List<String> pathToRoot : pathsToRoot) {
		// // System.err.println("PATH TO ROOT: " + Arrays.toString(pathToRoot.toArray(new String[]
		// // {})));
		// // }
		//
		// String TEST_PROTEGE_PROJECT_FILE =
		// "data/test/edu.uchsc.ccp.util.nlp.tool.external.protege/testProject2.pprj";
		//
		// Protege_Util protegeUtil = new Protege_Util(TEST_PROTEGE_PROJECT_FILE);
		// System.err.println("Creating classes...");
		// protegeUtil.createOntologyClass("P01234", "protein");
		// protegeUtil.createOntologyClass("P01244", "protein");
		// System.err.println("Saving...");
		// protegeUtil.save();
		BasicConfigurator.configure();

		String goProjectNAme = "/Users/bill/Documents/grants/CEGS-2009/knowtator-figure-for-mike/knowtator-projects/GO_BP+MF/GO_BP+MF.pprj";
		String newProjectName = "/Users/bill/Documents/grants/CEGS-2009/knowtator-figure-for-mike/knowtator-projects/tmp/tmp.pprj";

		(new File("/Users/bill/Documents/grants/CEGS-2009/knowtator-figure-for-mike/knowtator-projects/tmp/tmp.pprj"))
				.delete();
		(new File("/Users/bill/Documents/grants/CEGS-2009/knowtator-figure-for-mike/knowtator-projects/tmp/tmp.pont"))
				.delete();
		(new File("/Users/bill/Documents/grants/CEGS-2009/knowtator-figure-for-mike/knowtator-projects/tmp/tmp.pins"))
				.delete();

		Project newProject = ProtegeUtil.createNewClipsProject();
		ProtegeUtil.saveProject(newProject, newProjectName);

		ProtegeUtil.copyClasses(goProjectNAme, newProjectName);
	}

	public void mergeWithOntology(File protegeProjectFile) {
		mergeWithOntology(protegeProjectFile, (String[]) null);
	}

	public void mergeWithOntology(File protegeProjectFile, String... rootClassIDs) {
		Set<String> rootClsNames = null;
		// Map<String, Map<String, Set<String>>> termID2RelationType2TermIDMap =
		// computeRelationsMap(this.getKnowledgeBase(), rootClsNames);

		ProtegeUtil fromPU = new ProtegeUtil(protegeProjectFile.getAbsolutePath());
		// Protege_Util toPU = new Protege_Util(toProjectName);

		if (protegeProjectFile.getName().contains("typography")) {
			rootClsNames = new HashSet<String>();
			rootClsNames.add(TYPOGRAPHY_CLS_NAME);
			rootClsNames.add(SECTION_CLS_NAME);
			
			copyClses(fromPU, this, fromPU.getClasses(rootClsNames), null);
			copySlot(fromPU.getKnowledgeBase().getSlot(SECTION_NAME_SLOT_NAME), fromPU.getKnowledgeBase(),
					this.getKnowledgeBase(), STANDARD_SLOT);
		} else if (protegeProjectFile.getName().contains("EG")) {
			/*
			 * for the gene-or-gene product project and taxonomy id project, there are no meta
			 * classes/slots
			 */
//			rootClsNames = new HashSet<String>();
//			rootClsNames.add(GENE_OR_TRANSCRIPT_OR_POLYPEPTIDE_OR_MACROMOLECULAR_COMPLEX_CLS_NAME);
//			copyClses(fromPU, this, fromPU.getClasses(rootClsNames), null);
//			rootClsNames = new HashSet<String>();
//			rootClsNames.add(CDNA_CLS_NAME);
//			rootClsNames.add(PROMOTER_CLS_NAME);
			
			rootClsNames = new HashSet<String>();
			rootClsNames.add(EG_ROOT_REGION_CLS_NAME);
			copyClses(fromPU, this, fromPU.getClasses(rootClsNames), null);
			copySlot(fromPU.getKnowledgeBase().getSlot(HAS_ENTREZ_GENE_ID_SLOT_NAME), fromPU.getKnowledgeBase(),
					this.getKnowledgeBase(), STANDARD_SLOT);

		} else if (protegeProjectFile.getName().contains("organism")) {
			rootClsNames = new HashSet<String>();
			rootClsNames.add(ORGANISM_CLS_NAME);
			rootClsNames.add(TAXONOMIC_RANK_CLS_NAME);
			
			copyClses(fromPU, this, fromPU.getClasses(rootClsNames), null);
			copySlot(fromPU.getKnowledgeBase().getSlot(COMMON_NAME_SLOT_NAME), fromPU.getKnowledgeBase(),
					this.getKnowledgeBase(), STANDARD_SLOT);
			copySlot(fromPU.getKnowledgeBase().getSlot(TAXON_ABIGUITY_SLOT_NAME), fromPU.getKnowledgeBase(),
					this.getKnowledgeBase(), STANDARD_SLOT);
			copySlot(fromPU.getKnowledgeBase().getSlot(TAXONOMY_ID_SLOT_NAME), fromPU.getKnowledgeBase(),
					this.getKnowledgeBase(), STANDARD_SLOT);
			copySlot(fromPU.getKnowledgeBase().getSlot(HAS_CORRESPONDING_CONCEPTS_SLOT), fromPU.getKnowledgeBase(),
					this.getKnowledgeBase(), STANDARD_SLOT);
		} else {
			if (rootClassIDs != null) {
				rootClsNames = new HashSet<String>(Arrays.asList(rootClassIDs));
			}
			logger.info(String.format("Importing meta classes from: %s", protegeProjectFile.getName()));
			copyClses(fromPU, this, fromPU.getMetaClasses(), null);
			logger.info(String.format("Importing slots for meta classes from: %s", protegeProjectFile.getName()));
			copyMetaSlots(fromPU.getKnowledgeBase(), this.getKnowledgeBase());
			logger.info(String.format("Importing instances of meta classes from: %s", protegeProjectFile.getName()));
			copyClses(fromPU, this, fromPU.getClasses(rootClsNames), rootClsNames);

			// save();
			/* copy class-level obo relationships */
			logger.info(String.format("Importing class-level obo relationships from: %s", protegeProjectFile.getName()));
			copySlots(fromPU, this, CLS_LEVEL_OBO_RELATIONSHIP_CLS_NAME, CLS_LEVEL_OBO_RELATIONSHIP_CLS_NAME);
			/*
			 * This is a hack - here we copy the "has corresponding concepts" slot - this avoid
			 * having to filter out all of the knowtator slots and other meta slots that don't need
			 * to be copied
			 */

			if (fromPU.getKnowledgeBase().getSlot(HAS_CORRESPONDING_CONCEPTS_SLOT) != null) {
				logger.info(String.format("Importing has-corresponding-concepts slot from: %s",
						protegeProjectFile.getName()));
				copySlot(fromPU.getKnowledgeBase().getSlot(HAS_CORRESPONDING_CONCEPTS_SLOT), fromPU.getKnowledgeBase(),
						this.getKnowledgeBase(), STANDARD_SLOT);
			}
			if (fromPU.getKnowledgeBase().getSlot(HAS_CORRESPONDING_CONCEPT_SLOT) != null) {
				logger.info(String.format("Importing has-corresponding-concept slot from: %s",
						protegeProjectFile.getName()));
				copySlot(fromPU.getKnowledgeBase().getSlot(HAS_CORRESPONDING_CONCEPT_SLOT), fromPU.getKnowledgeBase(),
						this.getKnowledgeBase(), STANDARD_SLOT);
			}

			if (fromPU.getKnowledgeBase().getSlot(MACROMOLECULAR_COMPLEX_OR_PROTEIN_AMBIGUITY_SLOT) != null) {
				logger.info(String.format("Importing macromolecular complex or protein ambiguity slot from: %s",
						protegeProjectFile.getName()));
				copySlot(fromPU.getKnowledgeBase().getSlot(MACROMOLECULAR_COMPLEX_OR_PROTEIN_AMBIGUITY_SLOT),
						fromPU.getKnowledgeBase(), this.getKnowledgeBase(), STANDARD_SLOT);
			}

			saveAndReload();
			// copy slots - set domain and allowed classes - set metadata -- clone?
			// assertions are stored as instances of "slot with allowed classes"
			// traverse down class hierarchy and set add slot values

			/* reload method here???? */
			// toPU = new Protege_Util(toProjectName);
			logger.info(String.format("Filling OBO Term slots from: %s", protegeProjectFile.getName()));
			fillOboTermSlotsForCopiedClasses(fromPU, this, rootClsNames);
			logger.info(String.format("Removing knowtator classes"));
			removeKnowtatorClasses(this);

			logger.info(String.format("Transfering relations from: %s", protegeProjectFile.getName()));
			transferRelations(fromPU, this, rootClsNames);
			// createPartOfSlot(fromPU, this);
		}

		saveAndReload();
	}

	private void transferRelations(ProtegeUtil fromPU, ProtegeUtil toPU, Set<String> rootClsNames) {
		logger.info("Transfering relations...");

		Iterator<Cls> clsesIter = fromPU.getClasses(rootClsNames);
		while (clsesIter.hasNext()) {
			Cls cls = clsesIter.next();
			if (!cls.getName().contains("knowtator")) {
				Collection<Slot> slots = cls.getDirectTemplateSlots();// TemplateSlots();

				Map<String, Map<String, Set<String>>> relationType2domain2allowedClassesMap = new HashMap<String, Map<String, Set<String>>>();
				Map<String, Map<String, Set<String>>> domain2relationType2allowedClassesMap = new HashMap<String, Map<String, Set<String>>>();

//				logger.info("Transfering relations for class: " + cls.getName());
				for (Slot fromSlot : slots) {
//					logger.info("Transfering slot: " + fromSlot.getName() + " for class: " + cls.getName());

					// Collection<Object> slotValues = cls.getTemplateSlotValues(slot);
					// Collection<Object> slotValues = cls.getOwnSlotValues(slot);
					// Collection<Object> slotValues = cls.getDirectOwnSlotValues(slot);
					// Collection<Object> slotValues = cls.getDirectTemplateSlotValues(slot);
					// Collection<Instance> slotValues = cls.getTemplateSlotValues(slot);
					// if (slotValues != null) {
					Slot slot = toPU.getKnowledgeBase().getSlot(fromSlot.getName());
					if (slot == null) {
						logger.error("NULL SLOT DETECTED: " + fromSlot.getName() + " cls name: " + cls.getName());
					}
					toPU.getKnowledgeBase()
							.getCls(cls.getName())
							.setTemplateSlotAllowedClses(toPU.getKnowledgeBase().getSlot(fromSlot.getName()),
									cls.getTemplateSlotAllowedClses(fromSlot));
					toPU.getKnowledgeBase()
							.getCls(cls.getName())
							.setTemplateSlotAllowedClses(toPU.getKnowledgeBase().getSlot(DIRECT_DOMAIN_SLOT_NAME),
									getClses(toPU.getKnowledgeBase(), getClsNames(fromSlot.getDirectDomain())));
					// logger.info(" allowed classes: " +
					// getClsNames(cls.getTemplateSlotAllowedClses(fromSlot)).toString()
					// + " and domain: " + getClsNames(fromSlot.getDirectDomain()).toString());
					if (fromSlot.getName().contains("part")) {
						int i = 10;
					}
					// String domain = cls.getName();
					// String relationType = slot.getName();
					// for (Object slotValue : slotValues) {
					// if (slotValue instanceof Instance) {
					// /* if the slot value is an instance of some class, then make an instance of
					// the
					// class in the to-project */
					// slotValue = toPU.getKnowledgeBase().getCls(((Instance)slotValue).getName());
					// }
					// toPU.getKnowledgeBase().getCls(cls.getName()).addTemplateSlotValue(toPU.getKnowledgeBase().getSlot(slot.getName()),
					// slotValue);
					// }
					// }
				}
			}
		}
	}

	// private void transferRelations(Protege_Util fromPU, Protege_Util toPU, Set<String>
	// rootClsNames) {
	// logger.info("Transfering relations...");
	// Collection<Slot> slots = fromPU.getKnowledgeBase().getSlots();
	// slots = removeNonInstanceTypeSlots(slots);
	//
	// Map<String, Map<String, Set<String>>> relationType2domain2allowedClassesMap = new
	// HashMap<String, Map<String, Set<String>>>();
	// Map<String, Map<String, Set<String>>> domain2relationType2allowedClassesMap = new
	// HashMap<String, Map<String, Set<String>>>();
	//
	// Iterator<Cls> clsesIter = getClasses(rootClsNames);
	// while (clsesIter.hasNext()) {
	// Cls cls = clsesIter.next();
	// cls.getD
	// logger.info("Transfering relations for class: " + cls.getName());
	// for (Slot slot : slots) {
	// logger.info("Transfering slot: " + slot.getName() + " for class: " + cls.getName());
	//
	// // Collection<Object> slotValues = cls.getTemplateSlotValues(slot);
	// // Collection<Object> slotValues = cls.getOwnSlotValues(slot);
	// // Collection<Object> slotValues = cls.getDirectOwnSlotValues(slot);
	// // Collection<Object> slotValues = cls.getDirectTemplateSlotValues(slot);
	// Collection<Instance> slotValues = cls.getTemplateSlotValues(slot);
	// if (slotValues != null) {
	// logger.info("Slot has values ("+slotValues.size()+")");
	// String domain = cls.getName();
	// String relationType = slot.getName();
	// for (Instance instance : slotValues) {
	// if (domain2relationType2allowedClassesMap.containsKey(domain)) {
	// Map<String, Set<String>> relationType2AllowedClassesMap =
	// domain2relationType2allowedClassesMap
	// .get(domain);
	// if (relationType2AllowedClassesMap.containsKey(relationType)) {
	// relationType2AllowedClassesMap.get(relationType).add(instance.getName());
	// } else {
	// relationType2AllowedClassesMap.put(relationType, createSet(instance.getName()));
	// }
	// } else {
	// Map<String, Set<String>> relationType2AllowedClassesMap = new HashMap<String, Set<String>>();
	// relationType2AllowedClassesMap.put(relationType, createSet(instance.getName()));
	// domain2relationType2allowedClassesMap.put(domain, relationType2AllowedClassesMap);
	// }
	// }
	// }
	// }
	// }
	// }

	private Set<String> createSet(String... inputStrs) {
		return new HashSet<String>(Arrays.asList(inputStrs));
	}

	private Collection<Slot> removeNonInstanceTypeSlots(Collection<Slot> slots) {
		Collection<Slot> slotsToReturn = new ArrayList<Slot>();
		for (Slot slot : slots) {
			if (!slot.isSystem() && slot.getValueType().equals(ValueType.INSTANCE)) {
				slotsToReturn.add(slot);
			}
		}
		return slotsToReturn;
	}

	/**
	 * Returns the Cls object for the input cls name
	 * 
	 * @param clsName
	 * @return
	 */
	public Cls getCls(String clsName) {
		return project.getKnowledgeBase().getCls(clsName);
	}

	/**
	 * Returns true if the childCls is a subclass of the parent class, or if the childCls matches
	 * the parentCls
	 * 
	 * @param parentClsName
	 * @param childClsName
	 * @return
	 */
	public boolean hasParentChildClassRelationship(String parentClsName, String childClsName) {
		if (parentClsName.equals(childClsName))
			return true;
		Cls parent = getCls(parentClsName);
		Cls child = getCls(childClsName);
		if (parent == null || child == null)
			return false;
		logger.debug(String.format("checking for parent/child relation: %s -- %s ------ %b", parentClsName,
				childClsName, child.hasSuperclass(parent)));
		return child.hasSuperclass(parent);
	}

	public void close() {
		project.dispose();
		project = null;
	}

	// private static Map<String, Map<String, Set<String>>> computeRelationsMap(KnowledgeBase
	// knowledgeBase,
	// Set<String> rootClsNames) {
	//
	// }

	// private class OntologySubset {
	// private File protegeProjectFile;
	// private Set<String> rootClassIDs;
	//
	// public OntologySubset(File protegeProjectFile, String... rootClassIDs) {
	// this.protegeProjectFile = protegeProjectFile;
	// if (rootClassIDs == null) {
	// this.rootClassIDs = null;
	// } else {
	// this.rootClassIDs = new HashSet<String>(Arrays.asList(rootClassIDs));
	// }
	// }
	//
	// public File getProtegeProjectFile() {
	// return protegeProjectFile;
	// }
	//
	// public Set<String> getRootClassIDs() {
	// return rootClassIDs;
	// }
	//
	// }

}
