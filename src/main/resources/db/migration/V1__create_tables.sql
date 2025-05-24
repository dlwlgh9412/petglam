drop table if exists tb_users;
drop table if exists tb_roles;
drop table if exists tb_user_roles;
drop table if exists tb_user_social_accounts;
drop table if exists tb_app_version;
drop table if exists tb_pets;
drop table if exists tb_salons;
drop table if exists tb_salon_services;
drop table if exists tb_salon_bookings;
drop table if exists tb_salon_booking_services;
drop table if exists tb_salon_images;
drop table if exists tb_salon_review_images;
drop table if exists tb_salon_reviews;
drop table if exists tb_salon_staff;
drop table if exists tb_banners;

CREATE TABLE tb_users
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    email             VARCHAR(255) NOT NULL UNIQUE,
    password          VARCHAR(255),
    name              VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(1024),
    is_email_verified BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP on update current_timestamp,
    last_login_at     TIMESTAMP,
    refresh_token     VARCHAR(512)
);

create table tb_roles
(
    id   int auto_increment primary key,
    name varchar(20) not null unique
);

insert into tb_roles (name)
values ('ROLE_ADMIN'),
       ('ROLE_USER'),
       ('ROLE_SHOP_OWNER');

create table tb_user_roles
(
    id      bigint auto_increment primary key,
    user_id bigint not null,
    role_id bigint not null,
    unique unique_user_role (user_id, role_id)
);

CREATE TABLE tb_user_social_accounts
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    provider      VARCHAR(50)  NOT NULL,
    provider_id   VARCHAR(255) NOT NULL,
    access_token  VARCHAR(1024),
    refresh_token VARCHAR(1024),
    token_expiry  TIMESTAMP,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP on update current_timestamp,
    CONSTRAINT uk_provider_provider_id UNIQUE (provider, provider_id),
    CONSTRAINT uk_user_provider UNIQUE (user_id, provider)
);

CREATE INDEX idx_user_social_connections_user_id ON tb_user_social_accounts (user_id);
CREATE INDEX idx_user_social_connections_provider ON tb_user_social_accounts (provider);

CREATE TABLE tb_app_version
(
    id                    int primary key auto_increment,
    os                    varchar(10) not null,
    min_supported_version varchar(20) not null,
    description           text,
    is_active             boolean              default true not null,
    created_at            timestamp   not null default current_timestamp,
    updated_at            timestamp   not null default current_timestamp on update current_timestamp
);

create index idx_app_version_os on tb_app_version (os);

create table tb_pets
(
    id                bigint auto_increment primary key,
    user_id           bigint       not null,
    name              varchar(255) not null,
    breed             varchar(20)  not null,
    birth_date        date,
    weight            decimal(5, 2),
    gender            varchar(20),
    neutered_status   boolean      not null default false comment '중성화 여부',
    special_notes     text comment '특이사항',
    profile_image_url varchar(255),
    created_at        timestamp    not null default current_timestamp,
    updated_at        timestamp    not null default current_timestamp on update current_timestamp,
    index idx_pet_user_id (user_id)
);

create table tb_salons
(
    id             bigint auto_increment primary key,
    user_id        bigint          not null,
    name           varchar(255)    not null,
    contact        varchar(255)    not null,
    description    text,
    street_address varchar(255)    not null,
    city           varchar(255)    not null,
    district       varchar(255)    not null,
    postal_code    varchar(20)     not null,
    location       point srid 4326 not null comment '위치 좌표 (경도, 위도)',
    image_url      varchar(255)    not null,
    created_at     timestamp       not null default current_timestamp,
    updated_at     timestamp       not null default current_timestamp on update current_timestamp,
    spatial index idx_salon_location (location),
    index idx_salon_user_id (user_id),
    index idx_salon_city_district (city, district)
);

create table tb_salon_services
(
    id                  bigint auto_increment primary key,
    salon_id            bigint         not null,
    service_name        varchar(255)   not null,
    service_description text,
    service_price       decimal(10, 2) not null,
    is_offered          tinyint(1) default 1 comment '서비스 제공 여부',
    index idx_salon_service_foreign (salon_id)
);

