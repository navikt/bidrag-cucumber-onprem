kubectx dev-fss
kubectl exec --tty deployment/bidrag-cucumber-onprem printenv | grep -E 'AZURE_|_URL|SCOPE' > src/test/resources/application-lokal-nais-secrets.properties
