-- Insert users
INSERT INTO users (id, email, password, active, created_at, last_modified) VALUES
    (1, 'john.doe@test.com', '$2a$10$QXUVWMdqJ5XnrFpKbPqlF.B8JGQCf2gHJ1Kx1k8NzMjVjPqK3VqOu', true, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
    (2, 'jane.smith@test.com', '$2a$10$RYVXWNerK6YosFqLcRqmG.C9KHRDg3iI2Lx2l9ONaMkWkQrL4WrPv', false, '2024-01-02 11:00:00', '2024-01-02 11:00:00'),
    (3, 'mike.johnson@test.com', '$2a$10$SZWYXOfsL7ZpsTrMdSsnH.D0LISEh4jJ3My3m0PObnNlRsM5XsQw', true, '2024-01-03 12:00:00', '2024-01-03 12:00:00'),
    (4, 'sarah.wilson@test.com', '$2a$10$TaXZYPgtM8aqtUsNeStnoI.E1MJTFi5kK4Nz4n1PQcoOmStN6YtRx', false, '2024-01-04 13:00:00', '2024-01-04 13:00:00'),
    (5, 'david.brown@test.com', '$2a$10$UbYaZQhuN9brtVtOfTuopJ.F2NKUGj6lL5O05o2QRdpPnTuO7ZuSy', true, '2024-01-05 14:00:00', '2024-01-05 14:00:00');

-- Insert user_details
INSERT INTO user_details (user_id, first_name, last_name, age, gender, marital_status) VALUES
    (1, 'John', 'Doe', 28, 'MALE', 'SINGLE'),
    (2, 'Jane', 'Smith', 32, 'FEMALE', 'MARRIED'),
    (3, 'Mike', 'Johnson', 25, 'MALE', 'SINGLE'),
    (4, 'Sarah', 'Wilson', 29, 'FEMALE', 'DIVORCED'),
    (5, 'David', 'Brown', 35, 'MALE', 'MARRIED');

-- Insert residential_details
INSERT INTO residential_details (user_id, address, city, state, country, contact_no1, contact_no2) VALUES
    (1, '123 Main Street', 'New York', 'NY', 'USA', '+1-555-0101', '+1-555-0102'),
    (2, '456 Oak Avenue', 'Los Angeles', 'CA', 'USA', '+1-555-0201', NULL),
    (3, '789 Pine Road', 'Chicago', 'IL', 'USA', '+1-555-0301', '+1-555-0302'),
    (4, '321 Elm Street', 'Houston', 'TX', 'USA', '+1-555-0401', '+1-555-0402'),
    (5, '654 Maple Drive', 'Phoenix', 'AZ', 'USA', '+1-555-0501', NULL);

-- Insert official_details
INSERT INTO official_details (user_id, employee_code, address, city, state, country, company_contact_no, company_contact_email, company_name) VALUES
    (1, 'EMP001', '100 Corporate Blvd', 'New York', 'NY', 'USA', '+1-555-1001', 'hr@techcorp.com', 'TechCorp Inc'),
    (2, 'EMP002', '200 Business Park', 'Los Angeles', 'CA', 'USA', '+1-555-1002', 'hr@innovate.com', 'Innovate Solutions'),
    (3, 'EMP003', '300 Enterprise Way', 'Chicago', 'IL', 'USA', '+1-555-1003', 'hr@startuptech.com', 'StartupTech LLC'),
    (4, 'EMP004', '400 Commerce St', 'Houston', 'TX', 'USA', '+1-555-1004', 'hr@globalfirm.com', 'Global Firm Ltd'),
    (5, 'EMP005', '500 Industry Ave', 'Phoenix', 'AZ', 'USA', '+1-555-1005', 'hr@megacorp.com', 'MegaCorp Industries');

-- Insert followers relationships
INSERT INTO followers (followed_user, following_user) VALUES
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 4),
    (3, 1),
    (3, 5),
    (4, 2),
    (4, 5),
    (5, 3),
    (5, 4);

