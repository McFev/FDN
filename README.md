# FDN (forward & deleting notifications)

<p align="center">
  <img width="150" height="150" src="https://github.com/McFev/FDN/blob/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png?raw=true">
</p>
Это приложение, которое пересылает push-уведомление на указанный URL адрес.

## Пример настроек
`settings.json`
```json
{
    "url": "https://sample.com/fdn?name=%s&title=%s&text=%s&subtext=%s&bigtext=%s&infotext=%s",
    "applications": [
        {"appName": "io.simplepush", "toDelete": true},
        {"appName": "ru.cardsmobile.mw3", "toDelete": true},
        {"appName": "ru.serebryakovas.lukoilmobileapp", "toDelete": true},
        {"appName": "ru.instamart", "toDelete": false},
        {"appName": "com.citymobil", "toDelete": false}
    ]
}
```
`index.php`
```php
<?php
$name = $_GET["name"];
$title = $_GET["title"];
$text = $_GET["text"];
$subtext = $_GET["subtext"];
$bigtext = $_GET["bigtext"];
$infotext = $_GET["infotext"];

if($name === "com.sample.app") {
	$message = '☁️ #' . str_replace('.', '', $name) . '\r\n\r\ntitle: `' . $title . '`\r\ntext: `' . $text . '`\r\nsubtext: `' . $subtext . '`\r\nbigtext: `' . $bigtext . '`\r\ninfotext: `' . $infotext . '`';
    
	$cmd = "/usr/bin/curl -X POST -H 'Content-Type: application/json' -d '{\"chat_id\": \"********\", \"text\": \"" . $message . "\", \"parse_mode\": \"markdown\"}' https://api.telegram.org/bot******/sendMessage";
	$ret = exec($cmd);
	echo $ret;
}
?>
```


## Скриншот
![screenshot](https://github.com/user-attachments/assets/9b59c85f-b8d9-4621-84b8-36a9cb6ae1e2)


## Пример и аналог приложения

* Сделано на основе [примера](https://github.com/Chagall/notification-listener-service-example).
* [Аналог](https://play.google.com/store/apps/details?id=com.jojoagogogo.nf) из PlayMarket.

 **пример** | **аналог**
------------ | -------------
 ![example](https://github.com/user-attachments/assets/204ffe49-6e7f-47d9-90bd-83e1ad99cd13) | ![analogue](https://github.com/user-attachments/assets/94dcdf58-2767-498c-be48-51b00e9d1c57)

