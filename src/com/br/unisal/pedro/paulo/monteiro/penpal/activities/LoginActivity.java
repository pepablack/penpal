/**
 * @author Pedro Paulo Monteiro
 * @version 1.0
 * @since   2014-05-27 
 */

package com.br.unisal.pedro.paulo.monteiro.penpal.activities;

import com.br.unisal.pedro.paulo.monteiro.penpal.R;
import com.br.unisal.pedro.paulo.monteiro.penpal.network.RequisicaoPenPal;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private String mEmail, mEmailLido;
	private EditText mEmailView;
	private TextView mLoginStatusMessageView;
	private RequisicaoPenPal mRequisicaoPenPal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		mEmailView = (EditText) findViewById(R.id.email);
		mRequisicaoPenPal = new RequisicaoPenPal();
		lerPreferencias();
		atribuirPreferencias();
	}

	public void attemptLogin(View view) {

		mEmailView.setError(null);
		mEmail = mEmailView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			autentica();
		}
	}

	private void lerPreferencias() {
		SharedPreferences sharedPref = this.getSharedPreferences(
				getString(R.string.arquivo_preferencia), Context.MODE_PRIVATE);
		mEmailLido = sharedPref.getString(RequisicaoPenPal.stringEmail,
				new String(""));
	}

	private void gravarPreferencias() {
		SharedPreferences sharedPref = this.getSharedPreferences(
				getString(R.string.arquivo_preferencia), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(RequisicaoPenPal.stringEmail, mEmail);
		editor.commit();
	}

	private void atribuirPreferencias() {
		mEmailView.setText(mEmailLido);
	}

	public void autentica() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					String chaveAutenticacao = mRequisicaoPenPal
							.solicitaAutenticacao(mEmail);
					if (!chaveAutenticacao.isEmpty()) {
						gravarPreferencias();
						Intent intencaoActivityPrincipal = new Intent(
								getApplicationContext(), ActiviyPrincipal.class);
						intencaoActivityPrincipal.putExtra(
								RequisicaoPenPal.stringChaveAutenticacao,
								mRequisicaoPenPal.obterChave());
						startActivity(intencaoActivityPrincipal);
					} else {
						mEmailView
								.setError(getString(R.string.error_invalid_email));
						mEmailView.requestFocus();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

}
