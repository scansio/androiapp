package com.klye.ime.voiceime;

import android.annotation.TargetApi;
import android.inputmethodservice.InputMethodService;
import android.os.Build.VERSION;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.klye.ime.latin.M;
import java.util.List;
import java.util.Map;

@TargetApi(11)
class ImeTrigger implements Trigger {
    private static final String VOICE_IME_PACKAGE_PREFIX = "com.google.android";
    private static final String VOICE_IME_SUBTYPE_MODE = "voice";
    private final InputMethodService mInputMethodService;

    public ImeTrigger(InputMethodService inputMethodService) {
        this.mInputMethodService = inputMethodService;
    }

    public void startVoiceRecognition(String language1) {
        InputMethodManager inputMethodManager = getInputMethodManager(this.mInputMethodService);
        InputMethodInfo inputMethodInfo = getVoiceImeInputMethodInfo(inputMethodManager);
        if (inputMethodInfo != null) {
            inputMethodManager.setInputMethodAndSubtype(this.mInputMethodService.getWindow().getWindow().getAttributes().token, inputMethodInfo.getId(), getVoiceImeSubtype(inputMethodManager, inputMethodInfo));
        }
    }

    private static InputMethodManager getInputMethodManager(InputMethodService inputMethodService) {
        return (InputMethodManager) inputMethodService.getSystemService("input_method");
    }

    @TargetApi(11)
    private InputMethodSubtype getVoiceImeSubtype(InputMethodManager inputMethodManager, InputMethodInfo inputMethodInfo) throws SecurityException, IllegalArgumentException {
        Map<InputMethodInfo, List<InputMethodSubtype>> map = inputMethodManager.getShortcutInputMethodsAndSubtypes();
        List<InputMethodSubtype> list = inputMethodManager.getEnabledInputMethodSubtypeList(inputMethodInfo, true);
        if (list == null || list.size() <= 0) {
            return null;
        }
        for (InputMethodSubtype i : list) {
            if (i.getLocale().indexOf(M.voIL().substring(0, 2)) != -1) {
                return i;
            }
        }
        return (InputMethodSubtype) list.get(0);
    }

    @TargetApi(11)
    private static InputMethodInfo getVoiceImeInputMethodInfo(InputMethodManager inputMethodManager) throws SecurityException, IllegalArgumentException {
        for (InputMethodInfo inputMethodInfo : inputMethodManager.getEnabledInputMethodList()) {
            for (int i = 0; i < inputMethodInfo.getSubtypeCount(); i++) {
                if (VOICE_IME_SUBTYPE_MODE.equals(inputMethodInfo.getSubtypeAt(i).getMode()) && inputMethodInfo.getComponent().getPackageName().startsWith(VOICE_IME_PACKAGE_PREFIX)) {
                    return inputMethodInfo;
                }
            }
        }
        return null;
    }

    @TargetApi(11)
    public static boolean isInstalled(InputMethodService inputMethodService) {
        if (VERSION.SDK_INT < 14) {
            return false;
        }
        InputMethodInfo inputMethodInfo = getVoiceImeInputMethodInfo(getInputMethodManager(inputMethodService));
        if (inputMethodInfo == null || inputMethodInfo.getSubtypeCount() <= 0) {
            return false;
        }
        return true;
    }

    public void onStartInputView() {
    }
}
