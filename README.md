# bIoTope BaaS

The repository contains the sources codes that form the bases of the Billing-as-a-Service component within the bIoTope ecosystem.

## Description

The Billing-as-a-Service (BaaS component) implementation allows micro-transactions between different entities. It has been designed to be used mainly by the bIoTope Marketplace / Service Catalogue for providing to its users a way to sell/pay for IoT data/services. 

The BaaS is based on the Lightning Network technology that is a layer 2 technology of Bitcoin proposed as a mean to scale and permit microtransaction in bitcoin network.

## Getting Started

The BaaS was developed as a PoC within the bIoTope project and as such, is likely to contain numerous issues. The whole component is based on the Lightning Network implementation (cite it) and Bitcoin core. The component runs over the testnet rather then mainnet.

The repository contains two main parts of the component, namely, the front end (gui) and the backend (bitcoin core, lightning network and API manager) in the form of docker.

### Prerequisites

The full component requires Java, sdkman and Docker to be installed on the system. 

### Installing

### Backend

First, clone the repository using `git clone ....`. Next initiate the following variables (using `export ...` on most Linux systems):

```
NBITCOIN_NETWORK={regtest,testnet,mainnet} #Sets the type of network desired for the component; mainnet is the valued bitcoin, testnet being for testing applications; and regtest being a local network
LETSENCRYPT_EMAIL=abce@biotope.eu #We use HTTPS annd hence require an email to setup a letsencrypt certificate
CHARGED_API_TOKEN=<random-secure-number> #Generate a random key that will be used to authenticate the API calls that are made to the backend
CHARGED_HOST=<web-address-of-the-server> #The web reachable address of the server; for example baas.example.lu
CHARGED_ALIAS=<a-nickname-for-LN-node> #This alias helps in identifying the node on the underlying Bitcoin/Lightning network
CHARGED_IP=<IP-of-the-server> #The public IP address of the server
```

That’s all, you can now setup and run the full back-end using a simple `docker-compose up`. If successful, you will receive the notification that its up and running.

_Note:_ It takes quite some time for the Bitcoin network to be synchronized before you can start using the component. For testnet, it will take around 4-6 hours and maybe around a day for mainnet. Regtest is completely local and as such requires no synchronization. 

#### Test the installation

To verify that the backend is correct installed and the network is synchronized, the following can be used:

```
lightning info
```

If everything is correct, it will return the status of your lightning node.

### Frontend

Enter the directory `gui` of the cloned repository and simple enter `grails run-app`, once successful it will be accessible over `http://localhost:8080/list`. Now you can map your apache or nginx to correctly forward the traffic towards this :)

The front-end exposes some API's that can be used to integrate with outside applications. The list of these calls is listed below:

1. `http://localhost:8080/list` - List of new invoices

2. `http://localhost:8080/payment/pay?token=a&service=d&quantity=e&price=123` - This allows creating a new invoice token with the information including the service, quantity and price information.

3. `http://localhost:8080/payment/verify?token=a&service=d` - Verify if the token `a` has been used to pay for services `d`

4. `http://localhost:8080/payment/refresh?hash=a&service=d&api` - Repays for the token `a`

5. `https://localhost:8080/payment/verifyHash?hash=a&service=d` - Verifies the payment of hash token `a` with the services `d`.

## bIoTope integration

The component BaaS integrated with the marketplace IoTBnB and O-MI node (using a custom wrapper).

## Authors

* @Deadlyelder - *Initial work and maintenance* 

## License

This project is licensed under the MIT License - see the LICENSE.md file for details.

## Acknowledgments

This project has been developed as part of the [bIoTope Project](www.bIoTope-project.eu), which has received funding from the European Unionís Horizon 2020 Research and Innovation Programme under grant agreement No. 688203.

