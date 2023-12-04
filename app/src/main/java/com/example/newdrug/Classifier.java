package com.example.newdrug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.CastOp;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.Map;

public class Classifier {

    private Context context;
    Interpreter tflite;
    final String ASSOCIATED_AXIS_LABELS = "labels.txt";
    List<String> associatedAxisLabels = null;

    public Classifier(Context context)
    {
        this.context = context;

        try {
            associatedAxisLabels = FileUtil.loadLabels(context, ASSOCIATED_AXIS_LABELS);
        } catch (IOException e) {
            Log.e("tfliteSupport", "Error reading label file", e);
        }

        try{
            MappedByteBuffer tfliteModel
                    = FileUtil.loadMappedFile(context,
                    "model_unquant.tflite");
            tflite = new Interpreter(tfliteModel);
        } catch (IOException e){
            Log.e("tfliteSupport", "Error reading model", e);
        }
    }

    public String classify(ImageProxy image) {
        @SuppressLint("UnsafeExperimentalUsageError")
        Image img = image.getImage();
        Bitmap bitmap = Utils.toBitmap(img);
        int rotation = Utils.getImageRotation(image);

        // モデルの入力テンソルのサイズを取得
        int[] inputShape = tflite.getInputTensor(0).shape();
        int modelHeight = inputShape[1];
        int modelWidth = inputShape[2];
        int inputChannels = inputShape[3];

        // モデルの入力バッファを作成し、サイズを設定
        TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, modelHeight, modelWidth, 3}, DataType.FLOAT32);

// BitmapからTensorImageを作成
        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);

// BitmapをTensorImageにロード
        tensorImage.load(bitmap);

// 画像処理を行い、モデルの入力サイズに合わせてリサイズ
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(modelHeight, modelWidth, ResizeOp.ResizeMethod.BILINEAR))
                .add(new Rot90Op(rotation))
                .build();
        tensorImage = imageProcessor.process(tensorImage);

// モデルを実行
        if (null != tflite) {
            tflite.run(tensorImage.getBuffer(), inputBuffer.getBuffer());
        }


        // 出力TensorBufferのサイズをモデルの出力サイズに合わせて設定
        int[] outputShape = tflite.getOutputTensor(0).shape();
        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32);

        // モデルの出力データを正規化します。
        TensorProcessor probabilityProcessor = new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();
        outputBuffer = probabilityProcessor.process(outputBuffer);

        // 結果を解析してラベルとその確率を取得します。
        String result = " ";
        if (null != associatedAxisLabels) {
            TensorLabel labels = new TensorLabel(associatedAxisLabels, outputBuffer);
            Map<String, Float> floatMap = labels.getMapWithFloatValue();
            result = Utils.writeResults(floatMap);
        }
        return result;
    }
}
