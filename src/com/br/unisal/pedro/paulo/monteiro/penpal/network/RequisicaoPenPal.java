package com.br.unisal.pedro.paulo.monteiro.penpal.network;

import java.util.ArrayList;
import java.util.HashMap;

import com.br.unisal.pedro.paulo.monteiro.penpal.json.InterpretadorJSON;

public class RequisicaoPenPal {
	
	private ComunicadorHTTP meuComunicadorHTTP;
	private InterpretadorJSON meuInterpretadorJSON;
	private String chaveAutenticacao ;
	private static final String urlBase = new String ("http://scadabr.apinae.net/penpal/");
	public  static final String stringChaveAutenticacao = new String("chaveAutenticacao");
	public  static final String stringAmigos = new String("amigos");
	public  static final String stringNomeAmigo = new String("nomeAmigo");
	public  static final String stringLatitude = new String("latitude");
	public  static final String stringLongitude = new String("longitude");
	public  static final String stringIdioma = new String("idioma");
	public  static final String stringTelefone = new String("telefone");
	public  static final String stringEmail = new String("email");
	public  static final String stringTotalMensagens = new String("totalMensagens");
	public  static final String stringDataHora = new String("dataHora");
	public  static final String stringMensagem = new  String("mensagem");
	public  static final String stringRetorno = new  String("retorno");
	public  static final String stringOK = new  String("OK");
	
		
	public RequisicaoPenPal() {
			meuComunicadorHTTP = new ComunicadorHTTP(urlBase);
			meuInterpretadorJSON = new InterpretadorJSON();
			chaveAutenticacao = new String("");
	}
	
	public RequisicaoPenPal( String chave) {
		meuComunicadorHTTP = new ComunicadorHTTP(urlBase);
		meuInterpretadorJSON = new InterpretadorJSON();
		chaveAutenticacao = chave;
    }
	
	public String obterChave() {
		return this.chaveAutenticacao;
	}
	
   public String solicitaAutenticacao(String email) {
			String chave =  meuComunicadorHTTP.requisitarURL(new String("solicitaAutenticacao/?email=" + email));
			this.chaveAutenticacao =  meuInterpretadorJSON. extrairValorJSON(chave, stringChaveAutenticacao);
			return this.chaveAutenticacao;
}
   
   public String enviaMensagemParaAmigo(String nomeAmigo, String mensagem) {
			String retorno =  meuComunicadorHTTP.requisitarURL(new String("enviaMensagemParaAmigo/?chaveAutenticacao="+ chaveAutenticacao + "&nomeAmigo=" + nomeAmigo + "&mensagem=" + mensagem));
			return  meuInterpretadorJSON. extrairValorJSON(retorno, stringRetorno);
}
   
public ArrayList<String> solicitaAmigos() {
			String listaAmigos =  meuComunicadorHTTP.requisitarURL(new String("solicitaAmigos/?chaveAutenticacao=" + chaveAutenticacao) );
			return meuInterpretadorJSON.extrairArrayJSON(listaAmigos, stringAmigos);
}

public  HashMap<String, String> solicitaMinhasMensagens() {
			String mensagens =  meuComunicadorHTTP.requisitarURL(new String("solicitaMinhasMensagens/?chaveAutenticacao=" + chaveAutenticacao) );
			ArrayList <String> mensagensAmigo = new  ArrayList<String>();
			mensagensAmigo.add(stringTotalMensagens);
			Integer totalMensagem =  Integer.valueOf(meuInterpretadorJSON.extrairValorJSON(mensagens, stringTotalMensagens));
			
			if (totalMensagem>0) {
					mensagensAmigo.add(stringNomeAmigo);
					mensagensAmigo.add(stringDataHora);
					mensagensAmigo.add(stringMensagem);
			}
			return  meuInterpretadorJSON.extrairHashMapJSON(mensagens,mensagensAmigo);
}

public HashMap<String, String> solicitaInformacoesAmigo( String nomeAmigo) {
			String infoAmigo =  meuComunicadorHTTP.requisitarURL(new String("solicitaInformacoesAmigo/?chaveAutenticacao=" + chaveAutenticacao + "&nomeAmigo=" + nomeAmigo));
			ArrayList <String> atributosInfoAmigo = new  ArrayList<String>();
			atributosInfoAmigo.add(stringNomeAmigo);
			atributosInfoAmigo.add(stringLatitude);
			atributosInfoAmigo.add(stringLongitude);
			atributosInfoAmigo.add(stringIdioma);
			atributosInfoAmigo.add(stringTelefone);
			atributosInfoAmigo.add(stringEmail);
			return  meuInterpretadorJSON.extrairHashMapJSON(infoAmigo,atributosInfoAmigo);
}

}