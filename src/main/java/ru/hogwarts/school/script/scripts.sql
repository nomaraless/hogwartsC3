SELECT * FROM student;

select * from student where age > 0 and age < 5

select name from student;

select * from student where "name" like '%t%';

select * from student where age < id ;

select * from student order by age ;

select student.name, student.id, faculty.id from student, faculty
where student.faculty_id = faculty.id
and faculty.id = 1;

select faculty.id, faculty.color, faculty."name" from student, faculty
where student.faculty_id = faculty.id
and student.id = 4;
