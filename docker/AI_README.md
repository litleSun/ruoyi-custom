请根据以下参数修改 Docker Compose 配置文件中的端口映射：

后端服务对外暴露的主机端口: {BACKEND_PORT} = 11103
前端服务对外暴露的主机端口: {FRONTEND_PORT} = 11104
具体修改如下：

修改文件：docker/build/backend/docker-compose-ci-backend.yml
找到 services -> backend（或对应服务）下的 ports 配置。
修改主机端口映射，将格式为 "X:8080" 的条目更新为 "{BACKEND_PORT}:8080"，即只更改冒号前的主机端口部分。
修改文件：docker/build/frontend/docker-compose-ci-frontend.yml
找到环境变量 VUE_APP_API_URL，将其值中的端口号修改为 {BACKEND_PORT}，确保形如 http://192.168.1.102:{BACKEND_PORT}。
找到 ports 配置项，将格式为 "X:80" 的条目更新为 "{FRONTEND_PORT}:80"。