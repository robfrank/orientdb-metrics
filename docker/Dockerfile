############################################################
# Dockerfile to run an OrientDB (Graph) Container
# for current developed version
############################################################

FROM orientdb/orientdb-spatial:latest

MAINTAINER OrientDB LTD (info@orientdb.com)

ADD orientdb-metrics-*-dist.jar /orientdb/plugins/

# Default command start the server
CMD ["server.sh"]
