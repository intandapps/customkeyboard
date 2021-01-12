package tv.limehd.keyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class Keyboard extends LinearLayout {

    private final String TAG = "Keyboard";

    // Передаваемые параметры
    private WindowManager windowManager;
    private KeyListener callback;
    private ViewGroup viewGroup;
    private boolean isFocused = false;

    private final String[][] keyboard = new String[][] {
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "(", ")"},
            {"Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш", "Щ", "З", "Х", "Ъ"},
            {"Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д", "Ж", "Э"},
            {"Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю"},
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'"},
            {"Z", "X", "C", "V", "B", "N", "M", "<", ">"}
    };

    private Button firstKey, changeLanguage;

    // Параметры для установки размеров клавиатура
    private final int param = 12; // Ряд кнопок = 1/12 от высота экрана => клавиатура из 4х рядов занимает 1/3 экрана по высоте
    private final float sizeRation = 1.56F; // Высотка кнопки в портренной ориентации = ширине * sizeRation
    private int margin = 4; // Стандартный размер отступа между кнопками

    private final int spaceSize = 9; // Пробел по ширине как %spacesize% кнопок
    private View keyboardView;
    private int dpWidth, dpHeight;

    // Параметры клавиатуры
    private boolean numberLineEnabled;
    private boolean nightThemeEnabled;
    private boolean isRussian = true;

    public Keyboard(Context context, WindowManager windowManager, KeyListener callback, ViewGroup viewGroup) {
        super(context);
        this.callback = callback;
        this.viewGroup = viewGroup;
        this.windowManager = windowManager;
    }

    public void showKeyboard() {
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = Float.parseFloat(String.valueOf(size.x));
        float height = Float.parseFloat(String.valueOf(size.y));
        margin = Math.round(width / 135) / 2;
        //dpWidth = Math.round(width / param) - margin * 2;
        dpWidth = (int) Math.floor(width / param) - margin * 2;
        if (width > height) { // Пейзаж
            dpHeight = Math.round(height / param) - margin * 2;
        } else { // Портрет
            dpHeight = Math.round(dpWidth * sizeRation) - margin * 2;
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (numberLineEnabled) {
            keyboardView = inflater.inflate(R.layout.keyboard_view_nums, viewGroup, true);
        } else {
            keyboardView = inflater.inflate(R.layout.keyboard_view, viewGroup, true);
        }
        ArrayList<String[]> array = new ArrayList<>();
        array.add(keyboard[0]);
        if (isRussian) {
            for (int i = 1; i < 4; i++) {
                array.add(keyboard[i]);
            }
        } else {
            for (int i = 4; i < 7; i++) {
                array.add(keyboard[i]);
            }
        }
        addKeys(array);
    }

    public void setNightThemeEnabled(boolean nightThemeEnabled) {
        this.nightThemeEnabled = nightThemeEnabled;
    }

    private void addKeys(ArrayList<String[]> keyLines) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout host = keyboardView.findViewById(R.id.button_1);
        if (nightThemeEnabled) {
            host.setBackgroundColor(getResources().getColor(R.color.black));
        }
        LinearLayout linearLayout;
        int startIndex = numberLineEnabled ? 0 : 1;
        for (int i = startIndex; i < keyLines.size(); i++) {
            String[] array = keyLines.get(i);
            int pos = numberLineEnabled ? i : i - 1;
            linearLayout = keyboardView.findViewById(getLinearLayoutId(pos));

            if (isRussian && array.length == keyboard[3].length || !isRussian && array.length == keyboard[6].length) {
                View v = inflater.inflate(R.layout.keyboard_item, linearLayout, true);
                Button button = v.findViewById(R.id.key_button);
                changeLanguage = button;
                setLangunageButtonSize(button);
                if (isRussian) {
                    button.setText(R.string.ru_title);
                } else {
                    button.setText(R.string.eng_title);
                }
                button.setBackground(getResources().getDrawable(R.drawable.action_button_style));
                button.setOnClickListener(v1 -> { // Смена языка
                    boolean focused = changeLanguage.isFocused();
                    isRussian = !isRussian;
                    hideKeyboard();
                    showKeyboard();
                    if (focused) changeLanguage.requestFocusFromTouch();
                }); // Смена языка
                button.setId(R.id.button_1);
                if (nightThemeEnabled) {
                    button.setBackground(getResources().getDrawable(R.drawable.night_action_button_style));
                    button.setTextColor(getResources().getColor(R.color.white));
                } else {
                    button.setBackground(getResources().getDrawable(R.drawable.action_button_style));
                }
            }

            for (int j = 0; j < array.length; j++) {
                View v = inflater.inflate(R.layout.keyboard_item, linearLayout, true);
                Button b = v.findViewById(R.id.key_button);
                if (i == startIndex && j == 0) firstKey = b;
                b.setText(array[j]);
                b.setOnClickListener(v1 -> callback.onKeyClicked(b.getText().toString()));
                if (nightThemeEnabled) {
                    b.setTextColor(getResources().getColor(R.color.white));
                    b.setBackground(getResources().getDrawable(R.drawable.night_button_style));
                }
                setButtonSize(b);
                b.setId(R.id.button_1);
            }

            if (isRussian && array.length == keyboard[3].length || !isRussian && array.length == keyboard[6].length) {
                View v = inflater.inflate(R.layout.keyboard_clear, linearLayout, true);
                ImageButton button = v.findViewById(R.id.key_button);
                setClearSize(button);
                button.setBackgroundResource(R.drawable.action_button_style);
                button.setOnClickListener(v1 -> callback.onDeleteButtonClicked()); // Стереть 1 символ
                button.setOnLongClickListener(v1 -> {
                    callback.onLongDeleteButtonClicked();
                    return false;
                });
                button.setId(R.id.button_1);
                if (nightThemeEnabled) {
                    button.setBackground(getResources().getDrawable(R.drawable.night_action_button_style));
                    button.setColorFilter(getResources().getColor(R.color.white));
                } else {
                    button.setBackground(getResources().getDrawable(R.drawable.action_button_style));
                }
            }
        }

        // Добавление пробела
        linearLayout = keyboardView.findViewById(getLinearLayoutId(numberLineEnabled ? keyLines.size() : keyLines.size() - 1));
        View v = inflater.inflate(R.layout.keyboard_space, linearLayout, true);
        Button b = v.findViewById(R.id.key_button);
        b.setOnClickListener(v1 -> callback.onKeyClicked(" "));
        setSpaceSize(b);
        b.setId(R.id.button_1);
        if (nightThemeEnabled) {
            b.setBackground(getResources().getDrawable(R.drawable.night_action_button_style));
        } else {
            b.setBackground(getResources().getDrawable(R.drawable.action_button_style));
        }
    }

    public void setFocus() {
        firstKey.requestFocusFromTouch();
        isFocused = true;
    }

    private void setLangunageButtonSize(Button button) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.height = dpHeight ;
        params.width = (int) Math.round(dpWidth * 1.5);
        params.leftMargin = margin;
        params.rightMargin = margin;
        params.bottomMargin = margin;
        params.topMargin = margin;
        button.setLayoutParams(params);
    }

    public int getLinearLayoutId(int index) {
        switch (index) {
            case 0: return R.id.firstLine;
            case 1: return R.id.secondLine;
            case 2: return R.id.thirdLine;
            case 3: return R.id.fourthLine;
            case 4: return R.id.fifthLine;
            default: throw new IndexOutOfBoundsException();
        }
    }

    private void setButtonSize(Button button) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.height = dpHeight;
        params.width = dpWidth;
        params.leftMargin = margin;
        params.rightMargin = margin;
        params.bottomMargin = margin;
        params.topMargin = margin;
        button.setLayoutParams(params);
    }

    private void setSpaceSize(Button button) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.height = dpHeight ;
        params.width = dpWidth * spaceSize;
        params.leftMargin = margin;
        params.rightMargin = margin;
        params.bottomMargin = margin;
        params.topMargin = margin;
        button.setLayoutParams(params);
    }

    private void setClearSize(ImageButton button) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.height = dpHeight ;
        params.width = (int) Math.round(dpWidth * 1.5);
        params.leftMargin = margin;
        params.rightMargin = margin;
        params.bottomMargin = margin;
        params.topMargin = margin;
        button.setLayoutParams(params);
    }

    public void hideKeyboard() {
        if (keyboardView != null) {
            View v = keyboardView.findViewById(R.id.button_1);
            viewGroup.removeView(v);
            Log.e(TAG, "Keyboard was hidden");
        } else {
            Log.e("Keyboard", "Keyboard is null!");
        }
    }

    public interface KeyListener {
        void onKeyClicked(String key);
        void onDeleteButtonClicked();
        void onLongDeleteButtonClicked();
    }

    private void setNightMode(boolean status) {
        nightThemeEnabled = status;
    }

    private void setNumberLine(boolean status) {
        numberLineEnabled = status;
    }

    public static class Builder {

        private Context context;
        private WindowManager windowManager;
        private KeyListener callback;
        private ViewGroup viewGroup;
        private boolean nightMode = false;
        private boolean numberLine = false;

        public Builder(Activity activity, KeyListener callback) {
            this.context = activity.getApplicationContext();
            this.windowManager = activity.getWindowManager();
            this.callback = callback;
            this.viewGroup = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        }

        public Builder setNightMode(boolean status) {
            nightMode = status;
            return this;
        }

        public Builder enableNumberLine(boolean status) {
            numberLine = status;
            return this;
        }

        public Keyboard build() {
            Keyboard keyboard = new Keyboard(context, windowManager, callback, viewGroup);
            keyboard.setNightMode(nightMode);
            keyboard.setNumberLine(numberLine);
            return keyboard;
        }
    }
}