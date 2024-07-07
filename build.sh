#!/bin/bash

set -e

echo "---------------------------------"
echo "BUILDING SPRING BOOT"
echo "---------------------------------"
./gradlew build
echo "---------------------------------"
echo "BUILDING DOCKER COMPOSE"
echo "---------------------------------"
docker build -t vinothsridhar/parrot .