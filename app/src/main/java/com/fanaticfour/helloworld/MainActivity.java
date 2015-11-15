package com.fanaticfour.helloworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.io.IOException;
import java.util.ArrayList;

import io.indico.Indico;
import io.indico.network.IndicoCallback;
import io.indico.results.IndicoResult;
import io.indico.utils.IndicoException;


public class MainActivity extends Activity implements OnClickListener {
    private TextView mText, moodText;
    private SpeechRecognizer sr;
    private static final String TAG = "Poop:";

    private Button leftLang, rightLang;

    private TextView lang;

    TextView text;
    String translatedText;

    Indico indico;

    int languageIdx = 0; //English

    private static final int ENGLISH = 0, FRENCH = 1, SPANISH = 2, VIETNAMESE = 3, GERMAN = 4, DUTCH = 5;

    private String[] languages = {
            "English", "French", "Spanish", "Vietnamese", "German", "Dutch"
    };

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button speakButton = (Button) findViewById(R.id.btnSpeak);
        speakButton.setVisibility(View.INVISIBLE);
        mText = (TextView) findViewById(R.id.txtSpeechInput);
        moodText = (TextView) findViewById(R.id.sentimentText);
        mText.setTextSize(18);
        moodText.setTextSize(18);

        leftLang = (Button) findViewById(R.id.leftLang);
        rightLang = (Button) findViewById(R.id.rightLang);
        lang = (TextView) findViewById(R.id.langView);

        leftLang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (languageIdx == 0)  {
                    languageIdx = languages.length - 1;
                }
                else {
                    languageIdx = (languageIdx - 1);
                }
                //lang.setText(languages[languageIdx]);
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, languages[languageIdx], duration);
                toast.show();
            }
        });

        rightLang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                languageIdx = (languageIdx += 1) % 6;
                //lang.setText(languages[languageIdx]);

                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, languages[languageIdx], duration);
                toast.show();

            }
        });

        speakButton.setOnClickListener(this);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        final Listener listener = new Listener();
        sr.setRecognitionListener(listener);

        Indico.init(this, "d463ce24a7695ae2968ad995a1368fc8", null);


        /* Camera previews were done using http://developer.android.com/guide/topics/media/camera.html */

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        preview.setOnClickListener(this);
        preview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
                startRecording();
            }
        });
    }

    public void startRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        sr.startListening(intent);
        Log.i("111111", "11111111");
    }


    class Listener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
            //mText.setText("error " + error);
        }

        public void onResults(Bundle results) {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                Log.d(TAG, "result " + data.get(i));
                //str += data.get(i);
            }

            String phrase = (String) data.get(0);

            new MyAsyncTask() {
                protected void onPostExecute(Boolean result) {
                    mText.setText(translatedText);
                }
            }.execute(phrase);

            //mText.setText("" + phrase);

            try {
                Indico.sentiment.predict(phrase, new IndicoCallback<IndicoResult>() {
                    @Override public void handle(IndicoResult result) throws IndicoException {
                        Log.i("Indico Sentiment", "sentiment of: " + result.getSentiment());
                        Double sentiment = result.getSentiment();
                        if (sentiment < 0.4) {
                            new MyAsyncTask() {
                                protected void onPostExecute(Boolean result) {
                                    moodText.setText(translatedText);
                                    moodText.setTextColor(Color.RED);
                                }
                            }.execute("Negative");
                        }
                        else if (sentiment > 0.7) {
                            new MyAsyncTask() {
                                protected void onPostExecute(Boolean result) {
                                    moodText.setText(translatedText);
                                    moodText.setTextColor(Color.GREEN);
                                }
                            }.execute("Positive");
                        }
                        else {
                            new MyAsyncTask() {
                                protected void onPostExecute(Boolean result) {
                                    moodText.setText(translatedText);
                                    moodText.setTextColor(Color.DKGRAY);
                                }
                            }.execute("Neutral");
                        }
                    }
                });
            } catch (IOException | IndicoException e) {
                e.printStackTrace();
            }


        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnSpeak) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            sr.startListening(intent);
            Log.i("111111", "11111111");
        }
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {
            Translate.setClientId("MicrosoftTranslatorJavaAPI");
            Translate.setClientSecret("0VHbhXQnJrZ7OwVqcoX/PDZlyLJS9co3cVev1TPr8iM=");
            try {

                if (languageIdx == 1) {
                    translatedText = Translate.execute(arg0[0], Language.ENGLISH, com.memetix.mst.language.Language.FRENCH);
                }
                else if (languageIdx == 2) {
                    translatedText = Translate.execute(arg0[0], Language.ENGLISH, com.memetix.mst.language.Language.SPANISH);
                }
                else if (languageIdx == 3) {
                    translatedText = Translate.execute(arg0[0], Language.ENGLISH, com.memetix.mst.language.Language.VIETNAMESE);
                }
                else if (languageIdx == 4) {
                    translatedText = Translate.execute(arg0[0], Language.ENGLISH, com.memetix.mst.language.Language.GERMAN);
                }
                else if (languageIdx == 5) {
                    translatedText = Translate.execute(arg0[0], Language.ENGLISH, com.memetix.mst.language.Language.DUTCH);
                }
                else {
                    translatedText = Translate.execute(arg0[0], Language.ENGLISH, com.memetix.mst.language.Language.ENGLISH);
                }


            } catch(Exception e) {
                translatedText = e.toString();
            }
            return true;
        }
    }


}