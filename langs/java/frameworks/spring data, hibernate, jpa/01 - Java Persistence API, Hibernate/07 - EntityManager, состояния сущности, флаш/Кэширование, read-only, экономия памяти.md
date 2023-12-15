Стр.243 учебника, раздел 10.2.8



10.2.8 Caching in the persistence context
The persistence context is a cache of persistent instances. Every entity instance in per-
sistent state is associated with the persistence context.
Many Hibernate users who ignore this simple fact run into an OutOfMemory-
Exception. This is typically the case when you load thousands of entity instances in a
unit of work but never intend to modify them. Hibernate still has to create a snapshot
of each instance in the persistence context cache, which can lead to memory exhaus-
tion. (Obviously, you should execute a bulk data operation if you modify thousands of
rows—we’ll get back to this kind of unit of work in section 20.1.)
The persistence context cache never shrinks automatically. Keep the size of your
persistence context to the necessary minimum. Often, many persistent instances in
your context are there by accident—for example, because you needed only a few
items but queried for many. Extremely large graphs can have a serious performance
impact and require significant memory for state snapshots. Check that your queries
return only data you need, and consider the following ways to control Hibernate’s
caching behavior.
You can call EntityManager#detach(i) to evict a persistent instance manually
from the persistence context. You can call EntityManager#clear() to detach all per-
sistent entity instances, leaving you with an empty persistence context.
The native Session API has some extra operations you might find useful. You can
set the entire persistence context to read-only mode. This disables state snapshots and
dirty checking, and Hibernate won’t write modifications to the database:
em.unwrap(Session.class).setDefaultReadOnly(true);
Item item = em.find(Item.class, ITEM_ID);
item.setName("New Name");
em.flush();
You can disable dirty checking for a single entity instance:
Item item = em.find(Item.class, ITEM_ID);
em.unwrap(Session.class).setReadOnly(item, true);
item.setName("New Name");
em.flush();
A query with the org.hibernate.Query interface can return read-only results, which
Hibernate doesn’t check for modifications:
PATH: /examples/src/test/java/org/jpwh/test/fetching/ReadOnly.java
PATH: /examples/src/test/java/org/jpwh/test/fetching/ReadOnly.java
No UPDATE
No UPDATE
Licensed to Thomas Snead <n.ordickan@gmail.com>
244 C HAPTER 10 Managing data
org.hibernate.Query query = em.unwrap(Session.class)
.createQuery("select i from Item i");
query.setReadOnly(true).list();
List<Item> result = query.list();
for (Item item : result)
item.setName("New Name");
em.flush();
Thanks to query hints, you can also disable dirty checking for instances obtained with
the JPA standard javax.persistence.Query interface:
Query query = em.createQuery(queryString)
.setHint(
org.hibernate.annotations.QueryHints.READ_ONLY,
true
);
Be careful with read-only entity instances: you can still delete them, and modifications
to collections are tricky! The Hibernate manual has a long list of special cases you
need to read if you use these settings with mapped collections. You’ll see more query
examples in chapter 14.
So far, flushing and synchronization of the persistence context have occurred auto-
matically, when the transaction commits. In some cases, you need more control over
the synchronization process.



