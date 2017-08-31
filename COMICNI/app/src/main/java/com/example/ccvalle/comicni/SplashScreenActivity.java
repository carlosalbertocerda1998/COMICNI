package com.example.ccvalle.comicni;
//Clse del splash screen
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;


public class SplashScreenActivity extends AppCompatActivity {
    //Tiempo que durara el splash screen en milisegundos
    private static int SPLASH_TIME_OUT = 3000;
    //Variable de tipo Window para cambiar los colores  a la ventana
    private Window window;

    @Override
    //Creacion de la actividad
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //Obteniendo la ventana
        window = getWindow();
        //Cambiando los colores  a la ventana, RGB
        window.setStatusBarColor(Color.rgb(165, 163, 163));

        //Funcion que ejecuta una actividad en segundo plano
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    //Iniciando la actividad principal para ejecutarse en segundo plano
                   //Mientras esta desplegado el splash screen
                    Intent i = new Intent(SplashScreenActivity.this, Pricipal.class);
                    startActivity(i);

                    // close esta activity
                    finish();
                }


            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */



    }, SPLASH_TIME_OUT);
    }
}
