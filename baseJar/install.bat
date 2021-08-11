title install-common
set BASE_DIR=%~dp0

call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=2.1.6 -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-common-2.1.6.jar
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=2.1.6 -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-balancer-2.1.6.jar
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=2.1.6 -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-register-2.1.6.jar
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=2.1.6 -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-mqtt-starter-2.1.6.jar

call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-starter -Dversion=2.1.6 -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-iarpc-starter-2.1.6.xml
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=2.1.6 -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-common-2.1.6.xml
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=2.1.6 -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-balancer-2.1.6.xml
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=2.1.6 -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-register-2.1.6.xml
call mvn install:install-file  -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=2.1.6 -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-mqtt-starter-2.1.6.xml



pause
