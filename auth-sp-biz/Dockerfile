FROM openjdk:8-jdk-alpine
MAINTAINER zhengxing.hu "zhengxing.hu@nvxclouds.com"
# 设置时间
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN echo -e "https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/main\n\
https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/community" > /etc/apk/repositories
RUN apk --update add curl bash ttf-dejavu && \
      rm -rf /var/cache/apk/*
#拷贝字体文件
COPY ./simhei.ttf /usr/share/fonts/simhei.ttf
#设置字符集
ENV LANG en_US.UTF-8
#安装字体软件，完成字体配置
RUN apk add --update ttf-dejavu fontconfig && rm -rf /var/cache/apk/*
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]