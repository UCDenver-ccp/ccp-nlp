
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

import java.util.Set;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * Provides a static method for instantiating a CraftCollectionReader using the CCP type system
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpCraftCollectionReader extends CraftCollectionReader {

	/**
	 * This collection reader uses the CCP type system
	 */
	private static final CraftXmiTypeSystem XMI_TYPE_SYSTEM = CraftXmiTypeSystem.CCP;

	/**
	 * @param craftRelease
	 * @param conceptsToLoad
	 * @return an initialized {@link CraftCollectionReader} that uses the CCP typesystem
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription getDescription(CraftRelease craftRelease,
			Set<CraftConceptType> conceptsToLoad) throws ResourceInitializationException {
		String[] conceptTypes = null;
		if (conceptsToLoad != null) {
			conceptTypes = new String[conceptsToLoad.size()];
			int index = 0;
			for (CraftConceptType type : conceptsToLoad) {
				conceptTypes[index++] = type.name();
			}
		}

		return CollectionReaderFactory.createReaderDescription(CcpCraftCollectionReader.class,
				XMI_TYPE_SYSTEM.getTypeSystemDescription(), PARAM_XMI_TYPE_SYSTEM, XMI_TYPE_SYSTEM.name(),
				PARAM_CONCEPTS_TO_LOAD, conceptTypes, PARAM_CRAFT_RELEASE, craftRelease.name());
	}
}
