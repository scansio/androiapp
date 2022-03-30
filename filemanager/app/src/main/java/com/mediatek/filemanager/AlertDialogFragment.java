package com.mediatek.filemanager;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.ToastHelper;
import java.io.UnsupportedEncodingException;

public class AlertDialogFragment extends DialogFragment implements OnClickListener {
    private static final String CANCELABLE = "cancelable";
    private static final String ICON = "icon";
    public static final int INVIND_RES_ID = -1;
    private static final String LAYOUT = "layout";
    private static final String MESSAGE = "message";
    private static final String NEGATIVE_TITLE = "negativeTitle";
    private static final String POSITIVE_TITLE = "positiveTitle";
    public static final String TAG = "AlertDialogFragment";
    private static final String TITLE = "title";
    private OnDialogDismissListener mDialogDismissListener;
    protected OnDismissListener mDismissListener = null;
    protected OnClickListener mDoneListener;
    protected ToastHelper mToastHelper = null;

    public interface OnDialogDismissListener {
        void onDialogDismiss();
    }

    public static class AlertDialogFragmentBuilder {
        protected final Bundle mBundle = new Bundle();

        public AlertDialogFragment create() {
            AlertDialogFragment f = new AlertDialogFragment();
            f.setArguments(this.mBundle);
            return f;
        }

        public AlertDialogFragmentBuilder setTitle(int resId) {
            this.mBundle.putInt(AlertDialogFragment.TITLE, resId);
            return this;
        }

        public AlertDialogFragmentBuilder setLayout(int resId) {
            this.mBundle.putInt(AlertDialogFragment.LAYOUT, resId);
            return this;
        }

        public AlertDialogFragmentBuilder setCancelable(boolean cancelable) {
            this.mBundle.putBoolean(AlertDialogFragment.CANCELABLE, cancelable);
            return this;
        }

        public AlertDialogFragmentBuilder setIcon(int resId) {
            this.mBundle.putInt(AlertDialogFragment.ICON, resId);
            return this;
        }

        public AlertDialogFragmentBuilder setMessage(int resId) {
            this.mBundle.putInt(AlertDialogFragment.MESSAGE, resId);
            return this;
        }

        public AlertDialogFragmentBuilder setCancelTitle(int resId) {
            this.mBundle.putInt(AlertDialogFragment.NEGATIVE_TITLE, resId);
            return this;
        }

        public AlertDialogFragmentBuilder setDoneTitle(int resId) {
            this.mBundle.putInt(AlertDialogFragment.POSITIVE_TITLE, resId);
            return this;
        }
    }

    public static class ChoiceDialogFragment extends AlertDialogFragment {
        public static final String ARRAY_ID = "arrayId";
        public static final String CHOICE_DIALOG_TAG = "ChoiceDialogFragment";
        public static final String DEFAULT_CHOICE = "defaultChoice";
        public static final String ITEM_LISTENER = "itemlistener";
        private int mArrayId;
        private int mDefaultChoice;
        private OnClickListener mItemLinster = null;

        public void setItemClickListener(OnClickListener listener) {
            this.mItemLinster = listener;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args;
            LogUtils.d(CHOICE_DIALOG_TAG, "Show alertSortDialog");
            Builder builder = createAlertDialogBuilder(savedInstanceState);
            if (savedInstanceState == null) {
                args = getArguments();
            } else {
                args = savedInstanceState;
            }
            if (args != null) {
                this.mDefaultChoice = args.getInt(DEFAULT_CHOICE);
                this.mArrayId = args.getInt(ARRAY_ID);
            }
            builder.setSingleChoiceItems(this.mArrayId, this.mDefaultChoice, this);
            return builder.create();
        }

        public void onClick(DialogInterface dialog, int which) {
            if (this.mItemLinster != null) {
                this.mItemLinster.onClick(dialog, which);
            }
        }
    }

    public static class ChoiceDialogFragmentBuilder extends AlertDialogFragmentBuilder {
        public ChoiceDialogFragment create() {
            ChoiceDialogFragment f = new ChoiceDialogFragment();
            f.setArguments(this.mBundle);
            return f;
        }

