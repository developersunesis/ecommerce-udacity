package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class CreateUserRequest {

	@JsonProperty
	@NotBlank
	private String username;

	@JsonProperty
	@NotBlank
	private String password;

	@JsonProperty
	@NotBlank
	private String confirmPassword;
}
