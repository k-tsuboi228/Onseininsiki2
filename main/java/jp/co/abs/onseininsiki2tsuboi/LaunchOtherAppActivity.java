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

    Intent intent;
    Intent intent1;
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
    }

    public void speech(){
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {

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
                /*
                if((data.get(0).contains("じゃんけん") && data.get(0).contains("起動")) == true){
                    dataCheck("jp.co.abs.jankenver2");
                }else if((data.get(0).contains("ファイル Downloader ") && data.get(0).contains("起動")) == true){
                    dataCheck("jp.co.abs.filedownloaderver2_2");
                }else if((data.get(0).contains("テトリス") && data.get(0).contains("起動")) == true){
                    dataCheck("jp.co.abs.tetrisver2_1");
                }else if((data.get(0).contains("じゃんけん") && data.get(0).contains("ファイル Downloader ")) == true){
                    Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                }else if((data.get(0).contains("じゃんけん") && data.get(0).contains("テトリス")) == true){
                    Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                }else if((data.get(0).contains("ファイル Downloader ") && data.get(0).contains("テトリス")) == true){
                    Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "音声が正しく認識されていません", Toast.LENGTH_SHORT).show();
                }
                */
                
                //起動する別アプリの条件分岐
                intent1 = null;
                switch(data.get(0)){
                    case "じゃんけん起動":
                        dataCheck("jp.co.abs.jankenver2");
                        break;
                    case "ファイル Downloader 起動":
                        dataCheck("jp.co.abs.filedownloaderver2_2");
                        break;
                    case "テトリス起動":
                        dataCheck("jp.co.abs.tetrisver2_1");
                        break;
                    default :
                        Toast.makeText(getApplicationContext(), "音声が正しく認識されていません", Toast.LENGTH_SHORT).show();
                        data.remove(data.size()-1);
                        break;
                }

                // 音声認識を繰り返す
                recognizer.stopListening();
                recognizer.startListening(intent);
            }

            public void onError(int error) {

                Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();

                recognizer.stopListening();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 音声認識を繰り返す
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