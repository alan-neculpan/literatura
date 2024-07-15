package com.challenge.literatura.modelos.libros;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.challenge.literatura.modelos.autores.Autor;
import jakarta.persistence.*;

@Entity
@Table(name = "libros")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String titulo;
    private String idioma;
    private Integer descargas;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    @JsonBackReference
    private Autor autor;

    public Libro() {
    }

        public Libro(DatosLibro datosLibro) {
            this.titulo = datosLibro.titulo();
            this.autor = new Autor(datosLibro.autor().getFirst());
            this.idioma = datosLibro.idioma().getFirst();
            this.descargas = datosLibro.descargas();
        }

        public Libro(DetalleLibros detalleLibro, Autor autor) {
            this.titulo = detalleLibro.titulo();
            this.idioma = detalleLibro.idioma();
            this.descargas = detalleLibro.descargas();
            this.autor = autor;
        }

        public String getId() {
            return id;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getIdioma() {
            return idioma;
        }

        public Integer getDescargas() {
            return descargas;
        }

        public Autor getAutor() {
            return autor;
        }
}
