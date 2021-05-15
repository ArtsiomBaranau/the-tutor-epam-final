FROM adoptopenjdk/openjdk11:debian AS builder

WORKDIR /the-tutor-app

COPY . .

RUN ./mvnw clean package -DskipTests

FROM adoptopenjdk/openjdk11:debian-jre

RUN apt-get -q update && apt-get -qy install netcat

WORKDIR /the-tutor-app

COPY --from=builder the-tutor-app/the-tutor-web/target/the-tutor-web-*.jar the-tutor.jar

ADD https://raw.githubusercontent.com/eficode/wait-for/v2.1.0/wait-for wait-for

RUN chmod +x wait-for

CMD ["java", "-jar", "the-tutor.jar"]