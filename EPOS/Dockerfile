FROM openjdk:19
MAINTAINER baeldung.com
COPY IEPOS.jar IEPOS.jar
COPY IEPOS_lib/ IEPOS_lib/
COPY datasets/ datasets/
COPY output/ output/
COPY conf/ conf/
COPY log/ log/
ENTRYPOINT ["java","-jar","/IEPOS.jar"]
