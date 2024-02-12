# Локальная установка kubernetes

Ссылка на [официальный мануал](https://kubernetes.io/ru/docs/tasks/tools/install-kubectl/#%D1%83%D1%81%D1%82%D0%B0%D0%BD%D0%BE%D0%B2%D0%BA%D0%B0-kubectl-%D0%B2-windows)

Установка на Windows 10 с использованием встроенного в нее гипервизора Hyper-V.

## Windows

* Для работы нам понадобится гипервизор. В Windows 10 он встроен, но мб отключен. Влючаем:
  * Открываем PowerShell от имени администратора.
  * Выполняем команду `Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -All`
  * Перезагружаем компьютер.
* Качаем kubectl. [Ссылка на exe](https://dl.k8s.io/release/v1.29.1/bin/windows/amd64/kubectl.exe)
  * Это консольная утилита для взаимодействия с кластером кубера. Инструкция [тут](https://kubernetes.io/ru/docs/tasks/tools/install-kubectl/#%D1%83%D1%81%D1%82%D0%B0%D0%BD%D0%BE%D0%B2%D0%BA%D0%B0-kubectl-%D0%B2-windows).
  * Кладем его в директорию, прописанную в PATH.
    * Например, создадим директорию `C:\other\k8s` и положим туда скачанный `kubectl.exe`.
    * Добавим эту директорию в PATH. *Пуск > Параметры > О программе > Дополнительные параметры системы > Переменные среды > Path > Изменить*.
    * P.S. Это может сломать докер, потому что он тоже ставит kubectl. В этом случае можно просто удалить нашу запись.
* Качаем minikube. [Ссылка](https://github.com/kubernetes/minikube/releases/latest/download/minikube-installer.exe)
  * Устанавливаем как обычное настольное приложение.
  * Открываем PowerShell от имени администратора. 
  * Запускаем кластер командой `minikube start --driver=hyperv`
    * Для остановки набираем `minikube stop`
  * Проверяем, работает ли kubectl.
    * После установки minikube должен автоматически создаться конфиг для kubectl.
    * Запускаем PowerShell от имени администратора.
    * При выполнении `kubectl cluster-info` должны показаться адреса Control plane и CoreDNS.

