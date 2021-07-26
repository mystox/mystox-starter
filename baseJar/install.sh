#!/bin/sh
export BASE_DIR=`cd $(dirname $0); pwd`

mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=2.1.5 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-common-2.1.5.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=2.1.5 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-balancer-2.1.5.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=2.1.5 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-register-2.1.5.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=2.1.5 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-mqtt-starter-2.1.5.jar

mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-starter -Dversion=2.1.5 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-iarpc-starter-2.1.5.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=2.1.5 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-common-2.1.5.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=2.1.5 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-balancer-2.1.5.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=2.1.5 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-register-2.1.5.xml
mvn install:install-file  -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=2.1.5 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-mqtt-starter-2.1.5.xml


