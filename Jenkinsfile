@Library('pipelib@master') _

node {
    
    stage("Setup") {
    deleteDir()  
    git([
      url: "https://github.com/venicegeo/bftest-integration",
      branch: "master"
    ])
  }
    
    
    def nodejs = tool 'NodeJS_6'
    def root = pwd()
    
     withEnv(["PATH+=${nodejs}/bin", "NPM_CONFIG_CACHE=${root}/.npmcache", "HOME=${WORKSPACE}"]) {
        sh "mkdir -p ${root}/.npmcache"
        sh "npm install newman"
        sh "pwd"
        sh "ls -al"
        //sh "ls -al ${root}/.npmcache/newman/3.6.1/package/"
        sh "node -v"
        sh "npm -v"
        sh "newman -v"
       
     }

  try {
   stage ("GeoAxis-Health-Check") {
      withCredentials([file(credentialsId: '0efdaa83-18d6-4513-8a7c-c52ef5b07910', variable: 'GEOAXIS')]) { 
              
            withEnv(["PATH+=${nodejs}/bin"]) {
            sh "pwd"
            sh "curl -H 'Token: $GEOAXIS' https://gxisaccess.gxaccess.com/oam/servlet/CredCollector"

            sh "/jslave/workspace/venice/beachfront/health-job/node_modules/newman/bin/newman.js  -o results_GeoAxis.json --requestTimeout 240000 -x -g $GEOAXIS -c ./ci/Daily/collections/all/GeoAxis.postman_collection"  
        
              
         }    
       } 
    } 
    
    stage ("Geoserver-Int-Health-Check") {
        withCredentials([file(credentialsId: '09e71c41-d5a2-4936-88b2-7b4497cfb1df', variable: 'GEOSERVER')]) {            
          withCredentials([file(credentialsId: '579f8660-01e6-4feb-8764-ec132432ebb1', variable: 'POSTMAN_FILE')]) {   
            withEnv(["PATH+=${nodejs}/bin"]) {
            sh "pwd"
            sh "curl -H 'Token: $GEOSERVER' http://gsn-geose-LoadBala-17USYYB36BFDL-1788485819.us-east-1.elb.amazonaws.com"
            sh "ls -al /jslave/workspace/venice/beachfront/health-job/node_modules/newman/bin/"  
            sh "/jslave/workspace/venice/beachfront/health-job/node_modules/newman/bin/newman.js -o results_bf_ia_int.json --requestTimeout 240000 -x -e ./ci/Daily/environments/int.postman_environment -g $POSTMAN_FILE  -c ./ci/Daily/collections/all/BF-IA-Broker_Daily.postman_collection"     

           sh "/jslave/workspace/venice/beachfront/health-job/node_modules/newman/bin/newman.js -o results_GeoServer.json --requestTimeout 240000 -x  -g $GEOSERVER -c ./ci/Daily/collections/all/GeoServer.postman_collection"

  
         }

            }
        } 
    }    
   

    stage ("Geoserver-Stage-Health-Check") {
       
        withCredentials([file(credentialsId: '86f5b2c7-6006-4f8b-977f-49833fbc575c', variable: 'GEOSERVER')]) {            
           withCredentials([file(credentialsId: '579f8660-01e6-4feb-8764-ec132432ebb1', variable: 'POSTMAN_FILE')]) {  
             
            withEnv(["PATH+=${nodejs}/bin"]) {
            sh "pwd"
            sh "curl -H 'Token: $GEOSERVER' http://gsn-geose-loadbala-c826vph91bwm-584736092.us-east-1.elb.amazonaws.com/"
            sh "ls -al /jslave/workspace/venice/beachfront/health-job/node_modules/newman/bin/"
            sh "/jslave/workspace/venice/beachfront/health-job/node_modules/newman/bin/newman.js -o results_bf_ia_stage.json --requestTimeout 240000 -x -e ./ci/Daily/environments/stage.postman_environment -g $POSTMAN_FILE  -c ./ci/Daily/collections/all/BF-IA-Broker_Daily.postman_collection"
  
            sh "/jslave/workspace/venice/beachfront/health-job/node_modules/newman/bin/newman.js  -o results_GeoServer.json --requestTimeout 240000 -x -g $GEOSERVER -c ./ci/Daily/collections/all/GeoServer.postman_collection"
             }     
            }
        }
    }
  
     
    stage ("Geoserver-Dev-Health-Check") {
     // withCredentials([file(credentialsId: 'a0ec53eb-c626-4f82-85d2-eaf4c0f1608b', variable: 'GEOSERVER')]) {            
         withCredentials([file(credentialsId: '579f8660-01e6-4feb-8764-ec132432ebb1', variable: 'POSTMAN_FILE')]) {      
              withEnv(["PATH+=${nodejs}/bin"]) {
              sh "pwd"
              sh "curl -H 'Token: $GEOSERVER' http://gsn-geose-loadbala-1kip17sjd2int-1731019158.us-east-1.elb.amazonaws.com/"
              sh "ls -al /jslave/workspace/venice/beachfront/health-job/node_modules/newman/bin/"
              sh "/jslave/workspace/venice/beachfront/health-job/node_modules/newman/bin/newman.js -o results_bf_ia_int.json --requestTimeout 240000 -x -e ./ci/Daily/environments/prod.postman_environment -g $POSTMAN_FILE  -c ./ci/Daily/collections/all/BF-IA-Broker_Daily.postman_collection"
             sh "/jslave/workspace/venice/beachfront/health-job/node_modules/newman/bin/newman.js  -o results_GeoServer.json --requestTimeout 240000 -x -g $GEOSERVER -c ./ci/Daily/collections/all/GeoServer.postman_collection"
              //  }
               }
            }
        }
    }
      
    catch (err) {
      currentBuild.result = "FAILURE"
        mail body: "project build error is here: ${env.BUILD_URL}" ,
            subject: 'project build failed',
            to: 'afroje.reshma@gmail.com' 
        throw err
    }
  } 


