#!/bin/bash
./mvnw clean package -Pproduction
docker compose up --build --detach
