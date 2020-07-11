CREATE TABLE IF NOT EXISTS board_column (
    id BIGSERIAL PRIMARY KEY,
    external_id UUID UNIQUE NOT NULL,
    position DECIMAL UNIQUE NOT NULL CHECK (position > 0),
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS card (
    id BIGSERIAL PRIMARY KEY,
    board_column_id INT,
    external_id UUID UNIQUE NOT NULL,
    position DECIMAL UNIQUE NOT NULL CHECK (position > 0),
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (board_column_id) REFERENCES board_column(id)
);
