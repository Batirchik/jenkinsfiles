#! /usr/bin/env groovy

def repo = 'git@github.com:lrvan/jenkinsfiles.git'
def branch = 'master'
def creds = 'jenkins_ssh_user_key'
def action = "${env.JOB_NAME}".tokenize('_')[0]

fileLoader.fromGit(action, repo, branch, creds, '')
