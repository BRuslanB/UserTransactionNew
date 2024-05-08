#!/usr/bin/env bash
##
## This script will generate a patched docker-entrypoint.sh that:
## - executes any *.sh script found in /docker-entrypoint-initdb.d
## - boots cassandra up
## - executes any *.cql script found in docker-entrypoint-initdb.d
##
## It is compatible with any cassandra:* image
##

set -e

## Create script that executes files found in docker-entrypoint-initdb.d/
cat <<'EOF' >> /run-init-scripts.sh
#!/usr/bin/env bash

LOCK=/var/lib/cassandra/_init.done
INIT_DIR=docker-entrypoint-initdb.d

if [ -f "$LOCK" ]; then
    echo "@@ Initialization already performed."
    exit 0
fi

cd "$INIT_DIR"

echo "@@ Executing bash scripts found in $INIT_DIR"

# Execute scripts found in INIT_DIR
for f in $(find . -type f -name "*.sh" -executable -print | sort); do
    echo "$0: sourcing $f"
    . "$f"
    echo "$0: $f executed."
done

# Wait for cassandra to be ready and execute cql in background
(
    while ! cqlsh -e 'describe cluster' > /dev/null 2>&1; do sleep 6; done
    echo "$0: Cassandra cluster ready: executing cql scripts found in $INIT_DIR"
    for f in $(find . -type f -name "*.cql" -print | sort); do
        echo "$0: running $f"
        cqlsh -f "$f"
        echo "$0: $f executed"
    done
    # Mark things as initialized (in case /var/lib/cassandra was mapped to a local folder)
    touch "$LOCK"
) &

EOF

## Patch existing entrypoint to call our script in the background
EP=/patched-entrypoint.sh
cat <<'EOF' >> "$EP"
#!/usr/bin/env bash
/run-init-scripts.sh &
exec "$@"
EOF

## Make both scripts executable
chmod +x /run-init-scripts.sh
chmod +x "$EP"

CURRENT_IP=$(hostname -i)

## Replace Cassandra settings in cassandra.yaml with values from environment variables, if they are set
# Record 11 in cassandra.yaml
sed -i "s/^ *cluster_name:/cluster_name:/" /etc/cassandra/cassandra.yaml

if [ -n "$CASSANDRA_SEEDS" ]; then
# Record 553 in cassandra.yaml
    sed -i "s/^ *- seeds:.*/      - seeds: \"$CASSANDRA_SEEDS\"/" /etc/cassandra/cassandra.yaml
else
# CASSANDRA_SEEDS variable is not set or empty
    sed -i "s/^ *- seeds:.*/      - seeds: \"$CURRENT_IP\"/" /etc/cassandra/cassandra.yaml
fi

if [ -n "$CASSANDRA_LISTEN_ADDRESS" ]; then
# Record 763 in cassandra.yaml
    sed -i -e "s/^listen_address: .*/listen_address: $CASSANDRA_LISTEN_ADDRESS/" /etc/cassandra/cassandra.yaml
#    sed -i -e "s/^listen_address: .*/listen_address: $CURRENT_IP/" /etc/cassandra/cassandra.yaml
fi

if [ -n "$CASSANDRA_BROADCAST_ADDRESS" ]; then
# Record 777 in cassandra.yaml
    sed -i -e "s/^# broadcast_address: .*/broadcast_address: $CASSANDRA_BROADCAST_ADDRESS/" /etc/cassandra/cassandra.yaml
#    sed -i -e "s/^# broadcast_address: .*/broadcast_address: $CURRENT_IP/" /etc/cassandra/cassandra.yaml
fi

if [ -n "$CASSANDRA_RPC_ADDRESS" ]; then
# Record 859 in cassandra.yaml
    sed -i -e "s/^rpc_address: .*/rpc_address: $CASSANDRA_RPC_ADDRESS/" /etc/cassandra/cassandra.yaml
fi

if [ -n "$CASSANDRA_BROADCAST_RPC_ADDRESS" ]; then
# Record 875 in cassandra.yaml
    sed -i -e "s/^# broadcast_rpc_address: .*/broadcast_rpc_address: $CASSANDRA_BROADCAST_RPC_ADDRESS/" /etc/cassandra/cassandra.yaml
#    sed -i -e "s/^# broadcast_rpc_address: .*/broadcast_rpc_address: $CURRENT_IP/" /etc/cassandra/cassandra.yaml
fi

## Call the new entrypoint
"$EP" "$@"