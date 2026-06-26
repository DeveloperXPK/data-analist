CREATE TABLE IF NOT EXISTS users (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(10)  NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS credit_studies (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    score            INTEGER        NOT NULL CHECK (score >= 0 AND score <= 1000),
    institution      VARCHAR(100)   NOT NULL,
    status           VARCHAR(15)    NOT NULL CHECK (status IN ('PENDIENTE', 'APROBADO', 'RECHAZADO')),
    requested_amount NUMERIC(15, 2) NOT NULL,
    approved_amount  NUMERIC(15, 2),
    user_id          UUID           NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_credit_studies_user_id ON credit_studies (user_id);
CREATE INDEX IF NOT EXISTS idx_credit_studies_status  ON credit_studies (status);
