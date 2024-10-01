create table posts.t_likes (
    id serial primary key,
    c_user_id bigint not null,
    c_post_id bigint not null,
    unique (c_user_id, c_post_id),
    foreign key (c_post_id) references posts.t_posts(id) on delete cascade
);

create index idx_post_id on posts.t_likes(c_post_id);
create index idx_user_id on posts.t_likes(c_user_id);
