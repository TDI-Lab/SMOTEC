FROM openjdk:17
COPY ./MobileAgent.jar /tmp
COPY ./src/conf/ /tmp/src/conf/
COPY ./src/Mobility_Dataset/ /tmp/src/Mobility_Dataset/
WORKDIR /tmp
ENTRYPOINT ["java","-jar","MobileAgent.jar"]
CMD ["10001"]
