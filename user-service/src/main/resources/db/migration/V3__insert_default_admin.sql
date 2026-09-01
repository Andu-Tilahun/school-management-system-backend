INSERT INTO users (id,username, password, email, first_name, last_name, enabled)
VALUES (
           'a1cec8bb-1cf1-40c2-9ab1-2a392af385e4',
           'admin',
           '$2a$10$O.i9HVK2hKBiSExYIGWK8uJGOSAVssJIqAbJZBZttJmEVvj3LRz2W',
           'admin@example.com',
           'System',
           'Administrator',
           'a1cec8bb-1cf1-40c1-9ab1-2a392af385e4',
           true
       ) ON CONFLICT (username) DO NOTHING;
