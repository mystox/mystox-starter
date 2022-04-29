#!/bin/sh
export BASE_DIR=`cd $(dirname $0); pwd`
DEPLOY_URL=http://192.168.0.234:8881/nexus/repository/maven-releases/
REPOSITORY_Id=releases
VERSION=2.0.10

mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-common-$VERSION.jar
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-balancer-$VERSION.jar
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-register-$VERSION.jar
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-mqtt-starter-$VERSION.jar

mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-dependencies -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-iarpc-dependencies-$VERSION.xml
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-starter -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-iarpc-starter-$VERSION.xml
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-common-$VERSION.xml
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-balancer-$VERSION.xml
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-register-$VERSION.xml
mvn deploy:deploy-file -DrepositoryId=$REPOSITORY_Id -Durl=$DEPLOY_URL -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-mqtt-starter-$VERSION.xml
