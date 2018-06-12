PROCESS_COUNT=$(ps -ef | grep FullSync | grep -v "grep" | wc -l)
        if [ $PROCESS_COUNT -gt 0 ];
        then
                cd /data/oms95/Foundation/bin
                nohup ./stopIntegrationServer.sh -name Indg_InvDeltaSync /data/oms95/Foundation/logs/Delta_Sync.log &
        exit	
        fi

nohup /data/oms95/Foundation/bin/agentserverstop.sh -name Indg_RTAMDeltaAgent &