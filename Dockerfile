FROM tomcat:7.0

RUN apt-get update

RUN apt-get -y install ant

WORKDIR /usr/local/tomcat/webapps/cbum
