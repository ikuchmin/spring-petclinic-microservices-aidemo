-- HSQLDB schema migration for visits service

CREATE TABLE visits (
    id INTEGER IDENTITY PRIMARY KEY,
    pet_id INTEGER NOT NULL,
    visit_date DATE,
    description VARCHAR(8192)
);

CREATE INDEX idx_visits_pet_id ON visits (pet_id);
