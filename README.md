# word-count-system

## Your challenge

- Create an application that reads the lines emitted by the process and performs a windowed (arbitrary duration left for you to choose) word count, grouped by event_type.
- The current word count should be exposed over an HTTP interface from your application.
- Note that the binaries sometimes output garbage data so you’ll need to handle that gracefully.
- The application should be written in Scala with your frameworks/libraries of choice (no need to learn something new for this challenge - use what you know).

Solution by Or Taieb <or.taieb@gmail.com>

## Solution
I'm streaming with FS2 over cats-effect IO.

The solution comprised out of three streams interacting with a `Store` holding the valid information collected
- `DataProcessor` stream:
    - STDIN stream, taking advantage of service provided by the FS2 library to read from System.stdin
    - Split the incoming strings to lines
    - Decode line to `CountedRecord`, if a line could not be parsed into instance it considered as corrupt and dropped out.
    - Send the instance to the Store
- `CompactProcessor` stream:
    Running separately and sum up records with the same attributes (eventType, data and timestamp) to reduce the size of the store
- Http4s based restful service exposing the following endpoint
    - `/word/count` - list down event types in the store, this includes old entries with data already been swept out.
    - `/word/count/{eventType}` - presenting word count of records collected with the requested eventType and timestamp in the span of the configured window.

The `Store` holds an internal TrieMap, for performant concurrency. 

## Execution
As the blackbox generator writes to the stdout the easiest way to run the word count is by piping the blackbox products
to the application.

For the purpose of the exercise I've used `assembly` plugin to generate an uber jar allowing an easy command line instruction

First build the jar
```shell
$ sbt assembly 
```            

It will be created under <root>/target/scala-2.xx with a catchy name.

Then to run 
```shell
$ <blackbox> | java -jar word_count.jar  
```

### Query the count

Starting with checking the available eventTypes
```shell
http get http://localhost:8080/word/count
```

Then, from the list select the eventType you're interested in
```shell
http get http://localhost:8080/word/count/<eventType>
```

### Configuration
An application.conf file hold configurable entries for the following:
- webserver host and port
- Conuting window
- Compression interval
