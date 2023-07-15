FROM openjdk:17
COPY ./src/deployments/ /tmp/src/deployments/
COPY ./src/CityConfig.json /tmp/src/CityConfig.json
COPY ./src/agent.sh /tmp/src/agent.sh
COPY ./src/edge.sh /tmp/src/edge.sh
COPY ./Testbed.jar /tmp
COPY ./Testbed_lib /tmp/Testbed_lib
WORKDIR /tmp
ENTRYPOINT ["java","-jar","Testbed.jar"]
CMD ["/home/spring/Documents/Testbed/src/CityConfig.json","edgContainerInputFile","/home/spring/Documents/Testbed/src/Mobility_Dataset"]