--liquibase formated sql

--changeset nomad:1
CREATE INDEX student_name_index ON students (name);

--changeset nomad:2
CREATE INDEX name_color_faculty ON faculty (name, color);