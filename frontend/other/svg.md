# Копирование svg

Если на сайте есть какое-то svg и нужно сделать такое же, то:

* Через просмотр страницы ищем элемент.

* Нужно скопировать тег svg целиком, например:

  ```
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 50 50" class="tw-size-4" data-state="closed"><path fill-rule="evenodd" clip-rule="evenodd" d="M19.262 3.015l-1.148-1.15A1.092 1.092 0 0118.884 0h4.025A1.092 1.092 0 0124 1.09v4.024a1.093 1.093 0 01-1.865.773l-1.152-1.15-1.05 1.051c3.603 4.439 3.448 10.915-.469 15.177l.763 1.271a.65.65 0 01-.165.857c-.31.234-.713.533-1.037.778a.636.636 0 01-.5.119.642.642 0 01-.432-.281c-.4-.598-1.016-1.52-1.376-2.063a1.206 1.206 0 00-.828-.522c-1.857-.26-8.092-1.13-10.479-1.462a1.26 1.26 0 01-1.07-1.073C3.957 15.857 2.877 8.11 2.877 8.11a1.197 1.197 0 00-.519-.825C1.81 6.92.89 6.305.291 5.907a.655.655 0 01-.162-.934c.245-.323.547-.726.778-1.034a.65.65 0 01.856-.167l1.271.762C6.731 1.141 12.088.571 16.34 2.827a.535.535 0 01.126.852L15.094 5.05a.538.538 0 01-.609.107 8.72 8.72 0 00-9.27 1.328l1.35 9.228L19.263 3.015zm-1.4 4.844l-9.576 9.578 9.227 1.347a8.723 8.723 0 00.35-10.925z" fill="#b970ca"></path></svg>
  ```

* Далее можно просто вставить все это в текстовый файл и сохранить под расширением `.svg`

* Или можно зайти на сайт https://www.svgviewer.dev/ (самый удобный из найденных), вставить текст туда и скачать как уже готовый svg-файл.