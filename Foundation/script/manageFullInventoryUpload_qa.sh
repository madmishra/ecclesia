curl -u sftp_SterlingDev:P6@a64Tm93?7 -T /home/oms95/inventoryUpload/*.gz sftp://sdex.indigo.ca/Sterling-QA/store-inventory-full-from-sterling/

touch /home/oms95/inventoryUpload/execute.done

curl -u sftp_SterlingDev:P6@a64Tm93?7 -T /home/oms95/inventoryUpload/*.done sftp://sdex.indigo.ca/Sterling-QA/store-inventory-full-from-sterling/

rm /home/oms95/inventoryUpload/*

nohup /datadrive/opt/IBM/bin/agentserver.sh Indg_RTAMDeltaAgent \"-Xms512m -Xmx512m -XX:MaxPermSize=512m\" > /datadrive/opt/IBM/logs/Indg_RTAMDeltaAgent.log &