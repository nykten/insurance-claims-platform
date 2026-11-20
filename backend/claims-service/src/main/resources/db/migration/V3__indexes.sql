CREATE INDEX idx_claims_status_market ON claims(status, market);
CREATE INDEX idx_claims_officer_status ON claims(assigned_officer_id, status);
CREATE INDEX idx_claims_claimant ON claims(claimant_id);
CREATE INDEX idx_outbox_status_created ON outbox_events(status, created_at);
