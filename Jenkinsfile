pipeline {
    agent any
    stages{
        stage ("Build"){
            steps { sh 'mvn clean compile'}
        }
        stage ('Testing Stage') {
            steps { sh 'mvn test' }
        }
        stage ("Package"){
             steps { sh 'mvn package -DskipTests'}
        }
        stage ("copy artifact"){
             steps { sh 'cp target/auth-course-0.0.1-SNAPSHOT.jar /tmp/ecom_app.jar && chmod 755 /tmp/ecom_app.jar'}
        }
        stage ("Deploy"){
             steps { sh 'cd /tmp  && java -jar ecom_app.jar &'}
        }
    }
}
