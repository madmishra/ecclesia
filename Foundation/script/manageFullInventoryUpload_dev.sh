rm /home/oms95/inventoryUpload/*.gz

sed -i -e '/^[[:blank:]]*$/d' /home/oms95/inventoryUpload/InventoryUplaod.xml

sed -i -e '1i\'"<?xml version="1.0" ?>\n<AvailabilityChanges>" -e '$ a\'"</AvailabilityChanges>" /home/oms95/inventoryUpload/InventoryUplaod.xml

cd /home/oms95/inventoryUpload
  for f in *.xml
        do
             echo "Compressing file: ${f}"
            gzip -c -f < $f > ${f%%.xml}.gz
        done

  echo "GZip Compression Completed..."
  
curl -u sftp_SterlingDev:P6@a64Tm93?7 -T /home/oms95/inventoryUpload/*.gz sftp://sdex.indigo.ca/Sterling/store-inventory-full-from-sterling/

rm /home/oms95/inventoryUpload/*.xml