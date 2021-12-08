title install-common
set BASE_DIR=%~dp0
set VERSION=2.0.9

call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=%VERSION% -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-common-%VERSION%.jar
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=%VERSION% -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-balancer-%VERSION%.jar
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=%VERSION% -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-register-%VERSION%.jar
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=%VERSION% -Dpackaging=jar -Dfile=%BASE_DIR%/yyds-mqtt-starter-%VERSION%.jar

call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-starter -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-iarpc-starter-%VERSION%.xml
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-common-%VERSION%.xml
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-balancer-%VERSION%.xml
call mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-register-%VERSION%.xml
call mvn install:install-file  -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=%VERSION% -Dpackaging=pom -Dfile=%BASE_DIR%/yyds-mqtt-starter-%VERSION%.xml



pause
