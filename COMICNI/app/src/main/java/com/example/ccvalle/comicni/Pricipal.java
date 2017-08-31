package com.example.ccvalle.comicni;
//Clase principal
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class Pricipal extends AppCompatActivity  {
    //declaracion de variables globales
    //variables de tipo boolean para validacion de eventos
    boolean boton_seleccionado_adelante=false;
    boolean boton_seleccionado_atras=false;
    boolean boton_seleccionado_buscar=false;
    //declaracion de los botones de la interfaz para enlazar
    Button button_atras;
    Button button_adelante;
    Button Search;
    //variable tipo de la clase TouchImageView
    TouchImageView imagen;
    //Declaracion de los TextView para enlazar con la interfaz
    TextView texttitle;
    TextView textnumero;
    EditText buscar;
    //variable de tipo Comic con todos sus atributos
    Comic c;
    //variable donde se guarda lo recibido por la barra de busqueda
    int numero_buscar;
    //variable donde se guarda el numero del comic del dia, es 0 no se ha interactuado con la aplicacion
    int id_actual=0;
    //variable que guarda el id cambiante de los comic segun como ocurran los eventos
    int id;
    //Url para obtener el comic mas reciente
    String url = "https://xkcd.com/info.0.json";
    //URL para obtener un comic especifico
    String url1;
    //variable para imprimir en pantalla el numero del comic en que se encuentra
    String numero;
    //ProgressDialog para indicar al usuario que se estan cargando los datos
    ProgressDialog progressDialog_barra=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricipal);

        //Creando un nuevo objeto ProgressDialog
        progressDialog_barra = new ProgressDialog(this);
        //enlazando con los elementos del xml
        textnumero = (TextView) findViewById(R.id.numero);
        texttitle = (TextView) findViewById(R.id.title);
        imagen = (TouchImageView) findViewById(R.id.comic);
        button_atras = (Button) findViewById(R.id.f1);
        button_adelante = (Button) findViewById(R.id.f2);
        buscar = (EditText) findViewById(R.id.buscar);
        Search = (Button) findViewById(R.id.Buton_buscar);
        //Ejecutando de forma asincrona la clase JsonTask para el cargado y pintado de datos
        //como estamos en el onCreate(), es decir lo primmero que se ejecuta, mandamos como parametro el URL para obtener el comic mas actual
        new JsonTask().execute(url);


        //Evento escuchador de los click sobre el boton Search
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Esondiendo el teclado para poder apreciar la pantalla completa y los mensajes que se muestren
                InputMethodManager imn = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imn.hideSoftInputFromWindow(buscar.getWindowToken(), 0);
                //validando si el EditText de buscar esta vacio
                if (buscar.getText().toString().isEmpty()) {
                    //usando el Mensaje de Material desing SnakBar
                    //mensaje : no se ingreso ningun digito
                    Snackbar.make(textnumero, ("Por favor ingrese un numero"), Snackbar.LENGTH_SHORT).show();
                } else {
                    //si el EditText no esta vacio, procedemos a obtener la informacion y convertirla a entero
                    numero_buscar = Integer.parseInt(buscar.getText().toString());
                    //si el numero  a buscar en menor que o mayor que el numero del comic mas actual, no existe
                    if (numero_buscar < 1 || numero_buscar > id_actual) {
                        //mandando mensaje con Snakbar
                        Snackbar.make(textnumero, ("El comic #" + numero_buscar + " no existe"), Snackbar.LENGTH_SHORT).show();
                    } else {
                        //Si el numero esta en un rango valido de valores, procedemos a construir el URL con el numero del comic deseado
                        url1 = "https://xkcd.com/" + numero_buscar + "/info.0.json";
                        //asignamos verdadero a la variable que  valida que el boton de buscar fue activado
                        boton_seleccionado_buscar = true;
                        //prodedemos a ejecutar la clase asincrona con el URL del comic deseado
                        new JsonTask().execute(url1);
                    }
                    //Limpiamos el EditText para que el usario no tenga que borrar
                    buscar.setText("");
                }
            }
        });


        //Evento escucha del boton atras
        button_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si el boton atras se presiona al numero del comic se le resta 1
                id--;
                //Indicamos que el boton atras fue seleccionado y no adelante
                boton_seleccionado_atras = true;
                boton_seleccionado_adelante = false;
                //Construimos el URL con el numero -1
                url1 = "https://xkcd.com/" + id + "/info.0.json";
                //Desactivamos los click de los 3 botones para que el usuario no pueda interacturar con ellos mientras se cargan los datos
                button_atras.setClickable(false);
                button_adelante.setClickable(false);
                Search.setClickable(false);
                //Ejecutamos la clase asincrona para el cargado de datos
                new JsonTask().execute(url1);
            }
        });


       //Evento escucha del boton adelante
        button_adelante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si se presiona el boton adelante al numero actual de comic se suma 1
                id++;
                //se indica que el seleccionado es el boton adelante y no atras
                boton_seleccionado_atras = false;
                boton_seleccionado_adelante = true;
                //se construye el URL con el numero-1
                //se bloquea el click de los 3 botones
                url1 = "https://xkcd.com/" + id + "/info.0.json";
                button_atras.setClickable(false);
                button_adelante.setClickable(false);
                Search.setClickable(false);
                //se ejecuta la clase asincrona
                new JsonTask().execute(url1);
            }
        });


    }


    //Meotodo que retorna si hay conexion a internet o no
    public static boolean isOnline(Context context) {
        //Creando objetos para la comprobacion
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //solo si las 3 variables son verdaderas habra internet, si alguna es false no existe conexion
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }



    //Clase donde se ejecuta el cuerpo de la aplicacio
    //es asincrona por que extiende de AsynTask
    private class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
           //En este metodo de la clase se ejecutan las acciones que anteceden a el cargado de los datos
            //Inflamos la activad que contiene la ProgressDialog personalizada
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            View inflatedView = inflater.inflate(R.layout.layout_barra,null, false);
            //Mostramos el ProgressDialog
            progressDialog_barra.show();
            //Lo enlazamos con el XML del ProgressDialog personalizado
            progressDialog_barra.setContentView(inflatedView);
            //Esta barra estara visible mientras se realiza el hilo secundario
        }


        protected String doInBackground(String... params) {
            //En este metodo se ejecta las setencias que obtienen los datos, esta se realiza en un hilo secundario
            //procedemos a hacer la solicitud de lo datos
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);
                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //En este metodo se realiza comprueban si se obtuvieron los datos
            //Se obtiene si no se recibio ningun dato
            //si el String donde se encuentran la estructura Json es diferente de null, entonces si se obtubieron los datos
            if (result != null) {
                Log.d("JSON", result);
                //Procedemos a transforma ese String en un Objeto de tipo de la clase Comic
                //Creamos un objeto Gson
                Gson g = new Gson();
                //Transformamos
                c = g.fromJson(result, Comic.class);
                //si id_actual es cero, quiere decir que se mostrara el comic mas reciente
                if (id_actual == 0)
                    //asignamos al id_actual el numero del comic mas reciente, ya obtenido el Objeto Json en la clase Comic
                    id_actual = c.num;
                //el id se va actualizado, dependiento del comic que se muestre
                id = c.num;

                Log.d("JSON", "COMIC: " + c.toString());
                //Mostramos el la interfaz el titulo del comic
                texttitle.setText(c.title);
                numero = "Comic #" + c.num;
                //Mostramos el nuemro del comic
                textnumero.setText(numero);
                //Con la libreria externa Picaso, obtenemos cargamos la imagen, del link que brinda Json
                //Tambien ubicamos una imagen mientras la imagen principal carga, ademas de una en caso que la carga no se realize
                Picasso.with(Pricipal.this).load(c.img).placeholder(R.drawable.cargando).error(R.drawable.attention).into(imagen);
                //Ya cargado los elementos, procedemos a activar los click de los botones para que el usario pueda interactura
                button_atras.setClickable(true);
                button_adelante.setClickable(true);
                Search.setClickable(true);
                //Originalmente los botones estaban invisibles, procedemos a ponerlos viisbles al usuario
                Search.setVisibility(View.VISIBLE);
                buscar.setVisibility(View.VISIBLE);

                //si el id del comic que se muestra condide con el id del comid mas actual, significa que por el momento no hay comic posteriores
               //solo dejamos viisble el boton atras
                if (id == id_actual) {
                    button_adelante.setVisibility(View.INVISIBLE);
                } else {
                    button_adelante.setVisibility(View.VISIBLE);
                }


                //por defecto el primer comic es el, si el id del comic que se muestra, coincide con el id nuemero 1 , no existen comic anteriores
                //solo dejamos visible el boton adelante
                if (id == 1) {
                    button_atras.setVisibility(View.INVISIBLE);

                } else {
                    button_atras.setVisibility(View.VISIBLE);
                }

                //en caso que el comic a mostrar haya sido seleccionado por el buscador, entonces indicamos que ya fue cargado el comic poniendo en faslo la seleccion del boton buscar

                if (boton_seleccionado_buscar) {
                    boton_seleccionado_buscar = false;
                }


            } else {
                //si no se obtuvo ningun dato, puedo ser un error de conexion o el sitio no existe, aqui validamos eso
                //si no esta conecato
                if (!isOnline(getBaseContext())) {
                    //indicamos que no hay conexion con un Snakbar y agregamos un boton a este para intertar reconectar si el usuario lo desea
                    Snackbar.make(texttitle, "No hay coneccion a internet", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Reintentar", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //si el usuario intento reconectarse mediante el boton del snakbar
                                    //tenemos 3 opciones para mandar el link correcto
                                    //si id_actual=0, entonces no se cargo el comic del dia, entonces mandamos el link para recargar el comic mas reciente
                                    if(id_actual==0)
                                        url1="https://xkcd.com/info.0.json";
                                    else
                                    //en caso que el comic a cargar cuando fallo la conexion no fue el inicial, mandamos el link
                                    //de el id del comic especifico
                                    url1 = "https://xkcd.com/" + id + "/info.0.json";
                                    //si el comic a cargar se escogio mediante el buscador, comprobamos si el boton buscar fue activamos
                                    //y construiomos el URL de acuerdo al numero que se ingreso en la busqueda
                                    if(boton_seleccionado_buscar)
                                        url1 = "https://xkcd.com/" + numero_buscar + "/info.0.json";
                                    //si existe un ProgressDialog corriente, lo cerramos
                                    if (progressDialog_barra.isShowing())
                                        progressDialog_barra.dismiss();
                                    new JsonTask().execute(url1);
                                    //ejecutamos de nuevo la misma clase, es decir recursividad
                                    //y va a parar hasta que se tenga acceso a interet, o se cierre la aplicacion
                                    //de esta forma evitamos posibles errores en la aplicacion, con URL no obtenidos
                                }
                            }).show();//Mostramos el Snakbar
                  //Si no se logro cargar los datos correctamente, pero hay internet, siginifica que ese comid especifico no existe
                    //de esta manera tambien se valida el 404, sin necesidad de poner esto en el codigo, solo con la respuesta que da el api
                } else {
                    //comprobamos si el comic invalido se escoge mediante el buscador
                    if (boton_seleccionado_buscar) {
                        //al id del comic le asignamos el numero que se ingreso para buscar
                        id = numero_buscar;
                        //y indicamos que ya fue comprobado y ponemos que el boton ya no se encuentra seleccionado
                        boton_seleccionado_buscar = false;
                    }
                    //mandamos el mensaje que el comic especificado no existe
                    Snackbar.make(textnumero, ("El comic #" + id + " no existe"), Snackbar.LENGTH_SHORT).show();
                    //mandamos a comprobar si existe el comic anterior o posterior al inexistente
                    //si el ultimo boton seleccionado fue el de adelante o no se ha presionado ninguno, le sumamos
                    //al id del comic no encontrado para pasar al siguiente
                    if ((boton_seleccionado_adelante) || (boton_seleccionado_adelante == false && boton_seleccionado_atras == false))
                        id++;
                    //en caso el boton atras fue el ultimo seleccionado, restamos al id del comic no encontrado
                    if (boton_seleccionado_atras)
                        id--;
                    //construimos el URL con el id del comic siguiente o anterior del no encontrado
                    url1 = "https://xkcd.com/" + id + "/info.0.json";
                    //y volvemos a llamar a la clase, de nuevo en forma recursiva para que siga validando en caso de no encontrar
                    //el comic anterior o posterior
                    //la recursividad solo se rompe si se encuentra un comic valido
                    //jamas llegara al id=0 o id> que el comic del dia
                    //por las validaciones anteriores
                    //ya que tienen que existir la mayoria de los comic, la recursividad se rompe en determinado momento
                    //esta recursividad valida posibles comis posteriores vacios
                    new JsonTask().execute(url1);

                }

            }
            //si existe un ProgressDialog mostrandose, lo quitamos
            if (progressDialog_barra.isShowing())
                progressDialog_barra.dismiss();
        }
    }

}
//este es mi codigo, hice todas las validaciones que mire necesarias