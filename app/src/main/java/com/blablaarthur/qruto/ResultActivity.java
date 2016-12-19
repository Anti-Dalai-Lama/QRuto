package com.blablaarthur.qruto;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;

public class ResultActivity extends AppCompatActivity {

    TextView tw;
    TextView f1;
    TextView f2;
    TextView f3;
    TextView f4;
    TextView f5;
    TextView f6;
    Button button;

    Barcode barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tw = (TextView) findViewById(R.id.title);
        f1 = (TextView)  findViewById(R.id.field1);
        f2 = (TextView)  findViewById(R.id.field2);
        f3 = (TextView)  findViewById(R.id.field3);
        f4 = (TextView)  findViewById(R.id.field4);
        f5 = (TextView)  findViewById(R.id.field5);
        f6 = (TextView)  findViewById(R.id.field6);
        button = (Button) findViewById(R.id.actionButton);

        Intent intent = getIntent();
        barcode = intent.getParcelableExtra("barcode");
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            switch (barcode.valueFormat) {
                case Barcode.URL:
                    tw.setText("URL");
                    f1.setText(barcode.rawValue);
                    button.setText("Open in Browser");
                    break;
                case Barcode.CONTACT_INFO:
                    tw.setText("CONTACT");
                    f1.setText("First Name: " + barcode.contactInfo.name.first);
                    f2.setText("Middle Name: " + barcode.contactInfo.name.middle);
                    f3.setText("Last Name: " + barcode.contactInfo.name.last);
                    Barcode.Phone ph = barcode.contactInfo.phones[0];
                    if (ph != null) {
                        f4.setText("Phone: " + ph.number);
                    }
                    Barcode.Email em = barcode.contactInfo.emails[0];
                    if (em != null) {
                        f5.setText("Email: " + em.address);
                    }
                    break;
                case Barcode.SMS:
                    tw.setText("SMS");
                    f1.setText("Number: " + barcode.sms.phoneNumber);
                    f2.setText("Message: " + barcode.sms.message);
                    break;
                case Barcode.GEO:
                    tw.setText("GEO");
                    f1.setText("Latitude: " + barcode.geoPoint.lat);
                    f2.setText("Longitude: " + barcode.geoPoint.lng);
                    button.setVisibility(View.INVISIBLE);
                    break;
                case Barcode.PHONE:
                    tw.setText("PHONE");
                    f1.setText("Number: " + barcode.phone.number);
                    button.setText("Call");
                    break;
                case Barcode.EMAIL:
                    tw.setText("EMAIL");
                    f1.setText("Address: " + barcode.email.address);
                    f2.setText("Subject: " + barcode.email.subject);
                    f3.setText("Body: " + barcode.email.body);
                    button.setText("Send");
                    break;
                case Barcode.WIFI:
                    tw.setText("WI-FI");
                    f1.setText("SSID: " + barcode.wifi.ssid);
                    switch (barcode.wifi.encryptionType) {
                        case Barcode.WiFi.OPEN:
                            f2.setText("Security: No");
                            break;
                        case Barcode.WiFi.WEP:
                            f2.setText("Security: WEP");
                            f3.setText("Passowrd: " + barcode.wifi.password);
                            break;
                        case Barcode.WiFi.WPA:
                            f2.setText("Security: WPA");
                            f3.setText("Passowrd: " + barcode.wifi.password);
                            break;
                    }
                    button.setVisibility(View.INVISIBLE);
                    break;
                case Barcode.TEXT:
                    tw.setText("TEXT");
                    f1.setText(barcode.rawValue);
                    button.setVisibility(View.INVISIBLE);
                    break;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void action(View v){
        try {
            switch (barcode.valueFormat) {
                case Barcode.URL:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(barcode.rawValue));
                    startActivity(browserIntent);
                    break;
                case Barcode.CONTACT_INFO:
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, barcode.contactInfo.name.formattedName);
                    if (barcode.contactInfo.emails[0] != null) {
                        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, barcode.contactInfo.emails[0].address);
                    }
                    if (barcode.contactInfo.phones[0] != null) {
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, barcode.contactInfo.phones[0].number);
                    }
                    startActivity(intent);
                    break;
                case Barcode.SMS:
                    Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", barcode.sms.phoneNumber);
                    smsIntent.putExtra("sms_body", barcode.sms.message);
                    startActivity(smsIntent);
                    break;
                case Barcode.PHONE:
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + barcode.phone.number));
                        startActivity(phoneIntent);
                    }
                    break;
                case Barcode.EMAIL:
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", barcode.email.address, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, barcode.email.subject);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, barcode.email.body);
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    break;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}