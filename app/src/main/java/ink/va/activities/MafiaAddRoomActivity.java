package ink.va.activities;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.ink.va.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MafiaAddRoomActivity extends BaseActivity {
    @BindView(R.id.roomNameTV)
    EditText roomNameTV;
    @BindView(R.id.durationMorningED)
    EditText durationMorningED;
    @BindView(R.id.nightDurationED)
    EditText nightDurationED;

    @BindView(R.id.languageSpinner)
    AppCompatSpinner languageSpinner;
    @BindView(R.id.gameTypeSpinner)
    AppCompatSpinner gameTypeSpinner;
    @BindView(R.id.gameMorningDurationSpinner)
    AppCompatSpinner gameMorningDurationSpinner;
    @BindView(R.id.gameNightDurationSpinner)
    AppCompatSpinner gameNightDurationSpinner;

    private List<String> languages;
    private List<String> gameTypes;
    private List<String> timeUnits;
    private String chosenLanguage;
    private String chosenGameType;
    private String chosenMorningTimeUnit;
    private String chosenNightTimeUnit;
    private boolean hasTimeError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mafia_add_room);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(getString(R.string.addRoom));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        languages = new ArrayList<>();
        languages.add(getString(R.string.english));
        languages.add(getString(R.string.russian));

        gameTypes = new ArrayList<>();
        gameTypes.add(getString(R.string.classic));
        gameTypes.add(getString(R.string.yakudza));

        timeUnits = new ArrayList<>();
        timeUnits.add(getString(R.string.secondsUnit));
        timeUnits.add(getString(R.string.minutesUnit));
        timeUnits.add(getString(R.string.hoursUnit));
        timeUnits.add(getString(R.string.daysUnit));

        initAdapters();
        initEditTexts();

    }

    private void initEditTexts() {
        durationMorningED.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!durationMorningED.getText().toString().trim().isEmpty()) {
                        try {
                            int chosenNumber = Integer.valueOf(durationMorningED.getText().toString());
                            if (chosenMorningTimeUnit.equals(getString(R.string.secondsUnit))) {
                                if (chosenNumber < 60) {
                                    hasTimeError = true;
                                    durationMorningED.setError(getString(R.string.minimumSxiteenSeconds));
                                } else {
                                    hasTimeError = false;
                                }
                            }
                        } catch (NumberFormatException e) {
                            durationMorningED.setError(getString(R.string.onlyNumbersAllowed));
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        nightDurationED.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!nightDurationED.getText().toString().trim().isEmpty()) {
                        try {
                            int chosenNumber = Integer.valueOf(nightDurationED.getText().toString());

                            if (chosenNightTimeUnit.equals(getString(R.string.secondsUnit))) {
                                if (chosenNumber < 60) {
                                    hasTimeError = true;
                                    nightDurationED.setError(getString(R.string.minimumSxiteenSeconds));
                                } else {
                                    hasTimeError = false;
                                }
                            }
                        } catch (NumberFormatException e) {
                            nightDurationED.setError(getString(R.string.onlyNumbersAllowed));
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void initAdapters() {
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, languages);
        ArrayAdapter<String> gameTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gameTypes);
        ArrayAdapter<String> timeUnitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, timeUnits);

        languageSpinner.setAdapter(languageAdapter);
        gameTypeSpinner.setAdapter(gameTypeAdapter);
        gameMorningDurationSpinner.setAdapter(timeUnitAdapter);
        gameNightDurationSpinner.setAdapter(timeUnitAdapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenLanguage = languages.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gameTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenGameType = gameTypes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gameMorningDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenMorningTimeUnit = timeUnits.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gameNightDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenNightTimeUnit = timeUnits.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.saveAddRoom)
    public void saveAddRoomClicked() {
        proceedChecking();
    }

    private void proceedChecking() {

    }
}
