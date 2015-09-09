package felipe.cl.spm.actividades;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import felipe.cl.spm.R;
import felipe.cl.spm.actividades.extras.HttpFileUploader;

public class FormValidacionVisual extends Activity implements View.OnClickListener, SignaturePad.OnSignedListener {
    ImageButton btn_back, btn_save, btn_camera, btn_picture;
    Button btn_borrar, btn_bloquear;
    ImageView image_secure, firma_preview;
    LinearLayout blocked;
    public static Activity activity;
    String rut;
    Context mContext;

    SignaturePad signaturePad;
    final CharSequence[] opcionCaptura = {
            "Tomar Fotografía"
    };
    private Bitmap b = null;
    private Bitmap firma = null;
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    String name = Environment.getExternalStorageDirectory() + "/SPM/"; //picture filename

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_form_validacion_visual);

        activity = this;
        mContext = this;
        rut = getIntent().getStringExtra("RUT");
        signaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        signaturePad.setOnSignedListener(this);

        image_secure = (ImageView) findViewById(R.id.iv_secure);
        firma_preview = (ImageView) findViewById(R.id.firma_prev);

        btn_back = (ImageButton) findViewById(R.id.ib_back);
        btn_save = (ImageButton) findViewById(R.id.ib_save);
        btn_camera = (ImageButton) findViewById(R.id.ib_camera);
        btn_picture = (ImageButton) findViewById(R.id.ib_picture);
        btn_borrar = (Button) findViewById(R.id.ib_borrar);
        btn_bloquear = (Button) findViewById(R.id.ib_bloquear);

        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        btn_borrar.setOnClickListener(this);
        btn_bloquear.setOnClickListener(this);

        btn_picture.setEnabled(false);
        btn_borrar.setEnabled(false);

    }

    private void subirFoto() {
        String nameFoto = rut + "_FOTO.png";
        UploadImage up = new UploadImage(mContext, null, b, nameFoto);
        up.execute();
    }


    private void subirFirma() {
        String nameFirma = rut + "_FIRMA.png";
        UploadImage up = new UploadImage(mContext, null, firma, nameFirma, 1);
        up.execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_back) {
            finish();
        }
        if (v.getId() == R.id.ib_save) {
            if(b == null){
                Toast.makeText(mContext, "Debe tomar una fotografia al carnet del Cliente", Toast.LENGTH_LONG).show();
                return;
            }
            if(firma == null){

                Toast.makeText(mContext, "Debe tomar la firma del Cliente", Toast.LENGTH_LONG).show();
                return;
            }
            subirFoto();

        }
        if (v.getId() == R.id.ib_borrar) {
            signaturePad.clear();
            firma = null;
            if (signaturePad.getVisibility() == View.GONE) {
                firma_preview.setVisibility(View.GONE);
                signaturePad.setVisibility(View.VISIBLE);
                image_secure.setVisibility(View.GONE);
                btn_bloquear.setText("BLOQUEAR\nPANEL");
            }

        }
        if (v.getId() == R.id.ib_bloquear) {
            if (signaturePad.getVisibility() == View.VISIBLE) {
                firma = signaturePad.getSignatureBitmap();
                signaturePad.setVisibility(View.GONE);
                firma_preview.setImageBitmap(firma);
                firma_preview.setVisibility(View.VISIBLE);
                image_secure.setVisibility(View.VISIBLE);
                btn_bloquear.setText("DESBLOQUEAR\nPANEL");
            } else {
                signaturePad.setVisibility(View.VISIBLE);
                firma_preview.setVisibility(View.GONE);
                image_secure.setVisibility(View.GONE);
                btn_bloquear.setText("BLOQUEAR\nPANEL");
            }
        }

        if (v.getId() == R.id.ib_camera) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Escoja una Opción:");
            builder.setIcon(android.R.drawable.ic_menu_camera);
            builder.setItems(opcionCaptura, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    int code = TAKE_PICTURE;
                    if (item == TAKE_PICTURE) {
                        Uri output = Uri.fromFile(new File(name));
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                    } else if (item == SELECT_PICTURE) {
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        code = SELECT_PICTURE;
                    }
                    startActivityForResult(intent, code);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        if (v.getId() == R.id.ib_picture) {
            AlertDialog.Builder dialog_preview = new AlertDialog.Builder(this);
            dialog_preview.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            View preview_view = LayoutInflater.from(this).inflate(R.layout.view_preview, null, false);
            ImageView IVpreview = (ImageView) preview_view.findViewById(R.id.preview);
            IVpreview.setImageBitmap(b);
            dialog_preview.setView(preview_view);
            dialog_preview.setTitle("Vista Previa");
            dialog_preview.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    b = (Bitmap) data.getParcelableExtra("data");
                }
            } else {
                b = BitmapFactory.decodeFile(name);

            }
        } else if (requestCode == SELECT_PICTURE) {
            Uri selectedImage = data.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                b = BitmapFactory.decodeStream(bis);

            } catch (FileNotFoundException e) {
            }
        }
        try {
            b = Bitmap.createScaledBitmap(b, 640, 480, true);
            btn_picture.setEnabled(true);
        } catch (Exception ex) {
        }


    }

    @Override
    public void onSigned() {
        btn_borrar.setEnabled(true);
    }

    @Override
    public void onClear() {
        btn_borrar.setEnabled(false);
    }


    private class UploadImage extends AsyncTask<String, String, String> {

        private Context mContext;
        ProgressDialog dialog;
        View view;
        Bitmap bitmap;
        String name;
        int flag= 0;

        public UploadImage(Context mContext, View v, Bitmap foto, String n) {
            name = n;
            bitmap = foto;
            this.view = v;
            this.mContext = mContext;
        }


        public UploadImage(Context mContext, View v, Bitmap foto, String n, int i) {
            flag = i;
            name = n;
            bitmap = foto;
            this.view = v;
            this.mContext = mContext;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            dialog.setMessage(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";


            try {
                String fileName = name;

                publishProgress(fileName);
                Log.i("ENVIANDO", fileName);
                HttpURLConnection conn;
                DataOutputStream dos;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;

                File done = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
                done.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(done);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                if (!done.isFile())
                    Log.e("DownloadManager", "no existe");
                else {
                    FileInputStream fileInputStream = new FileInputStream(done);
                    URL url = new URL("http://madgoatstd.com/SPM/upper.php");

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    //conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    //conn.setRequestProperty("uploadedfile", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, 1 * 1024 * 1024);
                    byte[] buf = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buf, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buf, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, 1 * 1024 * 1024);
                        bytesRead = fileInputStream.read(buf, 0, bufferSize);

                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    int serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();


                    Log.i("UploadManager", "HTTP response is: " + serverResponseMessage + ": " + serverResponseCode);

                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    InputStream responseStream = new BufferedInputStream(conn.getInputStream());

                    BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = responseStreamReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    responseStreamReader.close();

                    response = stringBuilder.toString();

                    if (response.contains("<MESSAGE>OK</MESSAGE>")) {
                        Log.d("ENVIANDO", "OK");
                    } else {
                        Log.d("ENVIANDO", "NOK");
                    }
                }


            } catch (Exception e) {
                Log.d("TAG", "Error: " + e.getMessage());
                response = "ERROR";
            }

            return response;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(mContext);
            dialog.setTitle("Subiendo imagenes");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing())
                dialog.dismiss();
            if(flag == 0) {
                subirFirma();
            }else{
                Intent n = new Intent(mContext, FormValidacionAuditiva.class);
                n.putExtra("RUT", rut);
                startActivity(n);
            }
        }


    }
}
