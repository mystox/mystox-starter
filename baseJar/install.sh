source ~/.bash_profile
#!/bin/bash
export BASE_DIR=`cd $(dirname $0); pwd`

mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=1.2.4 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-common-1.2.4.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=1.2.4 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-balancer-1.2.4.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=1.2.4 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-register-1.2.4.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=1.2.4 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-mqtt-starter-1.2.4.jar

mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-starter -Dversion=1.2.4 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-iarpc-starter-1.2.4.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=1.2.4 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-common-1.2.4.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=1.2.4 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-balancer-1.2.4.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=1.2.4 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-register-1.2.4.xml
mvn install:install-file  -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=1.2.4 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-mqtt-starter-1.2.4.xml


