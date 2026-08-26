INSERT INTO roles(id,role_name) VALUES ('a1cec8bb-1cf1-40c1-9ab1-2a393af385e4','OPERATOR');
    INSERT INTO users (id,username, password, email, first_name, last_name, role_id, enabled)
VALUES (
           'a1cec8bb-1cf1-40c1-9ab1-2a492af385e4',
           'operator',
           '$2a$10$npTAaAL1DzXN5Rmqbs5QUepMLZqYBoGtJq91IXJOvAMvdPTz0a7Ey', -- password is 'operator123'
           'operator@example.com',
           'System',
           'Operator',
           'a1cec8bb-1cf1-40c1-9ab1-2a393af385e4',
           true
       ) ON CONFLICT (username) DO NOTHING;
