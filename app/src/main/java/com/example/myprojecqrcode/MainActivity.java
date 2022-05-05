package com.example.myprojecqrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button LoginOn_btn;
    EditText usu_txt, pass_txt;
    Intent login_exitoso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usu_txt = findViewById(R.id.UserName);
        pass_txt = findViewById(R.id.UserPassword);
        LoginOn_btn = findViewById(R.id.LoginOn);

        LoginOn_btn.setOnClickListener(v -> {
            String usu = usu_txt.getText().toString();
            String pass = pass_txt.getText().toString();

            if(usu.length() == 0){
                usu_txt.setError("Introduzca su nombre de usuario");
                usu_txt.requestFocus();
            }
            if (pass.length() == 0){
                pass_txt.setError("Introduzca su contraseña");
                pass_txt.requestFocus();
            }
            if(usu.length() != 0 && pass.length() != 0){
                ValidarUsuario("http://192.168.1.7/condunet.mx/validar_usuarios.php");
            }
        });
    }

    private void ValidarUsuario(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            if (!response.isEmpty()) {
                login_exitoso = new Intent(getApplicationContext(), Info_BD.class);
                login_exitoso.putExtra("nombre_usuario", usu_txt.getText().toString());
                startActivity(login_exitoso);
            } else
                Toast.makeText(getApplicationContext(), "USUARIO Y/O CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("usuario", usu_txt.getText().toString());
                parametros.put("password", pass_txt.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}