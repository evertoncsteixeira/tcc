package montarscript.montarscript;

public interface IMontarScript {
	
	public String caminhoJuntaScript = "C:\\CriacaoScript\\";
	
	public String pastaScript = "Checkout\\";
	
	public String USE_CC = 
			   "\r\n" +
	    	   "\r\n" + 
			   "USE AB_CONTACORRENTE \r\n" + 
			   "GO \r\n" +
			   "SET QUOTED_IDENTIFIER ON; \r\n" +
			   "\r\n" +
			   "\r\n" ;
	
	public String USE_P2 =
			  "\r\n" + 
			  "\r\n" +  
	          "USE AB_POUPANCA \r\n" + 
			  "GO \r\n" +
			  "SET QUOTED_IDENTIFIER ON; \r\n" +
			  "\r\n" +
			  "\r\n" ;

	public String USE_E2 = 	
			"\r\n" + 
			"USE AB_CONVTERC \r\n" +
			"GO \r\n" +
			"SET QUOTED_IDENTIFIER ON; \r\n" +
			"\r\n" +
			"\r\n" ;
			  
	public String VERSAO_OUTROS = "{3} " +
								  " \r\n";
	
	public String INSERT_VERSAO_SISTEMA = "INSERT VERSAO_SISTEMA_{0} \r\n" + 
										   "   (VERSAO, NOMESCRIPT, CODUSUARIO, DATAATU) \r\n" + 
										   "SELECT \r\n" + 
										   "   {1}, {2}, HOST_NAME() , GETDATE() \r\n" + 
										   "GO \r\n" + 
										   "	\r\n";
	
	public String VERSAO_TABELA_CC = 
			   "USE AB_CONTACORRENTE \r\n" + 
			   "GO \r\n" +
			   "IF OBJECT_ID(''VERSAO_SISTEMA_CC'') IS NULL BEGIN \r\n" +
			   " CREATE TABLE VERSAO_SISTEMA_CC (VERSAO VARCHAR(100), \r\n" +
			   " NOMESCRIPT VARCHAR(100), \r\n" +
			   " CODUSUARIO VARCHAR(20), \r\n" +
			   " DATAATU DATETIME)  \r\n" +
			   "END\r\n" +
			   "GO\r\n";
	

	
			
}
