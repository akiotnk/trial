2015-06-20
平均値などがおかしいので検証
間違いがいろいろあり。
static void calcAlertLevel () を訂正。

PS O:\test\10> jar cvfm Sokie.jar Sokie.mf *.class
マニフェストが追加されました
Sokie$1.classを追加中です(入=420)(出=306)(27%収縮されました)
Sokie.classを追加中です(入=7834)(出=4344)(44%収縮されました)
GraphPanel.classを追加中です(入=4429)(出=2537)(42%収縮されました)
readCSV.classを追加中です(入=3129)(出=1748)(44%収縮されました)
EvalDivide.classを追加中です(入=9653)(出=4981)(48%収縮されました)
Data.classを追加中です(入=862)(出=560)(35%収縮されました)

2015-06-18
ファイル選択をカレントディレクトリから選ぶように。
できたらその次からは前回に選んだディレクトリから探すように。
できた!!

ボタンの配置も変更する。
評価サンプル数を指定できるようにする。

https://www.travelblog.org/pix/Wallpaper/sunset_wallpaper_brazil-1600x1200.jpg

2015-06-16
一応すべての機能が動作。
スペックとしては、評価データの個数を可変にすることが残っている。
できた事柄：
(1) xxx.csvファイルを読み込んで、daily, evalデータを出力する。
    paramファイルも出力。
(2) dailyファイルとparamファイルを表示
(3) evalファイルとparamファイルを表示

2015-06-13
プログラムの形ができた。
ボタンなどのGUIを作るSokie.javaと描画をつかさどるGraphPanelに分けてある。
