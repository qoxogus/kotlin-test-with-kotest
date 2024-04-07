create table member
(
    id    int unsigned auto_increment,
    email varchar(256) unique not null comment '이메일',
    name  varchar(64)  not null comment '이름',

    created_date_time   DATETIME default current_timestamp() not null,
    updated_date_time   DATETIME default current_timestamp() not null on update current_timestamp(),

    primary key (id)
)collate = utf8mb4_unicode_ci;


insert into member
values
    (1, 'email', 'name', current_time(), current_time())