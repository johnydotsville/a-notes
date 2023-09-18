fxml

```xml
<TableView fx:id="entries">
    <columns>
        <TableColumn text="Имя" fx:id="name"/>
        <TableColumn text="Тип" fx:id="type"/>
        <TableColumn text="Src" fx:id="src"/>
        <TableColumn text="Doc" fx:id="doc"/>
    </columns>
</TableView>
```

Контроллер

```java
public class MainController implements Initializable {

    @FXML
    private TableView<Entry> entries;
    @FXML
    private TableColumn<Entry, String> name;
    @FXML
    private TableColumn<Entry, String> type;
    @FXML
    private TableColumn<Entry, String> src;
    @FXML
    private TableColumn<Entry, String> doc;

    private ObservableList<Entry> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setCellValueFactory(new PropertyValueFactory<Entry, String>("name"));
        type.setCellValueFactory(new PropertyValueFactory<Entry, String>("type"));
        src.setCellValueFactory(new PropertyValueFactory<Entry, String>("src"));
        doc.setCellValueFactory(new PropertyValueFactory<Entry, String>("doc"));

        data.addAll(getEntries());
        entries.setItems(data);
    }

    @FXML
    protected void addEntry() {
        data.add(new Entry("SampleName", "I", "source", "doc"));
        data.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
    }

    private ObservableList<Entry> getEntries() {
        return FXCollections.observableArrayList(
                new Entry("SecurityFilterChain", "FC", "src-link", "doc-link"),
                new Entry("SecurityBuilder", "I", "src-link", "doc-link"),
                new Entry("HttpSecurity", "FC", "src-link", "doc-link")
        );
    }
}
```

Класс данных

```java
public class Entry {
    private String name;
    private String type;
    private String src;
    private String doc;

    public Entry(String name, String type, String src, String doc) {
        this.name = name;
        this.type = type;
        this.src = src;
        this.doc = doc;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }
    public void setSrc(String src) {
        this.src = src;
    }

    public String getDoc() {
        return doc;
    }
    public void setDoc(String doc) {
        this.doc = doc;
    }
}
```

