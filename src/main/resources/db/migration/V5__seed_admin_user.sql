INSERT INTO users (name, email, password_hash, role, active)
VALUES (
           'Admin',
           'admin@vetcare.local',
           '$2a$10$pz5nwUWufjUYHwJV5brDoOyIEmIGPydRwT1Na7npkb4uDkVZ0M7CO',
           'ADMIN',
           true
       )
    ON CONFLICT (email) DO NOTHING;
