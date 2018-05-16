PROCESS_COUNT=$(ps -ef | grep FullSync | grep -v "grep" | wc -l)
        if [ $PROCESS_COUNT -gt 0 ];
        then
                cd /Indigo/OMS95/Foundation/bin
                nohup ./stopIntegrationServer.sh -name DeltaSync /Indigo & >/dev/null
        exit
        fi