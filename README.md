![Maven test](https://github.com/s0l0c0ding/shorter-url-quarkus/workflows/Maven%20test/badge.svg?branch=master)
![GCP deploy](https://github.com/s0l0c0ding/shorter-url-quarkus/workflows/GCP%20deploy/badge.svg)
<br>
# shorter-url-quarkus project
The aim of the app is to shorten a given url and saving the visitors count with each redirect by using my own tool.

This project uses Quarkus and MongoDb



## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
- first, launch a MongoDb container using docker:
 ```
 docekr run --name mongodb -d mongo:4.2-bionic 
 ```
- second, launch the following mvn comand:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `shorter-url-quarkus-0.1-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/shorter-url-quarkus-0.1-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/shorter-url-quarkus-0.1-runner`

If you want to learn more about building native executables, please look at the [docs](https://quarkus.io/guides/building-native-image).

## Multi-stage Docker build in GCP console
>Before launching our Docker build, we need to update the default .dockerignore file as it filters everything except the target directory and as we plan to build inside a container we need to be able to copy the src directory. So edit your .dockerignore and remove or comment its content.
```bash
docker build -f src/main/docker/Dockerfile.multistage -t gcr.io/<projectId>/shorter-url:tag .
```
After image build:
```bash
docker push gcr.io/<projectId>/shorter-url:tag
```
More info about Multi-satge on [Quarkus site](https://quarkus.io/guides/building-native-image#using-a-multi-stage-docker-build)
