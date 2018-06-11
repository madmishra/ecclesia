curl -u sftp_SterlingDev:P6@a64Tm93?7 -T /home/oms95/inventoryUpload/*.gz sftp://sdex.indigo.ca/Sterling/store-inventory-full-from-sterling/

touch /home/oms95/inventoryUpload/execute.done

curl -u sftp_SterlingDev:P6@a64Tm93?7 -T /home/oms95/inventoryUpload/*.done sftp://sdex.indigo.ca/Sterling/store-inventory-full-from-sterling/

rm /home/oms95/inventoryUpload/*

nohup /data/oms95/Foundation/bin/agentserver.sh Indg_RTAMDeltaAgent \"-Xms512m -Xmx512m -XX:MaxPermSize=512m\" > /data/oms95/Foundation/logs/Indg_RTAMDeltaAgent.log &