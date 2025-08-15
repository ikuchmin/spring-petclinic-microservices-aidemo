CREATE TABLE vet_specialties
(
    specialty_id INT NOT NULL,
    vet_id       INT NOT NULL,
    CONSTRAINT pk_vet_specialties PRIMARY KEY (specialty_id, vet_id)
);

CREATE TABLE specialties
(
    id   INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)       NULL,
    CONSTRAINT pk_specialties PRIMARY KEY (id)
);

CREATE TABLE vets
(
    id         INT AUTO_INCREMENT NOT NULL,
    first_name VARCHAR(255)       NULL,
    last_name  VARCHAR(255)       NULL,
    CONSTRAINT pk_vets PRIMARY KEY (id)
);

ALTER TABLE vet_specialties
    ADD CONSTRAINT fk_vetspe_on_specialty FOREIGN KEY (specialty_id) REFERENCES specialties (id);

ALTER TABLE vet_specialties
    ADD CONSTRAINT fk_vetspe_on_vet FOREIGN KEY (vet_id) REFERENCES vets (id);
