package com.challenge.literatura.modelos.libros;

public record DetalleLibros (
        String titulo,
        String autor,
        String idioma,
        Integer descargas
) {
    public DetalleLibros (DatosLibro datosLibro) {
        this(
                datosLibro.titulo(),
                datosLibro.autor().getFirst().nombre(),
                datosLibro.idioma().getFirst().split(",")[0],
                datosLibro.descargas()
        );
    }

    public DetalleLibros (Libro libro) {
        this(libro.getTitulo(),libro.getAutor().getName(), libro.getIdioma(), libro.getDescargas());
    }
}