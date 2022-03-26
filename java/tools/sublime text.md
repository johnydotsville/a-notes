# Увеличение шрифта боковой панели

Без регистрации и смс и прочих цыганских аддонов.

* Выбираем дефолтную темную тему *Preferences > Select Theme > Default Dark*

* Открываем через *Preferences > Browse Packages* папку *User*

* Создаем там файл *Default.sublime-theme*

* В него помещаем настройки

  ```json
  [
      {
          "class": "sidebar_label",
          "font.bold": false,
          "font.size": 14,
          "spacegray_tabs_font_large": true
      },
  ]
  ```

  