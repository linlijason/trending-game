#!/bin/bash


ver=2201013_1
docker build -t bistromgt:$ver .
#
#
#aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 702414840389.dkr.ecr.ap-south-1.amazonaws.com
#
#
#docker tag bistromgt:$ver 702414840389.dkr.ecr.ap-south-1.amazonaws.com/bistromgt:$ver
#
#
#docker push 702414840389.dkr.ecr.ap-south-1.amazonaws.com/bistromgt:$ver
#
#docker rmi 702414840389.dkr.ecr.ap-south-1.amazonaws.com/bistromgt:$ver
#
#docker rmi bistromgt:$ver


docker build -t bistromgt:$ver .

docker login --username=hanbinll@hotmail.com -p bistro@937464  registry.cn-hangzhou.aliyuncs.com
docker tag bistromgt:$ver registry.cn-hangzhou.aliyuncs.com/bistro/mgt:$ver
docker push registry.cn-hangzhou.aliyuncs.com/bistro/mgt:$ver
docker rmi registry.cn-hangzhou.aliyuncs.com/bistro/mgt:$ver
#
docker rmi bistromgt:$ver