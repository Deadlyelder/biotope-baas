# BAAS

## API calls

1. [http://localhost:8080/list](http://localhost:8080/list) - list of new invoices

2. [http://localhost:8080/payment/pay?token=a&service=d&quantity=e&price=123](http://localhost:8080/payment/pay?token=a&service=d&quantity=e&price=123) - to create new invoice

3. [http://localhost:8080/payment/verify?token=a&service=d](http://localhost:8080/payment/verify?token=t&service=s) - to verify an invoice

4. [http://localhost:8080/payment/refresh?hash=a&service=d&api](http://localhost:8080/payment/refresh?hash=h&service=s&api=a) - to refresh an invoice

5. [https://localhost:8080/payment/verifyHash?hash=a&service=d](http://localhost:8080/payment/verifyhash?hash=a&service=d) - to verify the hash


## Run the project locally

1. Type in terminal:
    ```bash
    cd baas
    grails run-app
    ```
2. Go to [http://localhost:8080/list](http://localhost:8080/list)

### ngrok

You ca use [ngrok](https://ngrok.com/) to create a URL that is accessible in the Internet.

Once you have ngrok installed you can initialize it for your application by using the following line in your terminal:

`ngrok http -region eu 8080`

Your terminal will then display a URL in the format `http://[unique identifier].eu.ngrok.io`.

## Create and run docker container

1. Make JAR file:
    ```bash
    grails clean && grails package
    ```
2. Copy JAR file to `docker` folder
    ```bash
    cp ../build/libs/baas-0.1.war docker/application.jar
    ```
3. Build image
    ```bash
    cd docker
    docker build -t baas .
    ```
3. Run application
    ```bash
    docker run -p 8080:8080 baas
    ```
    or:
    ```bash
    docker-compose up
    ```
