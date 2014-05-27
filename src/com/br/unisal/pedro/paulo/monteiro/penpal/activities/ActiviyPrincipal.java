/**
 * @author Pedro Paulo Monteiro
 * @version 1.0
 * @since   2014-05-27 
 */
package com.br.unisal.pedro.paulo.monteiro.penpal.activities;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.br.unisal.pedro.paulo.monteiro.penpal.R;
import com.br.unisal.pedro.paulo.monteiro.penpal.network.RequisicaoPenPal;

public class ActiviyPrincipal extends Activity {

	private RequisicaoPenPal mRequisicaoPenPal;
	private ArrayList<String> arrayListaAmigos;
	private HashMap<String, String> arrayMensagens;
	private Builder mNotifyBuilder;
	private NotificationManager mNotificationManager;
	private Integer totalMensagem;
	private int notifyID = 1;
	private Boolean solicitaMensagem;

	@Override
	protected void onStart() {
		super.onStart();
		solicitaMensagem = true;
		solicitarAmigos();
	};

	@Override
	protected void onStop() {
		super.onStop();
		solicitaMensagem = false;
	};

	@Override
	protected void onResume() {
		super.onStop();
		solicitaMensagem = true;
		solicitarAmigos();
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		solicitaMensagem = false;

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activiy_principal);

		Intent intencao = getIntent();
		String chave = intencao
				.getStringExtra(RequisicaoPenPal.stringChaveAutenticacao);
		mRequisicaoPenPal = new RequisicaoPenPal(chave);

		final ListView listView = (ListView) findViewById(R.id.listViewAmigos);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intencaoDetalheAmigo = new Intent(
						getApplicationContext(), DetalheAmigoActivity.class);
				intencaoDetalheAmigo.putExtra(
						RequisicaoPenPal.stringChaveAutenticacao,
						mRequisicaoPenPal.obterChave());
				intencaoDetalheAmigo.putExtra(RequisicaoPenPal.stringNomeAmigo,
						arrayListaAmigos.get(position));
				startActivity(intencaoDetalheAmigo);
			}
		});

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyBuilder = new NotificationCompat.Builder(this).setContentTitle(
				"Pen Pal - Nova mensagem").setSmallIcon(
				R.drawable.ic_action_mail);
		totalMensagem = Integer.valueOf(0);
		solicitaMensagem = true;
		solicitarMensagens();

	}

	private void solicitarMensagens() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {

					while (solicitaMensagem) {
						sleep(10000);
						arrayMensagens = mRequisicaoPenPal
								.solicitaMinhasMensagens();
						totalMensagem = Integer.valueOf(arrayMensagens
								.get(RequisicaoPenPal.stringTotalMensagens));

						if (totalMensagem > 0) {

							String nome = arrayMensagens
									.get(RequisicaoPenPal.stringNomeAmigo);
							Uri alarmSound = RingtoneManager
									.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
							mNotifyBuilder.setSound(alarmSound);

							mNotifyBuilder.setContentText(nome);

							mNotificationManager.notify(notifyID,
									mNotifyBuilder.build());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	private void solicitarAmigos() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					arrayListaAmigos = mRequisicaoPenPal.solicitaAmigos();
					adicionarAmigosLista();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	private void adicionarAmigosLista() {
		final ListView listView = (ListView) findViewById(R.id.listViewAmigos);

		listView.post(new Runnable() {
			public void run() {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getBaseContext(), android.R.layout.simple_list_item_1,
						arrayListaAmigos);
				listView.setAdapter(adapter);
			}
		});
	}

}
