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

    Intent intent; // SpeechRecognizerに渡すIntent
    Intent intent1; // 別アプリに遷移するためのIntent
    SpeechRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageview = findViewById(R.id.mic);
    }
    @Override
    protected void onResume(){
        super.onResume();
        startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopListening();

        // 音声認識中にアプリから離れた場合、認識結果を破棄
        if(data.size() != 0) {
            data.remove(data.size()-1);
        }

        stopListening();
    }

        protected void startListening(){
            try{
                if(recognizer == null) {
                    recognizer = SpeechRecognizer.createSpeechRecognizer(this);
                    if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), "音声認識が使えません",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                    recognizer.setRecognitionListener(new listener());
                }

                if (getPackageManager().queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0).size() == 0) {
                    return;
                }
                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP");
                intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
                recognizer.startListening(intent);
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "startListening()でエラーが起こりました",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }

        // 音声認識を終了する
        protected void stopListening() {
            if (recognizer != null) recognizer.destroy();
            recognizer = null;
        }

        // 音声認識を再開する
        public void restartListeningService() {
            stopListening();
            startListening();
        }


        class listener implements RecognitionListener{

            //別アプリ起動のためのパッケージ及びクラスの情報
            PackageManager packageManager;
            @Override
            public void onReadyForSpeech(Bundle bundle) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                 //   intent1 = null;
                    if (data.get(0).contains(jan) && data.get(0).contains(start)) {
                        if(data.get(0).contains(file) || data.get(0).contains(tet)){
                            data.remove(data.size() - 1);
                            Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_SHORT).show();
                        }else {
                            dataCheck("jp.co.abs.jankenver2");
                        }
                    } else if (data.get(0).contains(file) && data.get(0).contains(start)) {
                        if(data.get(0).contains(jan) || data.get(0).contains(tet)){
                            data.remove(data.size() - 1);
                            Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_SHORT).show();
                        }else {
                            dataCheck("jp.co.abs.filedownloaderver2_2");
                        }
                    } else if (data.get(0).contains(tet) && data.get(0).contains(start)) {
                        if(data.get(0).contains(file) || data.get(0).contains(jan)){
                            data.remove(data.size() - 1);
                            Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_SHORT).show();
                        }else {
                            dataCheck("jp.co.abs.tetrisver2_1");
                        }
                    } else {
                        data.remove(data.size() - 1);
                        Toast.makeText(getApplicationContext(), "音声が正しく認識されていません", Toast.LENGTH_SHORT).show();
                    }

                    // 音声認識を繰り返す
                    restartListeningService();
                }
            }

            public void onError(int error) {
                switch(error){
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                        break;
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
                restartListeningService();
            }

            // 引数にパッケージ名を渡すとそのパッケージ名に一致したアプリを起動
            public void dataCheck(String packageName){
                data.remove(data.size()-1);
                packageManager = getPackageManager();
                intent1 = packageManager.getLaunchIntentForPackage(packageName);
                startActivity(intent1);
            }

            // その他のメソッド RecognitionListenerの特性上記述が必須
            public void onRmsChanged(float v) { }
            public void onBufferReceived(byte[] bytes) { }
            public void onPartialResults(Bundle bundle) { }
            public void onEvent(int i, Bundle bundle) { }
        }
    }
