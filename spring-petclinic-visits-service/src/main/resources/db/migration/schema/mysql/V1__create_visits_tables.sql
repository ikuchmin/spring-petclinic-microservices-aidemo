-- MySQL schema migration for visits service

CREATE TABLE visits (
    id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,
    pet_id INT(4) UNSIGNED NOT NULL,
    visit_date DATE,
    description VARCHAR(8192),
    CONSTRAINT pk_visits PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE INDEX idx_visits_pet_id ON visits (pet_id);
