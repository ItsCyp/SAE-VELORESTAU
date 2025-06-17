#!/bin/bash
# Remote setup script to build and start the application
# Usage: remote_setup.sh [registry-host]

set -e
REGISTRY_HOST=${1:-localhost}

# Compile Java components
if [ -d serviceDb ]; then
    echo "Compiling serviceDb..."
    (cd serviceDb && javac -classpath "lib/*:." *.java)
fi

if [ -d serviceHttp ]; then
    echo "Compiling serviceHttp..."
    (cd serviceHttp && javac *.java)
fi

# Build the website assets if Node.js is available
if command -v npm >/dev/null 2>&1 && [ -d site ]; then
    echo "Building site resources..."
    (cd site && npm install --no-progress && npm run build)
fi

# Example start commands (adjust as needed)
if [ -d serviceHttp ]; then
    echo "Starting ServiceHttp..."
    (cd serviceHttp && nohup java Main "$REGISTRY_HOST" conf.ini >/tmp/serviceHttp.log 2>&1 &)
fi

if [ -d serviceDb ]; then
    echo "Starting ServiceDb..."
    (cd serviceDb && nohup java Main "$REGISTRY_HOST" >/tmp/serviceDb.log 2>&1 &)
fi

echo "Remote setup complete on $(hostname)"