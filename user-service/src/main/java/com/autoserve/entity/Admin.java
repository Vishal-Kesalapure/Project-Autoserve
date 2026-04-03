package com.autoserve.entity;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ADMIN")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Admin extends User {

	@Override
	public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
		return List.of(() -> "ROLE_ADMIN");
	}
}
