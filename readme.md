Swagger: https://petstore.swagger.io/

Swagger Json: https://petstore.swagger.io/v2/swagger.json


## Prerequisites

Apache Maven: https://maven.apache.org/download.cgi

Version used in this sample: https://downloads.apache.org/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.tar.gz

Swagger Codegen: https://github.com/swagger-api/swagger-codegen/

Version used in this sample: https://github.com/swagger-api/swagger-codegen/releases/tag/v3.0.26


First we need to compile `swagger-codegen-cli` ussing `mvn compile` and after that `mvn package`


## Generate SDK

```
java -jar modules/swagger-codegen-cli/target/swagger-codegen-cli.jar generate -i /home/osvel/maven/pet_store.json -l java --library=retrofit2 -o /home/osvel/maven/swagger-codegen/pet_store --artifact-id io.swagger.petstore.PetStore --artifact-version 1.0.0 --api-package io.swagger.petstore.api --model-package io.swagger.petstore.model --group-id io.swagger.petstore
```

## Compile SDK

Now compile your library ussing `mvn compile` and after that `mvn package`