create database flower;
USE flower;

CREATE TABLE users (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(32) DEFAULT NULL,
  age int,
  gender char(1),
  PRIMARY KEY (id)
);

insert into users(name,age,gender) values("LiZhihui",11,'M');

create table orders
(
  id          int auto_increment
    primary key,
  totalPrice  decimal(10, 2)                     null,
  goodsNumber int                                null,
  state       int                                null,
  customerId  int                                null,
  remark      varchar(255)                       null,
  createTime  datetime default CURRENT_TIMESTAMP null
);

INSERT INTO orders(totalPrice,goodsNumber,state,customerId,remark)
values (100,1,1,1,'订单');

create table goods
(
  id          int auto_increment
    primary key,
  price       decimal(10, 2) null,
  description varchar(255)   null
);

INSERT INTO goods(price,description) VALUES (100,'100元的商品');

create table goods
(
  id          int auto_increment
    primary key,
  price       decimal(10, 2) null,
  description varchar(255)   null
);

INSERT INTO goods_recommend(goodsId,customerId,reason) VALUES (1,1,'多次查看');
