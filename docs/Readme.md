# InventoryManager: 本（または自分の所有物）を管理する Androidアプリケーション

InventoryManagerは、本やその他の所有物を管理するAndroidアプリケーションです。

## スクリーンショット

![Screenshot](images/screenshots.png "Screenshots")

## 機能概要

* アイテムの登録
    * アイテムの登録
    * カメラを使った書影の記録、バーコードの読み出し、文字認識
* 登録データの管理
    * 登録したアイテムの一覧表示
    * 一覧のフィルタリング
    * 登録データの詳細確認、メモやレーティングの記録
    * アイテムのISBN番号を使って国立国会図書館に登録されている情報で更新
* データのインポート/エクスポート
    * 登録した全データのエクスポート
    * エクスポートしたデータのインポート

## インストール

（登録方法 検討中）

## 画面遷移

以下に画面遷移を示します。アプリ起動直後は、メイン画面を表示します。

![画面遷移](images/screen_transition.png "画面遷移")

必要な機能に合わせ、画面を切り替えてください。

## 画面説明

### メイン画面 (Main Screen)

起動直後に表示する画面です。

![メイン画面](images/main_screen.png "メイン画面")

### 一覧画面 (Inventory List Screen)

登録済のデータ一覧を表示する画面です。

![一覧画面](images/list_screen.png "一覧画面")

リストをタッチすると、詳細画面に遷移します。『←』ボタンで、メイン画面に戻ることができます。

![詳細画面に遷移](images/inventory_list.png "詳細画面に遷移")

フィルタアイコンをタッチすると、一覧に表示する内容をフィルタリングすることができます。リロードボタンで、フィルタリングの解除ができます。

![一覧画面のフィルタリング](images/filter_settings_filter.png "一覧画面のフィルタリング")


### 詳細画面 (Detail Screen)

![詳細画面](images/detail_screen.png "詳細画面")

### 登録画面 (Register Screen)

![登録画面](images/register_screen.png "登録画面")

### インポート画面 (Import Screen)

![インポート画面](images/import_screen.png "インポート画面")

### 設定画面 (Preferences Screen)

![設定画面](images/preferences_screen.png "設定画面")


## その他


## ソースコード

* https://github.com/MRSa/InventoryManager.git
