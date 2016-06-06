package com.ghg.zxingdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.android.decode.CaptureActivity;
import com.google.zxing.client.android.encode.QRCodeEncoder;
import com.google.zxing.common.HybridBinarizer;

import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity" ;
    private Button mBtnScannerQRCode;
    private Button mBtnGenerateQRCode;
    private ImageView mImgQrcodeImg;

    public static final int REQUEST_CODE = 0;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        initView();
        initListener();
    }



    private void initView() {

        mBtnScannerQRCode = ( Button ) findViewById( R.id.qrcode_encode );
        mBtnGenerateQRCode = ( Button ) findViewById( R.id.qrcode_dencode );

        mImgQrcodeImg = ( ImageView ) findViewById( R.id.qrcode_img );

    }

    private void initListener() {

        mBtnScannerQRCode.setOnClickListener( this );
        mBtnGenerateQRCode.setOnClickListener( this );

        mImgQrcodeImg.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick( View v ) {


                mImgQrcodeImg.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(mImgQrcodeImg.getDrawingCache());
                mImgQrcodeImg.setDrawingCacheEnabled(false);
                decodeQRCode(bitmap);
                return true;
            }
        } );
    }
    /**
     * 解析二维码图片
     *
     * @param bitmap   要解析的二维码图片
     */
    public final Map<DecodeHintType, Object> HINTS = new EnumMap<>(DecodeHintType.class);

    private void decodeQRCode( final Bitmap bitmap ) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int[] pixels = new int[width * height];

                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                    Result             result = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source)), HINTS);
                    return result.getText();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d(TAG, "result=" + result);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }.execute();

    }

    @Override
    public void onClick( View v ) {


        switch ( v.getId() ){
            case R.id.qrcode_encode:
                //生成
                try {
                    Bitmap mBitmap = QRCodeEncoder.encodeAsBitmap("http://www.qq.com/", 300);
                    mImgQrcodeImg.setImageBitmap(mBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.qrcode_dencode:
                //扫描

              Intent  intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;


        }
    }


    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

        if (resultCode == RESULT_OK) { //RESULT_OK = -1
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Toast.makeText(MainActivity.this, scanResult, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText( MainActivity.this, "失败", Toast.LENGTH_SHORT ).show();
        }
    }
}
