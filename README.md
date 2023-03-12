# Телеграм бот для социальной сети
![Version](https://img.shields.io/badge/version-1.0-blue.svg?cacheSeconds=2592000)

> Телеграм бот для социальной сети https://github.com/Hebsat/socialNetwork-teamProject.<br>
> Бот написан на основе TelegramLongPollingBot.


## Автор

👤 **Yuri Kaleganov**

* Github: [@Yuri_Kaleganov](https://github.com/Hebsat)

### Описание проекта
Представляю вашему вниманию телеграм бота, который подключается к социальной сети. Бот позволяет посмотреть информацию о себе, посмотреть информацию о своих друзьях, а так же получать уведомления о появлении новых постов, комментариев, лайках, заявках в друзья и днях рождения друзей:)!
#### Основные команды
Список основных команд можно посмотреть в выпадающем меню:

![tb_menu](https://user-images.githubusercontent.com/109655199/224533795-e207499c-2420-449e-800d-1cfa80786aa5.png)

##### /start
![tb_start](https://user-images.githubusercontent.com/109655199/224534007-8facbe1b-c884-4cb2-addf-13bb963bdd15.png)

С команды /start начинается взаимодействие с ботом. При использовании данной команды прерываются все начатые с ботом диалоги, бот возвращается в начальное состояние.
##### /stop
![tb_stop](https://user-images.githubusercontent.com/109655199/224534076-af76813c-af95-41fe-8d1f-c82501f91c14.png)

Команда /stop прерывает все начатые диалоги, а также удаляет все данные авторизации в соцсети.
##### /auth
![tb_auth](https://user-images.githubusercontent.com/109655199/224534204-4ac2c7fc-ef95-4c78-aad8-53281bae4d3f.png)

Для запуска диалога авторизации необходимо воспользоваться командой /auth. После чего в начавшемся диалоге нужно ввести адрес электронной почты и пароль для доступа в социальную сеть. Авторизация открывает возможность использования команд из следующего блока.
##### /help
![tb_help](https://user-images.githubusercontent.com/109655199/224534433-c5a1e521-52bd-47e1-ac07-f17d2cc9ccd0.png)

Со списком всех команд можно ознакомиться набрав /help
#### Команды, доступные после авторизации
При запросе команд из данного списка будет выведено предложение авторизоваться:

![tb_unathorized](https://user-images.githubusercontent.com/109655199/224535520-3c16e161-db89-477e-b84e-ddd0547dc170.png)

##### /settings
![tb_notifications](https://user-images.githubusercontent.com/109655199/224534569-7b31e1a2-2bcb-410b-9b77-ecb06dbf5459.png)

Запуск диалока настроек. Команда /notification позволяет включить/отключить получение уведомлений от соцсети. Для этого после соответствующего вопроса необходимо написать yes или no.
##### /myself
![tb_myself](https://user-images.githubusercontent.com/109655199/224534828-13932e42-2a36-4007-baab-18a5203acb80.png)

Для получения краткой информации о себе нужно ввести команду /myself.
##### /friends
![tb_friends](https://user-images.githubusercontent.com/109655199/224534858-ec0fdf8d-2a27-4c49-b03c-81a3788eec7b.png)

Посмотреть список своих друзей можно по команде /friends. При этом друзья будут выводиться по трое. Для просмотра следующей троицы друзей нужно ввести команду /next. Если общее количество друзей менее 3 или показаны все друзья, команда /next будет недоступна.
![tb_friends_empty](https://user-images.githubusercontent.com/109655199/224535085-849869ad-a915-4c8a-baea-75b518805e63.png)

При попытке запроса этой команды бот выведет сообщение о том, что не понимает, какая информация от него требуется :)
![tb_incorrect_next](https://user-images.githubusercontent.com/109655199/224535169-2fcfb4b2-0c82-40bf-9276-1c3af3187464.png)

##### /gratzme
![tb_gratzme](https://user-images.githubusercontent.com/109655199/224535375-455ef631-4e8b-4856-a6ec-498f8fcdcd5f.png)

Также бот может написать приветственное сообщение по команде /gratzme. В этом сообщении будет указана информация о погоде в том населенном пункте, который пользователь указал в социальной сети, а так же курсовая стоимость валют.
<br>
После включения получения уведомлений, при возникновении соответствующих событий в социальной сети, бот будет присылать сообщения.
![tb_notification_comment](https://user-images.githubusercontent.com/109655199/224535667-ccf02ab5-ac4b-43a7-8091-e84f9f2b7e64.png)

По умолчанию приходят уведомления 
- о создании нового поста кем-либо из друзей пользователя
- о появлении нового комментария на опубликованный пользователем пост
- о комментарии оставленного пользователем комментария
- о получении лайка на пост или комментарий пользователя
- о заявках в друзья
- о днях рождения друзей
Выборочная настройка уведомлений доступна на странице настроек в самой социальной сети.

