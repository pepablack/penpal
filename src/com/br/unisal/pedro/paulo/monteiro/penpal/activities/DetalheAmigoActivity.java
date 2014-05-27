/**
 * @author Pedro Paulo Monteiro
 * @version 1.0
 * @since   2014-05-27 
 */

package com.br.unisal.pedro.paulo.monteiro.penpal.activities;

import java.util.HashMap;
import java.util.Locale;

import com.br.unisal.pedro.paulo.monteiro.penpal.R;
import com.br.unisal.pedro.paulo.monteiro.penpal.network.RequisicaoPenPal;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DetalheAmigoActivity extends Activity {

	private RequisicaoPenPal mRequisicaoPenPal;
	private HashMap<String, String> informacoesAmigo;
	private String nomeAmigo;
	private Double mLatitude;
	private Double mLongitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhe_amigo);

		Intent minhaIntencao = getIntent();
		String chave = minhaIntencao
				.getStringExtra(RequisicaoPenPal.stringChaveAutenticacao);
		nomeAmigo = minhaIntencao
				.getStringExtra(RequisicaoPenPal.stringNomeAmigo);
		mRequisicaoPenPal = new RequisicaoPenPal(chave);
		solicitarInformacoesAmigo();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detalhe_amigo, menu);
		return true;
	}

	private void solicitarInformacoesAmigo() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					informacoesAmigo = mRequisicaoPenPal
							.solicitaInformacoesAmigo(nomeAmigo);
					Log.d("infoAmigo", informacoesAmigo
							.get(RequisicaoPenPal.stringNomeAmigo));
					Log.d("infoAmigo",
							informacoesAmigo.get(RequisicaoPenPal.stringEmail));
					Log.d("infoAmigo", informacoesAmigo
							.get(RequisicaoPenPal.stringTelefone));
					Log.d("infoAmigo",
							informacoesAmigo.get(RequisicaoPenPal.stringIdioma));
					atribuirInformacoesAmigo();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	private void atribuirInformacoesAmigo() {

		final TextView textViewNomeAmigo = (TextView) findViewById(R.id.textViewNomeAmigo);
		final TextView textViewEmailAmigo = (TextView) findViewById(R.id.textViewEmail);
		final TextView textViewTelefoneAmigo = (TextView) findViewById(R.id.textViewTelefone);
		final TextView textViewIdiomaAmigo = (TextView) findViewById(R.id.textViewIdioma);
		mLatitude = Double.valueOf(informacoesAmigo
				.get(RequisicaoPenPal.stringLatitude));
		mLongitude = Double.valueOf(informacoesAmigo
				.get(RequisicaoPenPal.stringLongitude));

		textViewNomeAmigo.post(new Runnable() {
			public void run() {
				textViewNomeAmigo.setText(informacoesAmigo
						.get(RequisicaoPenPal.stringNomeAmigo));
				textViewEmailAmigo.setText(new String("Email: ")
						+ informacoesAmigo.get(RequisicaoPenPal.stringEmail));
				textViewTelefoneAmigo.setText(new String("Telefone: ")
						+ informacoesAmigo.get(RequisicaoPenPal.stringTelefone));
				Locale local = new Locale(informacoesAmigo
						.get(RequisicaoPenPal.stringIdioma));
				textViewIdiomaAmigo.setText(new String("Idioma: ")
						+ local.getDisplayLanguage());
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.localizarAmigo:
			localizarAmigoNoMapa();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void localizarAmigoNoMapa() {

		Intent intencaoAmigoNoMapa = new Intent(getApplicationContext(),
				AmigoNoMapaActivity.class);
		intencaoAmigoNoMapa
				.putExtra(RequisicaoPenPal.stringLatitude, mLatitude);
		intencaoAmigoNoMapa.putExtra(RequisicaoPenPal.stringLongitude,
				mLongitude);
		intencaoAmigoNoMapa.putExtra(RequisicaoPenPal.stringNomeAmigo,
				nomeAmigo);
		startActivity(intencaoAmigoNoMapa);
	}

	public void enviarMensagemAmigo(View view) {
		EditText mensagem = (EditText) findViewById(R.id.editTextMensagemAEnviar);

		if (TextUtils.isEmpty(mensagem.getText().toString())) {
			mensagem.setError(getString(R.string.error_field_required));
			return;
		}
		enviarMensagem(mensagem.getText().toString());
		mensagem.getText().clear();
		Toast.makeText(getApplicationContext(), "Mensagem Enviada!",
				Toast.LENGTH_SHORT).show();
	}

	private void enviarMensagem(final String mensagem) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					mRequisicaoPenPal.enviaMensagemParaAmigo(nomeAmigo,
							mensagem);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
}
