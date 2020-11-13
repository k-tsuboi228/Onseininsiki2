package jp.co.abs.onseininsiki2tsuboi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class LaunchOtherAppActivity extends AppCompatActivity {

    //UI
    ImageView imageview;

    //音声認識結果のリスト
    ArrayList<String> data;

    //別アプリ起動のためのパッケージ及びクラスの情報
    PackageManager packageManager;

    Intent intent; // SpeechRecognizerに渡すIntent
    Intent intent1; // 別アプリに遷移するためのIntent
    SpeechRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packageManager = getPackageManager();
        imageview=findViewById(R.id.mic);

        if (getPackageManager().queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0).size() == 0) {
            return;
        }
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP");
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

    }

    @Override
    protected void onResume(){
        super.onResume();
        speech();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 音声認識中にアプリから離れた場合、認識結果を破棄
        if(data.size() != 0) {
            data.remove(data.size());
        }
        recognizer.stopListening();
    }

    public void speech(){
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Toast.makeText(getApplicationContext(), "音声を入力してください", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                imageview.setImageResource(R.drawable.mic2);
            }

            @Override
            public void onEndOfSpeech() {
                imageview.setImageResource(R.drawable.mic);
            }

            @Override
            public void onResults(Bundle results) {
                data = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);

                String jan = "じゃんけん";
                String file = "ファイル";
                String tet = "テトリス";
                String start = "起動";
                String errorCode = "エラーが発生しました";

                if(data != null) {
                    //起動する別アプリの条件分岐
                    intent1 = null;
                    if (data.get(0).contains(jan) && data.get(0).contains(start)) {
                        dataCheck("jp.co.abs.jankenver2");
                    } else if (data.get(0).contains(file) && data.get(0).contains(start)) {
                        dataCheck("jp.co.abs.filedownloaderver2_2");
                    } else if (data.get(0).contains(tet) && data.get(0).contains(start)) {
                        dataCheck("jp.co.abs.tetrisver2_1");
                    } else if (data.get(0).contains(jan) && data.get(0).contains(file)) {
                        data.remove(data.size() - 1);
                        Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_SHORT).show();
                    } else if (data.get(0).contains(jan) && data.get(0).contains(tet)) {
                        data.remove(data.size() - 1);
                        Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_SHORT).show();
                    } else if (data.get(0).contains(file) && data.get(0).contains(tet)) {
                        data.remove(data.size() - 1);
                        Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_SHORT).show();
                    } else {
                        data.remove(data.size() - 1);
                        Toast.makeText(getApplicationContext(), "音声が正しく認識されていません", Toast.LENGTH_SHORT).show();
                    }

                    // 音声認識を繰り返す
                    recognizer.stopListening();

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    recognizer.startListening(intent);
                }
            }

            public void onError(int error) {

                switch(error){
                    case SpeechRecognizer.ERROR_NO_MATCH :
                        Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT :
                        Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                        break;
                    default :
                        break;
                }

                // 音声認識を繰り返す
                recognizer.stopListening();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                recognizer.startListening(intent);
            }

            // その他のメソッド RecognitionListenerの特性上記述が必須
            public void onRmsChanged(float v) { }
            public void onBufferReceived(byte[] bytes) { }
            public void onPartialResults(Bundle bundle) { }
            public void onEvent(int i, Bundle bundle) { }
        });
        recognizer.startListening(intent);
    }

    public void dataCheck(String packageName){
        data.remove(data.size()-1);
        intent1 = packageManager.getLaunchIntentForPackage(packageName);
        startActivity(intent1);
    }

}