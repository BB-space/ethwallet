create table account
(
  id int NULL PRIMARY KEY AUTO_INCREMENT,
  address VARCHAR(128),
  userId VARCHAR(128),
  amount VARCHAR(128),
  type VARCHAR(128),
  identify VARCHAR(128)
);

create table admin
(
  username VARCHAR(128) primary key,
  password VARCHAR(128)
);

create table syncinfo
(
  `key` VARCHAR(128) primary key,
  value VARCHAR(128)
);

create table token
(
  id INTEGER primary key AUTO_INCREMENT,
  name VARCHAR(128),
  contractAddress VARCHAR(128),
  mainAccount VARCHAR(128),
  mainPassword VARCHAR(128),
  childPassword VARCHAR(128),
  minCount DOUBLE,
  type VARCHAR(128),
  ico VARCHAR(128)
);

create table tx
(
  txid VARCHAR(128) primary key,
  account VARCHAR(128),
  address VARCHAR(128),
  category VARCHAR(128),
  amount VARCHAR(128),
  confirmations VARCHAR(128),
  blockhash VARCHAR(128),
  blockindex VARCHAR(128),
  blocktime VARCHAR(128),
  time VARCHAR(128),
  timereceived VARCHAR(128),
  fee VARCHAR(128),
  identify VARCHAR(128)
);
create table user
(
  username VARCHAR(128) primary key,
  password VARCHAR(128),
  identify VARCHAR(128)
);

insert into admin values ('admin', 'admin');
