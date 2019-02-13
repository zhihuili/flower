create database flower;

CREATE TABLE users (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(32) DEFAULT NULL,
  age int,
  gender char(1),
  PRIMARY KEY (id)
);

insert into users(name,age,gender) values("LiZhihui",11,'M');