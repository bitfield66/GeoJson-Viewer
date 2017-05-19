/*
 * Copyright 2017 Nextome S.r.l
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nextome.geojsonviewer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private Uri fileUri;
    private View mapPickerView;
    private View welcomeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        welcomeView = findViewById(R.id.activity_main_layout_welcome);
        mapPickerView = findViewById(R.id.activity_main_layout_map_picker);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(getApplicationContext(), R.string.activity_main_ask_permission, Toast.LENGTH_LONG).show();
                askPermissions();
            } else {
                askPermissions();
            }
        }
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    public void openGoogleMaps(View v) {
        openMap(GoogleMapsActivity.class);
    }

    public void openOsm(View v) {
        openMap(OpenStreetMapActivity.class);
    }

    public void openMapbox(View v) {
        openMap(MapBoxActivity.class);
    }

    public void openFilePicker(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void openMap(Class<?> activity) {
        if (fileUri != null) {
            Intent mapsIntent = new Intent(this, activity);
            mapsIntent.putExtra(GeoJsonViewerConstants.INTENT_EXTRA_JSON_URI, fileUri.toString());
            startActivity(mapsIntent);
        } else {
            Toast.makeText(getApplicationContext(), R.string.geojson_opener_unable_to_read, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void openNextomeWebsite(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.nextome.net"));
        startActivity(browserIntent);
    }

    public void onBackPressed(View v) {
        if (mapPickerView.getVisibility() == View.VISIBLE){
            welcomeView.setVisibility(View.VISIBLE);
            mapPickerView.setVisibility(View.INVISIBLE);
        } else if (welcomeView.getVisibility() == View.VISIBLE){
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onBackPressed(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                fileUri = resultData.getData();
                mapPickerView.setVisibility(View.VISIBLE);
                welcomeView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted

                } else {
                    finish();
                }
                return;
            }
        }
    }
}
