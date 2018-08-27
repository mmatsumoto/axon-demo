CREATE TABLE if not exists account (
  id varchar(255) not null,
  name varchar(255) not null,
  gender varchar(255) not null,
  balance bigint not null,
	status varchar(255) not null,
  primary key (id)
);

CREATE TABLE if not exists bank_transfer (
	transaction_id varchar(255) not null,
	source_id varchar(255) not null,
	destination_id varchar(255) not null,
	amount bigint not null,
	status varchar(255) not null,
	primary key (transaction_id)
);

CREATE TABLE if not exists association_value_entry
(
	id bigint not null,
	association_key varchar(255) not null,
	association_value varchar(255),
	saga_id varchar(255) not null,
	saga_type varchar(255),

	primary key (id)
);

create index if not exists idx_association_value_entry
	on association_value_entry (saga_type, association_key, association_value);

create index if not exists idx_association_value_entry
	on association_value_entry (saga_id, saga_type);

CREATE TABLE if not exists domain_event_entry
(
	global_index bigint not null
		constraint domain_event_entry_pkey
			primary key,
	event_identifier varchar(255) not null
		constraint uk_fwe6lsa8bfo6hyas6ud3m8c7x
			unique,
	meta_data BYTEA,
	payload BYTEA not null,
	payload_revision varchar(255),
	payload_type varchar(255) not null,
	time_stamp varchar(255) not null,
	aggregate_identifier varchar(255) not null,
	sequence_number bigint not null,
	type varchar(255),
	constraint uk8s1f994p4la2ipb13me2xqm1w
		unique (aggregate_identifier, sequence_number)
);

CREATE TABLE if not exists saga_entry
(
	saga_id varchar(255) not null
		constraint saga_entry_pkey
			primary key,
	revision varchar(255),
	saga_type varchar(255),
	serialized_saga BYTEA
);

CREATE TABLE if not exists snapshot_event_entry
(
	aggregate_identifier varchar(255) not null,
	sequence_number bigint not null,
	type varchar(255) not null,
	event_identifier varchar(255) not null
		constraint uk_e1uucjseo68gopmnd0vgdl44h
			unique,
	meta_data BYTEA,
	payload BYTEA not null,
	payload_revision varchar(255),
	payload_type varchar(255) not null,
	time_stamp varchar(255) not null,
	constraint snapshot_event_entry_pkey
		primary key (aggregate_identifier, sequence_number, type)
);

CREATE TABLE if not exists token_entry
(
	processor_name varchar(255) not null,
	segment integer not null,
	owner varchar(255),
	timestamp varchar(255) not null,
	token BYTEA,
	token_type varchar(255),
	constraint token_entry_pkey
		primary key (processor_name, segment)
);

CREATE SEQUENCE IF NOT EXISTS domain_event_entry_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS association_value_entry_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

-- QUARTZ

DROP TABLE IF EXISTS qrtz_fired_triggers;
DROP TABLE IF EXISTS qrtz_paused_trigger_grps;
DROP TABLE IF EXISTS qrtz_scheduler_state;
DROP TABLE IF EXISTS qrtz_locks;
DROP TABLE IF EXISTS qrtz_simple_triggers;
DROP TABLE IF EXISTS qrtz_cron_triggers;
DROP TABLE IF EXISTS qrtz_simprop_triggers;
DROP TABLE IF EXISTS qrtz_blob_triggers;
DROP TABLE IF EXISTS qrtz_triggers;
DROP TABLE IF EXISTS qrtz_job_details;
DROP TABLE IF EXISTS qrtz_calendars;

CREATE TABLE qrtz_job_details
(
	sched_name varchar(120) not null,
	job_name  varchar(200) not null,
	job_group varchar(200) not null,
	description varchar(250) null,
	job_class_name   varchar(250) not null,
	is_durable bool not null,
	is_nonconcurrent bool not null,
	is_update_data bool not null,
	requests_recovery bool not null,
	job_data bytea null,
	primary key (sched_name,job_name,job_group)
);

CREATE TABLE qrtz_triggers
(
	sched_name varchar(120) not null,
	trigger_name varchar(200) not null,
	trigger_group varchar(200) not null,
	job_name  varchar(200) not null,
	job_group varchar(200) not null,
	description varchar(250) null,
	next_fire_time bigint null,
	prev_fire_time bigint null,
	priority integer null,
	trigger_state varchar(16) not null,
	trigger_type varchar(8) not null,
	start_time bigint not null,
	end_time bigint null,
	calendar_name varchar(200) null,
	misfire_instr smallint null,
	job_data bytea null,
	primary key (sched_name,trigger_name,trigger_group),
	foreign key (sched_name,job_name,job_group)
	references qrtz_job_details(sched_name,job_name,job_group)
);

CREATE TABLE qrtz_simple_triggers
(
	sched_name varchar(120) not null,
	trigger_name varchar(200) not null,
	trigger_group varchar(200) not null,
	repeat_count bigint not null,
	repeat_interval bigint not null,
	times_triggered bigint not null,
	primary key (sched_name,trigger_name,trigger_group),
	foreign key (sched_name,trigger_name,trigger_group)
	references qrtz_triggers(sched_name,trigger_name,trigger_group)
);

