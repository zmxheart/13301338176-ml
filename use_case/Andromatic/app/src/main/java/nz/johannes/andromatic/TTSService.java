package nz.johannes.andromatic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;

import java.util.Locale;

public class TTSService extends Service {

    private TextToSpeech reader;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Context context = this;
        final String text = intent.getStringExtra("text");
        reader = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = reader.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                        Main.showToast(context, "Text-to-speech language data not found!");
                        stopSelf();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) reader.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                        else reader.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (reader != null) {
            reader.stop();
            reader.shutdown();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
