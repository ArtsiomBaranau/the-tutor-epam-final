FROM adoptopenjdk/openjdk11:debian-jre

RUN apt-get -q update && apt-get -qy install netcat
WORKDIR /the-tutor-app


COPY the-tutor-web/target/the-tutor-web-*.jar the-tutor.jar

COPY wait-for wait-for

RUN chmod +x wait-for

CMD ["java", "-jar", "the-tutor.jar"]