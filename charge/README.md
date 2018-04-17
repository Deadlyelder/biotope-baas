### Deploy with Docker

To deploy Lightning Charge with Docker, run these commands:

```bash
$ mkdir data # make sure to create the folder _before_ running docker
$ docker run -u `id -u` -v `pwd`/data:/data -p 9112:9112 \
             -e API_TOKEN=mySecretToken
```

This will start `bitcoind`, `lightningd` and `charged` and hook them up together.
You will then be able to access the REST API at `http://localhost:9112` using `mySecretToken`.

Runs in `testnet` mode by default, set `NETWORK` to override.

If you want to experiment in `regtest` mode and don't care about persisting data, this should do:

```bash
$ docker run -e NETWORK=regtest -e API_TOKEN=mySecretToken -p 9112:9112
```

To connect to an existing `lightningd` instance running on the same machine,
mount the lightning data directory to `/etc/lightning` (e.g. `-v $HOME/.lightning:/etc/lightning`).
Connecting to remote lightningd instances is currently not supported.

To connect to an existing `bitcoind` instance running on the same machine,
mount the bitcoin data directory to `/etc/bitcoin` (e.g. `-v $HOME/.bitcoin:/etc/bitcoin`).
To connect to a remote bitcoind instance, set `BITCOIND_URI=http://[user]:[pass]@[host]:[port]`
(or use `__cookie__:...` as the login for cookie-based authentication).
