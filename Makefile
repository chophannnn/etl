build:
	docker build -t chophannnn/hadoop:3.3.6 ./hadoop/
	docker build -t chophannnn/hive:3.1.2 ./hive/
	docker build -t chophannnn/spark:3.3.0 ./spark/
	docker build -t chophannnn/mysql:8.0.37 ./mysql

compose:
	docker compose up -d

start:
	docker start hadoop
	docker start hive
	docker start spark
	docker start mysql

tag:
	docker tag chophannnn/hadoop:3.3.6 chophannnn/hadoop:3.3.6
	docker tag chophannnn/hive:3.1.2 chophannnn/hive:3.1.2
	docker tag chophannnn/spark:3.3.0 chophannnn/spark:3.3.0
	docker tag chophannnn/mysql:8.0.37 chophannnn/mysql:8.0.37
	
push:
	docker push chophannnn/hadoop:3.3.6
	docker push chophannnn/hive:3.1.2
	docker push chophannnn/spark:3.3.0
	docker push chophannnn/mysql:8.0.37

pull:
	docker pull chophannnn/hadoop:3.3.6
	docker pull chophannnn/hive:3.1.2
	docker pull chophannnn/spark:3.3.0
	docker pull chophannnn/mysql:8.0.37
