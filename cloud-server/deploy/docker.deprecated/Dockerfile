FROM adoptopenjdk/openjdk11

RUN mkdir -p /opt/app
RUN apt-get update && apt-get install -y \
  wget \
  libssl-dev \
  libapr1-dev \
  libaprutil1-dev \
  make \
  gcc \
  autoconf

#RUN wget http://apache.mirror.cdnetworks.com/tomcat/tomcat-connectors/native/1.2.31/source/tomcat-native-1.2.31-src.tar.gz
#RUN tar -xvzf tomcat-native-1.2.31-src.tar.gz
#RUN cd tomcat-native-1.2.31-src/native && autoconf && ./configure --with-apr=/usr --prefix=/apr -with-java-home=/opt/java/openjdk && make && make install


#COPY /var/jenkins_home/workspace/SmartConstruction/onm_server/target/SmartConstruction-2.5.4.war /opt/app
COPY target/fuel-cell-cart-2.5.4.war /opt/app
ENTRYPOINT ["java","-Djava.library.path=/apr/lib","-Dspring.profiles.active=production","-jar","/opt/app/fuel-cell-cart-2.5.4.war"]
EXPOSE 6090


