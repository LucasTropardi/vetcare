INSERT INTO users (name, email, password_hash, role, active)
VALUES (
           'Admin',
           'admin@vetcare.local',
           '$2a$10$2pLk2W9m6Vd3bVv6l1mJpO2vP/0xwX2q1Y6W7rH9o3m0pY4p9l4k2',
           'ADMIN',
           true
       )
    ON CONFLICT (email) DO NOTHING;
