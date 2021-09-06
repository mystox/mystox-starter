#!/bin/sh
export BASE_DIR=`cd $(dirname $0); pwd`
DEPLOY_URL=http://192.168.0.234:8881/nexus/repository/maven-releases/
REPOSITORY_Id=releases

mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=2.1.7 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-common-2.1.7.jar
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=2.1.7 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-balancer-2.1.7.jar
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=2.1.7 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-register-2.1.7.jar
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=2.1.7 -Dpackaging=jar -Dfile=$BASE_DIR/yyds-mqtt-starter-2.1.7.jar

mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-starter -Dversion=2.1.7 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-iarpc-starter-2.1.7.xml
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=2.1.7 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-common-2.1.7.xml
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=2.1.7 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-balancer-2.1.7.xml
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=2.1.7 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-register-2.1.7.xml
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=2.1.7 -Dpackaging=pom -Dfile=$BASE_DIR/yyds-mqtt-starter-2.1.7.xml
