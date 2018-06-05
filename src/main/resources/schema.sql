create table if not exists account (
  id varchar(255) not null,
  name varchar(255) not null,
  balance bigint not null,
  primary key (id)
);

create table if not exists bank_transfer (
	transaction_id varchar(255) not null,
	source_id varchar(255) not null,
	destination_id varchar(255) not null,
	amount bigint not null,
	status varchar(255) not null,
	primary key (transaction_id)
);

create table if not exists association_value_entry
(
	id bigint not null
		constraint association_value_entry_pkey
			primary key,
	association_key varchar(255) not null,
	association_value varchar(255),
	saga_id varchar(255) not null,
	saga_type varchar(255)
)
;

create index if not exists idxk45eqnxkgd8hpdn6xixn8sgft
	on association_value_entry (saga_type, association_key, association_value)
;

create index if not exists idxgv5k1v2mh6frxuy5c0hgbau94
	on association_value_entry (saga_id, saga_type)
;

create table if not exists domain_event_entry
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
)
;

create table if not exists saga_entry
(
	saga_id varchar(255) not null
		constraint saga_entry_pkey
			primary key,
	revision varchar(255),
	saga_type varchar(255),
	serialized_saga BYTEA
)
;

create table if not exists snapshot_event_entry
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
)
;

create table if not exists token_entry
(
	processor_name varchar(255) not null,
	segment integer not null,
	owner varchar(255),
	timestamp varchar(255) not null,
	token BYTEA,
	token_type varchar(255),
	constraint token_entry_pkey
		primary key (processor_name, segment)
)
;

create sequence if not exists hibernate_sequence;
CREATE SEQUENCE IF NOT EXISTS domain_event_entry_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS association_value_entry_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;




