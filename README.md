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
![screenshot](https://i.ibb.co/4KKGkHq/screenshot.png)

## Пример и аналог приложения

* Сделано на основе [примера](https://github.com/Chagall/notification-listener-service-example).
* [Аналог](https://play.google.com/store/apps/details?id=com.jojoagogogo.nf) из PlayMarket.

 **пример** | **аналог**
------------ | -------------
 ![example](https://i.ibb.co/fC0FgVB/example.png) | ![analogue](https://i.ibb.co/NNdfkXD/analogue.webp) 
