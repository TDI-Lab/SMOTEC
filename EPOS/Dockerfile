FROM openjdk:19
MAINTAINER baeldung.com
COPY ./IEPOS.jar /tmp/IEPOS.jar
COPY ./datasets/ /tmp/datasets/
COPY ./output/ /tmp/output/
COPY ./conf/ /tmp/conf/
COPY ./log/ /tmp/log/
WORKDIR /tmp
ENTRYPOINT ["java","-jar","IEPOS.jar"]
