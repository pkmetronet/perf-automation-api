-- 1. Test Plan
CREATE TABLE test_plan (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. User Defined Variables
CREATE TABLE test_plan_variable (
    id BIGSERIAL PRIMARY KEY,
    test_plan_id BIGINT NOT NULL REFERENCES test_plan(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    value TEXT NOT NULL,
    description TEXT,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    UNIQUE(test_plan_id, name)
);

-- 3. Thread Group
CREATE TABLE thread_group (
    id BIGSERIAL PRIMARY KEY,
    test_plan_id BIGINT NOT NULL REFERENCES test_plan(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    
    request_sequence BIGINT[],
    
    num_threads INTEGER NOT NULL DEFAULT 1,
    ramp_up_time INTEGER NOT NULL DEFAULT 1,
    loop_count INTEGER NOT NULL DEFAULT 1,
    
    scheduler_enabled BOOLEAN DEFAULT FALSE,
    duration INTEGER,
    startup_delay INTEGER,
    
    on_sample_error VARCHAR(50) DEFAULT 'CONTINUE',
    
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. HTTP Request (Sampler)
CREATE TABLE http_request (
    id BIGSERIAL PRIMARY KEY,
    thread_group_id BIGINT NOT NULL REFERENCES thread_group(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    protocol VARCHAR(10) DEFAULT 'https',
    domain VARCHAR(255) NOT NULL,
    port INTEGER,
    method VARCHAR(15) NOT NULL DEFAULT 'GET',
    path TEXT NOT NULL,
    body_data TEXT,
    
    follow_redirects BOOLEAN DEFAULT TRUE,
    use_keepalive BOOLEAN DEFAULT TRUE,
    timeout_connect INTEGER,
    timeout_response INTEGER,
    
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. HTTP Header Manager
CREATE TABLE http_header (
    id BIGSERIAL PRIMARY KEY,
    thread_group_id BIGINT REFERENCES thread_group(id) ON DELETE CASCADE,
    http_request_id BIGINT REFERENCES http_request(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    value TEXT NOT NULL,
    
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    CONSTRAINT chk_header_parent CHECK (
        (thread_group_id IS NOT NULL AND http_request_id IS NULL) OR 
        (thread_group_id IS NULL AND http_request_id IS NOT NULL)
    )
);

-- 6. Data Extractors (Post-Processors)
CREATE TABLE data_extractor (
    id BIGSERIAL PRIMARY KEY,
    http_request_id BIGINT NOT NULL REFERENCES http_request(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    
    extractor_type VARCHAR(50) NOT NULL,
    variable_name VARCHAR(255) NOT NULL,
    expression TEXT NOT NULL,
    match_number INTEGER DEFAULT 1,
    default_value VARCHAR(255),
    
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Assertions
CREATE TABLE assertion (
    id BIGSERIAL PRIMARY KEY,
    http_request_id BIGINT NOT NULL REFERENCES http_request(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    
    assertion_type VARCHAR(50) NOT NULL,
    field_to_test VARCHAR(50) NOT NULL,
    match_rule VARCHAR(50) NOT NULL,
    expected_value TEXT NOT NULL,
    
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8. Timers
CREATE TABLE test_timer (
    id BIGSERIAL PRIMARY KEY,
    http_request_id BIGINT NOT NULL REFERENCES http_request(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    timer_type VARCHAR(50) NOT NULL DEFAULT 'CONSTANT',
    delay_ms INTEGER NOT NULL,
    deviation_ms INTEGER DEFAULT 0,
    
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- 9. Test Execution (Run History)
CREATE TABLE test_execution (
    id BIGSERIAL PRIMARY KEY,
    test_plan_id BIGINT NOT NULL REFERENCES test_plan(id) ON DELETE CASCADE,
    thread_group_id BIGINT REFERENCES thread_group(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'RUNNING',
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP WITH TIME ZONE,
    trigger_source VARCHAR(100),
    executed_by VARCHAR(255),
    
    s3_key_result_tree TEXT,
    s3_key_aggregated_report TEXT
);

-- 10. Execution Metrics (Results)
CREATE TABLE execution_metric (
    id BIGSERIAL PRIMARY KEY,
    test_execution_id BIGINT NOT NULL REFERENCES test_execution(id) ON DELETE CASCADE,
    http_request_id BIGINT NOT NULL REFERENCES http_request(id) ON DELETE CASCADE,
    thread_name VARCHAR(255),
    
    response_code VARCHAR(10),
    response_message TEXT,
    success BOOLEAN NOT NULL,
    
    response_time_ms INTEGER NOT NULL,
    latency_ms INTEGER NOT NULL,
    connect_time_ms INTEGER NOT NULL,
    bytes_sent INTEGER,
    bytes_received INTEGER,
    
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_http_req_thread ON http_request(thread_group_id);
CREATE INDEX idx_metric_execution ON execution_metric(test_execution_id);
CREATE INDEX idx_metric_request ON execution_metric(http_request_id);

-- Trigger to update request_sequence on INSERT
CREATE OR REPLACE FUNCTION update_request_sequence_on_insert()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE thread_group
  SET request_sequence = array_append(request_sequence, NEW.id)
  WHERE id = NEW.thread_group_id;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER http_request_insert_trigger
AFTER INSERT ON http_request
FOR EACH ROW
EXECUTE FUNCTION update_request_sequence_on_insert();

-- Trigger to update request_sequence on DELETE
CREATE OR REPLACE FUNCTION update_request_sequence_on_delete()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE thread_group
  SET request_sequence = array_remove(request_sequence, OLD.id)
  WHERE id = OLD.thread_group_id;
  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER http_request_delete_trigger
AFTER DELETE ON http_request
FOR EACH ROW
EXECUTE FUNCTION update_request_sequence_on_delete();