        public ChoiceDialogFragmentBuilder setDefault(int arrayId, int defaultChoice) {
            this.mBundle.putInt(ChoiceDialogFragment.DEFAULT_CHOICE, defaultChoice);
            this.mBundle.putInt(ChoiceDialogFragment.ARRAY_ID, arrayId);
            return this;
        }
    }

    public static class EditDialogFragmentBuilder extends AlertDialogFragmentBuilder {
        public EditTextDialogFragment create() {
            EditTextDialogFragment f = new EditTextDialogFragment();
            f.setArguments(this.mBundle);
            return f;
        }

        public EditDialogFragmentBuilder setDefault(String defaultString, int defaultSelection) {
            this.mBundle.putString(EditTextDialogFragment.DEFAULT_STRING, defaultString);
            this.mBundle.putInt(EditTextDialogFragment.DEFAULT_SELCTION, defaultSelection);
            return this;
        }
    }

    public static class EditTextDialogFragment extends AlertDialogFragment {
        public static final String DEFAULT_SELCTION = "defaultSelection";
        public static final String DEFAULT_STRING = "defaultString";
        public static final String TAG = "EditTextDialogFragment";
        private EditText mEditText;
        private EditTextDoneListener mEditTextDoneListener;

        public interface EditTextDoneListener {
            void onClick(String str);
        }

        public void onSaveInstanceState(Bundle outState) {
            getArguments().putString(DEFAULT_STRING, this.mEditText.getText().toString());
            getArguments().putInt(DEFAULT_SELCTION, this.mEditText.getSelectionStart());
            super.onSaveInstanceState(outState);
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args;
            setOnDoneListener(this);
            Builder builder = createAlertDialogBuilder(savedInstanceState);
            if (savedInstanceState == null) {
                args = getArguments();
            } else {
                args = savedInstanceState;
            }
            if (args != null) {
                String defaultString = args.getString(DEFAULT_STRING, "");
                int selction = args.getInt(DEFAULT_SELCTION, 0);
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
                builder.setView(view);
                this.mEditText = (EditText) view.findViewById(R.id.edit_text);
                if (this.mEditText != null) {
                    this.mEditText.setText(defaultString);
                    this.mEditText.setSelection(selction);
                }
            }
            return builder.create();
        }

        public void onResume() {
            super.onResume();
            if (this.mEditText != null && this.mEditText.getText().length() == 0) {
                Button button = ((AlertDialog) getDialog()).getButton(-1);
                if (button != null) {
                    button.setEnabled(false);
                }
            }
            getDialog().getWindow().setSoftInputMode(5);
            setTextChangedCallback(this.mEditText, (AlertDialog) getDialog());
        }

        private void setEditTextFilter(EditText edit, final int maxLength) {
            edit.setFilters(new InputFilter[]{new LengthFilter(maxLength) {
                private static final int VIBRATOR_TIME = 100;
                boolean mHasToasted = false;

                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    int oldSize = 0;
                    int newSize = 0;
                    if (EditTextDialogFragment.this.mEditText != null) {
                        String oldText = EditTextDialogFragment.this.mEditText.getText().toString();
                        try {
                            oldSize = oldText.getBytes("UTF-8").length;
                            LogUtils.d(EditTextDialogFragment.TAG, "filter,oldSize=" + oldSize + ",oldText=" + oldText);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            oldSize = oldText.length();
                        }
                    }
                    if (source != null) {
                        String newText = source.toString();
                        try {
                            newSize = newText.getBytes("UTF-8").length;
                            LogUtils.d(EditTextDialogFragment.TAG, "filter,newSize=" + newSize + ",newText =" + newText);
                        } catch (UnsupportedEncodingException e2) {
                            e2.printStackTrace();
                            newSize = newText.length();
                        }
                    }
                    if (source == null || source.length() <= 0 || oldSize + newSize <= maxLength) {
                        if (source != null && source.length() > 0 && !this.mHasToasted && dstart == 0 && source.charAt(0) == '.') {
                            EditTextDialogFragment.this.mToastHelper.showToast((int) R.string.create_hidden_file);
                            this.mHasToasted = true;
                        }
                        return super.filter(source, start, end, dest, dstart, dend);
                    }
                    LogUtils.d(EditTextDialogFragment.TAG, "oldSize + newSize) > maxLength,source.length()=" + source.length());
                    Vibrator vibrator = (Vibrator) EditTextDialogFragment.this.getActivity().getSystemService("vibrator");
                    boolean hasVibrator = vibrator.hasVibrator();
                    if (hasVibrator) {
                        vibrator.vibrate(new long[]{100, 100}, -1);
                    }
                    LogUtils.w(EditTextDialogFragment.TAG, "input out of range,hasVibrator:" + hasVibrator);
                    return "";
                }
            }});
        }

