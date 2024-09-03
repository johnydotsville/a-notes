const req = axios({
  url: "https://jsonplaceholder.typicode.com/posts",
  method: "get",
  params: {
    _limit: 10,
    _page: 1
  }
});

req.then(response => {
  console.log("Status:" + response.status);
  console.log("Data:");
  for (const d of response.data) {
    console.log(d.title);
  }
});