package com.example.myprojecqrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Info_BD extends AppCompatActivity {

    Button Scan_btn, Alta_btn, Baja_btn, Confirmar_btn;
    TextView txt1, txt2, txt3, txt4, txt5;
    ImageView img1;
    RequestQueue requestQueue;
    EditText mod_stock;
    String num_interno, nombre_usuario, accion = "", spinner;
    Spinner list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_bd);

        Scan_btn = findViewById(R.id.Scan);
        num_interno = getIntent().getStringExtra("num_interno");
        nombre_usuario = getIntent().getStringExtra("nombre_usuario");

        Alta_btn = findViewById(R.id.Alta_con);
        Baja_btn = findViewById(R.id.Baja_con);
        mod_stock = findViewById(R.id.Mod_stock);
        txt5 = findViewById(R.id.Pregunta);
        Confirmar_btn = findViewById(R.id.Confirmar);
        list = findViewById(R.id.spinner);
        String opciones [] = {"Material en kit", "Daño y/o extravio", "Mantenimiento", "Personal de ingeniería"};
        ArrayAdapter <String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, opciones);
        list.setAdapter(adapter);

        img1 = findViewById(R.id.Img_con);
        txt1 = findViewById(R.id.Num_interno);
        txt2 = findViewById(R.id.Nombre_con);
        txt3 = findViewById(R.id.Descripcion);
        txt4 = findViewById(R.id.Stock);

        MostrarDatos("http://192.168.1.7/condunet.mx/mostrar_datos.php?numero_interno="+num_interno);

        Scan_btn.setOnClickListener(v -> {
            Intent login_exitoso = new Intent(v.getContext(), ScanQR.class);
            login_exitoso.putExtra("nombre_usuario", nombre_usuario);
            startActivity(login_exitoso);
            finish();
        });

        String modificacion = mod_stock.getText().toString();
        String num = txt1.getText().toString();

        if(modificacion.length() == 0) {
            mod_stock.setError("Campo obligatorio");
            mod_stock.requestFocus();
        }

        Alta_btn.setOnClickListener(v -> {
            if (num.length() == 0) {
                Toast.makeText(getApplicationContext(), "INTRODUZCA UN CÓDIGO QR", Toast.LENGTH_LONG).show();
            } else {
                accion = "Alta";
                spinner = "Entrada de material";
                Ejecutar_servicio("http://192.168.1.7/condunet.mx/alta_datos.php");
                txt5.setVisibility(View.INVISIBLE);
                list.setVisibility(View.INVISIBLE);
                Confirmar_btn.setVisibility(View.INVISIBLE);
            }
        });

        Baja_btn.setOnClickListener(v -> {
            if (num.length() == 0) {
                Toast.makeText(getApplicationContext(), "INTRODUZCA UN CÓDIGO QR PRIMERO", Toast.LENGTH_LONG).show();
            } else {
                txt5.setVisibility(View.VISIBLE);
                list.setVisibility(View.VISIBLE);
                Confirmar_btn.setVisibility(View.VISIBLE);
            }
        });

        Confirmar_btn.setOnClickListener(v -> {
            accion = "Baja";
            spinner = list.getSelectedItem().toString();
            Ejecutar_servicio("http://192.168.1.7/condunet.mx/baja_datos.php");
        });
    }

    public void MostrarDatos(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, (response) -> {
            JSONObject jsonObject;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    byte[] byteCode = Base64.decode(jsonObject.getString("imagen"), Base64.DEFAULT);
                    Bitmap imagen = BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
                    img1.setImageBitmap(imagen);
                    txt1.setText("MF/G: "+jsonObject.getString("numero_interno"));
                    txt2.setText("Conector: "+jsonObject.getString("nombre"));
                    txt3.setText("Existencias: "+jsonObject.getString("cantidad"));
                    txt4.setText("Descripción: "+jsonObject.getString("descripcion"));
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            //Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN", Toast.LENGTH_LONG).show();
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void Ejecutar_servicio(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "OPERACIÓN EXITOSA", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
          @Override
          protected Map<String, String> getParams() throws AuthFailureError{
              Map<String, String> parametros = new HashMap<String, String>();
              parametros.put("numero_interno", num_interno);
              parametros.put("cantidad", mod_stock.getText().toString());
              parametros.put("motivo", spinner);
              parametros.put("nombre_usuario", nombre_usuario);
              parametros.put("accion", accion);
              return parametros;
          }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}