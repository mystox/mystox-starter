发布到https://central.sonatype.com 命令
mvn deploy -f ../pom.xml \
-s /mnt/c/Users/mystox/.m2/settings.xml \
-Dmaven.repo.local=/mnt/d/Environment/m2/repository \
-Dgpg.passphrase=****** 