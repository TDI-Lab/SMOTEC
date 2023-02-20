FROM openjdk:17
COPY ./Agent.jar /tmp
COPY ./src/conf/ /tmp/src/conf/
COPY ./src/Mobility_Dataset/ /tmp/src/Mobility_Dataset/
WORKDIR /tmp
ENTRYPOINT ["java","-jar","Agent.jar"]
CMD ["10001"]