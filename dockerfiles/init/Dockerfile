# Copyright (c) 2012-2016 Codenvy, S.A.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#   Tyler Jewell - Initial Implementation
#

# Initializes an empty directory with the templates needed to configure and run Che.
#
# Build:
#   cd ..\..\ <-- Root of the che source repository
#   docker build -f dockerfiles/init/Dockerfile -t eclipse/che-init:<version> .
#
# Use:
#    docker run -v $(pwd):/copy eclipse/che-init:<version>

FROM alpine:latest
RUN mkdir -p /files \
    && mkdir -p /files/modules \
    && mkdir -p /files/manifests

COPY manifests /files/manifests
COPY modules /files/modules
COPY README.md /files
COPY DOCS.md /files
COPY che.env /files
COPY dockerfiles/init/docker-entrypoint.sh /
RUN chmod +x docker-entrypoint.sh

ENV ENVFILE=che.env

ENTRYPOINT ["/docker-entrypoint.sh"]