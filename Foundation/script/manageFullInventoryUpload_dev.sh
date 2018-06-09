rm /home/oms95/inventoryUpload/*.gz

sed -i -e '/^[[:blank:]]*$/d' /home/oms95/inventoryUpload/InventoryUplaod.xml

sed -i -e '1i\'"<?xml version="1.0" ?>\n<AvailabilityChanges>" -e '$ a\'"</AvailabilityChanges>" /home/oms95/inventoryUpload/InventoryUplaod.xml

cd /home/oms95/inventoryUpload
  for f in *.xml
        do
             echo "Compressing file: ${f}"
            gzip $f
        done

  echo "GZip Compression Completed..."
  
curl -u sftp_SterlingDev:P6@a64Tm93?7 -T /home/oms95/inventoryUpload/*.gz sftp://sdex.indigo.ca/Sterling/store-inventory-full-from-sterling/

rm /home/oms95/inventoryUpload/*.xml

nohup /data/oms95/Foundation/bin/agentserver.sh Indg_RTAMDeltaAgent \"-Xms512m -Xmx512m -XX:MaxPermSize=512m\" > /data/oms95/Foundation/logs/Indg_RTAMDeltaAgent.log &