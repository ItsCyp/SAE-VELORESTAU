#!/bin/bash

set -e

if [ "$#" -ne 3 ]; then
    echo "Usage: $0 <hosts-file> <local-project-dir> <remote-dir>"
    exit 1
fi

HOSTS_FILE="$1"
LOCAL_DIR="$2"
REMOTE_DIR="$3"

if [ ! -f "$HOSTS_FILE" ]; then
    echo "Hosts file '$HOSTS_FILE' not found"
    exit 1
fi

for host in $(grep -v '^#' "$HOSTS_FILE" | xargs); do
    echo "Deploying to $host..."
    rsync -av --delete --exclude='.git' "$LOCAL_DIR/" "$host:$REMOTE_DIR/"
    echo "Running remote setup on $host..."
    ssh "$host" "cd $REMOTE_DIR && bash remote_setup.sh"
done

echo "Deployment finished."
