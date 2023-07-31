FROM us-central1-docker.pkg.dev/jbh-prd-devops/jbh-images/jbh-alpine-jre17:latest
EXPOSE 8080
ADD target/carrierpaymentautopay.jar carrierpaymentautopay.jar
ENTRYPOINT exec java $JAVA_OPTS  -Djava.security.krb5.conf=/etc/krb5.conf -Duser.timezone=America/Chicago -jar carrierpaymentautopay.jar
