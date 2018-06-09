nohup /datadrive/opt/IBM/bin/startIntegrationServer.sh Indg_InvDeltaSync \"-Xms512m -Xmx512m -XX:MaxPermSize=512m\" > /datadrive/opt/IBM/logs/Delta_Sync.log &

nohup /datadrive/opt/IBM/bin/stopIntegrationServer.sh -name Indg_RTAMDeltaAgent &
