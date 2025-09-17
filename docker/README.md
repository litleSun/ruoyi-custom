可以使用 AI_README.md中的提示词来快速修改


导出镜像:
docker save -o vote_backend.tar 192.168.1.102:32360/vote/vote_backend@sha256:b1d40f96d043d98921270d0268a19d86c469f1850741768b6e1befd0b60bbb2e

docker save -o vote_frontend.tar 192.168.1.102:32360/vote/vote_frontend@sha256:b0a888befdaf109b1226e13d8fcd55e5b79e1d0e504d4df1266fc5769b900339
导入镜像:
docker load -i  vote_backend.tar
docker load -i  vote_frontend.tar

  docker tag 8b6709d1b3ad vote_frontend:latest