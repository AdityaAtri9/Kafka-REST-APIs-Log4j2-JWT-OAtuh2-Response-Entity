package com.jobportal.jobportal_api.entity;

import java.util.HashSet;
import java.util.Set;


import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String username;
	
	@Column(unique = true)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	    @JoinTable(name="user_roles",
	        joinColumns=@JoinColumn(name="user_id"),
	        inverseJoinColumns=@JoinColumn(name="role_id"))
	private Set<Role> role = new HashSet<>();
	
	

}
