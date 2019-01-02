<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8"
		omit-xml-declaration="no" indent="yes" />

	<!-- use <document> tags to surround the entire document -->
	<xsl:template match="/">
		<document>
			<xsl:apply-templates />
		</document>
	</xsl:template>

	<!-- The following removes unnecessary whitespace -->
	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()" />
	</xsl:template>

	<!-- each article has a front section that includes the title, abstract(s), 
		and article metadata, e.g. author information, and a body section that includes 
		the article text, tables, figures, etc. -->
	<xsl:template match="article">
		<xsl:apply-templates select="front" />
		<xsl:apply-templates select="body" />
		<xsl:apply-templates select="front/article-meta/copyright-statement" />
		<source>PubMed Central:	<xsl:value-of select="front/article-meta/article-id[@pub-id-type='pmcid']" /></source>
	</xsl:template>

	<xsl:template match="front/article-meta/copyright-statement">
		<copyright>
			<xsl:apply-templates />
		</copyright>
	</xsl:template>


	<!-- the "front" section contains article metadata (author information, 
		etc.) which we ignore, but also contains the article title and abstract(s) 
		which we grab here -->
	<xsl:template match="front">
		<xsl:apply-templates select="article-meta/title-group" />
		<xsl:apply-templates select="article-meta/abstract" />
		<!-- uncomment line below to add keywords to output -->
		<!-- <xsl:apply-templates select="article-meta/kwd-group" /> -->
	</xsl:template>

	<xsl:template match="title-group">
		<xsl:apply-templates select="article-title" />
		<xsl:if test="subtitle">
			<xsl:text>: </xsl:text>
			<xsl:apply-templates select="subtitle" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="article-title | subtitle | trans-title">
		<article-title>
			<xsl:apply-templates select="*[not(self::fnr) and not(self::xref)]|text()" />
		</article-title>
	</xsl:template>

	<!-- Some articles have multiple abstracts of different types, e.g. author 
		summary. When an abstract has a specified type, we keep it as an attribute 
		in the abstract element called 'abstract-type' -->
	<xsl:template match="abstract">
		<xsl:variable name="abstract-type">
			<xsl:value-of select="@abstract-type" />
		</xsl:variable>
		<abstract>
			<xsl:choose>
				<xsl:when test="$abstract-type != ''">
					<xsl:attribute name="abstract-type">
				<xsl:value-of select="$abstract-type" />
			</xsl:attribute>
				</xsl:when>
				<xsl:otherwise />
			</xsl:choose>
			<xsl:apply-templates />
		</abstract>
	</xsl:template>

	<!-- some abstracts have sections within them, e.g. background, results, 
		conclusion, etc. If an abstract has a section, and if that section has a 
		title, then an explicit <section> element is created. If the abstract has 
		a section with no title, then the explicit <section> element is not created. -->
	<!-- <xsl:template match="abstract/sec"> if the section has a title, then 
		we insert it, otherwise we exclude the explicit <section> element <xsl:variable 
		name="section-title"> <xsl:value-of select="./title/text()" /> </xsl:variable> 
		<xsl:choose> <xsl:when test="$section-title != ''"> <section> <xsl:attribute 
		name="name"> <xsl:value-of select="$section-title" /> </xsl:attribute> <xsl:apply-templates 
		/> </section> </xsl:when> <xsl:otherwise> <xsl:apply-templates /> </xsl:otherwise> 
		</xsl:choose> </xsl:template> -->

	<xsl:template match="kwd-group/kwd">
		<xsl:element name="keyword">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>


	<!-- <xsl:template match="body"/> -->
	<xsl:template match="body">
		<xsl:apply-templates />
	</xsl:template>


	<!-- ==== sections & paragraphs ==== -->

	<xsl:template match="p">
		<paragraph>
			<xsl:apply-templates />
		</paragraph>
	</xsl:template>

	<xsl:template match="sec">
		<xsl:variable name="section-title">
			<xsl:value-of select="./title/text()" />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$section-title != ''">
				<section>
					<xsl:attribute name="name">
						<xsl:value-of select="$section-title" /> 
					</xsl:attribute>
					<xsl:apply-templates />
				</section>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- labels in figures are extracted explicitly -->
	<xsl:template match="label" />

	<xsl:template match="caption">
		<xsl:variable name="label">
			<xsl:value-of select="../label" />
		</xsl:variable>
		<xsl:variable name="type">
			<xsl:value-of select="name(..)" />
		</xsl:variable>
		<xsl:text>&#xa;</xsl:text>
		<caption>
			<xsl:choose>
				<xsl:when test="$label != ''">
					<xsl:attribute name="label">
				<xsl:value-of select="$label" /> 
			</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$type != ''">
					<xsl:attribute name="type">
				<xsl:value-of select="$type" /> 
			</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:apply-templates />
		</caption>
	</xsl:template>




	<!-- ==== Formatting templates ==== -->

	<!-- There are instances of empty <title /> elements, so we ensure the title 
		has content before returning it -->
	<xsl:template match="title">
		<xsl:variable name="title-text">
			<xsl:value-of select="." />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$title-text != ''">
				<title>
					<xsl:apply-templates />
				</title>
			</xsl:when>
			<xsl:otherwise />
		</xsl:choose>
	</xsl:template>


	<!-- for citations, print the reference number -->
	<xsl:template match="xref">
		<xsl:apply-templates />
	</xsl:template>

	<!-- print the content of "named-content", e.g. "... purified from <named-content 
		content-type="genus-species">Escherichia coli </named-content> showed ..." -->
	<xsl:template match="named-content">
		<xsl:text> </xsl:text>
		<xsl:apply-templates />
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="sup">
		<sup>
			<xsl:apply-templates />
		</sup>
	</xsl:template>

	<xsl:template match="sub">
		<sub>
			<xsl:apply-templates />
		</sub>
	</xsl:template>

	<!-- not sure what "sc" refers to -->
	<xsl:template match="sc">
		<xsl:apply-templates />
	</xsl:template>

	<!-- the <italic> and <bold> elements appear to imply a space so we add 
		spaces here, both before and after -->
	<xsl:template match="italic">
		<xsl:text> </xsl:text>
		<italic>
			<xsl:apply-templates />
		</italic>
		<xsl:text> </xsl:text>
	</xsl:template>

	<!-- the <italic> and <bold> elements appear to imply a space so we add 
		spaces here, both before and after -->
	<xsl:template match="bold">
		<xsl:text> </xsl:text>
		<bold>
			<xsl:apply-templates />
		</bold>
		<xsl:text> </xsl:text>
	</xsl:template>

</xsl:stylesheet>
