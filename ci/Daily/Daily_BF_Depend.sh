cmd="./node_modules/newman/bin/newman -o results_bf_ia_int.json --requestTimeout 240000 -x -e ./environments/int.postman_environment -g 579f8660-01e6-4feb-8764-ec132432ebb1  -c ./collections/all/BF-IA-Broker_Daily.postman_collection"
$cmd
 cmd="./node_modules/newman/bin/newman -o results_bf_ia_stage.json --requestTimeout 240000 -x -e ./environments/stage.postman_environment -g 579f8660-01e6-4feb-8764-ec132432ebb1 -c ./collections/all/BF-IA-Broker_Daily.postman_collection"
$cmd
cmd="./node_modules/newman/bin/newman -o results_bf_ia_prod.json --requestTimeout 240000 -x -e ./environments/prod.postman_environment -g 579f8660-01e6-4feb-8764-ec132432ebb1 -c ./collections/all/BF-IA-Broker_Daily.postman_collection"
$cmd
cmd="./node_modules/newman/bin/newman -o results_GeoAxis.json --requestTimeout 240000 -x -e -g 0efdaa83-18d6-4513-8a7c-c52ef5b07910 -c ./collections/all/GeoAxis.postman_collection"
$cmd
cmd="./node_modules/newman/bin/newman -o results_GeoServer.json --requestTimeout 240000 -x  -g a0ec53eb-c626-4f82-85d2-eaf4c0f1608b -c ./collections/all/GeoServer.postman_collection"
$cmd
cmd="./node_modules/newman/bin/newman -o results_GeoServer.json --requestTimeout 240000 -x -g 09e71c41-d5a2-4936-88b2-7b4497cfb1df -c ./collections/all/GeoServer.postman_collection"
$cmd
cmd="./node_modules/newman/bin/newman -o results_GeoServer.json --requestTimeout 240000 -x -g 86f5b2c7-6006-4f8b-977f-49833fbc575c -c ./collections/all/GeoServer.postman_collection"
$cmd
