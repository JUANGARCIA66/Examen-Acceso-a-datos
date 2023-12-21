package com.example.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.example.classes.Contacto;

/**
 * Agenda
 * 
 * Fichero de registros de longitud fija
 * 
 * Los datos de cada contacto son:
 * usuario String 10,
 * nombre String 100,
 * telefono String 13,
 * edad int
 * 
 * Para almacenar los String Se utiliza writeUTF.
 * 
 * @see Contacto
 * @since 2022-03-15
 * @author Amadeo
 */
public class Agenda {

	private static String rutaXml = "";

	private final String url = "jdbc:sqlite:" + "./sqlite/Examen.db";

	private final String FIL_PASSWD = "./passwd.txt";

	public Connection cn;
	public Statement st;

	private static String borrarUsuarios = "DROP TABLE IF EXISTS usuarios";
	private static String crearUsuarios = "CREATE TABLE usuarios " +
			"(username VARCHAR(100) not NULL, " +
			" uid VARCHAR(100), " +
			" gid VARCHAR(100), " +
			" command VARCHAR(100)," +
			" PRIMARY KEY ( username ))";

	private static String borrarGrupos = "DROP TABLE IF EXISTS grupos";
	private static String crearGrupos = "CREATE TABLE grupos " +
			"(gid VARCHAR(100) not NULL, " +
			"PRIMARY KEY (gid))";

	public Agenda() throws IOException, SQLException {

		System.out.println();
		this.cn = DriverManager.getConnection(url);
		this.st = cn.createStatement();

	}

	public boolean crearTablas() {
		try {
			this.st.executeUpdate(borrarUsuarios);
			this.st.executeUpdate(crearUsuarios);
			this.st.executeUpdate(borrarGrupos);
			this.st.executeUpdate(crearGrupos);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean insertarSql() {

		ArrayList<Contacto> contactos = extraerTxt();

		for (Contacto contacto : contactos) {
			try {
				writeSql(contacto);
			} catch (IOException | SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private void writeSql(Contacto c) throws IOException, SQLException {

		PreparedStatement ps = this.cn
				.prepareStatement("INSERT INTO usuarios (username, uid, gid, command) VALUES (?, ?, ?, ?)");
		ps.setString(1, c.getUsername());
		ps.setString(2, c.getUid());
		ps.setString(3, c.getGid());
		ps.setString(4, c.getCommand());
		ps.executeUpdate();

		try {

			PreparedStatement ps2 = this.cn
					.prepareStatement("INSERT INTO grupos (gid) VALUES (?)");
			ps2.setString(1, c.getGid());
			ps2.executeUpdate();

		} catch (SQLException e) {

		}

	}

	public ArrayList<Contacto> extraerTxt() {
		ArrayList<Contacto> ac = new ArrayList<Contacto>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(FIL_PASSWD)));

			reader.readLine();
			String linea;
			while ((linea = reader.readLine()) != null) {
				String[] l = linea.split(":");
				Contacto c = new Contacto(l[0], l[2], l[3], l[6]);
				ac.add(c);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ac;

	}

	public boolean crearXML() {
		try {
			ResultSet rs;
			File xml;
			xml = crearDocumento();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;

			builder = factory.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();
			Document document = impl.createDocument(null, "Examen", null);
			document.setXmlVersion("1.0");
			Element raiz = document.createElement("TablaUsuarios");

			PreparedStatement ps = this.cn.prepareStatement("SELECT username, uid FROM usuarios");
			rs = ps.executeQuery();
			while (rs.next()) {
				String username = rs.getString("username");
				String uid = rs.getString("uid");

				if (username != null && uid != null) {
					try {
						Element registro = document.createElement("Usuario");
						document.getDocumentElement().appendChild(raiz).appendChild(registro);
						crearElemento("username", username, registro, document);
						crearElemento("uid", uid, registro, document);

					} catch (DOMException e) {
						e.printStackTrace();
						System.err.println("Ha petao");
					}
				}

			}
			DOMSource fuente = new DOMSource(document);
			StreamResult result = new StreamResult(xml);
			Transformer transfor;

			transfor = TransformerFactory.newInstance().newTransformer();
			transfor.transform(fuente, result);
			return true;
		} catch (IOException | ParserConfigurationException | SQLException | TransformerFactoryConfigurationError
				| TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	static void crearElemento(String curso, String num, Element raiz, Document documento) {
		Element elem = documento.createElement(curso);
		Text text = documento.createTextNode(num);
		raiz.appendChild(elem);
		elem.appendChild(text);
	}

	private File crearDocumento() throws IOException {

		String rutaXML = "examen.xml";
		File xml = new File(rutaXML);
		int numArchivo = 001;
		while (xml.exists()) {
			String numCeros = String.format("%03d", numArchivo);
			rutaXML = "examen" + numCeros + ".xml";
			xml = new File(rutaXML);
			numArchivo++;
		}

		if (xml.createNewFile()) {
			rutaXml = xml.getAbsolutePath();
			return xml;
		} else {
			return null;
		}
	}

	public boolean listarUsuarios(String gid) {
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = this.cn.prepareStatement("SELECT username,gid,uid FROM usuarios WHERE gid = ? ORDER BY gid, username");
			ps.setString(1, gid);
			rs = ps.executeQuery();

			while (rs.next()) {

				System.out.println("username: " + rs.getString("username") + ", gid: " + rs.getString("gid"));

			}
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

}
