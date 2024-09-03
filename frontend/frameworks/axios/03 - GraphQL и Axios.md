# GraphQL и Axios

```javascript
const graphqlQuery = {
  operationName: "getGameVersions",
  query: "query getGameVersions { constants { gameVersions { id, name, asOfDateTime } } }",
  variables: {}
};

const response = await axios({
  url: "https://api.stratz.com/graphql",
  method: "post",
  headers: {
    "content-type": "application/json",
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJTdWJqZWN0IjoiNmVmNzRjMmItMjJiZi00Mzk3LTgyMTUtY2VjYjk3ZDY2YmNkIiwiU3RlYW1JZCI6IjU2ODMxNzY1IiwibmJmIjoxNzE2ODk2ODk5LCJleHAiOjE3NDg0MzI4OTksImlhdCI6MTcxNjg5Njg5OSwiaXNzIjoiaHR0cHM6Ly9hcGkuc3RyYXR6LmNvbSJ9.QoAd60oMIUV4D8N73Lcj6b2MTqc-96vv6PzFcLQqrhg"
  },
  data: graphqlQuery
});
```



