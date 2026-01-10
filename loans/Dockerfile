#Start with a base image containing Java runtime
FROM eclipse-temurin:17-jdk-jammy

# MAINTAINER instruction is deprecated in favor of using label
# MAINTAINER eazybytes.com
#Information around who maintains the image
LABEL maintainer="ameynagale007"

# Add the application's jar to the image
COPY target/loans-0.0.1-SNAPSHOT.jar loans-0.0.1-SNAPSHOT.jar

# execute the application
ENTRYPOINT ["java", "-jar", "loans-0.0.1-SNAPSHOT.jar"]