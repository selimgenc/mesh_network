package me.selim.mesh.web.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotEmpty;


@JsonPropertyOrder({"name"})
public record RestNodeCreateRequest(@JsonProperty("name") @NotEmpty String name) {
}
