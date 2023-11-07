Мапинг можно делать без аннотаций, в xml-стиле.

TODO Выглядит пока не особо нужным, останется как todo.

```xml
<entity-mappings
        version="2.1"
        xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence/orm
        http://xmlns.jcp.org/xml/ns/persistence/orm_2_1.xsd">
    <persistence-unit-metadata>
        <xml-mapping-metadata-complete/>
        <persistence-unit-defaults>
            <delimited-identifiers/>
        </persistence-unit-defaults>
    </persistence-unit-metadata>

    <entity class="org.jpwh.model.simple.Item" access="FIELD">
        <attributes>
            <id name="id">
                <generated-value strategy="AUTO"/>
            </id>
            <basic name="name"/>
            <basic name="auctionEnd">
                <temporal>TIMESTAMP</temporal>
            </basic>
        </attributes>
    </entity>
    
</entity-mappings>
```

