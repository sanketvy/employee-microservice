# Employee Microservice

This microservice is responsible for implementing all the employee operations related APIs, calling downstream mock server for fetching and persisting new entities.

## High Level Design

![img.png](high_level_design.png)

`Employee Server` - Primary internet exposed web server responsible to responding to user requests.

`Mock Server` - Downstream microservices, responsible to exposing endpoints to fetch and persist employee data.

`Cache Server` - In-memory/Distributed cache server, to store and improve performance of the employee service, by returning frequently accessed data from cache.

In this scenario, microservice uses EHCache, an in memory data store solution to implement caching, but when horizontal scaling, caching will be implemented by Redis cluster. To implement the same, create new interface `RedisCacheManager` and implement `ICacheManager`.

For Caching, read through cache pattern with cache aside for invalidation is used, we can either configure TTL for the data based on requirement. Cache is invalidated on create/update/delete operations to maintain consistency with the downstream system.
