package com.example.main;

import java.io.IOException;
import java.sql.SQLException;

import com.example.model.Agenda;

/**
 * Agenda de contactos
 * 
 * @since 2022-01-25
 * @author Amadeo
 *
 */
public class Main {

	public static void main(String[] args) {
		try {
			Agenda a = new Agenda();
			if (a.crearTablas()) {
				System.out.println("Tablas creadas");
			} else {
				System.out.println("No se ha podido crear las tablas");
			}

			if (a.insertarSql()) {
				System.out.println("Datos insertados correctamente");
			} else {
				System.out.println("No se pudo insertar los datos");
			}

			if (a.crearXML()) {
				System.out.println("Se ha creado el xml");
			} else {
				System.out.println("No se pudo crear el xml");
			}

			if (a.listarUsuarios("0")) {
				System.out.println("Lista generada");
			} else {
				System.out.println("No se puedo cargar la lista");
			}
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}