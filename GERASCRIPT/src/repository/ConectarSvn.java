package repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import util.IDMGeral;

public class ConectarSvn {
	
	
	private static void setupLibrary() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}
	
	public SVNRepository conectarSvn(String url, String name, String password) throws Throwable {
		
		try {
		
			setupLibrary();
			SVNRepository repository = null;
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
			
			@SuppressWarnings("deprecation")
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
			repository.setAuthenticationManager(authManager);
			
			return repository;
			
		} catch (SVNException svne) {
			throw new Throwable("Erro ao conectar ao SVN , verificar o caminho do repositorio , usuario e senha ");
		}
	}
	
	public void checkout(ArrayList<?> lista, SVNRepository repository, String tag, String siglaSistema, String versao){
		String caminhoTag = "tags/"+tag+ "/Scripts/";
		File diretorio = new File(IDMGeral.caminhoJuntaScript+ siglaSistema + "\\" +  versao.replace(".", "_") + "\\"); 
		if (!diretorio.exists()) {  
			diretorio.mkdirs(); 
		}
		diretorio = new File(IDMGeral.caminhoCheckOut); 
		if (!diretorio.exists()) {  
			diretorio.mkdirs();   
		}
		for (int i = 0; i < lista.size(); i++) {
			String arq = IDMGeral.caminhoCheckOut + lista.get(i);
			String dirArq = arq.substring(0 , arq.lastIndexOf("/"));
			diretorio = new File(dirArq);
			if (!diretorio.exists()) {  
				diretorio.mkdirs();   
			}
			File outputFile = new File(arq);
			outputFile = getCopy(caminhoTag + lista.get(i), outputFile, repository);
		}
	}
	
	public static void listEntries(SVNRepository repository, String path)
			throws SVNException {
		Collection<?> entries = repository.getDir(path, -1, null,
				(Collection<?>) null);
		Iterator<?> iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			System.out.println("/" + (path.equals("") ? "" : path + "/")
					+ entry.getName() + " (author: '" + entry.getAuthor()
					+ "'; revision: " + entry.getRevision() + "; date: "
					+ entry.getDate() + ")");
			if (entry.getKind() == SVNNodeKind.DIR) {
				listEntries(repository, (path.equals("")) ? entry.getName()
						: path + "/" + entry.getName());
			}
		}
	}
	
	public static File getCopy(String relativePath, File outputFile, SVNRepository repository){
		try{
			if (relativePath.isEmpty() || relativePath.endsWith("/"))
				throw new SVNException(SVNErrorMessage.create(SVNErrorCode.BAD_URL, "Invalide relativePath : " + relativePath));
			SVNNodeKind checkPath = repository.checkPath(relativePath, -1);
			if (checkPath == SVNNodeKind.FILE) {
				
				FileOutputStream fos = null;
				try { 
			         fos = new FileOutputStream(outputFile);
			         
					long file = repository.getFile(relativePath, -1, null, fos);
					if (file == -1)
						return null;
				} catch (IOException e) {
					  return null;
				} finally {
				  if(fos != null)
				    fos.close();
				}
			}
		}catch (Exception e) {
		}
		return outputFile;
	}
	
	public static void apagarCheckout(){
		File diretorio = new File(IDMGeral.caminhoCheckOut); 
		do{
			excluir(diretorio);
		}while(diretorio.exists());
	}
	
	public static void excluir(File diretorio){
		try{
			if (diretorio.exists() && diretorio.isDirectory()) {
				File[] arquivos = diretorio.listFiles(); 
				for(int i=0;i<arquivos.length;i++){ 
					excluir(arquivos[0]); 
				}
			}
			diretorio.delete();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
