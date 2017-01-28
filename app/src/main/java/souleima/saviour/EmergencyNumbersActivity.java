package souleima.saviour;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by pc on 1/26/2017.
 */

public class EmergencyNumbersActivity extends AppCompatActivity {

    private Button btPolice,btFirefighters,btEmergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_numbers);
        btPolice = (Button) findViewById(R.id.btPolice);
        btFirefighters = (Button) findViewById(R.id.btFirefighters);
        btEmergency = (Button) findViewById(R.id.btEmergency);

        btPolice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                call("20275748");
            }

        });
        btFirefighters.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                call("20275748");
            }

        });
        btEmergency.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                call("20275748");
            }

        });

    }

    //function that calls the 'number'
    public void call (String number){

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+number));
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }
}
