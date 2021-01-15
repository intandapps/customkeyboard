package tv.limehd.keyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class Keyboard extends LinearLayout {

    private final String TAG = "Keyboard";

    // Передаваемые параметры
    private WindowManager windowManager;
    private KeyListener callback;
    private ViewGroup viewGroup;
    private boolean isFocused = false;

    private boolean isKeyboardActive = false;

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
    private LinearLayout linearLayout;

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
    private int orientation = 4;
    private Window window;
    private View v;
    private View lastFocusedView;
    private boolean focus;
    int counter = 0;

    public Keyboard(Context context, WindowManager windowManager, KeyListener callback, ViewGroup viewGroup, Window window, View v) {
        super(context);
        this.callback = callback;
        this.viewGroup = viewGroup;
        this.windowManager = windowManager;
        this.window = window;
        this.v = v;
        OrientationEventListener orientationListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int i) {
                int rotate = windowManager.getDefaultDisplay().getRotation();
                if (rotate != orientation) {
                    orientation = rotate;
                    if (isKeyboardActive) {
                        hideKeyboard();
                        showKeyboard();
                    }
                }
            }
        };
        orientationListener.enable();
    }

    public void showKeyboard() {
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = Float.parseFloat(String.valueOf(size.x));
        float height = Float.parseFloat(String.valueOf(size.y));
        margin = Math.round(width / 135) / 2;
        dpWidth = (int) Math.floor(width / param) - margin * 2;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;

        dpHeight = width > height ? (Math.round(height / param) - margin * 2) : Math.round(dpWidth * sizeRation) - margin * 2;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int resId = numberLineEnabled ? R.layout.keyboard_view_nums : R.layout.keyboard_view;
        keyboardView = inflater.inflate(resId, null, false);

        ((ViewGroup) v).addView(keyboardView);
        keyboardView.setOnTouchListener((v, event) -> true);
        keyboardView.setLayoutParams(params);

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
        isKeyboardActive = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public void clearFocusFromKey() {
        if (focus) {
            callback.onKeyboardHideClicked();
        }
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
                //button.setFocusableInTouchMode(true);
                //button.setFocusable(true);
                changeLanguage = button;
                setLangunageButtonSize(button);
                if (isRussian) {
                    button.setText(R.string.ru_title);
                } else {
                    button.setText(R.string.eng_title);
                }
                button.setBackground(getResources().getDrawable(R.drawable.action_button_style));
                button.setOnClickListener(v1 -> { // Смена языка
                    //setFocusOnBoard();
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
                b.setFocusable(true);
                if (i == startIndex && j == 0) firstKey = b;
                b.setText(array[j]);
                b.setOnClickListener(v1 -> {
                    callback.onKeyClicked(b.getText().toString());
                });
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
                button.setOnClickListener(v1 -> {
                    callback.onDeleteButtonClicked();
                }); // Стереть 1 символ
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
        linearLayout = keyboardView.findViewById(getLinearLayoutId(numberLineEnabled ? keyLines.size() : keyLines.size() - 1));
        View v = inflater.inflate(R.layout.keyboard_hide, linearLayout, true);
        ImageButton ib = v.findViewById(R.id.key_button);
        if (nightThemeEnabled) {
            ib.setBackground(getResources().getDrawable(R.drawable.night_action_button_style));
            ib.setColorFilter(getResources().getColor(R.color.white));
        } else {
            ib.setBackground(getResources().getDrawable(R.drawable.action_button_style));
        }
        ib.setOnClickListener(v1 -> {
            callback.onKeyboardHideClicked();
        });
        ib.setId(R.id.button_1);
        setClearSize(ib);
        // Добавление пробела
        v = inflater.inflate(R.layout.keyboard_space, linearLayout, true);
        Button b = v.findViewById(R.id.key_button);
        b.setOnClickListener(v1 -> {
            callback.onKeyClicked(" ");
        });
        setSpaceSize(b);
        b.setId(R.id.button_1);
        if (nightThemeEnabled) {
            b.setBackground(getResources().getDrawable(R.drawable.night_action_button_style));
        } else {
            b.setBackground(getResources().getDrawable(R.drawable.action_button_style));
        }
        v = inflater.inflate(R.layout.keyboard_search, linearLayout, true);
        ib = v.findViewById(R.id.key_button);
        //ib.setFocusableInTouchMode(true);
        ib.setFocusable(true);
        if (nightThemeEnabled) {
            ib.setBackground(getResources().getDrawable(R.drawable.night_action_button_style));
            ib.setColorFilter(getResources().getColor(R.color.white));
        } else {
            ib.setBackground(getResources().getDrawable(R.drawable.action_button_style));
        }
        ib.setOnClickListener(v1 -> {
            callback.onKeyboardOkClicked();
        });
        setClearSize(ib);
    }

    public int getButtonsId() {
        return R.id.button_1;
    }

    public void setFocusOnFirstKey() {
        firstKey.requestFocusFromTouch();
        isFocused = true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = window.getCurrentFocus();
            if (v.getId() != R.id.button_1) {
                v.clearFocus();
                callback.onKeyboardHideClicked();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setFocusOnBoard() {
        keyboardView.requestFocus();
        keyboardView.requestFocusFromTouch();
    }

    public int getKeyboardHeight() {
        int d = dpHeight + margin * 2;
        int lines = 4;
        if (numberLineEnabled) lines++;
        return d * lines;
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
            ((ViewGroup) v).removeView(keyboardView);
        } else {
            Log.e(TAG, "Keyboard is null");
        }
        isKeyboardActive = false;
    }

    public interface KeyListener {
        void onKeyClicked(String key);
        void onDeleteButtonClicked();
        void onLongDeleteButtonClicked();
        void onKeyboardHideClicked();
        void onKeyboardOkClicked();
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
        private Window window;
        private View v;
        private Activity activity;
        private boolean nightMode = false;
        private boolean numberLine = false;

        public Builder(Activity activity, KeyListener callback, View v) {
            this.context = activity.getApplicationContext();
            this.windowManager = activity.getWindowManager();
            this.callback = callback;
            this.viewGroup = (ViewGroup) activity.getWindow().getDecorView().getRootView();
            this.v = v;
            this.window = activity.getWindow();
            this.activity = activity;
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
            Keyboard keyboard = new Keyboard(context, windowManager, callback, viewGroup, window, v);
            keyboard.setNightMode(nightMode);
            keyboard.setNumberLine(numberLine);
            return keyboard;
        }
    }
}