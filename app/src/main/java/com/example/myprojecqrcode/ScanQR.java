package com.example.myprojecqrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        IntentIntegrator scanner = new IntentIntegrator(ScanQR.this);
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        scanner.setPrompt("Lector - CDP");
        scanner.setCameraId(0);
        scanner.setBeepEnabled(true);
        scanner.setBarcodeImageEnabled(true);
        scanner.initiateScan();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Intent regresar = new Intent(this, Info_BD.class);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "Lectura cancelada", Toast.LENGTH_LONG).show();
                finish();
            } else {
                String nombre_usuario = getIntent().getStringExtra("nombre_usuario");
                regresar.putExtra("num_interno", result.getContents());
                regresar.putExtra("nombre_usuario", nombre_usuario);
                startActivity(regresar);
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}