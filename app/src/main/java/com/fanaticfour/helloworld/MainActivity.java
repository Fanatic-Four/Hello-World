package com.fanaticfour.helloworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.gtranslate.Language;
import com.gtranslate.Translator;

import java.util.ArrayList;

public class MainActivity extends Activity implements OnClickListener {
    private TextView mText;
    private SpeechRecognizer sr;
    private static final String TAG = "MyStt3Activity";

    private Camera mCamera;
    private CameraPreview mPreview;
/*
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: " +
                        e.getMessage());
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button speakButton = (Button) findViewById(R.id.btnSpeak);
        mText = (TextView) findViewById(R.id.txtSpeechInput);
        speakButton.setOnClickListener(this);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        final Listener listener = new Listener();
        sr.setRecognitionListener(listener);

        Button textButton = (Button) findViewById(R.id.onReadySpeech);
        textButton.setVisibility(View.INVISIBLE);
//        textButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startRecording();
//            }
//        });
//
        Button onStopSpeech = (Button) findViewById(R.id.onStopSpeech);
        onStopSpeech.setVisibility(View.INVISIBLE);
//        onStopSpeech.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sr.stopListening();
//            }
//        });


        /* Camera previews were done using http://developer.android.com/guide/topics/media/camera.html */

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

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
            //mText.setText("" + data.get(0));

            String currentData = (String) data.get(0);

            try {
                String text = new TranslateTask().execute(currentData).get();
                mText.setText(text);
                Log.v("Poop:", text);
            }
            catch (Exception e) {
                Log.e("Poop:", e.toString());
            }


/*
            Translator.getInstance().execute(currentData, Language.CHINESE_SIMPLIFIED, API_KEY, new Translator.Callback() {

                @Override
                public void onSuccess(Language detected_lang, String translated_text) {
                    Log.d(TAG, "onSuccess: language:" + detected_lang.toString() + "\ttext:" + translated_text);
                }

                @Override
                public void onFailed(TranslateError e) {
                    e.printStackTrace();
                }
            });
*/

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

    class TranslateTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... input) {
            try {
                Translator translate = Translator.getInstance();
                String text = translate.translate(input[0], Language.ENGLISH, Language.PORTUGUESE);

                return text;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(String string) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}