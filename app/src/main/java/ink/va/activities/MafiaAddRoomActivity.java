package ink.va.activities;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ink.va.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ink.va.utils.Retrofit;
import ink.va.utils.SharedHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MafiaAddRoomActivity extends BaseActivity {
    @BindView(R.id.roomNameTV)
    EditText roomNameTV;
    @BindView(R.id.durationMorningED)
    EditText durationMorningED;
    @BindView(R.id.nightDurationTV)
    TextView nightDurationTv;

    @BindView(R.id.languageSpinner)
    AppCompatSpinner languageSpinner;
    @BindView(R.id.gameTypeSpinner)
    AppCompatSpinner gameTypeSpinner;
    @BindView(R.id.gameMorningDurationSpinner)
    AppCompatSpinner gameMorningDurationSpinner;

    @BindView(R.id.addRoomScroll)
    ScrollView addRoomScroll;

    private List<String> languages;
    private List<String> gameTypes;
    private List<String> timeUnits;
    private String chosenLanguage;
    private int estimatedNightDuration;
    private String chosenGameType;
    private String chosenMorningTimeUnit;
    private boolean hasTimeError;
    private SharedHelper sharedHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mafia_add_room);
        ButterKnife.bind(this);
        sharedHelper = new SharedHelper(this);
        getSupportActionBar().setTitle(getString(R.string.addRoom));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        languages = new ArrayList<>();
        languages.add(getString(R.string.english));
        languages.add(getString(R.string.russian));

        gameTypes = new ArrayList<>();
        gameTypes.add(getString(R.string.classic));
        gameTypes.add(getString(R.string.yakudza));

        timeUnits = new ArrayList<>();
        timeUnits.add(getString(R.string.minutesUnit));
        timeUnits.add(getString(R.string.hoursUnit));
        timeUnits.add(getString(R.string.daysUnit));

        initAdapters();
        initEditTexts();

        chosenLanguage = getString(R.string.english);
        chosenGameType = getString(R.string.classic);
        chosenMorningTimeUnit = getString(R.string.minutesUnit);
    }

    private void initEditTexts() {
        durationMorningED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEditText();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        durationMorningED.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkEditText();
                }
            }
        });

    }

    private void checkEditText() {
        if (!durationMorningED.getText().toString().trim().isEmpty()) {
            try {
                int chosenNumber = Integer.valueOf(durationMorningED.getText().toString());
                if (chosenMorningTimeUnit.equals(getString(R.string.minutesUnit))) {
                    if (chosenNumber < 5) {
                        hasTimeError = true;
                        durationMorningED.setError(getString(R.string.minimumFiveMinutes));
                    } else {
                        hasTimeError = false;
                        durationMorningED.setError(null);
                    }
                }
                initChosenNightDuration();
            } catch (NumberFormatException e) {
                durationMorningED.setError(getString(R.string.onlyNumbersAllowed));
                e.printStackTrace();
            }
        } else {
            initChosenNightDuration();
        }
    }

    private void initChosenNightDuration() {
        if (!hasTimeError && !durationMorningED.getText().toString().trim().isEmpty()) {
            estimatedNightDuration = Integer.valueOf(durationMorningED.getText().toString()) / 2;
            nightDurationTv.setText(getString(R.string.estimatedNightDuration, estimatedNightDuration + " " +
                    chosenMorningTimeUnit));
        } else {
            nightDurationTv.setText(getString(R.string.chooseMorningFirst));
        }
    }


    private void initAdapters() {
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, languages);
        ArrayAdapter<String> gameTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gameTypes);
        ArrayAdapter<String> timeUnitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, timeUnits);

        languageSpinner.setAdapter(languageAdapter);
        gameTypeSpinner.setAdapter(gameTypeAdapter);
        gameMorningDurationSpinner.setAdapter(timeUnitAdapter);

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
                initChosenNightDuration();
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
        if (hasTimeError) {
            Toast.makeText(this, getString(R.string.fixTimeErros), Toast.LENGTH_SHORT).show();
        } else if (roomNameTV.getText().toString().trim().isEmpty()) {
            scroll(ScrollView.FOCUS_UP);
            roomNameTV.setError(getString(R.string.emptyField));
        } else if (durationMorningED.getText().toString().isEmpty()) {
            scroll(ScrollView.FOCUS_DOWN);
            durationMorningED.setError(getString(R.string.emptyField));
        } else {
            estimatedNightDuration = Integer.valueOf(durationMorningED.getText().toString()) / 2;
            addRoom();
        }
    }

    private void addRoom() {
        Call<ResponseBody> addRoomCall = Retrofit.getInstance().getInkService().addMafiaRoom(roomNameTV.getText().toString().trim(),
                chosenLanguage, chosenGameType, durationMorningED.getText().toString(), chosenMorningTimeUnit,
                String.valueOf(estimatedNightDuration), chosenMorningTimeUnit,
                sharedHelper.getUserId());
        addRoomCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response == null) {
                    addRoom();
                    return;
                }
                if (response.body() == null) {
                    addRoom();
                    return;
                }
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(MafiaAddRoomActivity.this, getString(R.string.serverErrorText), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MafiaAddRoomActivity.this, getString(R.string.serverErrorText), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scroll(int direction) {
        addRoomScroll.fullScroll(direction);
    }
}
