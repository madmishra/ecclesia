cd /data/oms95/Foundation/bin
nohup /data/oms95/Foundation/bin/startIntegrationServer.sh IndgDeltaSync \"-Xms512m -Xmx512m -XX:MaxPermSize=512m\" > /data/oms95/Foundation/logs/Delta_Sync_${date}.log &
