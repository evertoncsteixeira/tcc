package sqlserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class ConectarSql {

	private String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private String caminho = "jdbc:sqlserver://EVERTON\\SERVIDOR:49873;" + "databaseName=AB_CONTACORRENTE;";
	private String usuario = "sa";
	private String senha = "teste";
	public Connection con;
	
	
	//Vamos criar nosso método de conexão
	public void conectar() {
		try {
			
			System.setProperty("jdbc.Drivers", driver);
			con=DriverManager.getConnection(caminho, usuario, senha);
		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "Erro ao Conectar" + e.getMessage());
			
		}
	}
	
	public void ExecutaScript(String sb) {
		try {
			Statement stmt = con.createStatement();
			stmt.execute(sb);
		} catch (Exception e) {
			// TODO: handle exception
		
			JOptionPane.showMessageDialog(null, "Script com erro : " + e.getMessage());
			
		}
	}
	

	public StringBuffer lerArquivoSql(String arquivoCriado) throws IOException {
		File arquivoSql = new File(arquivoCriado);
		FileInputStream arquivo = new FileInputStream(arquivoSql);
		InputStreamReader isr = new InputStreamReader(arquivo);
		BufferedReader bf = new  BufferedReader(isr);
		String linha;
		StringBuffer sb = new StringBuffer();
		while ((linha=bf.readLine()) != null){
			sb.append("\n" + linha);
		}
		bf.close();
		
		return sb; 
	}
}
