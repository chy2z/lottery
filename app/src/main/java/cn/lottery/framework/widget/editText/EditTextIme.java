package cn.lottery.framework.widget.editText;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by admin on 2017/7/9.
 */
public class EditTextIme  extends EditText {

    public EditTextIme(Context context) {
        super(context);
    }

    public EditTextIme(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextIme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface BackListener {
        void back(EditText textView);
    }



    private BackListener listener;

    public void setBackListener(BackListener listener) {
        this.listener = listener;
    }



    @Override

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (listener != null) {
                listener.back(this);
            }
        }
        return false;
    }
}
