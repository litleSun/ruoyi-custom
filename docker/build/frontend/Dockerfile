# 使用nginx基础镜像
FROM 192.168.1.102:32360/mirror/nginx:1.27-bookworm-perl

# 维护者信息
LABEL maintainer="yusheng"

# 复制构建好的前端文件到nginx目录
COPY ruoyi-ui/dist /usr/share/nginx/html

# 创建脚本目录
RUN mkdir -p /usr/share/nginx/bin

# 复制入口脚本
COPY docker/build/frontend/enterpoint.sh /usr/share/nginx/bin

# 复制nginx配置文件
COPY docker/build/frontend/nginx.conf /etc/nginx/conf.d/default.conf

# 设置脚本执行权限
RUN chmod 777 /usr/share/nginx/bin/enterpoint.sh

# 设置入口脚本
ENTRYPOINT ["/usr/share/nginx/bin/enterpoint.sh"]

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]
