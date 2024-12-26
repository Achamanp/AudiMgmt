package com.InvertisAuditoriumManagement.AudiMgmt.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class User implements UserDetails{
	@Id
	@GeneratedValue(generator = "user_gen",strategy = GenerationType.AUTO)
	@SequenceGenerator(name = "user_gen",sequenceName = "user_seq",initialValue = 1,allocationSize = 1)
	private Long srNumber;
	 @NotNull
	    @Size(min = 7, max = 15)
	    @Column(name = "student_id", unique = true, nullable = false)
	private String userId;
	   @NotBlank(message = "Name cannot be blank")
	    @Size(max = 20, message = "Name must be less than 20 characters")
	private String name;
	   @NotBlank(message = "Email cannot be blank")
	    @Email(message = "Email should be valid")
	private String email;
	private String department;
	@JsonIgnore
	private String password;
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EmailUpdateToken> emailUpdateTokens = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<PasswordResetToken> passwordResetToken;
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
	private Set<Booking> bookings;
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
	private Set<Role> roles =  new HashSet<>();
	public Long getSrNumber() {
		return srNumber;
	}
	public void setSrNumber(Long srNumber) {
		this.srNumber = srNumber;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }
	@Override
	public String getUsername() {
		return this.userId;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public Set<Booking> getBookings() {
		return bookings;
	}
	public void setBookings(Set<Booking> bookings) {
		this.bookings = bookings;
	}	
}
