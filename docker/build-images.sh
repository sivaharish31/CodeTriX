#!/bin/bash

set -e

echo "Building CodeTriX execution images..."

echo "Building C image..."
docker build -t codetrix-c ./c

echo "Building C++ image..."
docker build -t codetrix-cpp ./cpp

echo "Building Java image..."
docker build -t codetrix-java ./java

echo "Building Python image..."
docker build -t codetrix-python ./python

echo ""
echo "All images built successfully!"
echo ""
docker images | grep codetrix
