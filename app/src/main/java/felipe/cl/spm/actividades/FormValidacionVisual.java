package felipe.cl.spm.actividades;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import felipe.cl.spm.R;

public class FormValidacionVisual extends Activity implements View.OnClickListener, SignaturePad.OnSignedListener {
    ImageButton btn_back, btn_save, btn_camera, btn_picture;
    Button btn_borrar, btn_bloquear;
    ImageView image_secure, firma_preview;
    LinearLayout blocked;
    public static  Activity activity;
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_back) {
            finish();
        }
        if (v.getId() == R.id.ib_save) {
            startActivity(new Intent(this, FormValidacionAuditiva.class));
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
}
