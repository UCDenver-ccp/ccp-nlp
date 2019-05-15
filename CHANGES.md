# Release 3.5.2

* Added the ccp-nlp-evaluation module which includes an implementation of the Bossy 2013 metric for evaluating entity annotations against a reference. 
> BioNLP shared Task 2013 – An Overview of the Bacteria Biotope Task 
> Robert Bossy , Wiktoria Golik , Zorana Ratkovic, Philippe Bessières, and Claire Nédellec
> Proceedings of the BioNLP Shared Task 2013 Workshop, pages 161–169,
> Sofia, Bulgaria, August 9 2013
 
* Updated the CRAFT data to CRAFT v3.1.1
  * The cell type ontologies included as part of the CRAFT collection were updated to UTF-8 versions
  * A missing constituency parse was added for a single treebank document (14611657).
  * For document 16098226, use of taxon ID: 1910954 was swapped with 10847
  
* Updated project with dependencies required to build using Java 12
  * There are some whitespace issues with the XML parsing used in the ccp-nlp-doc2txt module when using Java 12. Two unit tests fail under Java 12 b/c of the altered whitespace handling. This issue should be addressed in the future, but releasing as is for now so that the CRAFT shared task evaluation can be run on current Java releases if desired.

