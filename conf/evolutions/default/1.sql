# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "stickers" ("sticker_version" INTEGER NOT NULL,"package_id" INTEGER NOT NULL,"sticker_id" INTEGER NOT NULL,"url" VARCHAR NOT NULL);
alter table "stickers" add constraint "pk_stickers" primary key("sticker_version","package_id","sticker_id");

# --- !Downs

alter table "stickers" drop constraint "pk_stickers";
drop table "stickers";

