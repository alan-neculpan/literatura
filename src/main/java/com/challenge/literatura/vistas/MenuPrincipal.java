package com.challenge.literatura.vistas;
import com.challenge.literatura.Datos;
import com.challenge.literatura.modelos.autores.Autor;
import com.challenge.literatura.modelos.autores.ListarDatosAutor;
import com.challenge.literatura.modelos.autores.RegistroDatosAutor;
import com.challenge.literatura.modelos.libros.DatosLibro;
import com.challenge.literatura.modelos.libros.DetalleLibros;
import com.challenge.literatura.modelos.libros.Libro;
import com.challenge.literatura.repositorios.AutorRepositorio;
import com.challenge.literatura.repositorios.LibroRepositorio;
import com.challenge.literatura.servicios.CambiarDatos;
import com.challenge.literatura.servicios.ConsultarApiServicio;
import jakarta.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MenuPrincipal {

    private final String url = "https://gutendex.com/books/";
    private final Scanner teclado = new Scanner(System.in);
    private final ConsultarApiServicio consultaApi = new ConsultarApiServicio();
    private final CambiarDatos cambiarDatos = new CambiarDatos();
    private final LibroRepositorio libroRepositorio;
    private final AutorRepositorio autorRepositorio;


    public MenuPrincipal(AutorRepositorio autorRepositorio, LibroRepositorio libroRepositorio) {
        this.libroRepositorio = libroRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    @Transactional
    private void buscarLibro() throws Exception {

        System.out.println("""
                           Ingrese el nombre del libro
                           """);
        String libro = teclado.nextLine();

        if(libro == null){
            throw new Exception("el libro no puede ser nulo");
        }

        var libroParam = URLEncoder.encode(libro, StandardCharsets.UTF_8);

        var json = consultaApi.requestData(url + "?search=" + libroParam);

        Datos datos = cambiarDatos.obtenerDatos(json, Datos.class);

        Optional<DatosLibro> datosLibro = datos.listaLibros().stream().findFirst();

        if (datosLibro.isPresent()) {
            var datosLibroDetalle = datosLibro.map(DetalleLibros::new);
            DetalleLibros detalleLibros = datosLibroDetalle.get();

            System.out.println(
                    "\n" +
                            "*********** RESULTS ************ \n" +
                            "***********   FOR   ************ \n" +
                            "***********  " + libroParam + "  ************\n" +
                            "\n" +
                            "\n" +
                            "Titulo    : " + detalleLibros.titulo() + "\n" +
                            "Autor     : " + detalleLibros.autor() + "\n" +
                            "Idioma    : " + detalleLibros.idioma() + "\n" +
                            "Descargas : " + detalleLibros.descargas()
            );


            if (!libroRepositorio.existsByTitulo(detalleLibros.titulo())) {
                Autor autor;
                if(!autorRepositorio.existsByName(detalleLibros.autor())){
                    var datosDeAutor = new RegistroDatosAutor(datosLibro.get().autor());
                    autor = new Autor(datosDeAutor);
                    autor = autorRepositorio.save(autor);
                }else{
                    autor = autorRepositorio.getByName(detalleLibros.autor());
                }
                libroRepositorio.save(new Libro(detalleLibros, autor));
            } else {
                System.out.println(
                        "\n\n************************************************************\n" +
                                "***********  " + libroParam + " ya esta registrado en la DB  ************ \n" +
                                "************************************************************\n"
                );
            }
        } else {
            System.out.println(
                    "\n\n************************************************************************************\n" +
                            "***********  no se encontro ningun libro con el nombre de :" + libroParam + "  ************ \n" +
                            "************************************************************************************\n"
            );
        }
    }

    public void listarLibrosRegistrados() {
        List<DetalleLibros> libros = libroRepositorio.findAll().stream().map(DetalleLibros::new).collect(Collectors.toList());

        System.out.println(
                """

                        ********************************************************\s
                        ***********   LISTA DE LIBROS REGISTRADOS   ************\s
                        *********************************************************

                        """
        );

        libros.forEach(l ->
                System.out.println(
                        "\n" +
                                "------- LIBRO -------" +
                                "\n" +
                                "Titulo    : " + l.titulo() + "\n" +
                                "Autor     : " + l.autor() + "\n" +
                                "Idioma    : " + l.idioma() + "\n" +
                                "Descargas : " + l.descargas()
                )
        );
    }

    public void listarAutoresRegistrados(){

        List<ListarDatosAutor> autores = autorRepositorio.findAll().stream().map(a -> {
            return new ListarDatosAutor(
                    a.getName(),
                    a.getFechaNacimiento(),
                    a.getFechaFallecimiento(),
                    libroRepositorio.getAllLibrosByAutor(a.getId())
            );
        }).collect(Collectors.toList());

        autores.forEach(a ->
                System.out.println(
                        "\n" +
                                "------- AUTOR -------" +
                                "\n" +
                                "Autor                  : " + a.name() + "\n" +
                                "Fecha de Nacimiento    : " + a.fechaNacimiento() + "\n" +
                                "Fecha de Fallecimiento : " + a.fechaFallecimiento() + "\n" +
                                "Libros                 : " + a.libros()
                )
        );
    }

    public void listarAutoresVivosEnUnAnioDetermiando () {

        System.out.println("Ingrese el año para determinar los actores que estuvieron vivos");
        var year = teclado.nextInt();
        teclado.nextLine();

        List<ListarDatosAutor> autores = autorRepositorio.findAllAutoresAliveInYear(year)
                .stream()
                .map(a -> {
                    return new ListarDatosAutor(
                            a.getName(),
                            a.getFechaNacimiento(),
                            a.getFechaFallecimiento(),
                            libroRepositorio.getAllLibrosByAutor(a.getId())
                    );
                }).collect(Collectors.toList());

        System.out.printf("""
                ##########################################
                #####  AUTORES VIVOS EN EL AÑO %d  #####
                ##########################################
                """, year);

        autores.forEach(a ->
                System.out.println(
                        "\n" +
                                "------- AUTOR -------" +
                                "\n" +
                                "Autor                  : " + a.name() + "\n" +
                                "Fecha de Nacimiento    : " + a.fechaNacimiento() + "\n" +
                                "Fecha de Fallecimiento : " + a.fechaFallecimiento() + "\n" +
                                "Libros                 : " + a.libros()
                )
        );
    }

    private void listarLibrosPorIdioma() {
        System.out.println(
                """
                    Seleccione el idioma de los libros a buscar

                    es -> español
                    en -> ingles
                    pt -> portugues
                    fr -> franses

                """
        );
        var idioma = teclado.nextLine();

        var libros = libroRepositorio.findAllByIdioma(idioma).stream()
                .map(l -> {
                    return new DetalleLibros (
                            l.getTitulo(),
                            l.getAutor().getName(),
                            l.getIdioma(),
                            l.getDescargas()
                    );
                }).collect(Collectors.toList());

        if (!libros.isEmpty()){

            libros.forEach(l ->
                    System.out.println(
                            "\n" +
                                    "------- LIBRO -------" +
                                    "\n" +
                                    "Titulo    : " + l.titulo() + "\n" +
                                    "Autor     : " + l.autor() + "\n" +
                                    "Idioma    : " + l.idioma() + "\n" +
                                    "Descargas : " + l.descargas()
                    )
            );
        }else {
            System.out.printf(
                    """
                    ---------------------------------------------------------------
                    No se encontro ningun libro del idioma "%s" registrado en la BD
                    ---------------------------------------------------------------
                    """, idioma);
        }
    }

    public void MostrarMenu() {
        int opcion = -1;
        while (opcion != 0) {

            String menu = """
                     \n
                     \n
                     ########################################
                     ################  MENU  ################
                     ########################################
                                        \s
                     #########  INGRESE UNA OPCION  #########
                                        \s
                                        \s
                     1 -> Buscar Libro
                     2 -> Listar Libros Registrados
                     3 -> Listar Autores Registrados
                     4 -> Listar Autores Vivos En Un Determinado Año
                     5 -> Listar Libros por idioma
                                        \s
                     0 -> Cerrar el programa
                    \s""";

            System.out.println(menu);

            try {
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 0:
                        System.out.println("... Cerrando la aplicacion");
                        break;
                    case 1:
                        buscarLibro();
                        break;
                    case 2:
                        listarLibrosRegistrados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivosEnUnAnioDetermiando();
                    case 5:
                        listarLibrosPorIdioma();
                        break;
                    default:
                        System.out.println("Error opcion invalida, elija una opcion valida");
                        break;
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Error: Opcion invalida, elija una opcion valida");
                teclado.nextLine();
            }
        }
    }

    private void getDatos() {
        var json = consultaApi.requestData(url);
        System.out.println(json);
    }
}