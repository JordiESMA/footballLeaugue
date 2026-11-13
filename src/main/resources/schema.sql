-- Remove columns from the original Team model after Hibernate updates/creates the schema.
ALTER TABLE TEAM DROP COLUMN IF EXISTS CITY;
ALTER TABLE TEAM DROP COLUMN IF EXISTS FOUNDATION_YEAR;
