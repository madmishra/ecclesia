PROCESS_COUNT=$(ps -ef | grep FullSync | grep -v "grep" | wc -l)
        if [ $PROCESS_COUNT -gt 0 ];
        then
                cd /data/oms95/Foundation/bin
                nohup ./stopIntegrationServer.sh -name IndgDeltaSync /data/oms95/Foundation/logs/Delta_Sync_${date}.log &
        exit	
        fi