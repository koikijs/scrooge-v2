# scrooge-v2
## Docs
https://koiki-scrooge-v2.herokuapp.com/docs/index.html 

## Heroku
```
heroku config:set MAVEN_CUSTOM_GOALS="clean package" --app koiki-scrooge-v2
heroku config:set MAVEN_CUSTOM_OPTS="-DskipTests=false" --app koiki-scrooge-v2
```
- [Heroku buildpack for Java](https://github.com/heroku/heroku-buildpack-java)

## Kotlin + Coroutines + Spring Boot
- [Going Reactive with Spring, Coroutines and Kotlin Flow](https://spring.io/blog/2019/04/12/going-reactive-with-spring-coroutines-and-kotlin-flow)
- [Non-blocking Spring Boot with Kotlin Coroutines](https://www.baeldung.com/spring-boot-kotlin-coroutines)
- [DATACMNS-1508 Add support for Coroutines repositories](https://jira.spring.io/browse/DATACMNS-1508)
- [Reactive Transactions with Spring](https://spring.io/blog/2019/05/16/reactive-transactions-with-spring)
