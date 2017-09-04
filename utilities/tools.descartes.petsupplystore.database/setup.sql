CREATE DATABASE petsupplydb;
CREATE USER 'petsupplyuser'@'%' IDENTIFIED BY 'petsupplypassword';
GRANT ALL PRIVILEGES ON petsupplydb.* TO 'petsupplyuser'@'%';
FLUSH PRIVILEGES;
