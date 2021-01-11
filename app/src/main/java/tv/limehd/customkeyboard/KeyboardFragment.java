package tv.limehd.customkeyboard;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tv.limehd.keyboard.Keyboard;

public class KeyboardFragment extends Fragment implements Keyboard.KeyListener {

    private EditText input;
    private boolean isKeyboardActivated = false;
    private Keyboard keyboard;
    private ViewGroup v;

    private Switch switcher, switcherNumbers;
    private boolean nightMode = false;
    private boolean numbersMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_keyboard, container, false);

        switcher = v.findViewById(R.id.switcher);
        switcher.setOnCheckedChangeListener((b, s) -> {
            nightMode = s;
        });

        switcherNumbers = v.findViewById(R.id.switcher_numbers);
        switcherNumbers.setOnCheckedChangeListener((b, s) -> {
            numbersMode = s;
        });

        input = v.findViewById(R.id.text_input);
        input.setCursorVisible(false);
        input.setInputType(InputType.TYPE_NULL);
        input.setOnClickListener(v1 -> {
            if (!isKeyboardActivated) {
                if (keyboard == null) {
                    keyboard = new Keyboard.Builder(getActivity(), this)
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
        //input.setOnTouchListener((view, e) -> true);

        Button showKeyboardButton = v.findViewById(R.id.show_button);
        showKeyboardButton.setOnClickListener(v1 -> {
            if (!isKeyboardActivated) {
                if (keyboard == null) {
                    keyboard = new Keyboard.Builder(getActivity(), this)
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
        return v;
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
}
