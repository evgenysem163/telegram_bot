create table if not exists notification_task(
 id bigserial primary key,
 chat_id bigint,
 message_text varchar(250),
 local_date_time timestamp
);