

@Library('pipelib@master') _

node ("sl62") {
  stage("Setup") {
    git([
      url: "https://github.com/venicegeo/bftest-integration",
      branch: "master"
    ])
  }

try {

  stage ("Health Check") {
              sh "pwd"
     }
    } 
        
        catch (err) {
      currentBuild.result = "FAILURE"
       mail body: "project build error is here: ${env.BUILD_URL}" ,
            subject: 'project build failed',
            to: 'afroje.reshma@gmail.com' 
       throw err
    }
 
  stage ("Cleanup") {
    deleteDir()
  }
 }

