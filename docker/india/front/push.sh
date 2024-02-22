#!/bin/bash


ver=2201015_1
docker build -t bistrofront:$ver .
#
#
#aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 702414840389.dkr.ecr.ap-south-1.amazonaws.com
#
#
#docker tag bistrofront:$ver 702414840389.dkr.ecr.ap-south-1.amazonaws.com/bistrofront:$ver
#
#
#docker push 702414840389.dkr.ecr.ap-south-1.amazonaws.com/bistrofront:$ver
#
#docker rmi 702414840389.dkr.ecr.ap-south-1.amazonaws.com/bistrofront:$ver
#
#docker rmi bistrofront:$ver


docker build -t bistrofront:$ver .

docker login --username=hanbinll@hotmail.com -p bistro@937464  registry.cn-hangzhou.aliyuncs.com
docker tag bistrofront:$ver registry.cn-hangzhou.aliyuncs.com/bistro/front:$ver
docker push registry.cn-hangzhou.aliyuncs.com/bistro/front:$ver
docker rmi registry.cn-hangzhou.aliyuncs.com/bistro/front:$ver
#
docker rmi bistrofront:$ver