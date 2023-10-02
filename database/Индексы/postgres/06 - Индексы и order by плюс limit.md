# Индексы и order by + limit

Сортировка идет отдельным шагом после выборки. Но наличие индекса может позволить избежать сортировки вообще. Правда это справедливо только для B-tree индексов.

Исходные данные: таблица `boarding_passes`, всего около 7.5 млн записей:

```
   ticket_no   | flight_id | boarding_no | seat_no
---------------+-----------+-------------+---------
 0005435132075 |    214867 |          16 | 3B
 0005435132100 |    214867 |          15 | 4C
 0005435132134 |    214867 |          14 | 2A
```

И вот такой индекс:

```sql
create index boarding_passes_orderby_test on boarding_passes(boarding_no desc, flight_id asc);
```

## Выбираются все записи

Когда выбираются все записи, индекс даже мешает:

```sql
explain (analyze) select * from boarding_passes 
order by boarding_no desc, flight_id asc;

-- Без индекса: 3943.508 ms
-- С индексом:  10283.461 ms
```

```sql
-- Без индекса
Gather Merge  
   (cost=607906.52..1378525.45 rows=6604844 width=25) 
   (actual time=2348.478..3783.909 rows=7925812 loops=1)
   Workers Planned: 2
   Workers Launched: 2
   ->  Sort  
       (cost=606906.50..615162.55 rows=3302422 width=25) 
       (actual time=2316.755..2703.727 rows=2641937 loops=3)
           Sort Key: boarding_no DESC, flight_id
           Sort Method: external merge  Disk: 99128kB
           Worker 0:  Sort Method: external merge  Disk: 95480kB
           Worker 1:  Sort Method: external merge  Disk: 97584kB
         ->  Parallel Seq Scan on boarding_passes  
             (cost=0.00..91303.22 rows=3302422 width=25)
             (actual time=1.550..584.861 rows=2641937 loops=3)
Planning Time: 4.447 ms
Execution Time: 3943.508 ms
```

```sql
-- С индексом
Index Scan using boarding_passes_orderby_test on boarding_passes  
    (cost=0.43..438852.89 rows=7925812 width=25) 
    (actual time=0.068..10093.562 rows=7925812 loops=1)
Planning Time: 0.063 ms
Execution Time: 10283.461 ms
```

## Выбирается небольшое количество записей

Если выбирается небольшое количество записей (относительно общего), то индекс сильно ускоряет запрос:

```sql
explain (analyze) select * from boarding_passes 
order by boarding_no desc, flight_id asc 
limit 10000;
-- Без индекса: 294.982 ms
-- С индексом:  4.526 ms
```

```sql
-- Без индекса
Limit
    (cost=328223.52..329390.27 rows=10000 width=25) 
    (actual time=290.249..294.667 rows=10000 loops=1)
   ->  Gather Merge  
       (cost=328223.52..1098842.45 rows=6604844 width=25) 
       (actual time=289.396..293.458 rows=10000 loops=1)
       Workers Planned: 2
       Workers Launched: 2
         ->  Sort  
             (cost=327223.50..335479.55 rows=3302422 width=25) 
             (actual time=280.209..280.378 rows=3925 loops=3)
             Sort Key: boarding_no DESC, flight_id
             Sort Method: top-N heapsort  Memory: 2085kB
             Worker 0:  Sort Method: top-N heapsort  Memory: 2158kB
             Worker 1:  Sort Method: top-N heapsort  Memory: 2089kB
               ->  Parallel Seq Scan on boarding_passes  
                  (cost=0.00..91303.22 rows=3302422 width=25) 
                  (actual time=0.027..121.002 rows=2641937 loops=3)
Planning Time: 0.072 ms
JIT:
    Functions: 1
    Options: Inlining false, Optimization false, Expressions true, Deforming true
    Timing: Generation 0.067 ms, Inlining 0.000 ms, Optimization 0.084 ms, Emission 0.766 ms, Total 0.916 ms
Execution Time: 294.982 ms
```

```sql
-- С индексом
 Limit  
     (cost=0.43..554.13 rows=10000 width=25)
     (actual time=0.019..4.242 rows=10000 loops=1)
   ->  Index Scan using boarding_passes_orderby_test on boarding_passes
       (cost=0.43..438852.89 rows=7925812 width=25)
       (actual time=0.018..3.679 rows=10000 loops=1)
Planning Time: 0.071 ms
Execution Time: 4.526 ms
```

Если попробовать выбрать limit'ом почти все записи, то тут индекс конечно не поможет и запрос будет долгим:

```sql
explain (analyze) select * from boarding_passes
order by boarding_no desc, flight_id asc
limit 7000000;
-- С индексом: 9246.211 ms
```

## Порядок полей сохраняется, направление сортировки разное

Без индекса разницы между разными комбинациями desc-asc нет никакой. План выполнения идентичный и время примерно тоже. Поэтому все примеры дальше предполагают, что индекс есть и построен по полям вот так: `boarding_no desc, flight_id asc`

Проверим, как влияет изменение типа сортировки desc \ asc на скорость выполнения запроса. Подразумевается, что сам порядок полей сохраняется.

Резюме:

