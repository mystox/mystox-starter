FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY target/${JAR_FILE} /opt/app.jar
ENTRYPOINT java ${JAVA_OPTS} -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -jar /opt/app.jar