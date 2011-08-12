# This project depends on the Protege project (http://morphadorner.northwestern.edu/, version 3.3.1. The 
# protege-3.3.1.jar file and some of its dependencies are not available 
# in a maven repository (as far as we are aware). They are therefore packaged with this distribution.
# This script enables deployment of the protege-3.3.1.jar to your local maven repository manager, e.g. Artifactory.
# If you don't have a local repository manager, then run the intall-protege-jars.sh script instead.
#
# For details on how to configure this script correctly please visit
# http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html
#
# You must fill in appropriate values for the repositoryId and url fields.
#
# Once run, the Protege dependency can be referenced using the following in your maven pom files:
#
# <dependency>
#  <groupId>edu.stanford.smi</groupId>
#  <artifactId>protege</artifactId>
#  <version>3.3.1</version>
# </dependency>


mvn deploy:deploy-file -DgroupId=edu.stanford.smi \
  -DartifactId=protege \
  -Dversion=3.3.1 \
  -DpomFile=protege-3.3.1-jars/pom.xml \
  -Dpackaging=jar \
  -Dfile=protege-3.3.1-jars/protege-3.3.1.jar \
  -DrepositoryId=third-party \
  -Durl=http://amc-bakeoff.ucdenver.pvt:8081/artifactory/ext-release-local
    
mvn deploy:deploy-file -DgroupId=com.jgoodies \
  -DartifactId=looks \
  -Dversion=2.1.3 \
  -Dpackaging=jar \
  -Dfile=protege-3.3.1-jars/looks-2.1.3.jar \
  -DrepositoryId=third-party \
  -Durl=http://amc-bakeoff.ucdenver.pvt:8081/artifactory/ext-release-local
  
