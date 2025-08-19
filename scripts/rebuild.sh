#!/usr/bin/env bash

# Simple alias script for quick restart
# This script calls the main rebuild-and-run.sh script with default options (stop and start)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
"$SCRIPT_DIR/rebuild-and-run.sh" "$@"
