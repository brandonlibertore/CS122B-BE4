# CS122B Backend 4 - The Gateway Service

#### [Application](#application)

- [pom.xml](#pomxml)
- [application.yml](#applicationyml)
- [Resources](#resources)
- [Tests](#tests)

#### [Database](#database)

- [Schemas](#schemas)
- [Tables](#tables)

#### [Routes](#routes)

- [IDM](#idm)
- [Movies](#movies)
- [Billing](#billing)

#### [Filters](#filters)

- [GlobalLoggingFilter](#globalloggingfilter)
- [AuthFilter](#authfilter)

## Application

Our application depends on a lot of files and resources to be able to run correctly. These files
have been provided for you and are listed here for your reference. These files should **NEVER** be
modified and must be left **AS IS**.

### pom.xml

Maven gets all its settings from a file called `pom.xml`. This file determines the dependencies we
will use in our project as well as the plugins we use for compiling, testing, building, ect..

- [pom.xml](pom.xml)

### application.yml

Spring Boot has a large number of settings that can be set with a file called `application.yml`. We
have already created this file for you and have filled it with some settings. There is a file for
the main application as well as one for the tests.

- [Main application.yml](/src/main/resources/application.yml)
- [Test application.yml](/src/test/resources/application.yml)

### Resources

There are two folders in this project that contain resources, and application settings, as well as
files required for the tests.

- [Main Resources](/src/main/resources)
- [Test Resources](/src/test/resources)

### Tests

There is a Single class that contain all of our test cases:

- [GatewayServiceTest](/src/test/java/com/github/klefstad_teaching/cs122b/gateway/GatewayServiceTest.java)

## Database

### Schemas

<table>
  <thead>
    <tr>
      <th align="left" width="1100">🗄 gateway</th>
    </tr>
  </thead>
</table>

### Tables

<table>
  <tbody>
    <tr>
      <th colspan="3" align="left" width="1100">💾 gateway.request</th>
    </tr>
    <tr></tr>
    <tr>
      <th align="left" width="175">Column Name</th>
      <th align="left" width="175">Type</th>
      <th align="left">Attributes</th>
    </tr>
    <tr>
      <td>id</td>
      <td><code>INT</code></td>
      <td><code>NOT NULL</code> <code>PRIMARY KEY</code> <code>AUTO_INCREMENT</code></td>
    </tr>
    <tr></tr>
    <tr>
      <td>ip_address</td>
      <td><code>VARCHAR(64)</code></td>
      <td><code>NOT NULL</code></td>
    </tr>
    <tr></tr>
    <tr>
      <td>call_time</td>
      <td><code>TIMESTAMP</code></td>
      <td><code>NOT NULL</code></td>
    </tr>
    <tr></tr>
    <tr>
      <td>path</td>
      <td><code>VARCHAR(2048)</code></td>
      <td><code>NULL</code></td>
    </tr>
  </tbody>
</table>

## Routes

The first job the Gateway has is to create "Routes" for incoming requests. We can tell our Gateway that when a request comes in that matches a specific `regex` to **Redirect** the request to another `URI` (Another Service). When the request comes in that matches one of these `regex` we can then redirect the request with or without a filter and we can even manipulate the `path` that was send to us.

For all of the `Routes` we have we need to remove the prefix (`/idm`, `/movies`, `/billing`) since our other backends do not contain these prefix's. In our `IDM` we have a endpoint that has the path `/login` if a user wants to reach this endpoint they would talk to our Gateway server with the path `/idm/login`. It is up to us to *remove* the prefix and redirect the call to our `IDM`'s `URI` with the prefix removed, like so: `/login`

## Adding Routes

In our code we have the follow method:

```java
public RouteLocator routeLocator(RouteLocatorBuilder builder)
{
    return builder.routes()
                  .route("idm", r -> r.uri(config.getIdm()))
                  .route("movies", r -> r.uri(config.getMovies()))
                  .route("billing", r -> r.uri(config.getBilling()))
                  .build();
}
```

As we can see here we are creating a route for each of the three services we have created.( Note that the first argument in each `.route()` call is the *name* of the route and is done for logging purposes.) You will notice that we give a lambda as the second argument of this function. 

It is in that lambda that we can define the following:
1. The uri to Redirect to
2. The regex path that will match with that request
3. The filters to apply to this incoming request.

The first step is done for you using the `uri()` function. 

The second step use the `path()` function to apply the regex. 

The third step you can use the `filters(f -> f)` function that takes a lambda where you can apply a filter (Like the `AuthFilter`) or apply path manipulation to remove the prefix (there are functions in this chain that allow you to modify the path)

### IDM

Since the `IDM` is not Secured Service we do not need to apply the `AuthFilter` to it.

We need to capture all incoming requests that match the `Regex` descibed below in the table, and remove the `/idm` prefix from the path.

<table>
  <tbody >
    <tr>
      <th colspan="3" align="left" width="1100">🧳&nbsp;&nbsp;Path</th>
    </tr>
    <tr></tr>
    <tr>
      <th align="left">Regex</th>
      <th colspan="2" align="left">Example</th>
    </tr>
    <tr>
      <td align="left"><code>/idm/**</code></td>
      <td colspan="2" align="left"><code>/idm/login</code></td>
    </tr>
  </tbody>
</table>

### Movies

Since the `Movies` **IS** a Secured Service we **DO** need to `AuthFilter` to it. 

We also need to capture all incoming requests that match the `Regex` descibed below in the table, and remove the `/movies` prefix from the path.

<table>
  <tbody >
    <tr>
      <th colspan="3" align="left" width="1100">🧳&nbsp;&nbsp;Path</th>
    </tr>
    <tr></tr>
    <tr>
      <th align="left">Regex</th>
      <th colspan="2" align="left">Example</th>
    </tr>
    <tr>
      <td align="left"><code>/movies/**</code></td>
      <td colspan="2" align="left"><code>/movies/movie/search</code></td>
    </tr>
    <tr><td colspan="3" ></td></tr>
    <tr></tr>
    <tr>
      <th colspan="3" align="left" width="1100">🎛️&nbsp;&nbsp;Filter</th>
    </tr>
    <tr>
      <td colspan="3" align="left"><code>AuthFilter</code></td>
    </tr>
  </tbody>
</table>

### Billing

Since the `Billing` **IS** a Secured Service we **DO** need to `AuthFilter` to it. 

We also need to capture all incoming requests that match the `Regex` descibed below in the table, and remove the `/billing` prefix from the path.

<table>
  <tbody >
    <tr>
      <th colspan="3" align="left" width="1100">🧳&nbsp;&nbsp;Path</th>
    </tr>
    <tr></tr>
    <tr>
      <th align="left">Regex</th>
      <th colspan="2" align="left">Example</th>
    </tr>
    <tr>
      <td align="left"><code>/billing/**</code></td>
      <td colspan="2" align="left"><code>/billing/cart/insert</code></td>
    </tr>
    <tr><td colspan="3" ></td></tr>
    <tr></tr>
    <tr>
      <th colspan="3" align="left" width="1100">🎛️&nbsp;&nbsp;Filter</th>
    </tr>
    <tr>
      <td colspan="3" align="left"><code>AuthFilter</code></td>
    </tr>
  </tbody>
</table>

## Filters

### GlobalLoggingFilter

This filter will always be called for every incoming requests. It should **NOT** be added mannually to the routes above. It is considered a `GlobalFilter` and will apply automatically. 

#### LinkedBlockingQueue
In our database we are creating `gateway.request` we should first create a class that allows us to store the details of this request in a object to hold on to. (I will refer to this as the `GatewayRequestObject`.

We should also create a `LinkedBlockingQueue` that will keep track of all of the `GatewayRequestObject`'s 

When a request comes in we should map all the data from that request into a `GatewayRequestObject` and then add it into the `LinkedBlockingQueue`. We can get the incoming request by calling `exchange.getRequest()`.

When the `LinkedBlockingQueue` reaches a certain limit (this limit is defined in the `GatewayServiceConfig` and can be retrieved with the `GatewayServiceConfig::getMaxLogs()` function) we need to take all the requests from the queue and then do a database query to insert them all.

When we want to remove all the values in the queue we need to do so with "multi-threading" in mind. To do this we can create a new `List` and then call the `LinkedBlockingQueue::drainTo()` function like so:

```java
LinkedBlockingQueue<GatewayRequestObject> requests = new LinkedBlockingQueue<>();

List<GatewayRequestObject> drainedRequests = new ArrayList<>();

// This empties our "requests" queue and loads 
// the values inside of "drainedRequests"
requests.drainTo(drainedRequests); 
```

Once we have drained the queue we can take that new list and use it to create our `batchUpdate` call.

#### Batch Update

Since we are dealing with multiple objects we will want to use `NamedParameterJdbcTemplate::batchUpdate()` function.

To do this we will need to get a array of `MapSqlParameterSource` (one for each object we want to insert) and then pass that list into the `batchUpdate` function:

```java
public int[] insert()
{
    MapSqlParameterSource[] arrayOfSources = createSources();

    this.template.batchUpdate(
        "INSERT INTO example (first, second) VALUES (:first, :second)",
        arrayOfSources
    );
}
```

#### Not blocking the gateway

To prevent blocking the gateway we should wrap our insert function into a `Mono.fromCallable` like so:

```java

public Mono<int[]> createInsertMono()
{
    return Mono.fromCallable(() -> insert());
}

public int[] insert()
{
    MapSqlParameterSource[] arrayOfSources = createSources();

    return this.template.batchUpdate(
        "INSERT INTO example (first, second) VALUES (:first, :second)",
        arrayOfSources
    );
}
```

This will allow us to use the `subscribe()` function have the database query run on a new thread.

```java
repo.createInsertMono()
    .subscribeOn(SCHEDULER) // This just says "where" to subscibe on 
                            // (there is a DB_SCHEDULER given to you in this class for this)
    .subscribe();
```

### AuthFilter

This filter is applied only to `route()`s that have it listed in the routes `filters(f -> f)` lambda.

This filter is responsible for taking the `Authorization` header from the request. Then removing the `Bearer ` prefix from it in order to get the user's `accessToken` we can then take that `accessToken` and make a request to our `IDM`'s `/authenticate` endpoint in order to validate our user.

In this filter we have two options of how to "end" it. We either end it in success by returning `chain.filter(exchange)` (this means to just continue on the redirecting the request) or we return `exchange.getResponse().setComplete()` to end the request right there. Please note that before returning `exchange.getResponse().setComplete()` you should call `exchange.getResponse().setStatusCode(httpStatus)` in order to set the "error" for why we are ending the request. For this filter if we need to end in failure and return `exchange.getResponse().setComplete()` you should set the status code to `HttpStatus.UNAUTHORIZED`

Because we can not block in this filter we will need to make sure that out `rest call` to our idm is "part of the chain" that we return at the end of the function. We do this like so:

```java
// This returns a Mono that says to continue
return chain.filter(exchange); 

// This returns a Mono that says to end
return exchange.getResponse().setStatusCode(httpStatus); 

// Using flat map you can take the response of the IDM 
// and then dependant on the response you can choose to return either
// the first or second mono above
return getIdmResponse(accessToken)  
          .flatMap(response -> checkResponseAndReturnCorrectMono());
```
