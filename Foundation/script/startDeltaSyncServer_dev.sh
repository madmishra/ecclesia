nohup /data/oms95/Foundation/bin/startIntegrationServer.sh Indg_InvDeltaSync \"-Xms512m -Xmx512m -XX:MaxPermSize=512m\" > /data/oms95/Foundation/logs/Delta_Sync.log &

nohup /data/oms95/Foundation/bin/agentserverstop.sh -name Indg_RTAMDeltaAgent &