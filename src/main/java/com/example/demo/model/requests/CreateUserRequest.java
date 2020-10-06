package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
public class CreateUserRequest {

	@JsonProperty
	@NotBlank
	private String username;

	@JsonProperty
	@NotBlank
	@Size(min = 7)
	private String password;

	@JsonProperty
	@NotBlank
	@Size(min = 7)
	private String confirmPassword;
}
