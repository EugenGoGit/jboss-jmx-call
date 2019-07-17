FROM java:8-jdk-alpine as compiler
COPY ./JBossCaller.java /code/JBossCaller.java
WORKDIR /code
RUN javac JBossCaller.java

FROM java:8-jdk-alpine
COPY ./jboss-cli-client.jar /jboss-cli-client/jboss-cli-client.jar
COPY --from=compiler /code/JBossCaller.class /deployments/JBossCaller.class

WORKDIR /deployments
CMD java \
    -cp /jboss-cli-client/jboss-cli-client.jar:. \
    -Dorg.jboss.remoting-jmx.timeout=600 \
    JBossCaller \
    ${JBOSS_HOST} \
    ${JBOSS_PORT} \
    ${JBOSS_USER} \
    ${JBOSS_PASSWORD} \
    ${BEAN_DOMAIN} \
    ${BEAN_SERVICE} \
    ${FUNC}
