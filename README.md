# task_04_timeview
The custom view what shows time

Задание:
Необходимо реализовать собственный компонент для отображения круглых, аналоговых часов. Должны работать секундная, минутная и часовая стрелки.
Доп. задание:
Добавить возможность изменения цвета стрелочек, а также их размер, из xml с помощью аттрибутов.

Сделал одним целым View, компонент показывает текущее время, учитывает paddings (причём paddingStart и paddingEnd имеет больший приоритет, чем paddingRight и paddingLeft),
с помощью xml можно настроить длины стрелок относительно радиуса циферблата, длину задней части стрелок относительно длины часовой стрелки, цвета контура 
и меток циферблата, самого циферблата и стрелок.
