package me.selim.mesh.web.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@JsonPropertyOrder({"distance"})
public record RestConnectRequest(@JsonProperty("distance") @NotNull @Min(1) Integer distance) {
}
