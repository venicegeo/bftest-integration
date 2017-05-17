cmd="./node_modules/newman/bin/newman -o results_bf_ia_int.json --requestTimeout 240000 -x -e ./environments/int.postman_environment -c ./collections/all/BF-IA-Broker_Daily.postman_collection"
$cmd
 cmd="./node_modules/newman/bin/newman -o results_bf_ia_stage.json --requestTimeout 240000 -x -e ./environments/stage.postman_environment -c ./collections/all/BF-IA-Broker_Daily.postman_collection"
$cmd
cmd="./node_modules/newman/bin/newman -o results_bf_ia_prod.json --requestTimeout 240000 -x -e ./environments/prod.postman_environment -c ./collections/all/BF-IA-Broker_Daily.postman_collection"
$cmd
cmd="./node_modules/newman/bin/newman -o results_GeoAxis.json --requestTimeout 240000 -x -e ./environments/GeoAxis.postman_environment -c ./collections/all/GeoAxis.postman_collection"
$cmd
cmd="./node_modules/newman/bin/newman -o results_GeoServer.json --requestTimeout 240000 -x -e ./environments/GeoServer.postman_environment -c ./collections/all/GeoServer.postman_collection"
$cmd
