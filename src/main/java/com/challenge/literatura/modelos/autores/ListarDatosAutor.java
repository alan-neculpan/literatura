package com.challenge.literatura.modelos.autores;

import java.util.List;

public record ListarDatosAutor (
        String name,
        String fechaNacimiento,
        String fechaFallecimiento,
        List<String> libros
) {}
