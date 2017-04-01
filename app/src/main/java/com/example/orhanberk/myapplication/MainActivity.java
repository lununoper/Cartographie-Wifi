package com.example.orhanberk.myapplication;
//  Bibliothèques

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

//  Déclaration des variables

public class MainActivity extends AppCompatActivity {
    private final int[] viewCoords = new int[2];
    private TextView text;
    private String valeurs;

//  Calcul de la distance de la borne wifi en fonction de la puissance de réception

    private double calculateDistance(double levelInDb, double freqInMhz) {

        double exp = (27.55 - (20 * Math.log10(freqInMhz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

// Retrouver la position X,Y

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.textView);                      //   Liaison de la variable "text" à l'objet graphique textView
        ImageView image = (ImageView) findViewById(R.id.imageView);
        image.getLocationOnScreen(viewCoords);                              //  Coordonnées de imageView
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int touchX = (int) event.getX();
                int touchY = (int) event.getY();

                int imageX = touchX - viewCoords[0];                        // X écran - image
                int imageY = touchY - viewCoords[1];                        // Y écran - image
                valeurs = "Position X = " + imageX + "    Position Y = " + imageY;
                text.setText(valeurs);                                      //   Affichage dans le textView


                //  WifiManager permet de chercher les bornes Wifi aux alentours
                final WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        List<ScanResult> results = wifi.getScanResults();   // Autorisation android manifest
                        int rssi = 100;                                     //  Valeurs rssi de test
                        int chan = 1;                                       //  Valeurs de test
                        String mac = "";

                        for (ScanResult s : results) {                      //  Recherche le point d'accès et récupère ses informations
                            if (s.SSID.equals("Etudiants-Paris12") & Math.abs(s.level) < rssi) {
                                rssi = Math.abs(s.level);
                                mac = s.BSSID;
                                chan = s.frequency;

                            }

                        } // Déterminer la distance en fonction de la puissance reçue
                        DecimalFormat df = new DecimalFormat("#,##");
                        text.setText("Etudiant-Paris12 BSSID : " + mac + " RSSI : " + rssi + " , Distance : " +
                                df.format(calculateDistance((double) rssi, chan)) + "m -- " + "Canal : " + chan);
                        text.setText("Canal : " + chan);


                    }


                }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)); // Filtre les informations récupérées

                wifi.startScan();

                return true;


            }


        });

    }
}
