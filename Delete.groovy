#! /usr/bin/env groovy

node() {
  stage ('Checkout') {
    cleanWs()
    checkout([
      $class: 'GitSCM', branches: [[name: env.BRANCH_NAME]],
      extensions: [
        [$class: 'RelativeTargetDirectory', relativeTargetDir: 'arm_templates'],
        [$class: 'CleanBeforeCheckout']
      ],
      userRemoteConfigs: [[credentialsId: 'jenkins_ssh_user_key', url: 'git@github.com:lrvan/ARM-templates.git']]
    ])
  }

  stage ('Main') {
    println env.BRANCH_NAME
  }

}
