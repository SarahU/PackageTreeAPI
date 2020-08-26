# PackageTreeAPI

How to run:
Command Line using docker:
docker build -t packagetreeapi .

`docker run --rm -ti -p 8080:8080 packagetreeapi`

## Decisions / Assumptions:

Assumed that in our domain explicit dependencies and devDependencies from NPM, are both equally considered dependencies.

Not handling scenario: "@types/jest": "^24.0.15", as various other. These will instead fail and return an empty dependency list

For version processing, where there is a choice, the more recent version was selected.
Performing API calls per set of dependencies. This seems to scale well enough and was less complex than
a solution with a central map of packages and versions that gets updated via queued API calls. But is also
more susceptible to a scale issue if the set of dependencies for one package is very large.
It also means that not all variations are dealt with and this could also miss dependencies in other applicable versions.

Integration test is not separated out in the build from the others. 
It should ordinarily be because it's slower and could be flaky as it's
interacting with the real world.


## To Do:
Make model for Json Data
Change logging to go to file
More processing for versions
Return different formats / protocols
Autowire the retriever and parser


## Queries:
How to handle multiple versions in dependency list ie do we want to see this?
