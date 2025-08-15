-- HSQL schema migration for customers service

CREATE TABLE types (
    id INTEGER IDENTITY,
    name VARCHAR(80),
    CONSTRAINT pk_types PRIMARY KEY (id)
);

CREATE INDEX idx_types_name ON types (name);

CREATE TABLE owners (
    id INTEGER IDENTITY,
    first_name VARCHAR(30),
    last_name VARCHAR(30),
    address VARCHAR(255),
    city VARCHAR(80),
    telephone VARCHAR(20),
    CONSTRAINT pk_owners PRIMARY KEY (id)
);

CREATE INDEX idx_owners_last_name ON owners (last_name);

CREATE TABLE pets (
    id INTEGER IDENTITY,
    name VARCHAR(30),
    birth_date DATE,
    type_id INTEGER NOT NULL,
    owner_id INTEGER NOT NULL,
    CONSTRAINT pk_pets PRIMARY KEY (id),
    CONSTRAINT fk_pets_on_owner FOREIGN KEY (owner_id) REFERENCES owners (id),
    CONSTRAINT fk_pets_on_type FOREIGN KEY (type_id) REFERENCES types (id)
);

CREATE INDEX idx_pets_name ON pets (name);
