package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;

public class Util {

	public static String readFileAsString(String fileName) throws Exception {

		File f = new File(fileName);
		String currentDirectory = f.getAbsolutePath();

		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(currentDirectory));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	public static Object[] getArgumentos(String versao, String nomeObjeto) {
		return new Object[] { versao, 										   // 0
				              nomeObjeto.substring(0, nomeObjeto.indexOf(".")) // 1
		};
	}
	
	
	public static void escreverArquivo(Object[] param, BufferedWriter scripts, String mensagem) {

		String versaoCCInicio = MessageFormat.format(mensagem, param);

		try {
			scripts.newLine();
			scripts.write(versaoCCInicio);
			scripts.newLine();
			scripts.newLine();
			scripts.flush();
		} catch (IOException e) {
			throw new RuntimeException("Problemas para gravar o arquivo");
		}
	}
}
