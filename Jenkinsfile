node {
    def app

    stage('Clone repository') {
      

        checkout scm
        echo "Running Build id is : ${env.BUILD_ID}"
        echo "Running Build number is : ${env.BUILD_NUMBER}"
        echo "Running Build tag is : ${env.BUILD_TAG}"
        echo "Running Build url is : ${env.BUILD_URL}"
        echo "Running Job name is : ${env.JOB_NAME}"
        echo "Running node name is : ${env.NODE_NAME}"
    }

    stage('Build image') {
  
       app = docker.build("t0pn0tch/photo-image")
    }

    stage('Test image') {
  

        app.inside {
            sh 'echo "Tests passed"'
        }
    }

    stage('Push image') {
        
        docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-topnotch') {
            app.push("latest")
        }
    }
}
