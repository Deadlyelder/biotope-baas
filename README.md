# bIoTope BaaS

This repository contains the source code that form the basis of the Billing-as-a-Service component within the bIoTope ecosystem.

## Description

The Billing-as-a-Service (BaaS) component implementation allows micro-transactions between different entities. It has been designed to be used mainly by the bIoTope Marketplace / Service Catalogue for providing to its users a way to sell/pay for IoT data/services. 

BaaS is based on the Lightning Network technology (citation?), which is a layer 2 technology of Bitcoin proposed as a means to scale and permit microtransactions in the Bitcoin network.

## Getting Started

BaaS was developed as a PoC within the bIoTope project and as such, is likely to contain numerous issues. The whole component is based on the Lightning Network implementation (cite it) and Bitcoin core. The component runs over the testnet rather then the mainnet.

This repository contains the two main parts of the component, namely, the front end (gui) and the backend (Bitcoin core, Lightning Network and API manager) in the form of Docker and grails.

### Prerequisites

The full component requires Java, sdkman and Docker to be installed on the system. 

### Installing

### Backend

First, clone the repository using `git clone ....`. Next initiate the following variables (using `export ...` on most Linux systems):

```
NBITCOIN_NETWORK={regtest,testnet,mainnet} #Sets the type of network desired for the component; mainnet is for real Bitcoin, testnet is for testing applications over a real, decentralized Bitcoin-like network, and regtest is a purely local network simulation. It is advised to perform testing on the testnet or regtest networks to avoid actual financial risks.
LETSENCRYPT_EMAIL=abce@biotope.eu #We use HTTPS and hence require an email to setup a letsencrypt certificate (citation?)
CHARGED_API_TOKEN=<random-secure-number> #Generate a random key that will be used to authenticate the API calls that are made to the backend
CHARGED_HOST=<web-address-of-the-server> #The web-reachable address of the server; for example baas.example.lu
CHARGED_ALIAS=<a-nickname-for-LN-node> #This alias helps in identifying the node on the underlying Bitcoin/Lightning network
CHARGED_IP=<IP-of-the-server> #The public IP address of the server
```

That's all. You can now setup and run the full backend using a simple `docker-compose up`. If successful, you will receive the notification that it's up and running.

_Note:_ It takes quite some time for the Bitcoin network to be synchronized before you can start using the component. For the testnet, it will take around 4-6 hours, and maybe around a day for the mainnet. Regtest is completely local and as such requires no synchronization. 

#### Test the installation

To verify that the backend is correctly installed and the network is synchronized, the following can be used:

```
lightning info
```

If everything is correct, it will return the status of your lightning node.

### Frontend

Enter the directory `gui` of the cloned repository and simply enter `grails run-app`. Once successful it will be accessible over `http://localhost:8080/list`. Now you can map your apache or nginx to correctly forward the traffic towards this. :)

The frontend exposes some API's that can be used to integrate it with outside applications. The list of these calls is visible below:

1. `http://localhost:8080/list` - List of new invoices

2. `http://localhost:8080/payment/pay?token=a&service=d&quantity=e&price=123` - This allows creating a new invoice token with the information including the service, quantity, and pricing information

3. `http://localhost:8080/payment/verify?token=a&service=d` - Verify if the token `a` has been used to pay for services `d`

4. `http://localhost:8080/payment/refresh?hash=a&service=d&api` - Repays for the token `a`

5. `https://localhost:8080/payment/verifyHash?hash=a&service=d` - Verifies the payment of hash token `a` with the services `d`

## bIoTope integration

The component BaaS is integrated with the IoTBnB marketplace and O-MI node (using a custom wrapper).

## Authors

* @Deadlyelder - *Initial work and maintenance*

## License

This project is licensed under the MIT License - see the LICENSE.md file for details.

## Acknowledgments

This project has been developed collaboratively by itrust consulting and SnT as part of the [bIoTope Project](www.bIoTope-project.eu), which has received funding from the European Union’s Horizon 2020 Research and Innovation Programme under grant agreement No. 688203.

