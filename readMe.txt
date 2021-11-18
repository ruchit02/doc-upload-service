docker run -p 3306:3306 \
--memory="300m" \
--memory-swap="1g" \
--security-opt seccomp=unconfined \
--network proj-net \
--name photo-db-server \
-v "$(PWD):/docker-entrypoint-initdb.d/" \
-v photo-db-data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-e MYSQL_DATABASE=employeedocumentdetails \
mysql:8.0.26


docker run -p 8082:8082 \
--network proj-net \
--name photo-service \
-e MYSQL_USER=root \
-e MYSQL_PASSWORD=root \
-e MYSQL_DATABASE=employeedocumentdetails \
-e MYSQL_URL=jdbc:mysql://photo-db-server/employeedocumentdetails \
-e BROKER_HOST=192.168.99.100 -e BROKER_PORT=5672 \
-e TRANSFER_PROTOCOL=http \
-e GATEWAY_SERVICE_HOST=192.168.99.100 -e GATEWAY_SERVICE_PORT=8081 \
photo-image


