FROM openjdk:17.0.1-jdk-oracle

WORKDIR /opt/app

RUN mkdir -p /opt/app

copy backend-api-admin.jar /opt/app/app.jar

env SPRING_ACTIVE -Dspring.profiles.active=prod
env JAVA_OPTS -server -Duser.timezone=GMT+8
env JAVA_EXPORTS --add-exports java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS  $SPRING_ACTIVE $JASYPT_PASSWORD  $JAVA_EXPORTS -jar /opt/app/app.jar"]