INSERT INTO users (user_id, display_name)
VALUES ('user-001', 'Alice');

INSERT INTO devices (device_id, user_id, device_name, state)
VALUES ('cam-001', 'user-001', 'FrontDoor', 'ACTIVE');
