#!/bin/bash
set -eo pipefail
trap 'kill `jobs -p`' SIGTERM

: ${NETWORK:=testnet}
: ${LIGHTNINGD_OPT:=--log-level=debug}
: ${BITCOIND_OPT:=-debug=rpc}

[[ "$NETWORK" == "mainnet" ]] && NETWORK=bitcoin

if [ -d /etc/lightning ]; then
  echo -n "Using lightningd directory mounted in /etc/lightning... "
  LN_PATH=/etc/lightning

else

  # Setup bitcoind (only needed when we're starting our own lightningd instance)
  if [ -d /etc/bitcoin ]; then
    echo -n "Connecting to bitcoind configured in /etc/bitcoin... "

    RPC_OPT="-datadir=/etc/bitcoin $([[ -z "$BITCOIND_RPCCONNECT" ]] || echo "-rpcconnect=$BITCOIND_RPCCONNECT")"

  elif [ -n "$BITCOIND_URI" ]; then
    [[ "$BITCOIND_URI" =~ ^[a-z]+:\/+(([^:/]+):([^@/]+))@([^:/]+:[0-9]+)/?$ ]] || \
      { echo >&2 "ERROR: invalid bitcoind URI: $BITCOIND_URI"; exit 1; }

    echo -n "Connecting to bitcoind at ${BASH_REMATCH[4]}... "

    RPC_OPT="-rpcconnect=${BASH_REMATCH[4]}"

    if [ "${BASH_REMATCH[2]}" != "__cookie__" ]; then
      RPC_OPT="$RPC_OPT -rpcuser=${BASH_REMATCH[2]} -rpcpassword=${BASH_REMATCH[3]}"
    else
      RPC_OPT="$RPC_OPT -datadir=/tmp/bitcoin"
      [[ "$NETWORK" == "bitcoin" ]] && NET_PATH=/tmp/bitcoin || NET_PATH=/tmp/bitcoin/$NETWORK
      mkdir -p $NET_PATH
      echo "${BASH_REMATCH[1]}" > $NET_PATH/.cookie
    fi

  else
    echo -n "Starting bitcoind... "

    mkdir -p /data/bitcoin
    RPC_OPT="-datadir=/data/bitcoin"

    bitcoind -$NETWORK $RPC_OPT $BITCOIND_OPTS &
    echo -n "waiting for cookie... "
    sed --quiet '/^\.cookie$/ q' <(inotifywait -e create,moved_to --format '%f' -qmr /data/bitcoin)
  fi

  echo -n "waiting for RPC... "
  bitcoin-cli -$NETWORK $RPC_OPT -rpcwait getblockchaininfo > /dev/null
  echo "ready."

  # Setup lightning
  echo -n "Starting lightningd... "

  LN_PATH=/data/lightning
  mkdir -p $LN_PATH

  lnopt=($LIGHTNINGD_OPT --network=$NETWORK --lightning-dir="$LN_PATH" --log-file=debug.log)
  [[ -z "$LN_ALIAS" ]] || lnopt+=(--alias="$LN_ALIAS")

  lightningd "${lnopt[@]}" $(echo "$RPC_OPT" | sed -r 's/(^| )-/\1--bitcoin-/g') > /dev/null &
fi

if [ ! -S /etc/lightning/lightning-rpc ]; then
  echo -n "waiting for RPC unix socket... "
  sed --quiet '/^lightning-rpc$/ q' <(inotifywait -e create,moved_to --format '%f' -qm $LN_PATH)
fi

lightning-cli --lightning-dir=$LN_PATH getinfo > /dev/null

echo "ready."

echo "Starting Lightning Charge"
DEBUG=$DEBUG,lightning-charge,lightning-client \
charged -d /data/charge.db -l $LN_PATH -i 0.0.0.0 $@ $CHARGED_OPTS &

# shutdown the entire process when any of the background jobs exits (even if successfully)
wait -n
kill -TERM $$
