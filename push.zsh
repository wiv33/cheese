#!/bin/zsh
docker build --platform=linux/amd64 -t psawesome/cheese-web:"$1" . && docker push psawesome/cheese-web:"$1"
