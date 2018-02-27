#! /usr/bin/env groovy

stage ('Main') {
  node() {
    println "${env}"
  }
}

return this
