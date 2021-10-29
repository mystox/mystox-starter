#!/bin/sh
export BASE_DIR=`cd $(dirname $0); pwd`
VERSION=1.2.8

mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-common-$VERSION.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-balancer-$VERSION.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-register-$VERSION.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-mqtt-starter-$VERSION.jar

mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-starter -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-iarpc-starter-$VERSION.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-common-$VERSION.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-balancer-$VERSION.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-register-$VERSION.xml
mvn install:install-file  -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-mqtt-starter-$VERSION.xml


