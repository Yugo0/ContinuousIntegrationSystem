CREATE TABLE controller_db.public.tester (
	id serial PRIMARY KEY,
	address VARCHAR(255) UNIQUE NOT NULL,
	level INTEGER NOT NULL,
	request_retry_count INTEGER NOT NULL,
	available BOOL NOT NULL
);
CREATE TABLE controller_db.public.test (
	id serial PRIMARY KEY,
	repo_url VARCHAR(255) NOT NULL,
	branch_name VARCHAR(255),
	email VARCHAR(255) NOT NULL,
	webhook_address VARCHAR(255) NOT NULL,
	finished BOOL NOT NULL,
	succeeded BOOL NOT NULL,
	tester_id INTEGER NOT NULL,
	CONSTRAINT fk_tester
        FOREIGN KEY(tester_id) 
	  	    REFERENCES controller_db.public.tester(id)
);