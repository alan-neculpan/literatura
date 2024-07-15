package com.challenge.literatura.modelos.autores;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosAutor (
        @JsonAlias("name") String nombre,
        @JsonAlias("birth_year") String fecha_nacimiento,
        @JsonAlias("death_year") String fecha_fallecimiento
) {}