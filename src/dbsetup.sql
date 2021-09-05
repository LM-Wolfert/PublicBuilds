
CREATE TABLE IF NOT EXISTS player_data
(
	uuid		CHAR(36)		NOT NULL, 
	name		VARCHAR(20)		NOT NULL,
	b_points	INT				NOT NULL,
	last_join	BIGINT			NOT NULL,
	role		VARCHAR(20)		NOT NULL,
	last_submit	BIGINT			NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS tutorial_data
(
	uuid 		CHAR(36)		NOT NULL,
	type		INT				NOT NULL,
	stage		INT				NOT NULL,
	first_time	TINYINT(1)		NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS plot_data
(
	id			INT				NOT NULL,
	uuid		CHAR(36)		NOT NULL,
	status		TEXT			NOT NULL,
	last_enter	BIGINT			NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS deny_data
(
	id			INT				NOT NULL
	uuid		CHAR(36)		NOT NULL,
	feedback	TEXT			NOT NULL,
	type		VARCHAR(20)		NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS accept_data
(
	id			INT				NOT NULL,
	uuid		CHAR(36)		NOT NULL,
	points		INT				NOT NULL, 
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS area_data
(
	id			INT				NOT NULL,
	name		VARCHAR(64)		NOT NULL,
	type		VARCHAR(32)		NOT NULL,
	status		VARCHAR(32)		NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS submit_data
(
	id			INT				NOT NULL,
	PRIMARY KEY (id)
);
