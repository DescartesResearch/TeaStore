FROM mariadb:latest
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

# The setup.sql must be run before the data sql file. The container will execute the files in alphabetical order
COPY setup.sql /docker-entrypoint-initdb.d/setup_0.sql
# Uncomment the following line if the petsupplydb schema should come with a standard initialized data set
# COPY setupData.sql /docker-entrypoint-initdb.d/setup_1.sql

ENV MYSQL_ROOT_PASSWORD rootpassword

EXPOSE 3306