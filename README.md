# BESTCardReader

Проект разработан в рамках участия в "Best Hack 2019".

Помимо основного задания были выполнены все дополнительные:

Более подробную информацию о работе каждой функции можно посмотреть ниже в разделе "Быстрый старт".

## Для пользователя
Спасибо, что уделили внимание нашему проекту!
>**Обратите внимание!**
>Данный проект разрабатывался в рамках мероприятия "Best Hack 2019" и предоставляется в пользование исключительно в ознакомительных целях.

## Установка
1. Скачайте .apk файл данного приложения <a href="https://github.com/Shaniser/Canteen/blob/master/builds/canteen%201.4.apk">НАДО ДОБАВИТЬ ССЫЛКУ, ЕСЛИ НЕ ЛЕНЬ</a>
2. Следуйте дальнейшим инструкциям при установке

### Команда разработчиков
* Алексей Костюченко,   [ВК](https://vk.com/shaniser)
* Михаил Соколовский,   [ВК](https://vk.com/sokolmish)
* Илья Щербаков,   [ВК](https://vk.com/ylyxa)
* Илья Каркин,   [ВК](https://vk.com/id210438588)


## Принцип распознания информации топливной карты
Предварительая обработка осуществляется при помощи mobile vision api. Распознанный текст разделяется по строкам и по высоте символов и поступает на обработку. Весь входной поток данных обрабатывается на совпадение с шаблонами и наилучшие результаты с наиболее удачных кадров для отдельного шаблона запоминаются. Скнирование завершается, когда будет распознана вся требуемоя информация. При возникновении ошибок пользователь имеет возсожность дополнить данные вручную. В таком случе распознание данного поля приостанавливается.
> Пример шаблона даты вида ММ/ГГ: [0-1][0-9]/[0-9][0-9]
<ul>
    <li><a href="https://github.com/Shaniser/Canteen/blob/master/canteenExample.txt">Пример 1</li>
    <li><a href="https://github.com/Shaniser/Canteen/blob/master/dishes.txt">Пример 2</a></li>
</ul>

## Дополнительный функционал
**???**

## Быстрый старт
**После установки и запуска приложения перед вами откароется меню распознания карты.**
>Поднесите карту к камере, чтобы распознать ее

<img src="images/1.png" width="300dp">

**После распознания поля, оно становится зеленым.**

<img src="images/2.png" width="300dp">

>Для того, чтобы редактировать поле в ручную нажмите на кнопку "редактировать"

<img src="images/3.png" width="300dp">

**Если данное поле отсутствует на топливной карте его можно отключить**

>Так как приложение создано в условиях недостатка информации о топливных картах, мы не можем определить, какие дополнительные поля должны присутствовать на карте.
 Предполагается, что по основным данным можно однозначно определить, какие дополнительные данные необходимо распознавать для данной карты.

<img src="images/4.png" width="300dp">

#### Надеемся, что вам понравится наше приложение!
