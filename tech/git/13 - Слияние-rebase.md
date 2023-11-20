# Rebase-слияние

## Механика ребейза

При выполнении rebase меняется *базовый* коммит фичеветки. Т.е. если фичеветка изначально началась от коммита m2, то после ребейза ее базовым коммитом будет последний коммит мастера (m4). При этом операция заключается не просто в изменении указателя родительского коммита в f1, а в создании абсолютно новых коммитов rf1 и rf2, которые содержат код из f1 и f2 с учетом кода из и m4. И если будут конфликты между m4 и кодом из новых коммитов, придется их решить.

Поскольку ребейз сам по себе не вливает фичеветку в мастер, все равно придется потом сделать merge фичеветки. Просто после свежего ребейза merge выполнится путем fast-forward, без образования merge-коммита.



![rebase vs merge](img/rebase vs merge.png)

## Выполнение ребейза и конфликты

Чтобы выполнить ребейз:

* Переходим на фичеветку

* Выполняем

  ```
  git rebase somemaster
  ```

* Решаем конфликты, если они есть

Так выглядит конфликт при ребейзе - в нижнем маркере показан конкретный коммит, вызвавший конфликт:

```java
public class Person {
<<<<<<< HEAD
    private String name1337;
=======
    private String name;  // Поменял имя на нормальное
>>>>>>> de6e0ef (Моделируем ситуацию конфликта)
}
```

> Когда-то у меня сложилось впечатление, будто ребейз заставил меня двадцать раз решать один и тот же конфликт. Не помню как такое вышло, воспроизвести не получилось. Так что сейчас я говорю, что один конфликт нужно решить единожды

В целом ситуация с конфликтами решается похожим на merge образом - смотрим подсказки гита:

```
Auto-merging src/main/java/johny/dotsville/Person.java
CONFLICT (content): Merge conflict in src/main/java/johny/dotsville/Person.java
error: could not apply de6e0ef... Моделируем ситуацию конфликта
hint: Resolve all conflicts manually, mark them as resolved with
hint: "git add/rm <conflicted_files>", then run "git rebase --continue".
hint: You can instead skip this commit: run "git rebase --skip".
hint: To abort and get back to the state before "git rebase", run "git rebase --abort".
Could not apply de6e0ef... Моделируем ситуацию конфликта
```

```
interactive rebase in progress; onto eece732
Last command done (1 command done):
   pick de6e0ef Моделируем ситуацию
Next commands to do (2 remaining commands):
   pick b36ef7c Готовим несоответствия
   pick 0d37910 Тестирование
  (use "git rebase --edit-todo" to view and edit)
You are currently rebasing branch 'feat/f1' on 'eece732'.
  (fix conflicts and then run "git rebase --continue")
  (use "git rebase --skip" to skip this patch)
  (use "git rebase --abort" to check out the original branch)

Unmerged paths:
  (use "git restore --staged <file>..." to unstage)
  (use "git add <file>..." to mark resolution)
        both modified:   src/main/java/johny/dotsville/Person.java

no changes added to commit (use "git add" and/or "git commit -a")
```

* Правим исходники
* Через `git add` добавляем исправленные файлы в стейдж
* Выполняем `git rebase --continue`
* Решаем следующий конфликт и так пока ребейз не закончится
* Если запутались, `git rebase --abort`
* Когда все закончили, переходим на главную ветку и делаем `git merge features/f1`, merge выполнится через fast-forward и получится ровная история без merge-коммита

## Интерактивный ребейз

ыфв