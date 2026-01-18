CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email varchar(255) UNIQUE,
    password_hash text,
    full_name varchar(255) NOT NULL,
    role varchar(20) NOT NULL CHECK (role IN ('STUDENT','TEACHER','ADMIN')),
    is_email_verified boolean NOT NULL DEFAULT false,
    vip_status varchar(20) NOT NULL DEFAULT 'FREE' CHECK (vip_status IN ('FREE','VIP')),
    vip_expires_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE user_identities (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider varchar(30) NOT NULL CHECK (provider IN ('GOOGLE')),
    provider_user_id varchar(255) NOT NULL,
    email varchar(255),
    created_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(provider, provider_user_id),
    UNIQUE(user_id, provider)
);

CREATE TABLE user_otps (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    purpose varchar(30) NOT NULL CHECK (purpose IN ('EMAIL_VERIFY','LOGIN','RESET_PASSWORD')),
    otp_hash text NOT NULL,
    expires_at timestamptz NOT NULL,
    used_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_user_otps_user_purpose ON user_otps(user_id, purpose);

CREATE TABLE classes (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id uuid NOT NULL REFERENCES users(id),
    title varchar(255) NOT NULL,
    description text,
    is_published boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_classes_teacher ON classes(teacher_id);

CREATE TABLE enrollments (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id uuid NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    student_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status varchar(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','CANCELED','COMPLETED')),
    enrolled_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(class_id, student_id)
);

CREATE TABLE materials (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id uuid NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    title varchar(255) NOT NULL,
    content_type varchar(20) NOT NULL CHECK (content_type IN ('TEXT','LINK','FILE')),
    content text,
    -- from s3 (soon)
    path text,
    position int NOT NULL DEFAULT 0,
    is_published boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_materials_class_position ON materials(class_id, position);

CREATE TABLE class_tests (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id uuid NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    title varchar(255) NOT NULL,
    google_form_url text NOT NULL,
    max_score numeric(10,2),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE material_progress (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id uuid NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    material_id uuid NOT NULL REFERENCES materials(id) ON DELETE CASCADE,
    student_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    completed_at timestamptz,
    UNIQUE(material_id, student_id)
);

CREATE TABLE test_results (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    test_id uuid NOT NULL REFERENCES class_tests(id) ON DELETE CASCADE,
    student_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    score numeric(10,2),
    submitted_at timestamptz,
    UNIQUE(test_id, student_id)
);

CREATE TABLE class_feedback (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id uuid NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    student_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    generated_at timestamptz NOT NULL DEFAULT now(),
    feedback_text text NOT NULL,
    based_on_test_id  uuid REFERENCES class_tests(id),
    UNIQUE(class_id, student_id)
);

-- chat (?)
CREATE TABLE conversations (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id uuid REFERENCES classes(id) ON DELETE CASCADE,
    type varchar(20) NOT NULL CHECK (type IN ('CLASS_GROUP','CLASS_PRIVATE')),
    created_by uuid REFERENCES users(id),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE conversation_participants (
    conversation_id uuid NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_in_chat varchar(20) NOT NULL DEFAULT 'MEMBER' CHECK (role_in_chat IN ('MEMBER','MODERATOR')),
    joined_at timestamptz NOT NULL DEFAULT now(),
    left_at timestamptz,
    last_read_at timestamptz,
    PRIMARY KEY(conversation_id, user_id)
);

CREATE TABLE messages (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id uuid NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id uuid NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    message_type varchar(20) NOT NULL DEFAULT 'TEXT' CHECK (message_type IN ('TEXT','SYSTEM')),
    body text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_messages_conversation_time ON messages(conversation_id, created_at);

-- payment
CREATE TABLE vip_plans (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(50) UNIQUE NOT NULL,
    name varchar(100) NOT NULL,
    duration_days int NOT NULL,
    price_amount numeric(18,2) NOT NULL,
    currency varchar(10) NOT NULL DEFAULT 'IDR',
    is_active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE vip_orders (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users(id),
    plan_id uuid NOT NULL REFERENCES vip_plans(id),
    status varchar(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','PAID','FAILED','EXPIRED','CANCELED')),
    total_amount numeric(18,2) NOT NULL,
    currency varchar(10) NOT NULL DEFAULT 'IDR',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE payment_transactions (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id uuid NOT NULL REFERENCES vip_orders(id) ON DELETE CASCADE,
    provider varchar(20) NOT NULL DEFAULT 'XENDIT' CHECK (provider IN ('XENDIT')),
    reference_id varchar(255) UNIQUE NOT NULL,
    payment_id varchar(255),
    status varchar(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','SUCCEEDED','FAILED')),
    total_amount numeric(18,2),
    raw_event jsonb,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE payment_webhook_events (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    provider varchar(20) NOT NULL DEFAULT 'XENDIT',
    event_type varchar(100) NOT NULL,
    reference_id varchar(255) NOT NULL,
    payload jsonb NOT NULL,
    received_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(provider, event_type, reference_id)
);
