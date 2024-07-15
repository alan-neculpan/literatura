package com.challenge.literatura;

import com.challenge.literatura.modelos.libros.DatosLibro;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Datos (@JsonAlias("results") List<DatosLibro> listaLibros ) {}
