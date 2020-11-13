package jp.co.abs.onseininsiki2tsuboi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LaunchOtherAppActivity extends AppCompatActivity {

    //UI
    ImageView imageview;

    //音声認識結果のリスト
    ArrayList<String> data;

    //別アプリ起動のためのパッケージ及びクラスの情報
    List<PackageInfo> packageInfoList;
    PackageManager packageManager;
    LaunchListAdapter mListAdapter;
    String packageName;
    String className;

    Intent intent;
    SpeechRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packageManager = getPackageManager();
        packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        for(PackageInfo packageInfo : packageInfoList){
            LaunchItem oLaunchItem = null;
            if(packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null){
                packageName = packageInfo.packageName;
                className = packageManager.getLaunchIntentForPackage(packageInfo.packageName).getComponent().getClassName()+"";
                oLaunchItem = new LaunchItem(true,packageName,className);
            }else{
                oLaunchItem = new LaunchItem(false,packageInfo.packageName,null);
            }
        }

        mListAdapter = new LaunchListAdapter(this.getApplicationContext());
        mListAdapter.setLaunchAppListener(new LaunchListAdapter.LaunchAppListener(){
            @Override
            public void onLaunch(Intent intent){
                startActivity(intent);
            }
        });
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

        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {

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

                // 音声認識を繰り返す
                recognizer.stopListening();
                recognizer.startListening(intent);
            }

            // その他のメソッド RecognitionListenerの特性上記述が必須
            public void onReadyForSpeech(Bundle bundle) { }
            public void onRmsChanged(float v) { }
            public void onBufferReceived(byte[] bytes) { }
            public void onError(int error) { }
            public void onPartialResults(Bundle bundle) { }
            public void onEvent(int i, Bundle bundle) { }
        });
        recognizer.startListening(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();

        // 音声認識中にアプリから離れた場合、認識結果を破棄
            data.remove(data.size());

    }

}