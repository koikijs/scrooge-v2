FROM ubuntu:disco

COPY . .
RUN apt-get update && apt-get install -y \
    maven \
    openjdk-8-jre \
 && mvn package
CMD ["java","-jar","target/scrooge-v2.jar"]