CREATE TABLE qrtz_cron_triggers
(
	sched_name varchar(120) not null,
	trigger_name varchar(200) not null,
	trigger_group varchar(200) not null,
	cron_expression varchar(120) not null,
	time_zone_id varchar(80),
	primary key (sched_name,trigger_name,trigger_group),
	foreign key (sched_name,trigger_name,trigger_group)
	references qrtz_triggers(sched_name,trigger_name,trigger_group)
);

CREATE TABLE qrtz_simprop_triggers
(
	sched_name varchar(120) not null,
	trigger_name varchar(200) not null,
	trigger_group varchar(200) not null,
	str_prop_1 varchar(512) null,
	str_prop_2 varchar(512) null,
	str_prop_3 varchar(512) null,
	int_prop_1 int null,
	int_prop_2 int null,
	long_prop_1 bigint null,
	long_prop_2 bigint null,
	dec_prop_1 numeric(13,4) null,
	dec_prop_2 numeric(13,4) null,
	bool_prop_1 bool null,
	bool_prop_2 bool null,
	primary key (sched_name,trigger_name,trigger_group),
	foreign key (sched_name,trigger_name,trigger_group)
	references qrtz_triggers(sched_name,trigger_name,trigger_group)
);

CREATE TABLE qrtz_blob_triggers
(
	sched_name varchar(120) not null,
	trigger_name varchar(200) not null,
	trigger_group varchar(200) not null,
	blob_data bytea null,
	primary key (sched_name,trigger_name,trigger_group),
	foreign key (sched_name,trigger_name,trigger_group)
	references qrtz_triggers(sched_name,trigger_name,trigger_group)
);

CREATE TABLE qrtz_calendars
(
	sched_name varchar(120) not null,
	calendar_name  varchar(200) not null,
	calendar bytea not null,
	primary key (sched_name,calendar_name)
);


CREATE TABLE qrtz_paused_trigger_grps
(
	sched_name varchar(120) not null,
	trigger_group  varchar(200) not null,
	primary key (sched_name,trigger_group)
);

CREATE TABLE qrtz_fired_triggers
(
	sched_name varchar(120) not null,
	entry_id varchar(95) not null,
	trigger_name varchar(200) not null,
	trigger_group varchar(200) not null,
	instance_name varchar(200) not null,
	fired_time bigint not null,
	sched_time bigint not null,
	priority integer not null,
	state varchar(16) not null,
	job_name varchar(200) null,
	job_group varchar(200) null,
	is_nonconcurrent bool null,
	requests_recovery bool null,
	primary key (sched_name,entry_id)
);

CREATE TABLE qrtz_scheduler_state
(
	sched_name varchar(120) not null,
	instance_name varchar(200) not null,
	last_checkin_time bigint not null,
	checkin_interval bigint not null,
	primary key (sched_name,instance_name)
);

CREATE TABLE qrtz_locks
(
	sched_name varchar(120) not null,
	lock_name  varchar(40) not null,
	primary key (sched_name,lock_name)
);

create index idx_qrtz_j_req_recovery on qrtz_job_details(sched_name,requests_recovery);
create index idx_qrtz_j_grp on qrtz_job_details(sched_name,job_group);

create index idx_qrtz_t_j on qrtz_triggers(sched_name,job_name,job_group);
create index idx_qrtz_t_jg on qrtz_triggers(sched_name,job_group);
create index idx_qrtz_t_c on qrtz_triggers(sched_name,calendar_name);
create index idx_qrtz_t_g on qrtz_triggers(sched_name,trigger_group);
create index idx_qrtz_t_state on qrtz_triggers(sched_name,trigger_state);
create index idx_qrtz_t_n_state on qrtz_triggers(sched_name,trigger_name,trigger_group,trigger_state);
create index idx_qrtz_t_n_g_state on qrtz_triggers(sched_name,trigger_group,trigger_state);
create index idx_qrtz_t_next_fire_time on qrtz_triggers(sched_name,next_fire_time);
create index idx_qrtz_t_nft_st on qrtz_triggers(sched_name,trigger_state,next_fire_time);
create index idx_qrtz_t_nft_misfire on qrtz_triggers(sched_name,misfire_instr,next_fire_time);
create index idx_qrtz_t_nft_st_misfire on qrtz_triggers(sched_name,misfire_instr,next_fire_time,trigger_state);
create index idx_qrtz_t_nft_st_misfire_grp on qrtz_triggers(sched_name,misfire_instr,next_fire_time,trigger_group,trigger_state);

create index idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(sched_name,instance_name);
create index idx_qrtz_ft_inst_job_req_rcvry on qrtz_fired_triggers(sched_name,instance_name,requests_recovery);
create index idx_qrtz_ft_j_g on qrtz_fired_triggers(sched_name,job_name,job_group);
create index idx_qrtz_ft_jg on qrtz_fired_triggers(sched_name,job_group);
create index idx_qrtz_ft_t_g on qrtz_fired_triggers(sched_name,trigger_name,trigger_group);
create index idx_qrtz_ft_tg on qrtz_fired_triggers(sched_name,trigger_group);

