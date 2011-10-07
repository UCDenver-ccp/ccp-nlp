<?xml version="1.0" encoding="utf-8"?>
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
<!--
	<xsl:template match="sec/title" priority='0'>
		<xsl:element name="TITLE"> 
			<xsl:text> </xsl:text>
			<xsl:value-of select="." /> 
			<xsl:text> </xsl:text>
		</xsl:element>
	</xsl:template>
-->

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
