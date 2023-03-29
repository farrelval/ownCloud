package com.example.owncloud;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class CloudList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GridView gridView;
    ArrayList<Cloud> list;
    CloudListAdapter adapter = null;
    SQLiteHelper db;
    ImageView imageViewCloud;
    FloatingActionButton floating_btn;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud_list_activity);

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new CloudListAdapter(this, R.layout.cloud_items, list);
        gridView.setAdapter(adapter);
        floating_btn = (FloatingActionButton) findViewById(R.id.floating_btn);


        /*----------------- FLOATING BUUTON --------------------*/
        floating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CloudList.this, UploadActivity.class);
                startActivity(intent);
            }
        });
        /*----------------- FLOATING BUUTON --------------------*/


        /*----------------- ACTION TOOLBAR AND NAVIGATION DRAWER --------------------*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        /*----------------- ACTION TOOLBAR AND NAVIGATION DRAWER --------------------*/


        SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "CloudDb.sqlite", null, 1);
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM CLOUD");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String format = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new Cloud(name, format, image, id));
        }
        adapter.notifyDataSetChanged();


        /*----------------- DIALOG MESSAGE IN GRIDVIEW --------------------*/
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(CloudList.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // update
                            Cursor c = sqLiteHelper.getData("SELECT id FROM CLOUD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            // show dialog update at here
                            showDialogUpdate(CloudList.this, arrID.get(position));

                        } else {
                            // delete
                            Cursor c = sqLiteHelper.getData("SELECT id FROM CLOUD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));

                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
        /*----------------- DIALOG MESSAGE IN GRIDVIEW --------------------*/
    }

    /*----------------- OPTION NAVIGATION DRAWER --------------------*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Home_activity()).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new About_activity()).commit();
                break;
            case R.id.nav_privacy_police:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PrivacyPolice_activity()).commit();
                break;
            case R.id.nav_faq:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Faq_activity()).commit();
                break;
            case R.id.nav_version:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Version_activity()).commit();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*----------------- OPTION NAVIGATION DRAWER --------------------*/


    /*----------------- ONBACKPRESSED --------------------*/
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        finishAffinity();
        finish();
    }
    /*----------------- ONBACKPRESSED --------------------*/


    /*----------------- ACTION UPDATE --------------------*/
    private void showDialogUpdate(Activity activity, final int position) {

        SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "CloudDb.sqlite", null, 1);

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_cloud_activity);
        dialog.setTitle("Update");

        imageViewCloud = (ImageView) dialog.findViewById(R.id.imageView);
        final EditText edtName = (EditText) dialog.findViewById(R.id.edtName);
        final EditText edtFormat = (EditText) dialog.findViewById(R.id.edtFormat);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();
        /*----------------- ACTION UPDATE --------------------*/



        /*----------------- GET IMAGE FROM EXTERNAL STORAGE --------------------*/
        imageViewCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
                ActivityCompat.requestPermissions(
                        CloudList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });
        /*----------------- GET IMAGE FROM EXTERNAL STORAGE --------------------*/


        /*----------------- BUTTON UPDATE --------------------*/
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    sqLiteHelper.updateData(
                            edtName.getText().toString().trim(),
                            edtFormat.getText().toString().trim(),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update successfully!!!", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(CloudList.this, CloudList.class);
                    startActivity(intent);
                } catch (Exception error) {
                    Log.e("Update error", error.getMessage());
                }

            }
        });
        /*----------------- BUTTON UPDATE --------------------*/

    }

    /*----------------- ACTION DELETE --------------------*/
    private void showDialogDelete(final int idCloud) {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(CloudList.this);
        SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "CloudDb.sqlite", null, 1);
        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to this delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sqLiteHelper.deleteData(idCloud);
                    Toast.makeText(getApplicationContext(), "Delete successfully!!!", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(CloudList.this, CloudList.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }

            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }
    /*----------------- ACTION DELETE --------------------*/


    private void updateFoodList() {
        // get all data from sqlite
        Cursor cursor = db.getData("SELECT * FROM CLOUD");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String format = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new Cloud(name, format, image, id));
        }
        adapter.notifyDataSetChanged();
    }

    /*----------------- PERMISSIONS REQUEST --------------------*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    /*----------------- PERMISSIONS REQUEST --------------------*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 888 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewCloud.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
