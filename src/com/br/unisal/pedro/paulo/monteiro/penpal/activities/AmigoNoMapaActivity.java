/**
 * @author Pedro Paulo Monteiro
 * @version 1.0
 * @since   2014-05-27 
 */
package com.br.unisal.pedro.paulo.monteiro.penpal.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.w3c.dom.Document;
import com.br.unisal.pedro.paulo.monteiro.penpal.R;
import com.br.unisal.pedro.paulo.monteiro.penpal.map.GMapV2Direction;
import com.br.unisal.pedro.paulo.monteiro.penpal.network.RequisicaoPenPal;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class AmigoNoMapaActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private GoogleMap map;
	private LatLng localizacaoDoAmigo;
	private Double mLatitude;
	private Double mLongitude;
	private String mNomeAmigo;
	private LocationClient mLocationClient;
	private LatLng minhaCoordenada;
	private ArrayList<LatLng> pontosRota;

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	};

	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amigo_no_mapa);

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		if (map == null) {
			Toast.makeText(this, "Google Maps não disponível",
					Toast.LENGTH_LONG).show();
		}
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setTrafficEnabled(true);
		map.setMyLocationEnabled(true);

		mLocationClient = new LocationClient(this, this, this);
		obterLocalizacaoAmigo();
		irAtePosicaoAmigoMapa();
		marcarPosicaoAmigoMapa();
	}

	private void obterLocalizacaoAmigo() {
		Intent minhaIntencao = getIntent();
		mLatitude = minhaIntencao.getDoubleExtra(
				RequisicaoPenPal.stringLatitude, 0);
		mLongitude = minhaIntencao.getDoubleExtra(
				RequisicaoPenPal.stringLongitude, 0);
		mNomeAmigo = minhaIntencao
				.getStringExtra(RequisicaoPenPal.stringNomeAmigo);
		localizacaoDoAmigo = new LatLng(mLatitude, mLongitude);
		Log.d("obterLocalizacaoAmigo", mNomeAmigo);
	}

	private void irAtePosicaoAmigoMapa() {
		Log.d("irAtePosicaoAmigoMapa", mNomeAmigo);
		CameraPosition posicaoCamera = new CameraPosition.Builder()
				.target(localizacaoDoAmigo).zoom(10).bearing(0).tilt(45)
				.build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(posicaoCamera));

	}

	private void marcarMinhaPosicaoMapa() {

		Location minhaLocalizacao = mLocationClient.getLastLocation();
		minhaCoordenada = new LatLng(minhaLocalizacao.getLatitude(),
				minhaLocalizacao.getLongitude());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.rotaateamigo:
			rotaAteAmigo();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void marcarPosicaoAmigoMapa() {
		Log.d("marcarPosicaoAmigoMapa", mNomeAmigo);
		String endereço = obterEndereço(this.mLatitude, this.mLongitude);
		map.addMarker(new MarkerOptions()
				.position(localizacaoDoAmigo)
				.title(mNomeAmigo)
				.snippet(endereço)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

	}

	private String obterEndereço(Double mLatitude, Double mLongitude) {

		Geocoder gcd = new Geocoder(this, Locale.getDefault());
		StringBuilder result = new StringBuilder();
		try {
			List<Address> addresses = gcd.getFromLocation(mLatitude,
					mLongitude, 100);
			if (addresses.size() > 0 && addresses != null) {
				result.append(addresses.get(0).getAddressLine(0) + ", "
						+ addresses.get(0).getSubAdminArea());
			}
			return result.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.amigo_no_mapa, menu);
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle arg0) {
		marcarMinhaPosicaoMapa();
		String endereço = obterEndereço(minhaCoordenada.latitude,
				minhaCoordenada.longitude);
		map.addMarker(new MarkerOptions()
				.position(minhaCoordenada)
				.title("Você")
				.snippet(endereço)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

	private void rotaAteAmigo() {

		AsyncTask<String, Void, Void> tarefa = new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... arg0) {

				GMapV2Direction md = new GMapV2Direction();
				Document doc = md.getDocument(minhaCoordenada,
						localizacaoDoAmigo);
				pontosRota = md.getDirection(doc);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				PolylineOptions linhaRota = new PolylineOptions().width(13)
						.color(Color.BLUE).geodesic(true);
				for (int i = 0; i < pontosRota.size(); i++) {
					linhaRota.add(pontosRota.get(i));
				}
				map.addPolyline(linhaRota);
				super.onPostExecute(result);
			}
		};
		tarefa.execute("");
	}

}
