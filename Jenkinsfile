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
    
    stage('Deploy Kubernetes pod'){
        
        withKubeConfig([credentialsId: 'minikube-kubeconfig-file', serverUrl: 'https://192.168.99.102:8443']) {
            
            sh 'curl -LO "https://storage.googleapis.com/kubernetes-release/release/v1.22.1/bin/linux/amd64/kubectl"'  
            sh 'chmod u+x ./kubectl'
            sh 'kubectl apply -f doc-upload.yml'
        }
    }
}