```sql
order by boarding_no desc, flight_id asc   -- 6.148 ms
order by boarding_no desc, flight_id desc  -- 7.112 ms
order by boarding_no asc, flight_id asc    -- 246.035 ms
order by boarding_no asc, flight_id desc   -- 19.343 ms
```

### desc-asc как в индексе

```sql
explain (analyze) select * from boarding_passes
order by boarding_no desc, flight_id asc
limit 10000;
```

```sql
Limit
  (cost=0.43..554.13 rows=10000 width=25)
  (actual time=0.085..5.868 rows=10000 loops=1)
   ->  Index Scan using boarding_passes_orderby_test on boarding_passes
       (cost=0.43..438852.89 rows=7925812 width=25)
       (actual time=0.084..5.374 rows=10000 loops=1)
Planning Time: 0.077 ms
Execution Time: 6.148 ms
```

### 1 так же, 2 поменялось

```sql
explain (analyze) select * from boarding_passes
order by boarding_no desc, flight_id desc
limit 10000;
```

```sql
Limit  
    (cost=3639.32..5397.51 rows=10000 width=25)
    (actual time=0.159..6.853 rows=10000 loops=1)
   ->  Incremental Sort  
       (cost=3639.32..1397152.81 rows=7925812 width=25)
       (actual time=0.158..6.213 rows=10000 loops=1)
       Sort Key: boarding_no DESC, flight_id DESC
       Presorted Key: boarding_no
       Full-sort Groups: 37  Sort Method: quicksort  Average Memory: 28kB  Peak Memory: 28kB
       Pre-sorted Groups: 27  Sort Method: quicksort  Average Memory: 38kB  Peak Memory: 70kB
         ->  Index Scan using boarding_passes_orderby_test on boarding_passes
             (cost=0.43..438852.89 rows=7925812 width=25)
             (actual time=0.011..3.314 rows=10161 loops=1)
Planning Time: 0.079 ms
Execution Time: 7.112 ms
```

### 1 поменялось, 2 так же

```sql
explain (analyze) select * from boarding_passes
order by boarding_no asc, flight_id asc
limit 10000;
```

```sql
Limit  
    (cost=3639.32..5397.51 rows=10000 width=25)
    (actual time=245.254..245.853 rows=10000 loops=1)
   ->  Incremental Sort
       (cost=3639.32..1397152.81 rows=7925812 width=25)
       (actual time=245.252..245.512 rows=10000 loops=1)
       Sort Key: boarding_no, flight_id
       Presorted Key: boarding_no
       Full-sort Groups: 1  Sort Method: quicksort  Average Memory: 30kB  Peak Memory: 30kB
       Pre-sorted Groups: 1  Sort Method: top-N heapsort  Average Memory: 2331kB  Peak Memory: 2331kB
         ->  Index Scan Backward using boarding_passes_orderby_test on boarding_passes
             (cost=0.43..438852.89 rows=7925812 width=25)
             (actual time=0.058..211.693 rows=139881 loops=1)
Planning Time: 0.078 ms
Execution Time: 246.035 ms
```

### оба поменялись

```sql
explain (analyze) select * from boarding_passes
order by boarding_no asc, flight_id desc
limit 10000;
```

```sql
 Limit
     (cost=0.43..554.13 rows=10000 width=25)
     (actual time=0.063..19.014 rows=10000 loops=1)
   ->  Index Scan Backward using boarding_passes_orderby_test on boarding_passes
       (cost=0.43..438852.89 rows=7925812 width=25)
       (actual time=0.061..18.432 rows=10000 loops=1)
Planning Time: 0.075 ms
Execution Time: 19.343 ms
```

## Порядок полей меняется

Если поменять порядок полей на `order by flight_id asc, boarding_no desc`, то индекс `boarding_no desc, flight_id asc` становится бесполезен:

```sql
explain (analyze) select * from boarding_passes 
order by flight_id asc, boarding_no desc
limit 10000;
```

```sql
 Limit
     (cost=18.25..1300.92 rows=10000 width=25)
     (actual time=2.128..9.694 rows=10000 loops=1)
   ->  Incremental Sort
       (cost=18.25..1016638.17 rows=7925812 width=25)
       (actual time=2.127..9.116 rows=10000 loops=1)
       Sort Key: flight_id, boarding_no DESC
       Presorted Key: flight_id
       Full-sort Groups: 101  Sort Method: quicksort  Average Memory: 30kB  Peak Memory: 30kB
       Pre-sorted Groups: 101  Sort Method: quicksort  Average Memory: 32kB  Peak Memory: 32kB
         ->  Index Scan using boarding_passes_flight_id_seat_no_key on boarding_passes
             (cost=0.43..438943.56 rows=7925812 width=25)
             (actual time=2.053..4.614 rows=10041 loops=1)
Planning Time: 0.081 ms
Execution Time: 9.987 ms
```

P.S. Здесь используется другой индекс, который уже был в БД до моего. Я не стал его удалять ради одного запроса, потому что и так видно, что предыдущие запросы использовали индекс `boarding_passes_orderby_test`, а этот последний использует `boarding_passes_flight_id_seat_no_key`.

