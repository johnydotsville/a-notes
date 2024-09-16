Для типизации асинхронной функции используется дженерик `Promise<>`. Его надо закрыть типом, который мы хотим вернуть из функции:

```typescript
async loadConstants() {
    const gv = await this.loadGameVersion();
    console.log(gv.name);
}

private async loadGameVersion(): Promise<GameVersion> {  // <-- Хотим вернуть GameVersion
    const gameVersionGQL = {
    operationName: "getGameVersions",
    query: "query getGameVersions { constants { gameVersions { id, name, asOfDateTime } } }",
    variables: {}
};

this.requestConfig.data = gameVersionGQL;
const response = await axios.request(this.requestConfig);
const gv = response.data.data.constants.gameVersions[0];

return new GameVersion(gv.id, gv.name, gv.asOfDateTime);
}
```

