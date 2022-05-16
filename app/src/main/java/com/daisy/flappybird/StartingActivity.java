package com.daisy.flappybird;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;

public class StartingActivity extends AppCompatActivity {

    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 0x00;

    private int volumeThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // громкость
        SharedPreferences settings = getPreferences(0);
        volumeThreshold = settings.getInt("VolumeThreshold", 50);
    }

    public void goToTouchActivity(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", "Touch");
        startActivity(intent);
    }

    public void goToVoiceActivity(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    RECORD_AUDIO_PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("Mode", "Voice");
            intent.putExtra("VolumeThreshold", volumeThreshold);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra("Mode", "Voice");
                    startActivity(intent);
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Permission Denied...");
                    alertDialog.setMessage("Without record audio permission granted, " +
                            "you cannot play with voice control.");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                        }
                    });
                    alertDialog.show();
                }
                return;
            }
        }
    }

    public void adjustVolumeThreshold(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("           восприятие голоса");
        View alertDialogView = LayoutInflater.from(this)
                .inflate(R.layout.alert_dialog_adjust_volume_threshold, null);
        NumberPicker numberPicker = (NumberPicker) alertDialogView.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(0);
        numberPicker.setValue(volumeThreshold);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker np, int oldValue, int newValue) {
                volumeThreshold = np.getValue();
            }
        });
        alertDialog.setView(alertDialogView);
        alertDialog.setPositiveButton("сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                SharedPreferences settings = StartingActivity.this.getPreferences(0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("VolumeThreshold", StartingActivity.this.volumeThreshold);
                editor.apply();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
