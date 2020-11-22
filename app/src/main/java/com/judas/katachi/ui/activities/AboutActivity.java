package com.judas.katachi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.judas.katachi.R;

import static com.judas.katachi.BuildConfig.VERSION_NAME;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;

public class AboutActivity extends AppCompatActivity {
    private static final String TAG = AboutActivity.class.getSimpleName();

    public static void start(final Context context) {
        log(DEBUG, TAG, "start");
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log(DEBUG, TAG, "onCreate");

        setContentView(R.layout.about);

        // Toolbar
        final MaterialToolbar toolbar = findViewById(R.id.about_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        ((TextView) findViewById(R.id.about_version)).setText(getString(R.string.about_version, VERSION_NAME));
    }
}
