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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.apache.log4j.Logger;

/**
 * 
 * This class is used to allow DTD's to be added to the classpath and then found accordingly.<br>
 * Code snippet from: http://www.theserverside.com/discussions/thread.tss?thread_id=24895 <br>
 * Reference: http://www.ibm.com/developerworks/library/x-tipent.html
 * 
 * The latest PMC DTD can be downloaded from: ftp://ftp.ncbi.nih.gov/pub/archive_dtd/archiving/
 * 
 * Download and unpack archive-interchange-dtd-3.0.zip for example. When unpacked there will be an
 * archiving/ directory. From that directory, create a jar file using the following command:
 * 
 * jar -cf pmc-dtd-3.0.jar *
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class PmcDtdClasspathResolver implements EntityResolver {

	private static final Logger logger = Logger.getLogger(PmcDtdClasspathResolver.class);

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		logger.debug("Input system ID: " + systemId);
		String sysId = null;
		if (systemId.contains("/mathml/")) {
			sysId = systemId.substring(systemId.lastIndexOf("/mathml/"));  
		} else if (systemId.contains("/iso8879/")) {
			sysId = systemId.substring(systemId.lastIndexOf("/iso8879/"));
		} else if (systemId.contains("/iso9573-13/")) {
			sysId = systemId.substring(systemId.lastIndexOf("/iso9573-13/"));
		} else if (systemId.contains("/xmlchars/")) {
			sysId = systemId.substring(systemId.lastIndexOf("/xmlchars/"));
		} else {
			sysId = systemId.substring(systemId.lastIndexOf("/"));
		}
		logger.debug("RESOLVING ENTITY: publicID: \"" + publicId + "\"   systemID: \"" + sysId + "\"");

		InputStream stream = getClass().getResourceAsStream(sysId);
		if (stream == null) {
			logger.warn("Entity resolution failure: null stream from systemId; publicID: \"" + publicId
					+ "\"   systemID: \"" + sysId + "\"");
			return null;
		}
		return new InputSource(stream);
	}

}