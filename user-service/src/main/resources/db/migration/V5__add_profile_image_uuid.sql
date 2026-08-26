ALTER TABLE users ADD COLUMN profile_image_uuid UUID;
UPDATE users SET profile_url = NULL;
