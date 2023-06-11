package com.android.proyecto_frutas.dibujo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.proyecto_frutas.R;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity_Dibujo_nivel_1 extends AppCompatActivity {

    private ImageView iv_dibujo;
    private EditText et_respuesta;
    private Button btn_listo;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private Path path;

    private int[] inputPixels = new int[28 * 28];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dibujo_nivel1);

        iv_dibujo = findViewById(R.id.iv_dibujo);
        et_respuesta = findViewById(R.id.editText_resultado);
        btn_listo = findViewById(R.id.button_listo);

        // Configurar el lienzo de dibujo
        bitmap = Bitmap.createBitmap(480, 640, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10);

        iv_dibujo.setImageBitmap(bitmap);

        iv_dibujo.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float posX = event.getX();
                float posY = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        path.moveTo(posX, posY);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        path.lineTo(posX, posY);
                        break;
                    case MotionEvent.ACTION_UP:
                        canvas.drawPath(path, paint);
                        path.reset();
                        break;
                    default:
                        return false;
                }
                iv_dibujo.invalidate();
                return true;
            }
        });

        btn_listo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interpretarNumero();
            }
        });
    }

    private void interpretarNumero() {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 28, 28, false);
        int[] pixels = new int[scaledBitmap.getWidth() * scaledBitmap.getHeight()];
        scaledBitmap.getPixels(pixels, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                int pixel = pixels[i * 28 + j];
                int grayscale = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
                int invertedGrayscale = 255 - grayscale;
                inputPixels[i * 28 + j] = invertedGrayscale;
            }
        }

        float[] result = makePrediction(inputPixels);
        int predictedNumber = getPredictedNumber(result);
        if (predictedNumber >= 0 && predictedNumber <= 9) {
            et_respuesta.setText(String.valueOf(predictedNumber));
        } else {
            Toast.makeText(this, "Dibuja de nuevo", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearCanvas();
                }
            }, 2000);
        }
    }

    private float[] makePrediction(int[] inputPixels) {
        // Lee el modelo TensorFlow Lite desde el archivo model.tflite en la carpeta de activos
        try {
            Interpreter.Options options = new Interpreter.Options();
            options.setNumThreads(1);
            Interpreter interpreter = new Interpreter(loadModelFile(), options);

            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(784 * 4);
            inputBuffer.order(ByteOrder.nativeOrder());

            for (int pixelValue : inputPixels) {
                inputBuffer.putFloat((pixelValue & 0xFF) / 255.0f);
            }

            float[][] outputScores = new float[1][10];
            interpreter.run(inputBuffer, outputScores);

            return outputScores[0];
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int getPredictedNumber(float[] prediction) {
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIndex = -1;

        for (int i = 0; i < prediction.length; i++) {
            if (prediction[i] > maxScore) {
                maxScore = prediction[i];
                maxScoreIndex = i;
            }
        }

        return maxScoreIndex;
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public void borrarDibujo(View view) {
        clearCanvas();
        et_respuesta.setText(""); // Vaciar el campo de respuesta
    }

    private void clearCanvas() {
        path.reset();
        canvas.drawColor(Color.WHITE);
        iv_dibujo.invalidate();
        et_respuesta.setText("");
    }
}
