package tv.limehd.customkeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import tv.limehd.keyboard.Keyboard;

public class ExampleActivity extends AppCompatActivity implements Keyboard.KeyListener {

    private EditText input;
    private boolean isKeyboardActivated = false;
    private Keyboard keyboard;
    private ViewGroup v;

    private Switch switcher, switcherNumbers;
    private boolean nightMode = false;
    private boolean numbersMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        switcher = findViewById(R.id.switcher);
        switcher.setOnCheckedChangeListener((b, s) -> {
            nightMode = s;
        });

        switcherNumbers = findViewById(R.id.switcher_numbers);
        switcherNumbers.setOnCheckedChangeListener((b, s) -> {
            numbersMode = s;
        });

        input = findViewById(R.id.text_input);
        input.setCursorVisible(false);
        input.setInputType(InputType.TYPE_NULL);
        v = (ViewGroup) getWindow().getDecorView().getRootView();
        input.setOnClickListener(v -> {
            if (!isKeyboardActivated) {
                if (keyboard == null) {
                    keyboard = new Keyboard.Builder(this, this)
                        .setNightMode(nightMode)
                        .enableNumberLine(numbersMode)
                        .build();
                }
                keyboard.showKeyboard();
            } else {
                keyboard.hideKeyboard();
                keyboard = null;
            }
            isKeyboardActivated = !isKeyboardActivated;
        });
        //Toast.makeText(this, "Size: " + String.valueOf(getSize()), Toast.LENGTH_SHORT).show();
    }


    public void onClick(View v) {
        if (!isKeyboardActivated) {
            if (keyboard == null) {
                keyboard = new Keyboard.Builder(this, this)
                        .setNightMode(nightMode)
                        .enableNumberLine(numbersMode)
                        .build();
            }
            keyboard.showKeyboard();
        } else {
            keyboard.hideKeyboard();
            keyboard = null;
        }
        isKeyboardActivated = !isKeyboardActivated;
    }

    @Override
    public void onKeyClicked(String key) {
        input.setText(input.getText().toString() + key);
    }

    @Override
    public void onDeleteButtonClicked() {
        if (input.length() > 0) {
            String text = input.getText().toString().substring(0, (input.getText().length() - 1));
            input.setText(text);
        }
    }

    @Override
    public void onLongDeleteButtonClicked() {
        if (input.length() > 0) {
            input.setText("");
        }
    }

    @Override
    public void onKeyboardHideClicked() {
        Toast.makeText(this, "onKeyboardHideClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onKeyboardOkClicked() {
        Toast.makeText(this, "onKeyboardOkClicked", Toast.LENGTH_SHORT).show();
    }


}