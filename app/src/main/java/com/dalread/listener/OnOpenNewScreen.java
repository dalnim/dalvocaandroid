package com.dalread.listener;

public interface OnOpenNewScreen {

    void onOpen(); // stop playing vocas from current screen when opening a new screen 근데 이건 BaseActvity에서 상속 받기 때문에 쓸데 없이 모든 클래스에 적용된다. 또 openNewScreen 로 새로운 액티비티를 부를 필요도 없어 보인다. openNewScreen가 뭔가 했네...
}
