package util;

import java.util.ArrayList;

public class OrdenarScripts {

	public ArrayList<String> ordernarScripts(ArrayList<?> listaScripts) {

		ArrayList<String> listaOrdenada = new ArrayList<String>();
		
		//SEQUENCES
		for (int i = 0; i < listaScripts.size(); i++) {
			String script = (String) listaScripts.get(i);
			script = script.substring(script.indexOf("|") + 1, script.length());

			if (IDMGeral.SEQUENCES.equals(script.substring(0, script.indexOf("/")))) {
				listaOrdenada.add((String) listaScripts.get(i));
			}
		}
		
		//TABELAS
		for (int i = 0; i < listaScripts.size(); i++) {
			String script = (String) listaScripts.get(i);
			script = script.substring(script.indexOf("|") + 1, script.length());

			if (IDMGeral.TABELAS.equals(script.substring(0, script.indexOf("/")))) {
				listaOrdenada.add((String) listaScripts.get(i));
			}
		}
		


		// TRIGGERS;
		for (int i = 0; i < listaScripts.size(); i++) {
			String script = (String) listaScripts.get(i);
			script = script.substring(script.indexOf("|") + 1, script.length());

			if (IDMGeral.TRIGGER.equals(script.substring(0, script.indexOf("/")))) {
				listaOrdenada.add((String) listaScripts.get(i));
			}
		}

		// VIEWS
		for (int i = 0; i < listaScripts.size(); i++) {
			String script = (String) listaScripts.get(i);
			script = script.substring(script.indexOf("|") + 1, script.length());

			if (IDMGeral.VIEWS.equals(script.substring(0, script.indexOf("/")))) {
				listaOrdenada.add((String) listaScripts.get(i));
			}
		}
		
		// FUNCTIONS
		for (int i = 0; i < listaScripts.size(); i++) {
			String script = (String) listaScripts.get(i);
			script = script.substring(script.indexOf("|") + 1, script.length());

			if (IDMGeral.FUNCTIONS.equals(script.substring(0, script.indexOf("/")))) {
				listaOrdenada.add((String) listaScripts.get(i));
			}
		}
		
		// SINONIMOS
		for (int i = 0; i < listaScripts.size(); i++) {
			String script = (String) listaScripts.get(i);
			script = script.substring(script.indexOf("|") + 1, script.length());

			if (IDMGeral.SINONIMOS.equals(script.substring(0, script.indexOf("/")))) {
				listaOrdenada.add((String) listaScripts.get(i));
			}
		}
		
		//CARGAS
		for (int i = 0; i < listaScripts.size(); i++) {
			String script = (String) listaScripts.get(i);
			script = script.substring(script.indexOf("|") + 1, script.length());

			if (IDMGeral.CARGAS.equals(script.substring(0, script.indexOf("/")))) {
				listaOrdenada.add((String) listaScripts.get(i));
			}
		}
		
		// PROCEDURES
		for (int i = 0; i < listaScripts.size(); i++) {
			String script = (String) listaScripts.get(i);
			script = script.substring(script.indexOf("|") + 1, script.length());

			if (IDMGeral.PROCEDURES.equals(script.substring(0, script.indexOf("/")))) {
				listaOrdenada.add((String) listaScripts.get(i));
			}
		}

		return listaOrdenada;
	}
}
