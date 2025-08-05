FROM 636831486460.dkr.ecr.us-east-1.amazonaws.com/dockerhub/library/openjdk:17-jdk-slim

RUN groupadd -r -g 2000 nonroot && useradd -m -d /home/nonroot/ -s /bin/bash -u 2000 -r -g nonroot nonroot
USER nonroot

WORKDIR /app

COPY ./build/libs/engepro-1.0.0.jar ./app.jar

CMD ["sh", "-c", "java -server ${JAVA_OPTS} -Dserver.port=8080 -jar app.jar"]
