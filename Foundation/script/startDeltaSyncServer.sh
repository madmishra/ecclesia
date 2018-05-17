cd /Indigo/OMS95/Foundation/bin
nohup /Indigo/OMS95/Foundation/bin/startIntegrationServer.sh DeltaSync \"-Xms512m -Xmx512m -XX:MaxPermSize=512m\" > /Indigo/OMS95/Foundation/logs/Delta_Sync_${date}.log &
