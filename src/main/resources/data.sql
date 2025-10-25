-- Initialize Operation Types
INSERT INTO operation_types (operation_type_id, description) VALUES (1, 'PURCHASE');
INSERT INTO operation_types (operation_type_id, description) VALUES (2, 'INSTALLMENT PURCHASE');
INSERT INTO operation_types (operation_type_id, description) VALUES (3, 'WITHDRAWAL');
INSERT INTO operation_types (operation_type_id, description) VALUES (4, 'PAYMENT');

-- Initialize Roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Initialize Default Users (password: password123)
-- Admin user
INSERT INTO users (username, email, password, enabled) 
VALUES ('admin', 'admin@pismo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true);

-- Regular user
INSERT INTO users (username, email, password, enabled) 
VALUES ('user', 'user@pismo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true);

-- Assign roles to users (roles: 1=ROLE_USER, 2=ROLE_ADMIN, users: 1=admin, 2=user)
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1); -- admin has ROLE_USER
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2); -- admin has ROLE_ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1); -- user has ROLE_USER
