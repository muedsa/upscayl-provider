FROM gradle:8.6.0-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/upscayl-provider
WORKDIR /home/gradle/upscayl-provider
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
ENV TZ=Asia/Shanghai
EXPOSE 8091:8091
RUN ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo ${TZ} > /etc/timezone &&\
    dpkg-reconfigure --frontend noninteractive tzdata && \
    mkdir /app
COPY --from=build /home/gradle/upscayl-provider/build/libs/*.jar /app/upscayl-provider-all.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","/app/upscayl-provider-all.jar"]