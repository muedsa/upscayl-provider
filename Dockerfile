FROM gradle:8.6.0-jdk11 AS build
WORKDIR /home/gradle
RUN git clone https://github.com/muedsa/upscayl-provider && \
    cd upscayl-provider && \
    gradle buildFatJar --no-daemon

FROM openjdk:11
ENV TZ=Asia/Shanghai
EXPOSE 8091:8091
RUN ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo ${TZ} > /etc/timezone &&\
    dpkg-reconfigure --frontend noninteractive tzdata && \
    mkdir /app
COPY --from=build /home/gradle/upscayl-provider/build/libs/*.jar /app/upscayl-provider-all.jar
ENTRYPOINT ["java","-jar","/app/upscayl-provider-all.jar"]