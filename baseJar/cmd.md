发布到https://central.sonatype.com 命令
bash环境
```
mvn clean deploy -f ../pom.xml \
-s /mnt/c/Users/mystox/.m2/settings.xml \
-Dmaven.repo.local=/mnt/m/Environment/m2/repository \
-Dgpg.passphrase=****** 
```
