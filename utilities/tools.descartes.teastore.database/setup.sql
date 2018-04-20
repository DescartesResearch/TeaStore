CREATE DATABASE teadb;
CREATE USER 'teauser'@'%' IDENTIFIED BY 'teapassword';
GRANT ALL PRIVILEGES ON teadb.* TO 'teauser'@'%';
FLUSH PRIVILEGES;
