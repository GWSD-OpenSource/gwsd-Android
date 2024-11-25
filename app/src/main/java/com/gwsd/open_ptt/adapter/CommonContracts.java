package com.gwsd.open_ptt.adapter;

import android.view.View;

public interface CommonContracts {

    interface State{
        int NONE=0;
        int START=1;
        int ING=2;
        int SUCCESS=3;
        int FAIL=4;
        int END=5;
        int REPEAT=6;
    }

    interface HolderCallAdapter{
        void holderCallAdapterOnItemClick(CommonHolder holder, View view);
        void  holderCallAdapteronClick(CommonHolder holder, View view);
        boolean   holderCallAdapteronLongClick(CommonHolder holder, View view);
    }

}
