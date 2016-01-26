# ccp nlp
a library of utility code for a variety of "common" tasks

## Development
This project follows the Git-Flow approach to branching as originally described [here](http://nvie.com/posts/a-successful-git-branching-model/). 
To facilitate the Git-Flow branching approach, this project makes use of the [jgitflow-maven-plugin](https://bitbucket.org/atlassian/jgit-flow) as described [here](http://george-stathis.com/2013/11/09/painless-maven-project-releases-with-maven-gitflow-plugin/).

Code in the [master branch](https://github.com/UCDenver-ccp/ccp-nlp/tree/master) reflects the latest release of this library. Code in the [development](https://github.com/UCDenver-ccp/ccp-nlp/tree/development) branch contains the most up-to-date version of this project.

## Module descriptions
The ccp-nlp project is a multi-module Maven project. Below is a brief description of the contents of each module.
* ccp-nlp
  * the parent module
* ccp-nlp-core
  * contains utility code for working with text annotations
* ccp-nlp-doc2txt
  * contains code for converting documents from the PubMed Central XML format to plain text
* ccp-nlp-uima
  * includes the CCP type system and some utility code supporting UIMA component construction
* ccp-nlp-annotators
  * contains various utility annotators for such tasks as:
    * filtering annotations from the CAS
    * modifying metadata of annotations in the CAS
    * comparing groups of annotations to each other
* ccp-nlp-uima-collections
  * contains UIMA collection reader component implementations capable of reading documents from:
    * the file system
    * individual lines in a single file
    * a collection of Medline or PubMed XML files
* ccp-nlp-uima-serialization
  * contains analysis engines for annotation serialization in various formats (XMI, Inline XML, BioNLP)
* ccp-nlp-uima-shims
  * a library of simple interfaces designed to facilitate the development of type-system-independent UIMA components
  * contains implementations specific to the CCP type system
* ccp-nlp-wrapper-banner
  * a wrapper of the [BANNER annotation tool](http://banner.sourceforge.net/) for use with the CCP type system
* ccp-nlp-wrapper-conceptmapper
  * a wrapper of the [UIMA Sandbox ConceptMapper tool](https://uima.apache.org/sandbox.html#concept.mapper.annotator)
  * provides descriptor-free configuration via UIMAFit, dictionary construction from arbitrary OBO/OWL formatted ontology files, and integration with the CCP Type System
  
## Maven signature
```xml
<dependency>
  <groupId>edu.ucdenver.ccp</groupId>
  <artifactId>ccp-nlp-core</artifactId>
  <version>3.3.1</version>
</dependency>

<dependency>
  <groupId>edu.ucdenver.ccp</groupId>
  <artifactId>ccp-nlp-doc2txt</artifactId>
  <version>3.3.1</version>
</dependency>

<dependency>
  <groupId>edu.ucdenver.ccp</groupId>
  <artifactId>ccp-nlp-uima</artifactId>
  <version>3.3.1</version>
</dependency>

<dependency>
  <groupId>edu.ucdenver.ccp</groupId>
  <artifactId>ccp-nlp-uima</artifactId>
  <version>3.3.1</version>
  <type>test-jar</type>
  <scope>test</scope>
</dependency>

<dependency>
  <groupId>edu.ucdenver.ccp</groupId>
  <artifactId>ccp-nlp-uima-annotators</artifactId>
  <version>3.3.1</version>
</dependency>

<dependency>
  <groupId>edu.ucdenver.ccp</groupId>
  <artifactId>ccp-nlp-uima-collections</artifactId>
  <version>3.3.1</version>
</dependency>

<dependency>
  <groupId>edu.ucdenver.ccp</groupId>
  <artifactId>ccp-nlp-uima-serialization</artifactId>
  <version>3.3.1</version>
</dependency>

<dependency>
  <groupId>edu.ucdenver.ccp</groupId>
  <artifactId>ccp-nlp-uima-shims</artifactId>
  <version>3.3.1</version>
</dependency>

<dependency>
  <groupId>edu.ucdenver.ccp</groupId>
  <artifactId>ccp-nlp-wrapper-banner</artifactId>
  <version>3.3.1</version>
</dependency>

<dependency>
  <groupId>edu.ucdenver.ccp</groupId>
  <artifactId>ccp-nlp-wrapper-conceptmapper</artifactId>
  <version>3.3.1</version>
</dependency>

<repository>
	<id>bionlp-sourceforge</id>
	<url>http://svn.code.sf.net/p/bionlp/code/repo/</url>
</repository>
```