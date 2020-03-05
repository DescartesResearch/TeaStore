FROM mariadb:10.4.6
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

# The setup.sql must be run before the data sql file. The container will execute the files in alphabetical order
COPY setup.sql /docker-entrypoint-initdb.d/setup_0.sql
# Uncomment the following line if the teadb schema should come with a standard initialized data set
# COPY setupData.sql /docker-entrypoint-initdb.d/setup_1.sql
RUN sed -i "s/max_connections.*/max_connections = 2048/g" /etc/mysql/my.cnf

ENV MYSQL_ROOT_PASSWORD rootpassword

EXPOSE 3306
