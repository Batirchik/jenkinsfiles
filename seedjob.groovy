#! /usr/bin/env groovy

def env_type_mapping = ['PreProd': 'dev|stage', 'Prod': 'aws']

nestedView('AppGroup1') {
  views {
    nestedView('PreProd') {
      views {
        listView('US') {
          jobs {
              regex(/.*AppGroup1.*_US_PreProd/)
          }
          columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
          }
        }
        listView('CA') {
          jobs {
              regex(/.*AppGroup1.*_CA_PreProd/)
          }
          columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
          }
        }
        listView('JP') {
          jobs {
              regex(/.*AppGroup1.*_JP_PreProd/)
          }
          columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
          }
        }
        listView('0_Common') {
          jobs {
              regex(/.*AppGroup1.*_Common_PreProd/)
          }
          columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
          }
        }
      }
    }
    nestedView('Prod') {
      views {
        listView('US') {
          jobs {
              regex(/.*AppGroup1.*_US_Prod/)
          }
          columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
          }
        }
        listView('CA') {
          jobs {
              regex(/.*AppGroup1.*_CA_Prod/)
          }
          columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
          }
        }
        listView('JP') {
          jobs {
              regex(/.*AppGroup1.*_JP_Prod/)
          }
          columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
          }
        }
        listView('0_Common') {
          jobs {
              regex(/.*AppGroup1.*_Common_Prod/)
          }
          columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
          }
        }
      }
    }
  }
}

nestedView('AppGroup2') {
  views {
    listView('PreProd') {
      jobs {
          regex(/.*AppGroup2.*_PreProd/)
      }
      columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
      }
    }
    listView('Prod') {
      jobs {
          regex(/.*AppGroup2.*_Prod/)
      }
      columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
      }
    }
  }
}

listView('0_External_Callbacks') {
  jobs {
       name('HDIJobRunner')
       name('ChefServerCleaner')
       name('ChefBootstrapper')
  }
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
  }
}

listView('0_CICD') {
  jobs {
    name('ARMCI_Job')
    name('Create_ARM_Test')
    name('Scale_ARM_Test')
    name('Delete_ARM_Test')
  }
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
  }
}

['PreProd', 'Prod'].each { env_type ->
  ['Create', 'Scale', 'Delete'].each { action ->

    // AppGroup1 jobs
    ['US', 'CA', 'JP'].each { market ->
      def market_low = market.toLowerCase()

      multibranchPipelineJob("${action}_AppGroup1_${market}_${env_type}") {
        branchSources {
          branchSource {
            source {
              git {
                remote('git@github.com:lrvan/ARM-templates.git')
                credentialsId('jenkins_ssh_user_key')
                traits {
                  branchDiscoveryTrait()
                  headRegexFilter {
                    regex(/(${env_type_mapping[env_type]})-(app11|app12).*-${market_low}-.*$/)
                  }
                }
              }
            }
            strategy {
              defaultBranchPropertyStrategy {
                props {
                  noTriggerBranchProperty()
                }
              }
            }
          }
        }
        triggers {
          periodic(30)
        }
      }
    }
    multibranchPipelineJob("${action}_AppGroup1_Common_${env_type}") {
      branchSources {
        branchSource {
          source {
            git {
              remote('git@github.com:lrvan/ARM-templates.git')
              credentialsId('jenkins_ssh_user_key')
              traits {
                branchDiscoveryTrait()
                headRegexFilter {
                  regex(/(${env_type_mapping[env_type]})-(app10-common1)-.*$/)
                }
              }
            }
          }
          strategy {
            defaultBranchPropertyStrategy {
              props {
                noTriggerBranchProperty()
              }
            }
          }
        }
      }
      triggers {
        periodic(30)
      }
    }

    // AppGroup2 jobs
    multibranchPipelineJob("${action}_AppGroup2_${env_type}") {
      branchSources {
        branchSource {
          source {
            git {
              remote('git@github.com:lrvan/ARM-templates.git')
              credentialsId('jenkins_ssh_user_key')
              traits {
                branchDiscoveryTrait()
                headRegexFilter {
                  regex(/(${env_type_mapping[env_type]})-(app21|app22)-.*$/)
                }
              }
            }
          }
          strategy {
            defaultBranchPropertyStrategy {
              props {
                noTriggerBranchProperty()
              }
            }
          }
        }
      }
      triggers {
        periodic(30)
      }
    }
  }
  multibranchPipelineJob("HDIJobRunner_AppGroup2_${env_type}") {
    branchSources {
      branchSource {
        source {
          git {
            remote('git@github.com:lrvan/ARM-templates.git')
            credentialsId('jenkins_ssh_user_key')
            traits {
              branchDiscoveryTrait()
              headRegexFilter {
                regex(/(${env_type_mapping[env_type]})-AppGroup2-hb$/)
              }
            }
          }
        }
        strategy {
          defaultBranchPropertyStrategy {
            props {
              noTriggerBranchProperty()
            }
          }
        }
      }
    }
    triggers {
      periodic(30)
    }
  }
}

// External callback jobs
[
  'ChefBootstrapper': 'ChefBootstrap.groovy',
  'ChefServerCleaner': 'ChefCleanup.groovy',
  'HDIJobRunner': 'HDIJobRunner.groovy'
].each {jobname, filename ->
  pipelineJob("${jobname}") {
      authenticationToken('')
      concurrentBuild()
      logRotator(numToKeep=100)
      definition {
        cpsScmFlowDefinition {
          scm {
            gitSCM {
              branches {
                branchSpec {
                  name('develop')
                }
              }
              doGenerateSubmoduleConfigurations(false)
              browser {}
              gitTool('')
              userRemoteConfigs {
                userRemoteConfig {
                  url('git@github.com:lrvan/jenkinsfiles.git')
                  credentialsId('jenkins_ssh_user_key')
                  name('origin')
                  refspec('develop')
                }
              }
            }
          }
          lightweight(true)
          scriptPath("${filename}")
        }
      }
  }
}

// CICD jobs
['ARMCI_Job', 'Create_ARM_Test', 'Scale_ARM_Test', 'Delete_ARM_Test'].each { jobname ->
  multibranchPipelineJob("${jobname}") {
    branchSources {
      branchSource {
        source {
          git {
            remote('git@github.com:lrvan/ARM-templates.git')
            credentialsId('jenkins_ssh_user_key')
            traits {
              branchDiscoveryTrait()
              headRegexFilter {
                regex(/^.*-develop$/)
              }
            }
          }
        }
        strategy {
          defaultBranchPropertyStrategy {
            props {
              noTriggerBranchProperty()
            }
          }
        }
      }
    }
    triggers {
      periodic(30)
    }
  }
}
