CREATE TABLE IF NOT EXISTS url_mappings (
    id              SERIAL          PRIMARY KEY,
    original_url    TEXT            NOT NULL,
    short_url       VARCHAR(10)     NOT NULL    UNIQUE,
    created_at      TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_short_url ON url_mappings (short_url);
CREATE INDEX IF NOT EXISTS idx_original_url ON url_mappings(original_url);
