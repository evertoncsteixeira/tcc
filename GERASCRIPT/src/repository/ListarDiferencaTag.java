package repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.ISVNDiffStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import util.OrdenarScripts;

public class ListarDiferencaTag {

	public List<?> listarDiferencaTag(String url, String tagversao) {

		SVNDiffClient diffClient;
		SVNClientManager clientManager = SVNClientManager.newInstance();

		diffClient = clientManager.getDiffClient();
		
		url = (url.lastIndexOf("//") == url.length()-1) ? 
				   url + "tags":	
				   url + "/tags";
		
		final ArrayList<String> arrayList = new ArrayList<String>();
		final String tag1Path = "/" + tagversao + "/Scripts/";
		final String tag2Path = "/V_0_0_0H/Scripts/";

		try {
			diffClient.doDiffStatus(SVNURL.parseURIEncoded(url + tag2Path), SVNRevision.HEAD,
					SVNURL.parseURIEncoded(url + tag1Path), SVNRevision.HEAD, SVNDepth.INFINITY, true,
					new ISVNDiffStatusHandler() {
						public void handleDiffStatus(SVNDiffStatus diffStatus) throws SVNException {

							if (diffStatus.getKind() == SVNNodeKind.FILE) {
								SVNStatusType status = diffStatus.getModificationType();
								String path = diffStatus.getPath();
								if (!".project".equalsIgnoreCase(path)  // dever� ser adicionado pois se existir altera��o no .projetc ele da erro de substring.
								    && !"lista.txt".equalsIgnoreCase(path) 
									&& (!"ZZ".equalsIgnoreCase(path.substring(0, 2))
									//&& !"cargas".equalsIgnoreCase(path.substring(0, path.indexOf("/")))
									//&& !"functions".equalsIgnoreCase(path.substring(0, path.indexOf("/")))
									//&& !"procs".equalsIgnoreCase(path.substring(0, path.indexOf("/")))
									//&& !"sinonimos".equalsIgnoreCase(path.substring(0, path.indexOf("/")))
									&& !"cargas".equalsIgnoreCase(path)
									&& !"functions".equalsIgnoreCase(path)
									&& !"procs".equalsIgnoreCase(path)
									&& !"sinonimos".equalsIgnoreCase(path)
									&& !"tabelas".equalsIgnoreCase(path)
									&& !"triggers".equalsIgnoreCase(path)
									&& !"views".equalsIgnoreCase(path))) { // NAO RETORNAR
																									// DADOS DE
																									// CARGAS
									arrayList.add("Objeto" + "|" + path);
								}
							}
						}
					});

		} catch (SVNException e) {
			throw new RuntimeException("Problemas para Listar commits: " + e.getMessage());
		}

		Collections.sort(arrayList);
		OrdenarScripts ordenarScripts = new OrdenarScripts();

		return ordenarScripts.ordernarScripts(arrayList);
	}
}
