package com.example.evaluacion2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    EditText Rut;
    Button btnGrabar;
    EditText nombre;
    EditText problema;
    Button btnCerrarSinGrabar;
    private SensorManager sm;
    private boolean grabando = false;
    TextView fechaHora;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Rut = findViewById(R.id.rut);
        btnGrabar = findViewById(R.id.btn_grabar);
        nombre = findViewById(R.id.nombre);
        problema = findViewById(R.id.problema);
        fechaHora = findViewById(R.id.fecha_hora);
        btnCerrarSinGrabar = findViewById(R.id.btnCerrarSingrabar);

        btnCerrarSinGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        String currentDateTime = fechayHora();
        fechaHora.setText(currentDateTime);

        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarYGrabar();
            }
        });


        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    private String fechayHora() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float y = event.values[1];


        if (y > 9 && !grabando) {
            validarYGrabar();
            grabando = true;
        }


        if (y < 7) {
            grabando = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private void validarYGrabar() {
        String rutIngresado = Rut.getText().toString();
        String nombreIngresado = nombre.getText().toString();
        String problemaIngresado = problema.getText().toString();


        if (rutIngresado.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, ingrese el RUT.", Toast.LENGTH_SHORT).show();
        } else if (!validarRut(rutIngresado)) {
            Toast.makeText(MainActivity.this, "RUT inválido.", Toast.LENGTH_SHORT).show();
        } else if (nombreIngresado.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, ingrese su nombre.", Toast.LENGTH_SHORT).show();
        } else if (problemaIngresado.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, describa el problema.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Grabando...", Toast.LENGTH_SHORT).show();

        }
    }


    public boolean validarRut(String rut) {
        String rutLimpio = rut.replace(".", "").replace("-", "").toUpperCase();

        if (rutLimpio.length() < 8) {
            return false;
        }

        String rutNumero = rutLimpio.substring(0, rutLimpio.length() - 1);
        char dvIngresado = rutLimpio.charAt(rutLimpio.length() - 1);

        char dvCalculado = calcdigitoverificador(rutNumero);

        return dvIngresado == dvCalculado;
    }

    private char calcdigitoverificador(String rutNumero) {
        int suma = 0;
        int multiplicador = 2;

        for (int i = rutNumero.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(rutNumero.charAt(i)) * multiplicador;
            multiplicador = (multiplicador < 7) ? multiplicador + 1 : 2;
        }

        int residuo = 11 - (suma % 11);

        switch (residuo) {
            case 11:
                return '0';
            case 10:
                return 'K';
            default:
                return (char) (residuo + '0');
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        sm.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }
}
