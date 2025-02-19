# Table name extractor

## What this is

This is a PoC of a little REST API using `http4s`. It exposes an endpoint (`/extractTableNames`).
Hit this endpoint with a POST, a SQL string as body, and get the name of the table(s) targeted in return.

## Running it

### Prerequisites

You need to have `sbt` installed on your system (install it with `sdkman` if you don't have it). 

### Running the tests

`sbt test`

### Running the API server

`sbt run`

### If the default port is already in use

Unlucky! If it's the case, change the port defined in `./src/main/scala/EntryPoint.scala`
with a value that works for you. 

## Usage

Once the server is up, you can hit it with:
`curl -X POST -H "Content-Type: text/plain" --data "$QUERY" $HOST:$PORT/extractTableNames`

For instance:
`curl -X POST -H "Content-Type: text/plain" --data "SELECT * FROM a_table" localhost:8080/extractTableNames`

## Technical choices

### Functional choices

I decided to support varied SQL statements as well as a series of statements within the same body (separated by a `;`).

### Language and library choices

Using http4s for such a simple use case is clearly overkill, but it is interesting to see how a purely functional REST
API backend would look like with it.

Regarding the SQL parsing aspect, I had to choose among 3 options: a simple error-prone manual parsing, a complex and
long sophisticated manual parsing, and a parsing that relies upon a 3rd party library. I chose the latter. 

### Absence of a service layer

We have 2 main entities in this code (in addition to the entry point): the API layer, and the "domain" layer.
Given that the interactions between those 2 entities are so thin and straightforward, I decided to not add an
intermediary (service?) layer. The route is simply built by being injected the "compute" function, in the entry point.
