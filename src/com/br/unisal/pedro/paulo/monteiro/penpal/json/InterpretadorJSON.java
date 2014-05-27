/**
 * @author Pedro Paulo Monteiro
 * @version 1.0
 * @since   2014-05-27 
 */

package com.br.unisal.pedro.paulo.monteiro.penpal.json;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InterpretadorJSON {
	
	public String extrairValorJSON(String stringJSON, String chave){
		String valor = new String("");
		try {
			JSONObject meuObjeto = new JSONObject(stringJSON);
			valor = meuObjeto.getString(chave);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return valor;
	}
	
	
	public HashMap<String,String>  extrairHashMapJSON(String stringJSON, ArrayList <String> chaves){

		  HashMap<String,String> hashMapValores= new HashMap<String, String>(); 			
		   for (int i = 0; i < chaves.size() ; i++) {
				    String chave = chaves.get(i);
				   	String valor =  extrairValorJSON(stringJSON,chave);
				   	hashMapValores.put(chave ,valor);
			}
		   return hashMapValores;
	}
	
	public ArrayList<String>  extrairArrayJSON(String stringJSON, String chave){
		ArrayList<String> arrayValores = new ArrayList<String>();
		try {
			JSONObject meuObjeto = new JSONObject(stringJSON);
			JSONArray  arrayJSON = meuObjeto.getJSONArray(chave);
  		    for (int i = 0; i <= meuObjeto.length(); i++) {
				arrayValores.add(arrayJSON.getString(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arrayValores;
	}
}
