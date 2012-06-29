/**
 * 
 */
package edu.ucdenver.ccp.nlp.core.uima.util;

import java.io.File;
import java.io.FilenameFilter;

public class XCASFilenameFilter implements FilenameFilter {
    public XCASFilenameFilter() {
    }
    
    public boolean accept(File dir, String name) {
        return name.endsWith("xcas");
    }
}