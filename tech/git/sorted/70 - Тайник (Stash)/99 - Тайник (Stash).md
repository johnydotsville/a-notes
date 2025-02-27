* Я находился на ветке feature и добавил несколько новых файлов. Потом выполнил stash и переключился на ветку main. Почему эти новые файлы при этом не исчезли, а остались в рабочей директории?
* Как посмотреть есть ли стэш? Стэш он вообще персональный для ветки или общий?









```
{
    player(steamAccountId:56831765) {
      steamAccountId,
      matches(request: {
        skip:10,take:2
      }) {
        id
        startDateTime
        durationSeconds
        lobbyType
        didRadiantWin
        gameMode
        bracket
        players {
          steamAccount {
            id
            timeCreated
            dotaAccountLevel
            isAnonymous
            seasonRank
            smurfFlag
          }
          hero {
            id
            displayName
            shortName
          }
          isRadiant
          isVictory
          kills
          deaths
          assists
          goldPerMinute
          experiencePerMinute
          networth
          level
          heroDamage
          towerDamage
          position
          lane
          intentionalFeeding
        }
      }
    }
  }

```

