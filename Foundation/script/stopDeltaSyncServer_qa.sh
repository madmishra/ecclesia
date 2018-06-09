PROCESS_COUNT=$(ps -ef | grep FullSync | grep -v "grep" | wc -l)
        if [ $PROCESS_COUNT -gt 0 ];
        then
                cd /datadrive/opt/IBM/bin
                nohup ./stopIntegrationServer.sh -name Indg_InvDeltaSync /datadrive/opt/IBM/logs/Delta_Sync.log &
        exit	
        fi