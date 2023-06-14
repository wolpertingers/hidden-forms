#!/bin/bash
mvn clean package -Pproduction
docker compose up --build --detach
