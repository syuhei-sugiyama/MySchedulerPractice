package com.example.myschedulerpractice

import android.app.Application
import io.realm.Realm

/**
 * データベースの設定処理をアプリケーション起動時に実行させるため
 * Applicationクラスを継承したクラスを作成
 */
class MyScheduleApplication : Application() {
    /**
     * ApplicationクラスのonCreateメソッドをoverride
     * ApplicationクラスのonCreateメソッドは、アプリの実行開始時に、Activityよりも先に呼ばれる
     */
    override fun onCreate() {
        super.onCreate()
        /**
         * Realmの初期化
         * →Realmライブラリを初期化、デフォルト構成の作成
         * context : Applicationを継承しているクラス内の場合はthisを指定
         */
        Realm.init(this)
    }
}