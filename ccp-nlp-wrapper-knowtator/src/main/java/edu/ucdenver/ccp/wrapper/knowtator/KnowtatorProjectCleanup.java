package edu.ucdenver.ccp.wrapper.knowtator;

import java.io.File;

import org.apache.log4j.BasicConfigurator;

/**
 * Currently this class will remove annotations that have null class mentions. All removals are
 * logged.
 * 
 * @author Bill Baumgartner
 * 
 */
public class KnowtatorProjectCleanup {

	public static void removeAnnotationsWithNullClassMentions(File pprjFile) {
		KnowtatorUtil ku = new KnowtatorUtil(pprjFile.getAbsolutePath());
		ku.removeAnnotationsWithNullClassMentions();
		ku.saveProject();
		ku.close();
	}

	/**
	 * 
	 * @param args
	 *            args[0-3] - pprj file names
	 */
	public static final void main(String[] args) {
		BasicConfigurator.configure();
		for (String arg : args) {
			File pprjFile = new File(arg);
			removeAnnotationsWithNullClassMentions(pprjFile);
		}
	}

}
