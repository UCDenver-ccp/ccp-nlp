/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.core.mention.comparison;

import edu.ucdenver.ccp.nlp.core.annotation.comparison.SpanComparator;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;

/**
 * This is an interface for comparing class mentions
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface ClassMentionComparator {

	/**
	 * Compares two <code>ClassMention</code> objects using the specified
	 * <code>SpanComparator</code> to a user-specified depth in the mention hierarchy.
	 * 
	 * @param classMention1
	 *            a <code>ClassMention</code> to compare
	 * @param classMention2
	 *            the other <code>ClassMention</code> to compare
	 * @param spanComparator
	 *            the <code>SpanComparator</code> to use during the comparison
	 * @param maximumComparisonDepth
	 *            This parameter specifies how deep into the mention hierarchy the comparison should
	 *            delve. A value of zero specifies that the comparison will look at only the types
	 *            of the <code>ClassMention</code> objects. A value of 1 will include a comparison
	 *            of the <code>ClassMention</code> type (name), but also of any
	 *            <code>SlotMentions</code> and <code>ComplexSlotMentions</code>. See
	 *            {@link MentionComparator} for a more detailed explanation.
	 * @return 0 if the <code>ClassMention</code> objects are equal, otherwise, something other than
	 *         zero
	 */
	public int compare(ClassMention classMention1, ClassMention classMention2, SpanComparator spanComparator,
			int maximumComparisonDepth);
}
