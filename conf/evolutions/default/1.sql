# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "folders" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"parent" BIGINT);
create table "projects" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"owner" VARCHAR NOT NULL,"root" BIGINT NOT NULL,"description" VARCHAR);
create unique index "idx_owner_project" on "projects" ("owner","name");
create table "rights" ("project" BIGINT NOT NULL,"user" VARCHAR NOT NULL,"policy" INTEGER NOT NULL);
alter table "rights" add constraint "pk_right" primary key("project","user");
create table "users" ("name" VARCHAR NOT NULL PRIMARY KEY,"email" VARCHAR NOT NULL,"password" VARCHAR NOT NULL,"session" VARCHAR,"timeout" DATE);
alter table "folders" add constraint "fk_folder_parent" foreign key("parent") references "folders"("id") on update NO ACTION on delete NO ACTION;
alter table "projects" add constraint "fk_project_user" foreign key("owner") references "users"("name") on update NO ACTION on delete NO ACTION;
alter table "projects" add constraint "fk_project_folder" foreign key("root") references "folders"("id") on update NO ACTION on delete NO ACTION;
alter table "rights" add constraint "fk_right_project" foreign key("project") references "projects"("id") on update NO ACTION on delete NO ACTION;
alter table "rights" add constraint "fk_right_user" foreign key("user") references "users"("name") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "folders" drop constraint "fk_folder_parent";
alter table "projects" drop constraint "fk_project_user";
alter table "projects" drop constraint "fk_project_folder";
alter table "rights" drop constraint "fk_right_project";
alter table "rights" drop constraint "fk_right_user";
drop table "folders";
drop table "projects";
alter table "rights" drop constraint "pk_right";
drop table "rights";
drop table "users";

