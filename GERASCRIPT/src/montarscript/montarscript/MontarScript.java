package montarscript.montarscript;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import util.IDMGeral;
import util.Util;

public class MontarScript implements IMontarScript {
	
	private BufferedWriter scripts;
	private boolean gravouCG;
	private boolean gravouFT;
	private boolean gravouSN;
	private boolean gravouTB;
	private boolean gravouST;
	private boolean gravouTG;
	private boolean gravouVW;
	private boolean gravouSQ;
	private String versaoReplace;
	private String tipoScriptAnterior;
	
	public String montarScript(ArrayList<?> listaSelecionada, 
			                   String versao, 
			                   String nomeScript , 
			                   String tagGeracaoAtual, 
			                   String sistema,
			                   String usuario) throws Throwable {
		
		versaoReplace = versao.replace(".", "_");
		String tagGeracaoAtualSubstringReplace = tagGeracaoAtual.substring(0, tagGeracaoAtual.lastIndexOf("_")).replace("_", ".");
		
		//workspace do arquivo		
		String arquivoGerado = caminhoJuntaScript + sistema + IDMGeral.SEPARADOR_BARRA  + versaoReplace + IDMGeral.SEPARADOR_BARRA + sistema+ "_" + versaoReplace + 
				(("").equals(nomeScript) ? IDMGeral.EXTENSAO_SQL :  "_" + nomeScript  + IDMGeral.EXTENSAO_SQL);
		File arquivoSaida = new File(arquivoGerado);
		try {
			scripts = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoSaida), IDMGeral.ENCONDING_UTF8) );
		} catch (UnsupportedEncodingException e1) {
			throw new Throwable(e1.getMessage());
		} catch (FileNotFoundException e1) {
			throw new Throwable(e1.getMessage());
		}
		
		String versaoAlterada = (sistema.equals(IDMGeral.SISTEMA_CC) ? USE_CC : sistema.equals(IDMGeral.SISTEMA_P2) ? USE_P2 : USE_E2)  + INSERT_VERSAO_SISTEMA;
		String versaoAlteradaOutros = versaoAlterada + VERSAO_OUTROS;
		//CARREGANDO PARAMETROS PARA ESCREVER NO ARQUIVO.
		Object[] param = {sistema,                                                 //0
					"'" + tagGeracaoAtualSubstringReplace + "'",  				   //1
					"'" + sistema + "_" + versaoReplace + IDMGeral.SQLIni + "'"};  //2
		//ESCREVER INICIO DE ARQUIVO.
		Util.escreverArquivo(param, scripts, VERSAO_TABELA_CC + versaoAlterada);
		
		// VERSOES INICIO.
		gravouSQ = false;
		gravouCG = false;
		gravouFT = false;
		gravouSN = false;
		gravouTB = false;
		gravouST = false;
		gravouTG = false;
		gravouVW = false;
		tipoScriptAnterior = "";

		//CORRER A LISTA DOS REGISTROS SELECIONADOS.
		for (int i = 0; i < listaSelecionada.size(); i++) {
			
			String lista = (String) listaSelecionada.get(i);
			String tipo = lista.substring(0, lista.indexOf("/"));
			
			String script = lista.substring(lista.lastIndexOf("/") + 1, lista.length());
								
			if (!tipo.equals(tipoScriptAnterior)) {
				escreverVersaoFim(tipo, 
								 tipoScriptAnterior, 
								 script, 
								 tagGeracaoAtualSubstringReplace,
								 versao, 
								 sistema,
								 versaoAlterada);
				
				escreverVersaoInicio(tipo, 
						             script, 
						             tagGeracaoAtualSubstringReplace, 
						             versao, 
						             sistema, 
						             versaoAlterada, 
						             versaoAlteradaOutros);
				
				tipoScriptAnterior = tipo;
			}
					

			String data  = null;
			//ESCREVENDO O  ARQUIVO DE OBJETOS ARQUIVOOS DE SCRIPTS.
			try {
				
				data = Util.readFileAsString(caminhoJuntaScript + pastaScript + ((String) listaSelecionada.get(i)).replace("/", IDMGeral.SEPARADOR_BARRA));
				
					scripts.write(data);
					scripts.newLine();
					scripts.newLine();
					scripts.flush();
			} catch (Exception e) {
				throw new RuntimeException("Problemas na gravação do arquivo:" + e.getMessage());
			} 			
		}

		//escrever o ultimo fim apos sair
		String lista = (String) listaSelecionada.get(listaSelecionada.size() -1);
		String tipo = lista.substring(0, lista.indexOf("/"));
		String script = lista.substring(lista.lastIndexOf("/") + 1, lista.length());
		escreverVersaoFim(tipo, 
				          tipoScriptAnterior, 
				          script, 
				          tagGeracaoAtualSubstringReplace,  
				          versao, 
				          sistema, 
				          versaoAlterada);
		
		//CARREGANDO PARAMETROS PARA ESCREVER NO ARQUIVO.
		Object[] param2 = {sistema,
					"'" + tagGeracaoAtualSubstringReplace + "'",
					"'" + sistema + "_" + versaoReplace + IDMGeral.SQLFim + "'"};	
		//ESCREVER FINAL DE ARQUIVO.
		Util.escreverArquivo(param2, scripts, versaoAlterada);
		
		scripts.close();
		return arquivoSaida.toString();
	}

	private void escreverVersaoInicio(String tipo, String script, String tagGeracao, String versao, String sistema, String versaoAlterada, String versaoAlteradaOutros) {
		
		Object[] param = null;

		if (!gravouTB && (IDMGeral.TABELAS).equals(tipo))  {
				param = new Object[] {sistema , 
							"'" + tagGeracao + "'",
							"'" + sistema+"_TABELAS_" + versaoReplace + IDMGeral.SQLIni + "'"};	
					//ESCREVER INICIO DE ARQUIVO.
				Util.escreverArquivo(param, scripts, versaoAlterada);
				gravouTB = true;
		}
		
		if (!gravouTG && (IDMGeral.TRIGGER).equals(tipo))  {
					param = new Object[] {sistema , 
								"'" + tagGeracao + "'",
								"'" + sistema+"_TRIGGERS_" + versaoReplace + IDMGeral.SQLIni + "'"};	
						//ESCREVER INICIO DE ARQUIVO.
					Util.escreverArquivo(param, scripts, versaoAlterada);
					gravouTG = true;
		}
		
		if (!gravouVW && (IDMGeral.VIEWS).equals(tipo))  {
					param = new Object[] {sistema , 
								"'" + tagGeracao + "'",
								"'" + sistema+"_VIEW_" + versaoReplace + IDMGeral.SQLIni + "'"};	
						//ESCREVER INICIO DE ARQUIVO.
					Util.escreverArquivo(param, scripts, versaoAlterada);
					gravouVW = true;
		}
		
		if (!gravouSQ && (IDMGeral.SEQUENCES).equals(tipo))  {
					param = new Object[] {sistema , 
								"'" + tagGeracao + "'",
								"'" + sistema+"_SEQUENCES_" + versaoReplace + IDMGeral.SQLIni + "'"};	
						//ESCREVER INICIO DE ARQUIVO.
					Util.escreverArquivo(param, scripts, versaoAlterada);
					gravouSQ = true;
		}
		
		
		if (!gravouFT && (IDMGeral.FUNCTIONS).equals(tipo))  {
					param = new Object[] {sistema , 
								"'" + tagGeracao + "'",
								"'" + sistema+"_FUNCTIONS_" + versaoReplace + IDMGeral.SQLIni + "'"};	
						//ESCREVER INICIO DE ARQUIVO.
					Util.escreverArquivo(param, scripts, versaoAlterada);
					gravouFT = true;
		}
		
		if (!gravouSN && (IDMGeral.SINONIMOS).equals(tipo))  {
					param = new Object[] {sistema , 
								"'" + tagGeracao + "'",
								"'" + sistema+"_SINONIMOS_" + versaoReplace + IDMGeral.SQLIni + "'"};	
						//ESCREVER INICIO DE ARQUIVO.
					Util.escreverArquivo(param, scripts, versaoAlterada);
					gravouSN = true;
		}
		
		if (!gravouCG && (IDMGeral.CARGAS).equals(tipo))  {
					param = new Object[] {sistema , 
								"'" + tagGeracao + "'",
								"'" + sistema+"_CARGAS_" + versaoReplace + IDMGeral.SQLIni + "'"};	
						//ESCREVER INICIO DE ARQUIVO.
					Util.escreverArquivo(param, scripts, versaoAlterada);
					gravouCG = true;
		}
		
		if (!gravouST && IDMGeral.PROCEDURES.equals(tipo)) {
			param = new Object[]  {sistema , 
							"'" + tagGeracao + "'",
							"'" + sistema+"_PROCEDURE_" + versaoReplace + IDMGeral.SQLIni + "'"};	
			//ESCREVER INICIO DE ARQUIVO.
			Util.escreverArquivo(param, scripts, versaoAlterada);
			gravouST = true;
		}
		
		
	}
	
	
	private void escreverVersaoFim(String tipo, String tipoAnterior, String script, String tagGeracao, String versao, String sistema, String versaoAlterada) {
		
		Object[] param = null;
		
		if (((IDMGeral.PROCEDURES).equals(tipo)
				|| (IDMGeral.VIEWS).equals(tipo) || (IDMGeral.TRIGGER).equals(tipo)
				|| (IDMGeral.SEQUENCES).equals(tipo) || (IDMGeral.FUNCTIONS).equals(tipo)
				|| (IDMGeral.SINONIMOS).equals(tipo) || (IDMGeral.CARGAS).equals(tipo))
				&& gravouTB && !script.contains(sistema+"_TABELAS_")) {
			param = new Object[]{sistema, 
							"'" + tagGeracao + "'",
							"'" + sistema+"_TABELAS_" + versaoReplace + IDMGeral.SQLFim + "'"};	
			//ESCREVER INICIO DE ARQUIVO.
			Util.escreverArquivo(param, scripts, versaoAlterada);
			gravouTB = false;
		}
		
		
		if (((IDMGeral.PROCEDURES).equals(tipo)
				|| (IDMGeral.VIEWS).equals(tipo) || (IDMGeral.TRIGGER).equals(tipo)
				|| (IDMGeral.SEQUENCES).equals(tipo) || (IDMGeral.FUNCTIONS).equals(tipo)
				|| (IDMGeral.TABELAS).equals(tipo) || (IDMGeral.CARGAS).equals(tipo))
				&& gravouSN && !script.contains(sistema+"_SINONIMOS_")) {
			param = new Object[]{sistema, 
							"'" + tagGeracao + "'",
							"'" + sistema+"_SINONIMOS_" + versaoReplace + IDMGeral.SQLFim + "'"};	
			//ESCREVER INICIO DE ARQUIVO.
			Util.escreverArquivo(param, scripts, versaoAlterada);
			gravouSN = false;
		}
		
		if (((IDMGeral.PROCEDURES).equals(tipo)
				|| (IDMGeral.VIEWS).equals(tipo) || (IDMGeral.TRIGGER).equals(tipo)
				|| (IDMGeral.SEQUENCES).equals(tipo) || (IDMGeral.FUNCTIONS).equals(tipo)
				|| (IDMGeral.TABELAS).equals(tipo) || (IDMGeral.SINONIMOS).equals(tipo))
				&& gravouCG && !script.contains(sistema+"_CARGAS_")) {
			param = new Object[]{sistema, 
							"'" + tagGeracao + "'",
							"'" + sistema+"_CARGAS_" + versaoReplace + IDMGeral.SQLFim + "'"};	
			//ESCREVER INICIO DE ARQUIVO.
			Util.escreverArquivo(param, scripts, versaoAlterada);
			gravouCG = false;
		}
		
		if (((IDMGeral.PROCEDURES).equals(tipo)
				|| (IDMGeral.VIEWS).equals(tipo) || (IDMGeral.CARGAS).equals(tipo)
				|| (IDMGeral.SEQUENCES).equals(tipo) || (IDMGeral.FUNCTIONS).equals(tipo)
				|| (IDMGeral.TABELAS).equals(tipo) || (IDMGeral.SINONIMOS).equals(tipo))
				&& gravouTG && !script.contains(sistema+"_TRIGGERS_")) {
			param = new Object[]{sistema, 
							"'" + tagGeracao + "'",
							"'" + sistema+"_TRIGGERS_" + versaoReplace + IDMGeral.SQLFim + "'"};	
			//ESCREVER INICIO DE ARQUIVO.
			Util.escreverArquivo(param, scripts, versaoAlterada);
			gravouTG = false;
		}
		
		if (((IDMGeral.PROCEDURES).equals(tipo)
				|| (IDMGeral.VIEWS).equals(tipo) || (IDMGeral.CARGAS).equals(tipo)
				|| (IDMGeral.TRIGGER).equals(tipo) || (IDMGeral.FUNCTIONS).equals(tipo)
				|| (IDMGeral.TABELAS).equals(tipo) || (IDMGeral.SINONIMOS).equals(tipo))
				&& gravouSQ && !script.contains(sistema+"_SEQUENCES_")) {
			param = new Object[]{sistema, 
							"'" + tagGeracao + "'",
							"'" + sistema+"_SEQUENCES_" + versaoReplace + IDMGeral.SQLFim + "'"};	
			//ESCREVER INICIO DE ARQUIVO.
			Util.escreverArquivo(param, scripts, versaoAlterada);
			gravouSQ = false;
		}
		
		if (((IDMGeral.PROCEDURES).equals(tipo)
				|| (IDMGeral.VIEWS).equals(tipo) || (IDMGeral.CARGAS).equals(tipo)
				|| (IDMGeral.TRIGGER).equals(tipo) || (IDMGeral.SEQUENCES).equals(tipo)
				|| (IDMGeral.TABELAS).equals(tipo) || (IDMGeral.SINONIMOS).equals(tipo))
				&& gravouFT && !script.contains(sistema+"_FUNCTIONS_")) {
			param = new Object[]{sistema, 
							"'" + tagGeracao + "'",
							"'" + sistema+"_FUNCTIONS_" + versaoReplace + IDMGeral.SQLFim + "'"};	
			//ESCREVER INICIO DE ARQUIVO.
			Util.escreverArquivo(param, scripts, versaoAlterada);
			gravouFT = false;
		}
		
		if (((IDMGeral.PROCEDURES).equals(tipo)
				|| (IDMGeral.FUNCTIONS).equals(tipo) || (IDMGeral.CARGAS).equals(tipo)
				|| (IDMGeral.TRIGGER).equals(tipo) || (IDMGeral.SEQUENCES).equals(tipo)
				|| (IDMGeral.TABELAS).equals(tipo) || (IDMGeral.SINONIMOS).equals(tipo))
				&& gravouVW && !script.contains(sistema+"_VIEWS_")) {
			param = new Object[]{sistema, 
							"'" + tagGeracao + "'",
							"'" + sistema+"_VIEWS_" + versaoReplace + IDMGeral.SQLFim + "'"};	
			//ESCREVER INICIO DE ARQUIVO.
			Util.escreverArquivo(param, scripts, versaoAlterada);
			gravouVW = false;
		}
	
		if (gravouST) {
			param = new Object[] {sistema , 
							"'" + tagGeracao + "'",
							"'" + sistema+"_PROCEDURE_" + versaoReplace + IDMGeral.SQLFim + "'"};	
			//ESCREVER INICIO DE ARQUIVO.
			Util.escreverArquivo(param, scripts, versaoAlterada);
			gravouTB = gravouST;
			gravouCG = gravouST;
			gravouSN = gravouST;
			gravouTG = gravouST;
			gravouSQ = gravouST;
			gravouFT = gravouST;
			gravouVW = gravouST;
		}
	}
}
