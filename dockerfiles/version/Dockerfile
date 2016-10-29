# Copyright (c) 2012-2016 Codenvy, S.A.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#   Tyler Jewell - Initial Implementation
#

# Contains a manifest of Docker images required to run Codenvy for a particular version.
# Also contains an upgrade summary showing which upgrade paths a particular version is allowed.
# This image is only tagged as "latest".  It is a single image that provides starting point of info.
#
# Build:
#   cd ..\..\ <-- Root of the codenvy source repository
#   docker build -f dockerfiles/version/Dockerfile -t eclipse/che-version .
# 
# Use:
#    docker run -v $(pwd):/copy eclipse/che-version
FROM alpine:3.4
RUN mkdir -p /files 

COPY version /files/
COPY dockerfiles/version/docker-entrypoint.sh /

RUN chmod +x docker-entrypoint.sh 
ENTRYPOINT ["/docker-entrypoint.sh"]