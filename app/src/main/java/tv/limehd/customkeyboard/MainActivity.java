package tv.limehd.customkeyboard;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import tv.limehd.keyboard.Keyboard;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements Keyboard.KeyListener {

    private EditText editText;
    private Button hideKeyboardButton;

    private Keyboard keyboard;
    private boolean isKeyboardActive = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initKeyboard();
        init();
    }

    private void init() {
        editText = findViewById(R.id.edit_text);
        editText.setFocusableInTouchMode(false);
        editText.setOnClickListener(click -> {
            if (!isKeyboardActive) {
                showKeyboard();
            }
        });

        hideKeyboardButton = findViewById(R.id.hide_keyboard_button);
        hideKeyboardButton.setOnClickListener(click -> {
            hideKeyboard();
        });
        updateHideButton();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        keyboard.clearFocusFromKey();
        return super.onTouchEvent(event);
    }

    private void updateHideButton() {
        int visibility = isKeyboardActive ? VISIBLE : INVISIBLE;
        hideKeyboardButton.setVisibility(visibility);
    }

    private void initKeyboard() {
        LinearLayout keyboardView = findViewById(R.id.keyboard_view);
        keyboard = new Keyboard.Builder(this, this, keyboardView)
            .enableNumberLine(false)
            .setNightMode(true)
            .build();
    }

    private void showKeyboard() {
        keyboard.showKeyboard();
        isKeyboardActive = true;
        updateHideButton();
    }

    private void hideKeyboard() {
        keyboard.hideKeyboard();
        isKeyboardActive = false;
        updateHideButton();
    }

    @Override
    public void onKeyClicked(String key) {
        editText.getText().append(key);
    }

    @Override
    public void onDeleteButtonClicked() {
        int length = editText.getText().length();
        if (length > 0) {
            editText.getText().delete(length - 1, length);
        }
    }

    @Override
    public void onLongDeleteButtonClicked() {
        editText.getText().clear();
    }

    @Override
    public void onKeyboardHideClicked() {
        hideKeyboard();
    }

    @Override
    public void onKeyboardOkClicked() {
        Toast.makeText(this, "OnKeyboardOkClicked", Toast.LENGTH_SHORT).show();
    }
}
