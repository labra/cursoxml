# Database schema for courses
 
# --- !Ups


CREATE SEQUENCE course_id_seq start with 1000;

CREATE TABLE course (
    id 				bigint NOT NULL DEFAULT nextval('course_id_seq'),
    courseName 		varchar(500) not null,
    constraint 		pk_course primary key (id)
);


# --- !Downs

DROP TABLE if exists course;

DROP SEQUENCE if exists course_id_seq;
