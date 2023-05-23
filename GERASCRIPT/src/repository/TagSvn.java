package repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

public class TagSvn {

	public List<String> recuperaTag(String url, String name, String password) throws Throwable {
		
		ConectarSvn conectarSvn = new ConectarSvn();
		
		url = (url.lastIndexOf("//") == url.length()-1) ? 
				   url + "tags":	
				   url + "/tags";
		
		SVNRepository repository = conectarSvn.conectarSvn(url, name, password);

		ArrayList<String> listaRetorno = null;

//		public String url = "http://autklinux3/sistemas/CCM/tags";
		
		try {
			SVNNodeKind nodeKind = repository.checkPath("", -1);
			if (nodeKind == SVNNodeKind.NONE) {
				System.err.println("There is no entry at '" + url + "'.");
				System.exit(1);
			} else if (nodeKind == SVNNodeKind.FILE) {
				System.err.println("The entry at '" + url + "' is a file while a directory was expected.");
				System.exit(1);
			}

			listaRetorno = listEntries(repository, "");
		} catch (SVNException svne) {
			System.err.println("error while listing entries: " + svne.getMessage());
			return null;
		}
		long latestRevision = -1;
		try {
			latestRevision = repository.getLatestRevision();
		} catch (SVNException svne) {
			System.err.println("error while fetching the latest repository revision: " + svne.getMessage());
		}

		return listaRetorno;
	}

	public ArrayList<String> listEntries(SVNRepository repository, String path) throws SVNException {

		ArrayList<String> arrayList = new ArrayList<String>();
		Collection<?> entries = repository.getDir(path, -1, null, (Collection<?>) null);
		Iterator<?> iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			arrayList.add(entry.getName());
		}

		Collections.sort(arrayList);

		return arrayList;
	}
}
