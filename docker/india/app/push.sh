#!/bin/bash


#aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 702414840389.dkr.ecr.ap-south-1.amazonaws.com
#
#
#docker tag bistroapi:$ver 702414840389.dkr.ecr.ap-south-1.amazonaws.com/bistroapi:$ver
#
#
#docker push 702414840389.dkr.ecr.ap-south-1.amazonaws.com/bistroapi:$ver
#
#docker rmi 702414840389.dkr.ecr.ap-south-1.amazonaws.com/bistroapi:$ver
#
#docker rmi bistroapi:$ver



ver=2201015_1
docker build -t bistroapi:$ver .

docker login --username=hanbinll@hotmail.com -p bistro@937464  registry.cn-hangzhou.aliyuncs.com
docker tag bistroapi:$ver registry.cn-hangzhou.aliyuncs.com/bistro/api:$ver
docker push registry.cn-hangzhou.aliyuncs.com/bistro/api:$ver
docker rmi registry.cn-hangzhou.aliyuncs.com/bistro/api:$ver
#
docker rmi bistroapi:$ver