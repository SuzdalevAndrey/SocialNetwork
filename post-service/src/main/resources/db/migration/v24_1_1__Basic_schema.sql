create schema if not exists posts;

create table posts.t_posts(
    id serial primary key,
    c_content varchar(1000) not null check (length(trim(c_content))>0),
    c_number_views BIGINT DEFAULT 0,
    c_date_create TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    c_user_id BIGINT not null
);