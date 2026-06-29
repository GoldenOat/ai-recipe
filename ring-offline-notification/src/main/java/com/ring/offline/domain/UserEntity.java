package com.ring.offline.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@Column(name = "user_id", length = 64)
	private String userId;

	@Column(name = "display_name", nullable = false)
	private String displayName;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected UserEntity() {
	}

	public String getUserId() {
		return this.userId;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public Instant getCreatedAt() {
		return this.createdAt;
	}

}
