# PackageTreeAPI

How to run:
Command Line using docker:
docker build -t packagetreeapi .

`docker run --rm -ti -p 8080:8080 packagetreeapi`

## Decisions:

For version processing, where there is a choice, the more recent version was selected.
Performing API calls per set of dependencies. This seems to scale well enough and was less complex than
a solution with a central map of packages and versions that gets updated via queued API calls. But is also
more susceptible to a scale issue if the set of dependencies for one package is very large.


## To Do:
Make model for Json Data
Change logging to go to file
More processing for versions
Return different formats / protocols
Autowire the retriever and parser


## Queries:
How to handle multiple versions in dependency list ie do we want to see this?
