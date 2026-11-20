CREATE TABLE processed_events (event_id UUID PRIMARY KEY, event_type VARCHAR(64) NOT NULL, processed_at TIMESTAMPTZ NOT NULL);
CREATE TABLE sent_notifications (id UUID PRIMARY KEY, event_id UUID NOT NULL, recipient_email VARCHAR(255) NOT NULL, subject VARCHAR(255) NOT NULL, body TEXT NOT NULL, sent_at TIMESTAMPTZ NOT NULL);
