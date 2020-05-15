package com.example.spielen.ui.joined;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class JoinedViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public JoinedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is joined events fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