        protected void setTextChangedCallback(EditText editText, final AlertDialog dialog) {
            setEditTextFilter(editText, FileInfo.FILENAME_MAX_LENGTH);
            editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable arg0) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Button botton;
                    if (s.toString().length() <= 0 || s.toString().matches(".*[/\\\\:*?\"<>|\t].*") || s.toString().equals(".") || s.toString().equals("..")) {
                        if (s.toString().matches(".*[/\\\\:*?\"<>|\t].*")) {
                            EditTextDialogFragment.this.mToastHelper.showToast((int) R.string.invalid_char_prompt);
                        }
                        botton = dialog.getButton(-1);
                        if (botton != null) {
                            botton.setEnabled(false);
                            return;
                        }
                        return;
                    }
                    botton = dialog.getButton(-1);
                    if (botton != null) {
                        botton.setEnabled(true);
                    }
                }
            });
        }

        public String getText() {
            if (this.mEditText != null) {
                return this.mEditText.getText().toString().trim();
            }
            return null;
        }

        public void setOnEditTextDoneListener(EditTextDoneListener listener) {
            this.mEditTextDoneListener = listener;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (this.mEditTextDoneListener != null) {
                this.mEditTextDoneListener.onClick(getText());
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putAll(getArguments());
        super.onSaveInstanceState(outState);
    }

    public void setOnDoneListener(OnClickListener listener) {
        this.mDoneListener = listener;
    }

    public void onClick(DialogInterface dialog, int which) {
        if (this.mDoneListener != null) {
            this.mDoneListener.onClick(dialog, which);
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createAlertDialogBuilder(savedInstanceState).create();
    }

    protected Builder createAlertDialogBuilder(Bundle savedInstanceState) {
        Bundle args;
        if (savedInstanceState == null) {
            args = getArguments();
        } else {
            args = savedInstanceState;
        }
        Builder builder = new Builder(getActivity());
        if (args != null) {
            int title = args.getInt(TITLE, -1);
            if (title != -1) {
                builder.setTitle(title);
            }
            int icon = args.getInt(ICON, -1);
            if (icon != -1) {
                builder.setIcon(icon);
            }
            int message = args.getInt(MESSAGE, -1);
            int layout = args.getInt(LAYOUT, -1);
            if (layout != -1) {
                builder.setView(getActivity().getLayoutInflater().inflate(layout, null));
            } else if (message != -1) {
                builder.setMessage(message);
            }
            int cancel = args.getInt(NEGATIVE_TITLE, -1);
            if (cancel != -1) {
                builder.setNegativeButton(cancel, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            }
            int done = args.getInt(POSITIVE_TITLE, -1);
            if (done != -1) {
                builder.setPositiveButton(done, this);
            }
            this.mToastHelper = new ToastHelper(getActivity());
            builder.setCancelable(args.getBoolean(CANCELABLE, true));
        }
        return builder;
    }

    public void setDismissListener(OnDismissListener listener) {
        this.mDismissListener = listener;
    }

    public void setOnDialogDismissListener(OnDialogDismissListener listener) {
        this.mDialogDismissListener = listener;
    }

    public void onDismiss(DialogInterface dialog) {
        if (this.mDismissListener != null) {
            this.mDismissListener.onDismiss(dialog);
        }
        if (this.mDialogDismissListener != null) {
            this.mDialogDismissListener.onDialogDismiss();
        }
        super.onDismiss(dialog);
    }
}
