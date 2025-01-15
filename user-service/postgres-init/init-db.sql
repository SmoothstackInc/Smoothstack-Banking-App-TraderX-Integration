BEGIN;

DROP TABLE IF EXISTS public.password_reset_token;
DROP TABLE IF EXISTS public.verification_token;
DROP TABLE IF EXISTS public.app_user;

CREATE TABLE IF NOT EXISTS public.app_user (
                                               user_id SERIAL PRIMARY KEY,
                                               username VARCHAR NOT NULL,
                                               password VARCHAR NOT NULL,
                                               email VARCHAR NOT NULL,
                                               first_name VARCHAR,
                                               last_name VARCHAR,
                                               is_verified BOOLEAN DEFAULT FALSE,
                                               is_active BOOLEAN DEFAULT TRUE,
                                               date_created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                               date_modified TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                               date_of_birth TIMESTAMP WITHOUT TIME ZONE,
                                               phone_number BIGINT,
                                               address VARCHAR,
                                               secret_question VARCHAR,
                                               secret_answer VARCHAR,
                                               role VARCHAR(255),
                                               failed_login_attempts INT DEFAULT 0,
                                               lock_time TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS public.verification_token (
                                                         token_id SERIAL PRIMARY KEY,
                                                         user_id INT REFERENCES public.app_user(user_id) ON DELETE CASCADE,
                                                         token VARCHAR(255) NOT NULL,
                                                         expiration TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                                         is_used BOOLEAN DEFAULT FALSE,
                                                         last_verification_request TIMESTAMP
);

CREATE TABLE IF NOT EXISTS public.password_reset_token (
                                                           token_id SERIAL PRIMARY KEY,
                                                           user_id INT REFERENCES public.app_user(user_id) ON DELETE CASCADE,
                                                           token VARCHAR(255) NOT NULL,
                                                           expiration TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                                           is_used BOOLEAN DEFAULT FALSE,
                                                           last_verification_request TIMESTAMP
);


INSERT INTO public.app_user (username, password, email, first_name, last_name, is_verified, is_active, date_created, date_modified, date_of_birth, phone_number, address, secret_question, secret_answer, role, failed_login_attempts)
VALUES
    ('user3', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user3@example.com', 'John', 'Doe', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1995-03-15', 5555555555, '123 Main St', 'What is your favorite color?', 'Blue', 'CUSTOMER', 0),
    ('Admin', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user4@example.com', 'Jane', 'Smith', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1988-08-25', 6666666666, '456 Elm St', 'What is your favorite movie?', 'Johnson', 'ADMIN', 0),
    ('user5', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user5@example.com', 'Michael', 'Brown', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1977-04-10', 7777777777, '789 Oak St', 'What is your favorite movie?', 'The Matrix', 'CUSTOMER', 0),
    ('user6', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user6@example.com', 'Emily', 'Wilson', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1992-12-05', 8888888888, '101 Pine St', 'What is your favorite food?', 'Pizza', 'CUSTOMER', 0),
    ('user7', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user7@example.com', 'David', 'Lee', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1980-09-20', 9999999999, '202 Cedar St', 'What is your favorite book?', 'To Kill a Mockingbird', 'CUSTOMER', 0),
    ('user8', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user8@example.com', 'Sarah', 'Miller', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1998-07-12', 4444444444, '303 Birch St', 'What is your favorite sport?', 'Basketball', 'CUSTOMER', 0),
    ('user9', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user9@example.com', 'Daniel', 'Garcia', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1985-01-30', 3333333333, '404 Willow St', 'What is your favorite holiday?', 'Christmas', 'CUSTOMER', 0),
    ('user10', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user10@example.com', 'Linda', 'Harris', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1974-06-18', 2222222222, '505 Maple St', 'What is your favorite song?', 'Bohemian Rhapsody', 'CUSTOMER', 0),
    ('user11', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user11@example.com', 'James', 'Anderson', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1991-11-11', 1111111111, '606 Oakwood St', 'What is your favorite hobby?', 'Gardening', 'CUSTOMER', 0),
    ('user12', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user12@example.com', 'Catherine', 'Clark', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1982-03-28', 9999888877, '707 Pinecrest St', 'What is your favorite animal?', 'Dogs', 'CUSTOMER', 0),
    ('user13', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user13@example.com', 'Robert', 'Moore', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1996-05-22', 7777666655, '808 Elmwood St', 'What is your favorite movie?', 'Inception', 'CUSTOMER', 0),
    ('user14', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user14@example.com', 'Melissa', 'Young', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1979-08-14', 5555444333, '909 Willowbrook St', 'What is your favorite color?', 'Green', 'CUSTOMER', 0),
    ('user15', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user15@example.com', 'William', 'Lewis', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1987-12-09', 3333222111, '1010 Cedarwood St', 'What is your favorite book?', 'Harry Potter and the Sorcerers Stone', 'CUSTOMER', 0),
    ('user16', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user16@example.com', 'Amanda', 'Taylor', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1983-04-03', 9999666777, '1111 Oakdale St', 'What is your favorite sport?', 'Football', 'CUSTOMER', 0),
    ('user17', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user17@example.com', 'Daniel', 'Perez', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1994-07-24', 8888555444, '1212 Maplewood St', 'What is your favorite holiday?', 'Thanksgiving', 'CUSTOMER', 0),
    ('user18', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user18@example.com', 'Jessica', 'King', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1999-02-28', 6666111122, '1313 Willow Lane', 'What is your favorite song?', 'Imagine', 'CUSTOMER', 0),
    ('user19', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'user19@example.com', 'Joseph', 'Walker', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1981-10-19', 4444222233, '1414 Oak Lane', 'What is your favorite hobby?', 'Painting', 'CUSTOMER', 0),
    ('Dante', '$2a$10$eEgz5Q3U8Bxz13oLBZHzrukHZ6mJpTAUOuMdPPIkhPglJ806luJ16', 'securesentinelbank@protonmail.com', 'Dnte', 'Martin', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1993-06-07', 2222888899, '1515 Cedar Lane', 'What is your favorite animal?', 'Cats', 'CUSTOMER', 0);

COMMIT;