create table tb_salon_bookings
(
    partition_key     int         not null comment '파티션키 (202505)',
    booking_date_time timestamp   not null,
    user_id           bigint      not null,
    salon_id          bigint      not null,
    staff_id          bigint      not null,
    status            varchar(20) not null comment '예약 상태(REQUESTED, CONFIRMED, CANCELLED_BY_USER, CANCELLED_BY_SHOP, COMPLETED, NO_SHOW)',
    total_price       decimal(10, 2),
    created_at        timestamp   not null default current_timestamp,
    updated_at        timestamp   not null default current_timestamp on update current_timestamp,
    primary key (partition_key, booking_date_time, user_id, salon_id, staff_id),
    index idx_salon_booking_user (user_id, partition_key, booking_date_time),
    index idx_salon_booking_salon (salon_id, partition_key, booking_date_time),
    index idx_salon_booking_staff (staff_id, partition_key, booking_date_time)
)
    partition by range (partition_key) (
        partition p202505 values less than (202506),
        partition p202506 values less than (202507),
        partition p202507 values less than (202508),
        partition p202508 values less than (202509),
        partition p202509 values less than (202510),
        partition p202510 values less than (202511),
        partition p202511 values less than (202512),
        partition p202512 values less than (202601),
        partition p202601 values less than (202602),
        partition p202602 values less than (202603),
        partition p202603 values less than (202604),
        partition p202604 values less than (202605),
        partition p202605 values less than (202606),
        partition p202606 values less than (202607),
        partition p202607 values less than (202608),
        partition p202608 values less than (202609),
        partition p202609 values less than (202610),
        partition p202610 values less than (202611),
        partition p202611 values less than (202612),
        partition p202612 values less than (202701)
        );

create table tb_salon_booking_services
(
    partition_key     int            not null comment '파티션키 (202505)',
    booking_date_time timestamp      not null,
    user_id           bigint         not null,
    salon_id          bigint         not null,
    staff_id          bigint         not null,
    service_id        bigint         not null,
    quantity          int            not null default 1,
    price_at_booking  decimal(10, 2) not null,
    primary key (partition_key, booking_date_time, user_id, salon_id, staff_id, service_id)
)
    partition by range (partition_key) (
        partition p202505 values less than (202506),
        partition p202506 values less than (202507),
        partition p202507 values less than (202508),
        partition p202508 values less than (202509),
        partition p202509 values less than (202510),
        partition p202510 values less than (202511),
        partition p202511 values less than (202512),
        partition p202512 values less than (202601),
        partition p202601 values less than (202602),
        partition p202602 values less than (202603),
        partition p202603 values less than (202604),
        partition p202604 values less than (202605),
        partition p202605 values less than (202606),
        partition p202606 values less than (202607),
        partition p202607 values less than (202608),
        partition p202608 values less than (202609),
        partition p202609 values less than (202610),
        partition p202610 values less than (202611),
        partition p202611 values less than (202612),
        partition p202612 values less than (202701)
        );

create table tb_salon_images
(
    id          bigint auto_increment primary key,
    salon_id    bigint       not null,
    image_url   varchar(255) not null,
    uploaded_at timestamp default current_timestamp on update current_timestamp,
    index idx_salon_image_salon_id (salon_id)
);

create table tb_salon_reviews
(
    id         bigint auto_increment primary key,
    salon_id   bigint not null,
    staff_id   bigint,
    user_id    bigint not null,
    comment    text,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp,
    index idx_salon_review_salon_id (salon_id),
    index idx_salon_review_user_id (user_id)
);

create table tb_salon_review_images
(
    id          bigint auto_increment primary key,
    review_id   bigint       not null,
    image_url   varchar(255) not null,
    uploaded_at timestamp default current_timestamp on update current_timestamp,
    index idx_salon_review_image_review_id (review_id)
);

create table tb_salon_staff
(
    id                bigint auto_increment primary key,
    salon_id          bigint,
    name              varchar(255) not null,
    position          varchar(255),
    profile_image_url varchar(255) not null,
    is_active         tinyint(1) default 1,
    created_at        timestamp  default current_timestamp,
    updated_at        timestamp  default current_timestamp on update current_timestamp,
    index idx_salon_staff_salon_id (salon_id)
);

create table tb_banners
(
    id            bigint auto_increment primary key,
    image_url     varchar(255)         not null,
    target_url    varchar(255)         not null,
    title         varchar(255)         not null,
    description   varchar(255)         not null,
    is_active     tinyint(1) default 0 not null,
    banner_type   varchar(10)          not null comment '배너 타입(이미지, 동영상 등)',
    display_order int                  not null,
    created_at    timestamp  default current_timestamp,
    updated_at    timestamp  default current_timestamp on update current_timestamp
);
