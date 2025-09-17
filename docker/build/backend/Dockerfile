# 使用openjdk:8-oracle基础镜像
FROM 192.168.1.102:32360/mirror/openjdk:8-oracle

# 维护者信息
LABEL maintainer="yusheng"

# 创建应用目录
RUN mkdir -p /data

# 复制jar文件到应用目录
COPY target/*.jar /data/jar/ruoyi-admin.jar

# 复制backend目录内容到/data
COPY docker/build/backend/ /data

# 给脚本文件添加可执行权限
RUN chmod +x /data/start.sh

# 执行start.sh脚本
CMD ["/bin/bash", "/data/start.sh"]
