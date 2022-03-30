package com.klye.ime.voiceime;

interface Trigger {
    void onStartInputView();

    void startVoiceRecognition(String str);
}
