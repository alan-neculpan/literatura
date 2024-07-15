package com.challenge.literatura;
import com.challenge.literatura.vistas.MenuPrincipal;
import com.challenge.literatura.repositorios.AutorRepositorio;
import com.challenge.literatura.repositorios.LibroRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraturaApplication implements CommandLineRunner {

	private final AutorRepositorio autorRepositorio;
	private final LibroRepositorio libroRepositorio;

	public LiteraturaApplication(AutorRepositorio autorRepositorio, LibroRepositorio libroRepositorio) {
		this.autorRepositorio = autorRepositorio;
		this.libroRepositorio = libroRepositorio;
	}

	public static void main(String[] args) {
		SpringApplication.run(LiteraturaApplication.class, args);}

	@Override
	public void run(String... args) throws Exception {
		MenuPrincipal menuPrincipal = new MenuPrincipal (autorRepositorio,libroRepositorio);

		menuPrincipal.MostrarMenu();
	}
}