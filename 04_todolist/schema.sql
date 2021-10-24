create table TasksLists (
	list_id int generated always as identity, 
	name varchar(50) not null,
    primary key(list_id)
);

create table Tasks (
	list_id int not null, 
    task_id int generated always as identity,
	name varchar(50) not null,
    description varchar(255) not null,
    done boolean not null,
    primary key(task_id),
	constraint task_owner foreign key(list_id)
		references TasksLists(list_id)
);