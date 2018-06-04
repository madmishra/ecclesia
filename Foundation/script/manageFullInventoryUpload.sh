sed -i -e '/^[[:blank:]]*$/d' /home/devadmin/InventoryUplaod.xml

sed -i -e '1i\'"<?xml version="1.0" ?>\n<AvailabilityChanges>" -e '$ a\'"</AvailabilityChanges>" /home/devadmin/InventoryUplaod.xml