# This project depends on the Knowtator project, version 1.7.4. The 
# knowtator-1.7.4.jar file and some of its dependencies are not available 
# in a maven repository (as far as we are aware). They are therefore packaged with this distribution.
# This script enables deployment of the knowtator-1.7.4.jar to your local maven repository manager, e.g. Artifactory.
# If you don't have a local repository manager, then run the intall-protege-jars.sh script instead.
#
# For details on how to configure this script correctly please visit
# http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html
#
# You must fill in appropriate values for the repositoryId and url fields.
#
# Once run, the Knowtator dependency can be referenced using the following in your maven pom files:
#
# <dependency>
#  <groupId>edu.uchsc.ccp</groupId>
#  <artifactId>knowtator</artifactId>
#  <version>1.7.4</version>
# </dependency>


mvn deploy:deploy-file -DgroupId=edu.uchsc.ccp \
  -DartifactId=knowtator \
  -Dversion=1.7.4 \
  -DpomFile=knowtator-1.7.4/pom.xml \
  -Dpackaging=jar \
  -Dfile=knowtator-1.7.4/knowtator-1.7.4.jar \
  -DrepositoryId=third-party \
  -Durl=http://amc-bakeoff.ucdenver.pvt:8081/artifactory/ext-release-local
    
