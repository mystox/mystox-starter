title install-common
set BASE_DIR=%~dp0
set DEPLOY_URL=http://nexus.mystox.tech:8881/repository/maven-releases/
set REPOSITORY_Id=releases
set VERSION=2.1.11

call mvn deploy:deploy-file -DrepositoryId=%REPOSITORY_Id% -Durl=%DEPLOY_URL% -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=%VERSION% -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-common-%VERSION%.jar
call mvn deploy:deploy-file -DrepositoryId=%REPOSITORY_Id% -Durl=%DEPLOY_URL% -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=%VERSION% -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-balancer-%VERSION%.jar
call mvn deploy:deploy-file -DrepositoryId=%REPOSITORY_Id% -Durl=%DEPLOY_URL% -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=%VERSION% -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-register-%VERSION%.jar
call mvn deploy:deploy-file -DrepositoryId=%REPOSITORY_Id% -Durl=%DEPLOY_URL% -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=%VERSION% -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-mqtt-starter-%VERSION%.jar

call mvn deploy:deploy-file -DrepositoryId=%REPOSITORY_Id% -Durl=%DEPLOY_URL% -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-dependencies -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-iarpc-dependencies-%VERSION%.xml
call mvn deploy:deploy-file -DrepositoryId=%REPOSITORY_Id% -Durl=%DEPLOY_URL% -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-starter -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-iarpc-starter-%VERSION%.xml
call mvn deploy:deploy-file -DrepositoryId=%REPOSITORY_Id% -Durl=%DEPLOY_URL% -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-common-%VERSION%.xml
call mvn deploy:deploy-file -DrepositoryId=%REPOSITORY_Id% -Durl=%DEPLOY_URL% -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-balancer-%VERSION%.xml
call mvn deploy:deploy-file -DrepositoryId=%REPOSITORY_Id% -Durl=%DEPLOY_URL% -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-register-%VERSION%.xml
call mvn deploy:deploy-file -DrepositoryId=%REPOSITORY_Id% -Durl=%DEPLOY_URL% -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-mqtt-starter-%VERSION%.xml

pause
