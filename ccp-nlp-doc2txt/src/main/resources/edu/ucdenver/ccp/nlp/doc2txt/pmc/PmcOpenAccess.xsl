<?xml version="1.0" encoding="utf-8"?>
<!--
  #%L
  Colorado Computational Pharmacology's nlp module
  %%
  Copyright (C) 2012 - 2016 Regents of the University of Colorado
  %%
  Redistribution and use in source and binary forms, with or without modification,
  are permitted provided that the following conditions are met:
  
  1. Redistributions of source code must retain the above copyright notice, this
     list of conditions and the following disclaimer.
  
  2. Redistributions in binary form must reproduce the above copyright notice,
     this list of conditions and the following disclaimer in the documentation
     and/or other materials provided with the distribution.
  
  3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
     may be used to endorse or promote products derived from this software without
     specific prior written permission.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
  OF THE POSSIBILITY OF SUCH DAMAGE.
  #L%
  -->

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8" />

	<!-- omitted: 
		ack
		ref-list
	-->

	<xsl:preserve-space elements="sec kwd def-item abstract italic p  caption "/>

	<xsl:template match="/|@*|node()">
		<xsl:apply-templates select="node()"/>
	</xsl:template>

	<xsl:template match="article" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:element name="doc"> 
		<xsl:apply-templates select="node()" />
		</xsl:element>
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="sec/sec" priority='0'>
		<xsl:element name="SUBSECTION"> 
		<xsl:text> </xsl:text>
		<xsl:attribute name="NAME">
			<xsl:value-of select="./title/text()" /> 
		</xsl:attribute>
		<xsl:text> </xsl:text>
		<xsl:apply-templates select="node()" />
		<xsl:text> </xsl:text>
		</xsl:element>
	</xsl:template>

	<xsl:template match="body/sec" priority='0'>
		<xsl:element name="SECTION"> 
		<xsl:text> </xsl:text>
			<xsl:attribute name="NAME">
				<xsl:value-of select="./title/text()" /> 
			</xsl:attribute>
			<xsl:apply-templates select="node()" />
		<xsl:text> </xsl:text>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="ack/sec" priority='0'>
		<xsl:element name="ACKNOWLEDGEMENT_SECTION"> 
		<xsl:text> </xsl:text>
			<xsl:attribute name="NAME">
				<xsl:value-of select="./title/text()" /> 
			</xsl:attribute>
			<xsl:apply-templates select="node()" />
		<xsl:text> </xsl:text>
		</xsl:element>
	</xsl:template>

	<xsl:template match="kwd-group/kwd" priority='0'>
	<!-- sometimes this is just a keyword, sometimes it's an abbreviation
	followed by an expansion or definition. See J Mol Biol -->
		<xsl:element name="KEYWORD">
		<xsl:text> </xsl:text>
			<xsl:value-of select="text()" />
		<xsl:text> </xsl:text>
		</xsl:element>
	</xsl:template>

	<xsl:template match="def-list/def-item" priority='0'>
			<xsl:element name="DEFINITION">
		<xsl:text> </xsl:text>
				<xsl:attribute name="NAME">
					<xsl:value-of select="./term/text()" /> 
				</xsl:attribute>
				<xsl:value-of select="./def/text()"/>
				<xsl:apply-templates select="node()" />
		<xsl:text> </xsl:text>
			</xsl:element>
	</xsl:template>

	<xsl:template match="abstract" priority='0'>
		<xsl:element name="ABSTRACT"> 
		<xsl:text> </xsl:text>
			<xsl:attribute name="NAME">
				<xsl:value-of select="./title/text()" /> 
			</xsl:attribute>
			<xsl:value-of select="./text()" />
			<xsl:apply-templates select="node()" />
		<xsl:text> </xsl:text>
		</xsl:element>
	</xsl:template>

	<xsl:template match="caption" priority='0'>
		<!-- figure or table caption-->
		<xsl:element name="FIGURE"> 
		<xsl:text> </xsl:text>
			<xsl:attribute name="NAME">
				<xsl:value-of select="./text()" /> 
			</xsl:attribute>
			<xsl:value-of select="./title/text()" />
			<xsl:apply-templates select="node()" />
		<xsl:text> </xsl:text>
		</xsl:element>
	</xsl:template>

	<xsl:template match="title-group/article-title" priority='0'>
	<!-- only want the article-title that is in the title-group
		 	otherwise you get them from references too -->
		<xsl:element name="TITLE"> 
			<xsl:text> </xsl:text> <xsl:value-of select="." /> <xsl:text> </xsl:text>
		</xsl:element>
	</xsl:template>

	<xsl:template match="sec/title" priority='0'>
		<xsl:element name="TITLE"> 
			<xsl:text> </xsl:text>
			<xsl:value-of select="." /> 
			<xsl:text> </xsl:text>
		</xsl:element>
	</xsl:template>


	<xsl:template match="p" priority='0'>
		<xsl:element name="PARAGRAPH">
		<xsl:text> </xsl:text>
			<xsl:apply-templates select="node()" />
		<xsl:text> </xsl:text>
		</xsl:element>
		<xsl:text>&#xA;</xsl:text>
	</xsl:template>

	<xsl:template match="p/text()" priority='0'>
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<xsl:template match="p/italic" priority='0'>
		<!-- don't want italics that are not in paragraphs like those in tables,
		since we're not doing tables yet -->
		<xsl:element name="ITALICS"> 
			<xsl:text> </xsl:text>
			<xsl:value-of select="normalize-space(.)" />	
			<xsl:text> </xsl:text>
		</xsl:element>
	</xsl:template>

	<xsl:template match="xref">
		<xsl:text> </xsl:text>
				<xsl:value-of select="text()" /> 
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="named-content">
		<xsl:text> </xsl:text>
				<xsl:value-of select="." /> 
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="sub">
				<xsl:value-of select="." /> 
	</xsl:template>

	<xsl:template match="sup">
				<xsl:value-of select="." /> 
	</xsl:template>
</xsl:stylesheet>
